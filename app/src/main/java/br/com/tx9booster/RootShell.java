package br.com.tx9booster;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;

public final class RootShell {

    private static final int LIMITE_SAIDA = 524288;
    private final Backend backend;
    private final PoliticaPacotes politicaPacotes;

    public RootShell() {
        this(new ProcessBackend(), new PoliticaPacotes() {
            @Override
            public boolean podeAlterar(String pacote) {
                return false;
            }
        });
    }

    RootShell(Backend backend) {
        this(backend, new PoliticaPacotes() {
            @Override
            public boolean podeAlterar(String pacote) {
                return false;
            }
        });
    }

    RootShell(Backend backend, PoliticaPacotes politicaPacotes) {
        if (backend == null || politicaPacotes == null) {
            throw new IllegalArgumentException("Executor e política de pacotes obrigatórios.");
        }
        this.backend = backend;
        this.politicaPacotes = politicaPacotes;
    }

    public Resultado executar(ComandosRoot.Comando comando) {
        if (comando == null) {
            return Resultado.falha(-1, "", "Comando root inválido.");
        }
        if (ComandosRoot.DESATIVAR_PACOTE.equals(comando.tipo())
            || ComandosRoot.REATIVAR_PACOTE.equals(comando.tipo())) {
            return Resultado.falha(-1, "",
                "Ação de pacote exige registro no Histórico antes da execução.");
        }
        return executarLeitura(comando);
    }

    public Resultado executarAcaoPacote(ComandosRoot.Comando comando,
                                         Historico historico,
                                         String valorOriginal,
                                         String valorNovo) {
        if (comando == null || historico == null
            || !(ComandosRoot.DESATIVAR_PACOTE.equals(comando.tipo())
            || ComandosRoot.REATIVAR_PACOTE.equals(comando.tipo()))) {
            return Resultado.falha(-1, "", "Ação root ou Histórico inválido.");
        }
        if (!politicaPacotes.podeAlterar(comando.alvo())) {
            return Resultado.falha(-1, "",
                    "Pacote bloqueado: ainda não há política baseada no inventário real.");
        }
        String esperadoOriginal = ComandosRoot.DESATIVAR_PACOTE.equals(comando.tipo())
            ? "ATIVO" : "DESATIVADO";
        String esperadoNovo = ComandosRoot.DESATIVAR_PACOTE.equals(comando.tipo())
            ? "DESATIVADO" : "ATIVO";
        if (!esperadoOriginal.equals(valorOriginal) || !esperadoNovo.equals(valorNovo)) {
            return Resultado.falha(-1, "", "Estado original ou novo não corresponde à ação.");
        }
        Resultado acessoRoot = executar(ComandosRoot.testarRoot());
        if (!acessoRoot.ok) {
            return acessoRoot;
        }
        ComandosRoot.Comando consultaPacotes = ComandosRoot.listarPacotesDesativados();
        Resultado estadoInicial = backend.executar(consultaPacotes.linha(),
            consultaPacotes.timeoutMillis());
        if (estadoInicial == null || !estadoInicial.ok) {
            return Resultado.falha(estadoInicial == null ? -1 : estadoInicial.codigo,
                estadoInicial == null ? "" : estadoInicial.saida,
                "Não foi possível ler o estado do pacote antes da ação.");
        }
        boolean desativadoAntes = pacoteDesativado(estadoInicial.saida, comando.alvo());
        boolean estadoOriginalConfere = "DESATIVADO".equals(valorOriginal)
            ? desativadoAntes : !desativadoAntes;
        if (!estadoOriginalConfere) {
            return Resultado.falha(0, estadoInicial.saida,
                "O estado atual não corresponde ao valor original informado.");
        }

        Historico.Registro registro;
        try {
            registro = historico.registrar(comando.tipo(), comando.alvo(), valorOriginal,
                valorNovo, "PENDENTE", comando.inverso());
        } catch (IOException e) {
            return Resultado.falha(-1, "", "Não foi possível registrar antes da ação: "
                + mensagem(e));
        }

        Resultado acao = backend.executar(comando.linha(), comando.timeoutMillis());
        if (acao == null || !acao.ok) {
            return concluirAcao(historico, registro, acao == null
                ? Resultado.falha(-1, "", "Executor root sem resultado.") : acao,
                "FALHA");
        }

        Resultado estado = backend.executar(ComandosRoot.listarPacotesDesativados().linha(),
            ComandosRoot.listarPacotesDesativados().timeoutMillis());
        boolean estaDesativado = estado != null && estado.ok
            && pacoteDesativado(estado.saida, comando.alvo());
        boolean resultadoEsperado = "DESATIVADO".equals(valorNovo)
            ? estaDesativado : estado != null && estado.ok && !estaDesativado;
        if (!resultadoEsperado) {
            String erro = estado == null || !estado.ok
                ? "Não foi possível confirmar o estado final do pacote."
                : "O estado final do pacote não corresponde ao esperado.";
            return concluirAcao(historico, registro,
                Resultado.falha(estado == null ? -1 : estado.codigo,
                    estado == null ? "" : estado.saida, erro), "FALHA");
        }
        return concluirAcao(historico, registro, acao, "OK");
    }

