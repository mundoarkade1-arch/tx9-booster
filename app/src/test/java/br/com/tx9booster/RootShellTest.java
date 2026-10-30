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
import java.util.concurrent.atomic.AtomicInteger;

public class RootShellTest {

    @Rule
    public final TemporaryFolder pastaTemporaria = new TemporaryFolder();

    @Test
    public void resultadoDeIdConfirmaRootSomenteComUidZero() {
        RootShell.Resultado rootComPermissao = new RootShell(new RootShell.Backend() {
            @Override
            public RootShell.Resultado executar(String comando, long timeoutMillis) {
                return RootShell.Resultado.sucesso(0, "uid=0(root) gid=0(root)");
            }
        }).executar(ComandosRoot.testarRoot());
        assertTrue(rootComPermissao.ok);

        RootShell.Resultado semPermissao = new RootShell(new RootShell.Backend() {
            @Override
            public RootShell.Resultado executar(String comando, long timeoutMillis) {
                return RootShell.Resultado.sucesso(0, "uid=2000(shell) gid=2000(shell)");
            }
        }).executar(ComandosRoot.testarRoot());
        assertFalse(semPermissao.ok);
        assertTrue(semPermissao.erro.contains("negado"));
    }

    @Test
    public void codigoNaoZeroPermaneceFalhaTipada() {
        RootShell.Resultado resultado = new RootShell(new RootShell.Backend() {
            @Override
            public RootShell.Resultado executar(String comando, long timeoutMillis) {
                return RootShell.Resultado.falha(7, "", "negado");
            }
        }).executar(ComandosRoot.listarPacotesDesativados());

        assertFalse(resultado.ok);
        org.junit.Assert.assertEquals(7, resultado.codigo);
        org.junit.Assert.assertEquals("negado", resultado.erro);
    }

    @Test
    public void mutacaoDiretaSemHistoricoNaoExecuta() {
        final AtomicInteger chamadas = new AtomicInteger();
        RootShell shell = new RootShell(new RootShell.Backend() {
            @Override
            public RootShell.Resultado executar(String comando, long timeoutMillis) {
                chamadas.incrementAndGet();
                return RootShell.Resultado.sucesso(0, "");
            }
        });

        RootShell.Resultado resultado = shell.executar(
                ComandosRoot.desativarPacote("br.exemplo.app"));

        assertFalse(resultado.ok);
        assertEquals(0, chamadas.get());
        assertTrue(resultado.erro.contains("Histórico"));
    }

    @Test
    public void acaoRegistraAntesDeExecutarEConfirmaEstadoDepois() throws Exception {
        final Historico historico = new Historico(pastaTemporaria.newFolder());
        final Set<String> desativados = new HashSet<String>();
        RootShell shell = new RootShell(new RootShell.Backend() {
            @Override
            public RootShell.Resultado executar(String comando, long timeoutMillis) {
                if ("id".equals(comando)) {
                    return RootShell.Resultado.sucesso(0, "uid=0(root)");
                }
                if ("pm disable-user --user 0 br.exemplo.app".equals(comando)) {
                    try {
                        List<Historico.Registro> antes = historico.listar();
                        assertEquals(1, antes.size());
                        assertEquals("PENDENTE", antes.get(0).resultado);
                    } catch (Exception e) {
                        throw new AssertionError(e);
                    }
                    desativados.add("br.exemplo.app");
                    return RootShell.Resultado.sucesso(0, "Package disabled");
                }
                if ("pm list packages -d".equals(comando)) {
                    String saida = desativados.contains("br.exemplo.app")
                            ? "package:br.exemplo.app\n" : "";
                    return RootShell.Resultado.sucesso(0, saida);
                }
                return RootShell.Resultado.falha(1, "", "comando inesperado");
            }
        }, permitirPacoteExemplo());

        RootShell.Resultado resultado = shell.executarAcaoPacote(
                ComandosRoot.desativarPacote("br.exemplo.app"), historico,
                "ATIVO", "DESATIVADO");

        assertTrue(resultado.ok);
        List<Historico.Registro> registros = historico.listar();
        assertEquals("OK", historico.resultadoAtual(registros.get(0), registros));
        assertEquals("pm enable --user 0 br.exemplo.app",
                registros.get(0).comandoInverso);
    }

    @Test
    public void acaoNaoCriaRegistroNemExecutaSemRoot() throws Exception {
        final AtomicInteger comandos = new AtomicInteger();
        Historico historico = new Historico(pastaTemporaria.newFolder());
        RootShell shell = new RootShell(new RootShell.Backend() {
            @Override
            public RootShell.Resultado executar(String comando, long timeoutMillis) {
                comandos.incrementAndGet();
                return RootShell.Resultado.sucesso(0, "uid=2000(shell)");
            }
        }, permitirPacoteExemplo());

        RootShell.Resultado resultado = shell.executarAcaoPacote(
                ComandosRoot.desativarPacote("br.exemplo.app"), historico,
                "ATIVO", "DESATIVADO");

        assertFalse(resultado.ok);
        assertEquals(1, comandos.get());
        assertTrue(historico.listar().isEmpty());
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