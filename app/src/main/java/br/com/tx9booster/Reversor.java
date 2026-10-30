package br.com.tx9booster;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Reversor {

    private final RootShell rootShell;
    private final Historico historico;

    public Reversor(RootShell rootShell, Historico historico) {
        if (rootShell == null || historico == null) {
            throw new IllegalArgumentException("RootShell e Histórico são obrigatórios.");
        }
        this.rootShell = rootShell;
        this.historico = historico;
    }

    public ResultadoReversao reverterUltimaAcao() throws IOException {
        List<Historico.Registro> registros = historico.listar();
        for (int indice = registros.size() - 1; indice >= 0; indice--) {
            Historico.Registro registro = registros.get(indice);
            if (ehAcaoDePacote(registro)
                    && "OK".equals(historico.resultadoAtual(registro, registros))
                    && !historico.foiRevertido(registro, registros)) {
                return reverter(registro);
            }
        }
        return ResultadoReversao.falha("Não há ação de pacote disponível para reverter.");
    }

    public List<ResultadoReversao> reverterGrupo(List<Historico.Registro> grupo)
            throws IOException {
        List<ResultadoReversao> resultados = new ArrayList<ResultadoReversao>();
        if (grupo == null) {
            return Collections.unmodifiableList(resultados);
        }
        for (int indice = grupo.size() - 1; indice >= 0; indice--) {
            ResultadoReversao resultado = reverter(grupo.get(indice));
            resultados.add(resultado);
            if (!resultado.ok) {
                break;
            }
        }
        return Collections.unmodifiableList(resultados);
    }

    public List<ResultadoReversao> reverterTudo() throws IOException {
        List<Historico.Registro> registros = historico.listar();
        List<Historico.Registro> elegiveis = new ArrayList<Historico.Registro>();
        for (Historico.Registro registro : registros) {
            if (ehAcaoDePacote(registro)
                    && "OK".equals(historico.resultadoAtual(registro, registros))
                    && !historico.foiRevertido(registro, registros)) {
                elegiveis.add(registro);
            }
        }
        return reverterGrupo(elegiveis);
    }

    public ResultadoReversao reverter(Historico.Registro registro) throws IOException {
        if (registro == null || !ehAcaoDePacote(registro)) {
            return ResultadoReversao.falha("Este registro não pode ser revertido por esta etapa.");
        }
        List<Historico.Registro> registros = historico.listar();
        if (!"OK".equals(historico.resultadoAtual(registro, registros))) {
            return ResultadoReversao.falha("A ação não tem resultado confirmado como concluído.");
        }
        if (historico.foiRevertido(registro, registros)) {
            return ResultadoReversao.falha("Esta ação já foi revertida.");
        }
        String originalEsperado = ComandosRoot.DESATIVAR_PACOTE.equals(registro.acao)
            ? "ATIVO" : "DESATIVADO";
        String novoEsperado = ComandosRoot.DESATIVAR_PACOTE.equals(registro.acao)
            ? "DESATIVADO" : "ATIVO";
        if (!originalEsperado.equals(registro.valorOriginal)
            || !novoEsperado.equals(registro.valorNovo)) {
            return ResultadoReversao.falha("Os valores registrados não correspondem à ação.");
        }

        RootShell.Resultado estado = rootShell.executar(ComandosRoot.listarPacotesDesativados());
        if (!estado.ok) {
            return ResultadoReversao.falha("Não foi possível conferir o estado atual: "
                    + estado.erro);
        }
        boolean desativadoAgora = RootShell.pacoteDesativado(estado.saida, registro.alvo);
        boolean estadoEsperado = "DESATIVADO".equals(registro.valorNovo)
                ? desativadoAgora : !desativadoAgora;
        if (!estadoEsperado) {
            return ResultadoReversao.falha(
                    "O pacote mudou desde esta ação. Nada foi alterado.");
        }

        ComandosRoot.Comando inverso;
        try {
            inverso = ComandosRoot.reverterPacote(registro.acao, registro.alvo,
                    registro.comandoInverso);
        } catch (IllegalArgumentException excecao) {
            return ResultadoReversao.falha(
                    "Comando inverso recusado: " + excecao.getMessage());
        }

        Historico.Registro evento;
        try {
            evento = historico.registrarReversao(registro, registro.valorOriginal,
                    inverso.inverso());
        } catch (IOException excecao) {
            return ResultadoReversao.falha("Não foi possível registrar a reversão antes da ação.");
        }

        RootShell.Resultado aplicado = rootShell.executarAcaoPacote(inverso, historico,
                registro.valorNovo, registro.valorOriginal);
        if (!aplicado.ok) {
            historico.registrarResultado(evento.id, "FALHA: " + aplicado.erro);
            return ResultadoReversao.falha(aplicado.erro);
        }
        if (aplicado.registroId != null) {
            historico.registrarResultado(aplicado.registroId, "REVERSAO");
        }
        historico.registrarResultado(evento.id, "OK");
        return ResultadoReversao.sucesso("Ação revertida e estado conferido.");
    }

    private static boolean ehAcaoDePacote(Historico.Registro registro) {
        return ComandosRoot.DESATIVAR_PACOTE.equals(registro.acao)
                || ComandosRoot.REATIVAR_PACOTE.equals(registro.acao);
    }

    public static final class ResultadoReversao {
        public final boolean ok;
        public final String mensagem;

        private ResultadoReversao(boolean ok, String mensagem) {
            this.ok = ok;
            this.mensagem = mensagem;
        }

        private static ResultadoReversao sucesso(String mensagem) {
            return new ResultadoReversao(true, mensagem);
        }

        private static ResultadoReversao falha(String mensagem) {
            return new ResultadoReversao(false, mensagem);
        }
    }
}