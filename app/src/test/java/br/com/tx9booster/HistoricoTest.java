package br.com.tx9booster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.List;

public class HistoricoTest {

    @Rule
    public final TemporaryFolder pastaTemporaria = new TemporaryFolder();

    @Test
    public void gravaELaRegistroComCamposEQuebrasDeLinha() throws Exception {
        Historico historico = new Historico(pastaTemporaria.newFolder());
        Historico.Registro salvo = historico.registrar("AJUSTE", "alvo\t1", "antigo\nvalor",
                "novo % valor", "OK", "comando inverso");

        List<Historico.Registro> lidos = historico.listar();
        assertEquals(1, lidos.size());
        Historico.Registro lido = lidos.get(0);
        assertEquals(salvo.id, lido.id);
        assertEquals("alvo\t1", lido.alvo);
        assertEquals("antigo\nvalor", lido.valorOriginal);
        assertEquals("novo % valor", lido.valorNovo);
        assertEquals("comando inverso", lido.comandoInverso);
        assertTrue(historico.arquivo().isFile());
    }

    @Test
    public void registraResultadoSemAlterarRegistroOriginal() throws Exception {
        Historico historico = new Historico(pastaTemporaria.newFolder());
        Historico.Registro acao = historico.registrar("ACAO", "alvo", "antes", "depois",
                "PENDENTE", "inverso");
        historico.registrarResultado(acao.id, "OK");

        List<Historico.Registro> registros = historico.listar();
        assertEquals("PENDENTE", acao.resultado);
        assertEquals("OK", historico.resultadoAtual(acao, registros));
    }

    @Test
    public void arquivoAusenteRetornaListaVazia() throws Exception {
        Historico historico = new Historico(pastaTemporaria.newFolder());
        assertTrue(historico.listar().isEmpty());
        assertFalse(historico.arquivo().exists());
    }
}