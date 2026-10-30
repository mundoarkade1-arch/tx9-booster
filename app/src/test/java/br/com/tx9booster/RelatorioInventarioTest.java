package br.com.tx9booster;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class RelatorioInventarioTest {

    @Test
    public void incluiOsQuatroComandosEAsSaidasRecebidas() {
        List<RootShell.Resultado> resultados = new ArrayList<RootShell.Resultado>();
        resultados.add(RootShell.Resultado.sucesso(0, "package:sistema.exemplo\n"));
        resultados.add(RootShell.Resultado.sucesso(0, "package:desativado.exemplo\n"));
        resultados.add(RootShell.Resultado.sucesso(0, "package:usuario.exemplo\n"));
        resultados.add(RootShell.Resultado.sucesso(0,
                "package:/system/app/Exemplo.apk=app.exemplo\n"));

        String relatorio = RelatorioInventario.formatar("2026-10-30 12:00:00", resultados);

        assertTrue(relatorio.contains("Gerado em: 2026-10-30 12:00:00"));
        assertTrue(relatorio.contains("Modo: somente leitura. Nenhum pacote foi alterado."));
        assertTrue(relatorio.contains("Comando: pm list packages -s"));
        assertTrue(relatorio.contains("Comando: pm list packages -d"));
        assertTrue(relatorio.contains("Comando: pm list packages -3"));
        assertTrue(relatorio.contains("Comando: pm list packages -f"));
        assertTrue(relatorio.contains("package:sistema.exemplo"));
        assertTrue(relatorio.contains("package:desativado.exemplo"));
        assertTrue(relatorio.contains("package:usuario.exemplo"));
        assertTrue(relatorio.contains("package:/system/app/Exemplo.apk=app.exemplo"));
    }

    @Test
    public void mantemFalhaCodigoEErroNoRelatorio() {
        List<RootShell.Resultado> resultados = new ArrayList<RootShell.Resultado>();
        resultados.add(RootShell.Resultado.sucesso(0, ""));
        resultados.add(RootShell.Resultado.falha(5, "", "Permissão negada"));
        resultados.add(RootShell.Resultado.sucesso(0, ""));
        resultados.add(RootShell.Resultado.sucesso(0, ""));

        String relatorio = RelatorioInventario.formatar("data", resultados);

        assertTrue(relatorio.contains("Status: FALHA"));
        assertTrue(relatorio.contains("Código de saída: 5"));
        assertTrue(relatorio.contains("Permissão negada"));
    }

    @Test
    public void informaQuandoUmaSaidaAtingiuOLimite() {
        List<RootShell.Resultado> resultados = new ArrayList<RootShell.Resultado>();
        resultados.add(RootShell.Resultado.sucesso(0, "", "", true));
        resultados.add(RootShell.Resultado.sucesso(0, ""));
        resultados.add(RootShell.Resultado.sucesso(0, ""));
        resultados.add(RootShell.Resultado.sucesso(0, ""));

        String relatorio = RelatorioInventario.formatar("data", resultados);

        assertTrue(relatorio.contains("saída limitada a 512 KiB"));
        assertTrue(relatorio.contains("esta seção está incompleta"));
    }
}