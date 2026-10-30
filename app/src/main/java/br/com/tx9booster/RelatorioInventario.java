package br.com.tx9booster;

import java.util.List;

public final class RelatorioInventario {

    private static final String[] TITULOS = {
            "Pacotes do sistema",
            "Pacotes desativados",
            "Pacotes de usuário",
            "Pacotes com caminhos"
    };

    private RelatorioInventario() {
    }

    public static String formatar(String dataHora, List<RootShell.Resultado> resultados) {
        List<ComandosRoot.Comando> comandos = ComandosRoot.comandosInventario();
        if (resultados == null || resultados.size() != comandos.size()) {
            throw new IllegalArgumentException("O inventário precisa dos quatro resultados.");
        }

        StringBuilder relatorio = new StringBuilder();
        relatorio.append("TX9 Booster - Inventário de pacotes\n");
        relatorio.append("Gerado em: ").append(valor(dataHora)).append("\n");
        relatorio.append("Modo: somente leitura. Nenhum pacote foi alterado.\n");

        for (int indice = 0; indice < comandos.size(); indice++) {
            ComandosRoot.Comando comando = comandos.get(indice);
            RootShell.Resultado resultado = resultados.get(indice);
            if (resultado == null) {
                throw new IllegalArgumentException("Resultado de inventário ausente.");
            }
            relatorio.append("\n=== ").append(TITULOS[indice]).append(" ===\n");
            relatorio.append("Comando: ").append(comando.linha()).append("\n");
            relatorio.append("Status: ").append(resultado.ok ? "OK" : "FALHA").append("\n");
            relatorio.append("Código de saída: ").append(resultado.codigo).append("\n");
                        relatorio.append("Código de saída: ").append(resultado.codigo).append("\n");
                        if (resultado.saidaTruncada) {
                            relatorio.append("Aviso: saída limitada a 512 KiB; esta seção está incompleta.\n");
                        }
            relatorio.append("Saída:\n").append(valor(resultado.saida));
            if (resultado.saida.length() > 0 && !resultado.saida.endsWith("\n")) {
                relatorio.append('\n');
            }
            if (resultado.erro.length() > 0) {
                relatorio.append("Erro:\n").append(resultado.erro);
                if (!resultado.erro.endsWith("\n")) {
                    relatorio.append('\n');
                }
            }
        }
        return relatorio.toString();
    }

    private static String valor(String texto) {
        return texto == null ? "" : texto;
    }
}