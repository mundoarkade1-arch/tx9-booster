package br.com.tx9booster;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Analisador somente leitura.
 * Percorre uma área autorizada, soma os bytes reais dos arquivos
 * e consulta a PoliticaCaminhos para cada arquivo.
 * Esta classe NUNCA apaga, move ou altera nada.
 */
public final class Analisador {

    public static final int MAX_ARQUIVOS = 20000;
    public static final int MAX_PROFUNDIDADE = 20;
    public static final int MAX_ITENS_NA_LISTA = 100;

    /** Um arquivo encontrado e o tamanho dele em bytes. */
    public static final class Item {
        public final String caminho;
        public final long bytes;

        public Item(String caminho, long bytes) {
            this.caminho = caminho;
            this.bytes = bytes;
        }
    }

    /** Resultado da análise. */
    public static final class Resultado {
        // false se a área não existe ou não é uma área autorizada.
        public boolean areaValida = false;
        // Soma dos bytes dos arquivos que a trava permitiria apagar.
        public long totalBytes = 0;
        // Quantidade desses arquivos.
        public int totalArquivos = 0;
        // Itens pulados: negados pela trava, links simbólicos, erros.
        public int ignorados = 0;
        // true se a análise parou por causa de um limite.
        public boolean truncado = false;
        // Só os primeiros itens, para mostrar na tela.
        public final List<Item> itens = new ArrayList<Item>();
    }

    private final PoliticaCaminhos politica;

    public Analisador(PoliticaCaminhos politica) {
        this.politica = politica;
    }

    public Resultado analisar(File area) {
        Resultado r = new Resultado();
        if (politica == null || area == null || !area.isDirectory()) {
            return r;
        }
        File raiz;
        try {
            raiz = area.getCanonicalFile();
        } catch (IOException e) {
            return r;
        }
        // Só analisa se a área for uma das áreas autorizadas da política.
        if (!politica.getRaizesPermitidas().contains(raiz.getPath())) {
            return r;
        }
        r.areaValida = true;
        percorrer(raiz, 0, r);
        return r;
    }

    private void percorrer(File pasta, int profundidade, Resultado r) {
        if (r.truncado) {
            return;
        }
        if (profundidade > MAX_PROFUNDIDADE) {
            r.truncado = true;
            return;
        }
        File[] filhos = pasta.listFiles();
        if (filhos == null) {
            r.ignorados++;
            return;
        }
        for (File f : filhos) {
            if (r.truncado) {
                return;
            }
            if (ehLinkOuErro(f)) {
                r.ignorados++;
                continue;
            }
            if (f.isDirectory()) {
                percorrer(f, profundidade + 1, r);
            } else if (f.isFile()) {
                if (!politica.podeApagar(f)) {
                    r.ignorados++;
                    continue;
                }
                if (r.totalArquivos >= MAX_ARQUIVOS) {
                    r.truncado = true;
                    return;
                }
                long bytes = f.length();
                r.totalArquivos++;
                r.totalBytes += bytes;
                if (r.itens.size() < MAX_ITENS_NA_LISTA) {
                    r.itens.add(new Item(f.getPath(), bytes));
                }
            } else {
                r.ignorados++;
            }
        }
    }

    // Link simbólico: o caminho real é diferente do caminho pelo qual chegamos.
    private static boolean ehLinkOuErro(File f) {
        try {
            return !f.getCanonicalPath().equals(f.getAbsolutePath());
        } catch (IOException e) {
            return true;
        }
    }
}
