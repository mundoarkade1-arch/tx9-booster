package br.com.tx9booster;

import java.io.File;
import java.io.IOException;

/**
 * Exclusão real, SÓ de dentro de uma área autorizada pela PoliticaCaminhos.
 * Cada arquivo passa pela trava antes de ser apagado.
 * Nunca apaga a própria área (a raiz), nunca segue link simbólico e
 * só soma como "liberado" o que foi realmente apagado.
 */
public final class ExclusorCache {

    public static final int MAX_ARQUIVOS = 20000;
    public static final int MAX_PROFUNDIDADE = 20;

    /** Resultado da exclusão. */
    public static final class Resultado {
        // false se a área não existe ou não é uma área autorizada. Nada foi apagado.
        public boolean areaValida = false;
        // Arquivos que foram realmente apagados.
        public int arquivosApagados = 0;
        // Soma dos bytes dos arquivos realmente apagados.
        public long bytesLiberados = 0;
        // Arquivos permitidos que não puderam ser apagados.
        public int falhas = 0;
        // Itens pulados: negados pela trava, links simbólicos, pastas ilegíveis.
        public int ignorados = 0;
        // true se parou por causa de um limite.
        public boolean truncado = false;
    }

    private final PoliticaCaminhos politica;

    public ExclusorCache(PoliticaCaminhos politica) {
        this.politica = politica;
    }

    public Resultado apagar(File area) {
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
                // File.delete() não apaga pasta com conteúdo: só some a pasta vazia.
                if (politica.podeApagar(f)) {
                    f.delete();
                }
            } else if (f.isFile()) {
                if (!politica.podeApagar(f)) {
                    r.ignorados++;
                    continue;
                }
                if (r.arquivosApagados + r.falhas >= MAX_ARQUIVOS) {
                    r.truncado = true;
                    return;
                }
                long bytes = f.length();
                if (f.delete()) {
                    r.arquivosApagados++;
                    r.bytesLiberados += bytes;
                } else {
                    r.falhas++;
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
