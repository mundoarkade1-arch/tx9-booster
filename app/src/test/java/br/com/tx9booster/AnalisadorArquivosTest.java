package br.com.tx9booster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Testes do AnalisadorArquivos. Usam pastas temporárias falsas.
 * Nada do aparelho real é tocado.
 */
public class AnalisadorArquivosTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private static File criarArquivo(File pasta, String nome, int bytes) throws IOException {
        pasta.mkdirs();
        File f = new File(pasta, nome);
        FileOutputStream out = new FileOutputStream(f);
        try {
            out.write(new byte[bytes]);
        } finally {
            out.close();
        }
        return f;
    }

    private static AnalisadorArquivos novoAnalisador(File areaPermitida) {
        List<File> areas = Collections.singletonList(areaPermitida);
        return new AnalisadorArquivos(new PoliticaCaminhos(areas));
    }

    @Test
    public void somaBytesDentroDaAreaPermitida() throws IOException {
        File raiz = tmp.newFolder("permitida");
        criarArquivo(raiz, "a.bin", 10);
        criarArquivo(new File(raiz, "sub"), "b.bin", 25);

        AnalisadorArquivos.Resultado r =
                novoAnalisador(raiz).analisar(Collections.singletonList(raiz));

        assertEquals(35L, r.bytesPermitidos);
        assertEquals(2, r.itensPermitidos.size());
        assertEquals(0, r.arquivosNegados);
        assertFalse(r.cortadoPeloLimite);
    }

    @Test
    public void listaOrdenadaDoMaiorParaOMenor() throws IOException {
        File raiz = tmp.newFolder("permitida");
        criarArquivo(raiz, "pequeno.bin", 5);
        criarArquivo(raiz, "grande.bin", 50);
        criarArquivo(raiz, "medio.bin", 20);

        AnalisadorArquivos.Resultado r =
                novoAnalisador(raiz).analisar(Collections.singletonList(raiz));

        assertEquals(50L, r.itensPermitidos.get(0).bytes);
        assertEquals(20L, r.itensPermitidos.get(1).bytes);
        assertEquals(5L, r.itensPermitidos.get(2).bytes);
    }

    @Test
    public void arquivoForaDaAreaFicaComoNegado() throws IOException {
        File pai = tmp.newFolder("pai");
        File permitida = new File(pai, "permitida");
        File outra = new File(pai, "outra");
        criarArquivo(permitida, "a.bin", 10);
        criarArquivo(outra, "b.bin", 20);

        AnalisadorArquivos.Resultado r =
                novoAnalisador(permitida).analisar(Collections.singletonList(pai));

        assertEquals(10L, r.bytesPermitidos);
        assertEquals(1, r.arquivosNegados);
        assertEquals(20L, r.bytesNegados);
    }

    @Test
    public void naoApagaNemAlteraNada() throws IOException {
        File raiz = tmp.newFolder("permitida");
        File a = criarArquivo(raiz, "a.bin", 10);
        File b = criarArquivo(new File(raiz, "sub"), "b.bin", 25);

        novoAnalisador(raiz).analisar(Collections.singletonList(raiz));

        assertTrue(a.exists());
        assertTrue(b.exists());
        assertEquals(10L, a.length());
        assertEquals(25L, b.length());
    }

    @Test
    public void linksSimbolicosSaoIgnorados() throws IOException {
        File raiz = tmp.newFolder("permitida");
        File fora = tmp.newFolder("fora");
        criarArquivo(raiz, "real.bin", 10);
        File alvo = criarArquivo(fora, "segredo.bin", 100);

        try {
            java.nio.file.Files.createSymbolicLink(
                    new File(raiz, "link-arquivo").toPath(), alvo.toPath());
            java.nio.file.Files.createSymbolicLink(
                    new File(raiz, "link-pasta").toPath(), fora.toPath());
        } catch (UnsupportedOperationException e) {
            Assume.assumeNoException(e);
        } catch (IOException e) {
            Assume.assumeNoException(e);
        }

        AnalisadorArquivos.Resultado r =
                novoAnalisador(raiz).analisar(Collections.singletonList(raiz));

        assertEquals(10L, r.bytesPermitidos);
        assertEquals(1, r.itensPermitidos.size());
        assertEquals(2, r.linksIgnorados);
        assertTrue(alvo.exists());
    }

    @Test
    public void paraNoLimiteDeArquivos() throws IOException {
        File raiz = tmp.newFolder("permitida");
        for (int i = 0; i < AnalisadorArquivos.LIMITE_ARQUIVOS + 1; i++) {
            criarArquivo(raiz, "f" + i + ".bin", 1);
        }

        AnalisadorArquivos.Resultado r =
                novoAnalisador(raiz).analisar(Collections.singletonList(raiz));

        assertTrue(r.cortadoPeloLimite);
        assertEquals(AnalisadorArquivos.LIMITE_ARQUIVOS, r.itensPermitidos.size());
    }

    @Test
    public void listaVaziaOuNulaNaoDaErro() throws IOException {
        File raiz = tmp.newFolder("permitida");
        AnalisadorArquivos analisador = novoAnalisador(raiz);

        AnalisadorArquivos.Resultado vazia =
                analisador.analisar(Collections.<File>emptyList());
        AnalisadorArquivos.Resultado nula = analisador.analisar(null);
        AnalisadorArquivos.Resultado inexistente = analisador.analisar(
                Arrays.asList(new File(raiz, "nao-existe")));

        assertEquals(0L, vazia.bytesPermitidos);
        assertEquals(0L, nula.bytesPermitidos);
        assertEquals(0L, inexistente.bytesPermitidos);
    }
}
