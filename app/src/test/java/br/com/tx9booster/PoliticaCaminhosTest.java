package br.com.tx9booster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import br.com.tx9booster.PoliticaCaminhos.Decisao;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;

public class PoliticaCaminhosTest {

    @Rule
    public TemporaryFolder pasta = new TemporaryFolder();

    private File area;
    private File area2;
    private File fora;
    private PoliticaCaminhos politica;

    @Before
    public void preparar() throws IOException {
        area = pasta.newFolder("area");
        area2 = pasta.newFolder("area2");
        fora = pasta.newFolder("fora");
        politica = new PoliticaCaminhos(Collections.singletonList(area));
    }

    @Test
    public void arquivoDentroDaAreaEPermitido() throws IOException {
        File f = new File(area, "a.tmp");
        assertTrue(f.createNewFile());
        assertEquals(Decisao.PERMITIDO, politica.avaliar(f));
        assertTrue(politica.podeApagar(f));
    }

    @Test
    public void caminhoInexistenteDentroDaAreaEPermitido() {
        assertEquals(Decisao.PERMITIDO, politica.avaliar(new File(area, "x/y/z.tmp")));
    }

    @Test
    public void raizDaAreaNaoPodeSerApagada() {
        assertEquals(Decisao.NEGADO_RAIZ_DA_AREA, politica.avaliar(area));
    }

    @Test
    public void pastaIrmaComMesmoPrefixoEstaForaDaArea() {
        assertEquals(Decisao.NEGADO_FORA_DA_AREA, politica.avaliar(new File(area2, "b.tmp")));
    }

    @Test
    public void arquivoForaDaAreaEhNegado() {
        assertEquals(Decisao.NEGADO_FORA_DA_AREA, politica.avaliar(new File(fora, "c.tmp")));
    }

    @Test
    public void escapeComPontoPontoEhNegado() {
        assertEquals(Decisao.NEGADO_FORA_DA_AREA,
                politica.avaliar(new File(area, "../fora/c.tmp")));
    }

    @Test
    public void linkSimbolicoParaForaEhNegado() throws IOException {
        File atalho = new File(area, "atalho");
        Files.createSymbolicLink(atalho.toPath(), fora.toPath());
        assertEquals(Decisao.NEGADO_FORA_DA_AREA, politica.avaliar(atalho));
        assertEquals(Decisao.NEGADO_FORA_DA_AREA,
                politica.avaliar(new File(atalho, "c.tmp")));
    }

    @Test
    public void pastasDoSistemaSaoProibidas() {
        assertEquals(Decisao.NEGADO_PROIBIDO, politica.avaliar(new File("/system/bin/su")));
        assertEquals(Decisao.NEGADO_PROIBIDO, politica.avaliar(new File("/data")));
        assertEquals(Decisao.NEGADO_PROIBIDO, politica.avaliar(new File("/data/system/x")));
        assertEquals(Decisao.NEGADO_PROIBIDO, politica.avaliar(new File("/")));
        assertEquals(Decisao.NEGADO_PROIBIDO, politica.avaliar(new File("/sdcard")));
    }

    @Test
    public void caminhoNuloEhNegadoPorErro() {
        assertEquals(Decisao.NEGADO_ERRO, politica.avaliar(null));
    }

    @Test
    public void areaPerigosaEhDescartada() {
        PoliticaCaminhos perigosa = new PoliticaCaminhos(Arrays.asList(
                new File("/"), new File("/data"), new File("/system")));
        assertTrue(perigosa.getRaizesPermitidas().isEmpty());
        assertEquals(Decisao.NEGADO_FORA_DA_AREA, perigosa.avaliar(new File("/data/data/x")));
    }

    @Test
    public void cacheDoProprioAppEPermitidoEDeOutroAppNao() {
        PoliticaCaminhos app = new PoliticaCaminhos(Collections.singletonList(
                new File("/data/data/br.com.tx9booster/cache")));
        assertEquals(Decisao.PERMITIDO,
                app.avaliar(new File("/data/data/br.com.tx9booster/cache/x.tmp")));
        assertEquals(Decisao.NEGADO_FORA_DA_AREA,
                app.avaliar(new File("/data/data/outro.app/cache/x.tmp")));
    }
}
