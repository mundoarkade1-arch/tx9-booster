package br.com.tx9booster;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Testes do VerificadorAtualizacao. Não usam internet:
 * alimentam a classe com respostas falsas do GitHub.
 */
public class VerificadorAtualizacaoTest {

    private static final String APK_OK =
            "https://github.com/mundoarkade1-arch/tx9-booster/releases/download/v0.3.0/tx9-booster-0.3.0.apk";

    private static String json(String tag, String urlApk) {
        String assets = urlApk == null
                ? "[]"
                : "[{\"name\":\"x.apk\",\"browser_download_url\":\"" + urlApk + "\"}]";
        return "{\"tag_name\":\"" + tag + "\",\"assets\":" + assets + "}";
    }

    @Test
    public void versaoMaiorEMaisNova() {
        assertTrue(VerificadorAtualizacao.ehMaisNova("0.3.0", "0.2.0"));
        assertTrue(VerificadorAtualizacao.ehMaisNova("v0.3.0", "0.2.0"));
        assertTrue(VerificadorAtualizacao.ehMaisNova("1.0.0", "0.9.9"));
    }

    @Test
    public void comparaNumerosENaoTexto() {
        assertTrue(VerificadorAtualizacao.ehMaisNova("0.10.0", "0.9.0"));
        assertFalse(VerificadorAtualizacao.ehMaisNova("0.9.0", "0.10.0"));
    }

    @Test
    public void versaoIgualOuMenorNaoEMaisNova() {
        assertFalse(VerificadorAtualizacao.ehMaisNova("0.2.0", "0.2.0"));
        assertFalse(VerificadorAtualizacao.ehMaisNova("0.1.0", "0.2.0"));
        assertFalse(VerificadorAtualizacao.ehMaisNova("1.0", "1.0.0"));
    }

    @Test
    public void versaoInvalidaNuncaEMaisNova() {
        assertFalse(VerificadorAtualizacao.ehMaisNova("abc", "0.2.0"));
        assertFalse(VerificadorAtualizacao.ehMaisNova(null, "0.2.0"));
        assertFalse(VerificadorAtualizacao.ehMaisNova("0.3.0", null));
        assertFalse(VerificadorAtualizacao.ehMaisNova("", "0.2.0"));
    }

    @Test
    public void partesLeNumerosEIgnoraSufixo() {
        assertArrayEquals(new int[] {0, 3, 0}, VerificadorAtualizacao.partes("v0.3.0"));
        assertArrayEquals(new int[] {0, 3, 0}, VerificadorAtualizacao.partes("0.3.0-beta"));
        assertNull(VerificadorAtualizacao.partes("1.a"));
        assertNull(VerificadorAtualizacao.partes("1.2.3.4.5"));
        assertNull(VerificadorAtualizacao.partes(""));
        assertNull(VerificadorAtualizacao.partes(null));
    }

    @Test
    public void novaVersaoComApkDevolveOEnderecoDoApk() {
        VerificadorAtualizacao.Resultado r =
                VerificadorAtualizacao.interpretar(json("v0.3.0", APK_OK), "0.2.0");

        assertEquals(VerificadorAtualizacao.Estado.NOVA_VERSAO, r.estado);
        assertEquals("v0.3.0", r.versaoRemota);
        assertEquals(APK_OK, r.urlDownload);
    }

    @Test
    public void novaVersaoSemApkDevolveAPaginaDaRelease() {
        VerificadorAtualizacao.Resultado r =
                VerificadorAtualizacao.interpretar(json("v0.3.0", null), "0.2.0");

        assertEquals(VerificadorAtualizacao.Estado.NOVA_VERSAO, r.estado);
        assertEquals(VerificadorAtualizacao.PREFIXO_CONFIAVEL + "releases/tag/v0.3.0",
                r.urlDownload);
    }

    @Test
    public void enderecoDeOutroSiteEIgnorado() {
        VerificadorAtualizacao.Resultado r = VerificadorAtualizacao.interpretar(
                json("v0.3.0", "https://exemplo-ruim.com/x.apk"), "0.2.0");

        assertEquals(VerificadorAtualizacao.Estado.NOVA_VERSAO, r.estado);
        assertTrue(r.urlDownload.startsWith(VerificadorAtualizacao.PREFIXO_CONFIAVEL));
        assertFalse(r.urlDownload.contains("exemplo-ruim"));
    }

    @Test
    public void mesmaVersaoDizAtualizado() {
        VerificadorAtualizacao.Resultado r =
                VerificadorAtualizacao.interpretar(json("v0.2.0", APK_OK), "0.2.0");

        assertEquals(VerificadorAtualizacao.Estado.ATUALIZADO, r.estado);
        assertNull(r.urlDownload);
    }

    @Test
    public void respostaSemVersaoOuVaziaDaErro() {
        assertEquals(VerificadorAtualizacao.Estado.ERRO,
                VerificadorAtualizacao.interpretar("{}", "0.2.0").estado);
        assertEquals(VerificadorAtualizacao.Estado.ERRO,
                VerificadorAtualizacao.interpretar("", "0.2.0").estado);
        assertEquals(VerificadorAtualizacao.Estado.ERRO,
                VerificadorAtualizacao.interpretar(null, "0.2.0").estado);
    }

    @Test
    public void numeroDeVersaoPerigosoDaErro() {
        assertEquals(VerificadorAtualizacao.Estado.ERRO,
                VerificadorAtualizacao.interpretar(json("../x", null), "0.2.0").estado);
        assertEquals(VerificadorAtualizacao.Estado.ERRO,
                VerificadorAtualizacao.interpretar(json("v 1", null), "0.2.0").estado);
        assertEquals(VerificadorAtualizacao.Estado.ERRO,
                VerificadorAtualizacao.interpretar(json("lixo", null), "0.2.0").estado);
    }
}
