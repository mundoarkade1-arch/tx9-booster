package br.com.tx9booster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class AnalisadorTest {

    @Rule
    public TemporaryFolder pasta = new TemporaryFolder();

    private static void escrever(File arquivo, int bytes) throws IOException {
        FileOutputStream saida = new FileOutputStream(arquivo);
        try {
            saida.write(new byte[bytes]);
        } finally {
            saida.close();
        }
    }

    private Analisador criar(File area) {
        return new Analisador(new PoliticaCaminhos(Arrays.asList(area)));
    }

    @Test
    public void somaOsBytesDosArquivosDaAreaEDasSubpastas() throws IOException {
        File area = pasta.newFolder("area");
        File sub = new File(area, "sub");
        assertTrue(sub.mkdir());
        escrever(new File(area, "a.tmp"), 10);
        escrever(new File(sub, "b.tmp"), 5);

        Analisador.Resultado r = criar(area).analisar(area);

        assertTrue(r.areaValida);
        assertEquals(2, r.totalArquivos);
        assertEquals(15L, r.totalBytes);
        assertEquals(0, r.ignorados);
        assertFalse(r.truncado);
        assertEquals(2, r.itens.size());
    }

    @Test
    public void pastaVaziaDaZeroArquivosEZeroBytes() throws IOException {
        File area = pasta.newFolder("vazia");

        Analisador.Resultado r = criar(area).analisar(area);

        assertTrue(r.areaValida);
        assertEquals(0, r.totalArquivos);
        assertEquals(0L, r.totalBytes);
    }

    @Test
    public void areaForaDaPoliticaNaoEAnalisada() throws IOException {
        File permitida = pasta.newFolder("permitida");
        File outra = pasta.newFolder("outra");
        escrever(new File(outra, "x.tmp"), 50);

        Analisador.Resultado r = criar(permitida).analisar(outra);

        assertFalse(r.areaValida);
        assertEquals(0, r.totalArquivos);
        assertEquals(0L, r.totalBytes);
    }

    @Test
    public void areaNulaOuInexistenteNaoEValida() throws IOException {
        File permitida = pasta.newFolder("permitida");
        Analisador analisador = criar(permitida);

        assertFalse(analisador.analisar(null).areaValida);
        assertFalse(analisador.analisar(new File(permitida, "nao_existe")).areaValida);
    }

    @Test
    public void analisarNaoApagaNada() throws IOException {
        File area = pasta.newFolder("area");
        File arquivo = new File(area, "a.tmp");
        escrever(arquivo, 20);

        criar(area).analisar(area);

        assertTrue(arquivo.exists());
        assertEquals(20L, arquivo.length());
    }

    @Test
    public void linkSimbolicoParaForaEIgnorado() throws IOException {
        File area = pasta.newFolder("area");
        File fora = pasta.newFile("fora.txt");
        escrever(fora, 100);
        escrever(new File(area, "real.tmp"), 7);
        File atalho = new File(area, "atalho.tmp");
        try {
            Files.createSymbolicLink(atalho.toPath(), fora.toPath());
        } catch (Exception e) {
            Assume.assumeNoException(e);
        }

        Analisador.Resultado r = criar(area).analisar(area);

        assertEquals(1, r.totalArquivos);
        assertEquals(7L, r.totalBytes);
        assertEquals(1, r.ignorados);
        assertTrue(fora.exists());
    }
}
