package br.com.tx9booster;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public final class Historico {

    private static final String NOME_ARQUIVO = "historico_root.txt";
    private final File arquivo;

    public Historico(File diretorioPrivado) {
        if (diretorioPrivado == null) {
            throw new IllegalArgumentException("Diretório do Histórico obrigatório.");
        }
        this.arquivo = new File(diretorioPrivado, NOME_ARQUIVO);
    }

    public synchronized Registro registrar(String acao, String alvo,
                                           String valorOriginal, String valorNovo,
                                           String resultado, String comandoInverso)
            throws IOException {
        Registro registro = new Registro(UUID.randomUUID().toString(), agora(), acao, alvo,
                valorOriginal, valorNovo, resultado, comandoInverso);
        acrescentar(registro);
        return registro;
    }

    public synchronized void registrarResultado(String idRegistro, String resultado)
            throws IOException {
        registrar("RESULTADO", idRegistro, "", resultado, resultado, "");
    }

    public synchronized Registro registrarReversao(Registro original, String novoValor,
                                                   String comandoInverso)
            throws IOException {
        if (original == null) {
            throw new IllegalArgumentException("Registro original obrigatório.");
        }
        Registro reversao = registrar("REVERSAO", original.id, original.valorNovo,
                novoValor, "PENDENTE", comandoInverso);
        registrarResultado(reversao.id, "PENDENTE");
        return reversao;
    }

    public synchronized List<Registro> listar() throws IOException {
        if (!arquivo.exists()) {
            return Collections.emptyList();
        }
        List<Registro> registros = new ArrayList<Registro>();
        BufferedReader leitor = new BufferedReader(new FileReader(arquivo));
        try {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                if (linha.length() == 0) {
                    continue;
                }
                registros.add(decodificar(linha));
            }
        } finally {
            leitor.close();
        }
        return Collections.unmodifiableList(registros);
    }

    public synchronized String resultadoAtual(Registro registro, List<Registro> registros) {
        if (registro == null || registros == null) {
            return "ERRO";
        }
        String resultado = registro.resultado;
        for (Registro evento : registros) {
            if ("RESULTADO".equals(evento.acao) && registro.id.equals(evento.alvo)) {
                resultado = evento.valorNovo;
            }
        }
        return resultado;
    }

    public synchronized boolean foiRevertido(Registro registro, List<Registro> registros) {
        if (registro == null || registros == null) {
            return false;
        }
        for (Registro evento : registros) {
            if ("REVERSAO".equals(evento.acao) && registro.id.equals(evento.alvo)
                    && "OK".equals(resultadoAtual(evento, registros))) {
                return true;
            }
        }
        return false;
    }

    public File arquivo() {
        return arquivo;
    }

    private void acrescentar(Registro registro) throws IOException {
        File pai = arquivo.getParentFile();
        if (!pai.exists() && !pai.mkdirs() && !pai.isDirectory()) {
            throw new IOException("Não foi possível criar a pasta privada do Histórico.");
        }
        BufferedWriter escritor = new BufferedWriter(new FileWriter(arquivo, true));
        try {
            escritor.write(codificar(registro));
            escritor.newLine();
        } finally {
            escritor.close();
        }
    }

    private static String codificar(Registro registro) throws IOException {
        String[] campos = {registro.id, registro.dataHora, registro.acao, registro.alvo,
                registro.valorOriginal, registro.valorNovo, registro.resultado,
                registro.comandoInverso};
        StringBuilder linha = new StringBuilder();
        for (int indiceCampo = 0; indiceCampo < campos.length; indiceCampo++) {
            if (indiceCampo > 0) {
                linha.append('\t');
            }
            linha.append(URLEncoder.encode(campos[indiceCampo], "UTF-8"));
        }
        return linha.toString();
    }

    private static Registro decodificar(String linha) throws IOException {
        String[] campos = linha.split("\\t", -1);
        if (campos.length != 8) {
            throw new IOException("Linha inválida no Histórico.");
        }
        try {
            for (int indiceCampo = 0; indiceCampo < campos.length; indiceCampo++) {
                campos[indiceCampo] = URLDecoder.decode(campos[indiceCampo], "UTF-8");
            }
        } catch (IllegalArgumentException excecao) {
            throw new IOException("Texto inválido no Histórico.", excecao);
        }
        return new Registro(campos[0], campos[1], campos[2], campos[3], campos[4],
                campos[5], campos[6], campos[7]);
    }

    private static String agora() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US)
                .format(new Date());
    }

    public static final class Registro {
        public final String id;
        public final String dataHora;
        public final String acao;
        public final String alvo;
        public final String valorOriginal;
        public final String valorNovo;
        public final String resultado;
        public final String comandoInverso;

        private Registro(String id, String dataHora, String acao, String alvo,
                         String valorOriginal, String valorNovo, String resultado,
                         String comandoInverso) {
            this.id = texto(id);
            this.dataHora = texto(dataHora);
            this.acao = texto(acao);
            this.alvo = texto(alvo);
            this.valorOriginal = texto(valorOriginal);
            this.valorNovo = texto(valorNovo);
            this.resultado = texto(resultado);
            this.comandoInverso = texto(comandoInverso);
        }

        private static String texto(String valor) {
            return valor == null ? "" : valor;
        }
    }
}