    private Resultado executarLeitura(ComandosRoot.Comando comando) {
        Resultado resultado = backend.executar(comando.linha(), comando.timeoutMillis());
        if (ComandosRoot.TESTAR_ROOT.equals(comando.tipo())
                && (resultado == null || !resultado.ok
                || !resultado.saida.matches("(?s).*\\buid=0\\b.*"))) {
            int codigo = resultado == null ? -1 : resultado.codigo;
            String saida = resultado == null ? "" : resultado.saida;
            String erro = resultado != null && resultado.erro.length() > 0
                    ? resultado.erro : "O comando id não confirmou uid=0.";
            return Resultado.falha(codigo, saida,
                    "Acesso root negado ou não confirmado. " + erro);
        }
        if (resultado == null) {
            return Resultado.falha(-1, "", "O executor root não retornou resultado.");
        }
        return resultado;
    }

    private static Resultado concluirAcao(Historico historico, Historico.Registro registro,
                                           Resultado resultado, String estado) {
        try {
            historico.registrarResultado(registro.id, estado);
            return resultado.comRegistroId(registro.id);
        } catch (IOException e) {
            return Resultado.falha(resultado.codigo, resultado.saida,
                    "A ação terminou, mas não foi possível salvar o resultado no Histórico: "
                            + mensagem(e)).comRegistroId(registro.id);
        }
    }

    static boolean pacoteDesativado(String saida, String pacote) {
        if (saida == null || pacote == null) {
            return false;
        }
        String[] linhas = saida.split("\\r?\\n");
        String esperado = "package:" + pacote;
        for (String linha : linhas) {
            if (esperado.equals(linha.trim())) {
                return true;
            }
        }
        return false;
    }

    private static String mensagem(Exception e) {
        return e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
    }

    interface Backend {
        Resultado executar(String comando, long timeoutMillis);
    }

    interface PoliticaPacotes {
        boolean podeAlterar(String pacote);
    }

    public static final class Resultado {
        public final boolean ok;
        public final int codigo;
        public final String saida;
        public final String erro;
        public final boolean saidaTruncada;
        public final String registroId;

        private Resultado(boolean ok, int codigo, String saida, String erro,
                          boolean saidaTruncada, String registroId) {
            this.ok = ok;
            this.codigo = codigo;
            this.saida = saida == null ? "" : saida;
            this.erro = erro == null ? "" : erro;
            this.saidaTruncada = saidaTruncada;
            this.registroId = registroId;
        }

        static Resultado sucesso(int codigo, String saida) {
            return sucesso(codigo, saida, "");
        }

