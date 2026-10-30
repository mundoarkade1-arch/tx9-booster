package br.com.tx9booster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReversorTest {

    @Rule
    public final TemporaryFolder pastaTemporaria = new TemporaryFolder();

    @Test
    public void reverteSomenteDepoisDeConferirEstadoAtual() throws Exception {
        final Set<String> desativados = new HashSet<String>();
        desativados.add("br.exemplo.app");
        RootShell shell = new RootShell(new RootShell.Backend() {
            @Override
            public RootShell.Resultado executar(String comando, long timeoutMillis) {
                if ("id".equals(comando)) {
                    return RootShell.Resultado.sucesso(0, "uid=0(root)");
                }
                if ("pm list packages -d".equals(comando)) {
                    String saida = desativados.contains("br.exemplo.app")
                            ? "package:br.exemplo.app\n" : "";
                    return RootShell.Resultado.sucesso(0, saida);
                }
                if ("pm enable --user 0 br.exemplo.app".equals(comando)) {
                    desativados.remove("br.exemplo.app");
                    return RootShell.Resultado.sucesso(0, "Package enabled");
                }
                return RootShell.Resultado.falha(1, "", "comando inesperado");
            }
        }, permitirPacoteExemplo());
        Historico historico = new Historico(pastaTemporaria.newFolder());
        Historico.Registro acao = historico.registrar(ComandosRoot.DESATIVAR_PACOTE,
                "br.exemplo.app", "ATIVO", "DESATIVADO", "OK",
                "pm enable --user 0 br.exemplo.app");

        Reversor.ResultadoReversao resultado = new Reversor(shell, historico)
                .reverter(acao);

        assertTrue(resultado.ok);
        assertFalse(desativados.contains("br.exemplo.app"));
        assertTrue(historico.foiRevertido(acao, historico.listar()));
    }

    @Test
    public void recusaReverterQuandoEstadoAtualDiverge() throws Exception {
        RootShell shell = new RootShell(new RootShell.Backend() {
            @Override
            public RootShell.Resultado executar(String comando, long timeoutMillis) {
                if ("id".equals(comando)) {
                    return RootShell.Resultado.sucesso(0, "uid=0(root)");
                }
                return RootShell.Resultado.sucesso(0, "");
            }
        }, permitirPacoteExemplo());
        Historico historico = new Historico(pastaTemporaria.newFolder());
        Historico.Registro acao = historico.registrar(ComandosRoot.DESATIVAR_PACOTE,
                "br.exemplo.app", "ATIVO", "DESATIVADO", "OK",
                "pm enable --user 0 br.exemplo.app");

        Reversor.ResultadoReversao resultado = new Reversor(shell, historico)
                .reverter(acao);

        assertFalse(resultado.ok);
        assertTrue(resultado.mensagem.contains("Nada foi alterado"));
    }

    @Test
    public void reverterTudoProcessaDaMaisNovaParaMaisAntiga() throws Exception {
        final Set<String> desativados = new HashSet<String>();
        desativados.add("br.exemplo.um");
        desativados.add("br.exemplo.dois");
        RootShell shell = new RootShell(new RootShell.Backend() {
            @Override
            public RootShell.Resultado executar(String comando, long timeoutMillis) {
                if ("id".equals(comando)) {
                    return RootShell.Resultado.sucesso(0, "uid=0(root)");
                }
                if ("pm list packages -d".equals(comando)) {
                    StringBuilder saida = new StringBuilder();
                    for (String pacote : desativados) {
                        saida.append("package:").append(pacote).append("\n");
                    }
                    return RootShell.Resultado.sucesso(0, saida.toString());
                }
                if (comando.startsWith("pm enable --user 0 ")) {
                    desativados.remove(comando.substring("pm enable --user 0 ".length()));
                    return RootShell.Resultado.sucesso(0, "Package enabled");
                }
                return RootShell.Resultado.falha(1, "", "comando inesperado");
            }
        }, new RootShell.PoliticaPacotes() {
            @Override
            public boolean podeAlterar(String pacote) {
                return "br.exemplo.um".equals(pacote)
                        || "br.exemplo.dois".equals(pacote);
            }
        });
        Historico historico = new Historico(pastaTemporaria.newFolder());
        Historico.Registro primeira = historico.registrar(ComandosRoot.DESATIVAR_PACOTE,
                "br.exemplo.um", "ATIVO", "DESATIVADO", "OK",
                "pm enable --user 0 br.exemplo.um");
        Historico.Registro segunda = historico.registrar(ComandosRoot.DESATIVAR_PACOTE,
                "br.exemplo.dois", "ATIVO", "DESATIVADO", "OK",
                "pm enable --user 0 br.exemplo.dois");

        List<Reversor.ResultadoReversao> resultados = new Reversor(shell, historico)
                .reverterTudo();

        assertEquals(2, resultados.size());
        assertTrue(resultados.get(0).ok);
        assertTrue(resultados.get(1).ok);
        assertFalse(desativados.contains("br.exemplo.um"));
        assertFalse(desativados.contains("br.exemplo.dois"));
        List<Historico.Registro> registros = historico.listar();
        assertTrue(historico.foiRevertido(primeira, registros));
        assertTrue(historico.foiRevertido(segunda, registros));
    }

    private static RootShell.PoliticaPacotes permitirPacoteExemplo() {
        return new RootShell.PoliticaPacotes() {
            @Override
            public boolean podeAlterar(String pacote) {
                return "br.exemplo.app".equals(pacote);
            }
        };
    }
}