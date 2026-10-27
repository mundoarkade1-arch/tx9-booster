package br.com.tx9booster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

/**
 * Testes do ExclusorCache. Usam só pastas falsas temporárias.
 * Não tocam o aparelho e não usam internet.
 */
public class ExclusorCacheTest {

    private File base;
    private File area;
    private File fora;

    @Before
    public void preparar() throws IOException {
        base = Files.createTempDirectory("tx9exclusor").toFile().getCanonicalFile();
        area = new File(base, "cache");
        fora = new File(base, "fora");
        assertTrue(area.mkdirs());
        assertTrue(fora.mkdirs());
    }

    @After
    public void limpar() {
        apagarTudo(base);
    }

    @Test
    public void apagaArquivosESubpastasEMantemARaiz() throws IOException {
        escrever(new File(area, "a.txt"), 10);
        escrever(new File(area, "sub/b.txt"), 20);
        escrever(new File(area, "sub/fundo/c.txt"), 5);

        ExclusorCache.Resultado r = novoExclusor().apagar(area);

        assertTrue(r.areaValida);
        assertEquals(3, r.arquivosApagados);
        assertEquals(0, r.falhas);
        assertTrue(area.exists());
        assertFalse(new File(area, "a.txt").exists());
        assertFalse(new File(area, "sub/b.txt").exists());
        assertFalse(new File(area, "sub/fundo/c.txt").exists());
    }

    @Test
    public void somaSoOsBytesApagados() throws IOException {
        escrever(new File(area, "a.txt"), 10);
        escrever(new File(area, "sub/b.txt"), 20);
        escrever(new File(area, "sub/fundo/c.txt"), 5);

        ExclusorCache.Resultado r = novoExclusor().apagar(area);

        assertEquals(35L, r.bytesLiberados);
    }

    @Test
    public void pastaVaziaNaoApagaNadaEContinuaValida() {
        ExclusorCache.Resultado r = novoExclusor().apagar(area);

        assertTrue(r.areaValida);
        assertEquals(0, r.arquivosApagados);
        assertEquals(0L, r.bytesLiberados);
        assertTrue(area.exists());
    }

    @Test
    public void naoApagaAreaForaDaPolitica() throws IOException {
        escrever(new File(fora, "keep.txt"), 7);

        ExclusorCache.Resultado r = novoExclusor().apagar(fora);

        assertFalse(r.areaValida);
        assertEquals(0, r.arquivosApagados);
        assertTrue(new File(fora, "keep.txt").exists());
    }

    @Test
    public void ignoraLinkSimbolicoParaFora() throws IOException {
        escrever(new File(fora, "keep.txt"), 7);
        escrever(new File(area, "a.txt"), 10);
        boolean criou;
        try {
            Files.createSymbolicLink(new File(area, "atalho").toPath(), fora.toPath());
            criou = true;
        } catch (Exception e) {
            criou = false;
        }
        assumeTrue(criou);

        ExclusorCache.Resultado r = novoExclusor().apagar(area);

        assertTrue(r.areaValida);
        assertTrue(r.ignorados >= 1);
        assertTrue(new File(fora, "keep.txt").exists());
        assertFalse(new File(area, "a.txt").exists());
    }

    @Test
    public void areaNulaOuInexistenteNaoApagaNada() {
        assertFalse(novoExclusor().apagar(null).areaValida);
        assertFalse(novoExclusor().apagar(new File(base, "nao-existe")).areaValida);
    }

    @Test
    public void politicaNulaNaoApagaNada() throws IOException {
        escrever(new File(area, "a.txt"), 10);

        ExclusorCache.Resultado r = new ExclusorCache(null).apagar(area);

        assertFalse(r.areaValida);
        assertTrue(new File(area, "a.txt").exists());
    }

    private ExclusorCache novoExclusor() {
        return new ExclusorCache(new PoliticaCaminhos(Arrays.asList(area)));
    }

    private static void escrever(File f, int bytes) throws IOException {
        File pai = f.getParentFile();
        if (pai != null) {
            pai.mkdirs();
        }
        FileOutputStream out = new FileOutputStream(f);
        try {
            out.write(new byte[bytes]);
        } finally {
            out.close();
        }
    }

    // Limpeza do teste: não segue link simbólico, só remove o próprio link.
    private static void apagarTudo(File f) {
        if (f == null) {
            return;
        }
        if (Files.isSymbolicLink(f.toPath())) {
            f.delete();
            return;
        }
        File[] filhos = f.listFiles();
        if (filhos != null) {
            for (File filho : filhos) {
                apagarTudo(filho);
            }
        }
        f.delete();
    }
}