        static Resultado sucesso(int codigo, String saida, String erro) {
            return sucesso(codigo, saida, erro, false);
        }

        static Resultado sucesso(int codigo, String saida, String erro,
                                 boolean saidaTruncada) {
            return new Resultado(true, codigo, saida, erro, saidaTruncada, null);
        }

        static Resultado falha(int codigo, String saida, String erro) {
            return falha(codigo, saida, erro, false);
        }

        static Resultado falha(int codigo, String saida, String erro,
                               boolean saidaTruncada) {
            return new Resultado(false, codigo, saida, erro, saidaTruncada, null);
        }

        private Resultado comRegistroId(String id) {
            return new Resultado(ok, codigo, saida, erro, saidaTruncada, id);
        }
    }

    private static final class ProcessBackend implements Backend {

        @Override
        public Resultado executar(final String comando, long timeoutMillis) {
            Process processo = null;
            try {
                processo = new ProcessBuilder("su", "-c", comando).start();
                final Process processoLeitura = processo;
                final StreamCollector stdout = new StreamCollector(processo.getInputStream());
                final StreamCollector stderr = new StreamCollector(processo.getErrorStream());
                stdout.start();
                stderr.start();

                final AtomicInteger codigo = new AtomicInteger(Integer.MIN_VALUE);
                Thread espera = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            codigo.set(processoLeitura.waitFor());
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }, "tx9-root-wait");
                espera.start();
                espera.join(timeoutMillis);
                if (espera.isAlive()) {
                    processo.destroy();
                    espera.join(1000L);
                    return Resultado.falha(-1, stdout.conteudo(),
                            "Tempo limite excedido ao executar comando root.",
                            stdout.foiTruncada());
                }

                stdout.join(1000L);
                stderr.join(1000L);
                int codigoSaida = codigo.get();
                String saida = stdout.conteudo();
                String erro = stderr.conteudo();
                if (codigoSaida == 0) {
                        return Resultado.sucesso(codigoSaida, saida, erro,
                            stdout.foiTruncada());
                }
                String detalhe = erro.length() > 0 ? erro
                        : "Comando root falhou (código " + codigoSaida + ").";
                return Resultado.falha(codigoSaida, saida, detalhe,
                    stdout.foiTruncada());
            } catch (IOException e) {
                return Resultado.falha(-1, "", "Não foi possível iniciar su: "
                        + mensagem(e));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                if (processo != null) {
                    processo.destroy();
                }
                return Resultado.falha(-1, "", "Execução root interrompida.");
            } finally {
                if (processo != null) {
                    processo.destroy();
                }
            }
        }

        private static String mensagem(Exception e) {
            return e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
        }
    }

    private static final class StreamCollector extends Thread {
        private final InputStream stream;
        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        private boolean truncada;

        StreamCollector(InputStream stream) {
            this.stream = stream;
            setName("tx9-root-output");
        }

        @Override
        public void run() {
            byte[] trecho = new byte[4096];
            int lidos;
            try {
                while ((lidos = stream.read(trecho)) != -1) {
                    synchronized (buffer) {
                        int espaco = LIMITE_SAIDA - buffer.size();
                        if (espaco > 0) {
                            buffer.write(trecho, 0, Math.min(espaco, lidos));
                        }
                        if (lidos > espaco) {
                            truncada = true;
                        }
                    }
                }
            } catch (IOException ignorado) {
                // O processo pode encerrar os fluxos ao atingir o tempo limite.
            } finally {
                try {
                    stream.close();
                } catch (IOException ignorado) {
                    // Não há ação adicional para um fluxo já encerrado.
                }
            }
        }

        synchronized String conteudo() {
            synchronized (buffer) {
                return new String(buffer.toByteArray(), Charset.forName("UTF-8"));
            }
        }

        synchronized boolean foiTruncada() {
            synchronized (buffer) {
                return truncada;
            }
        }
    }
}