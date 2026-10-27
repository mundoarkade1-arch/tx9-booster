package br.com.tx9booster;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Analisador SOMENTE LEITURA.
 * Percorre pastas, soma os bytes reais dos arquivos e pergunta à PoliticaCaminhos
 * o que poderia ser apagado. Esta classe NUNCA apaga, move ou altera nada.
 * Links simbólicos são ignorados. Há limite de arquivos e de profundidade.
 */
public final class AnalisadorArquivos {

    public static final int LIMITE_ARQUIVOS = 5000;
    public static final int LIMITE_PROFUNDIDADE = 12;

    /** Um arquivo que a PoliticaCaminhos permitiria apagar. */
    public static final class Item {
        public final String caminho;
        public final long bytes;

        Item(String caminho, long bytes) {
            this.caminho = caminho;
            this.bytes = bytes;
        }
    }

    /** Resultado da análise. Se cortadoPeloLimite for true, os totais são parciais. */
    public static final class Resultado {
        public final List<Item> itensPermitidos;
        public final long bytesPermitidos;
        public final int arquivosNegados;
        public final long bytesNegados;
        public final int linksIgnorados;
        public final int errosLeitura;
        public final boolean cortadoPeloLimite;

        Resultado(List<Item> itensPermitidos, long bytesPermitidos,
                  int arquivosNegados, long bytesNegados,
                  int linksIgnorados, int errosLeitura, boolean cortadoPeloLimite) {
            this.itensPermitidos = itensPermitidos;
            this.bytesPermitidos = bytesPermitidos;
            this.arquivosNegados = arquivosNegados;
            this.bytesNegados = bytesNegados;
            this.linksIgnorados = linksIgnorados;
            this.errosLeitura = errosLeitura;
            this.cortadoPeloLimite = cortadoPeloLimite;
        }
    }

    private final PoliticaCaminhos politica;

    // Estado de uma análise em andamento.
    private List<Item> permitidos;
    private long bytesPermitidos;
    private int negados;
    private long bytesNegados;
    private int links;
    private int erros;
    private int vistos;
    private boolean cortado;

    public AnalisadorArquivos(PoliticaCaminhos politica) {
        this.politica = politica;
    }

    public synchronized Resultado analisar(List<File> pastas) {
        permitidos = new ArrayList<Item>();
        bytesPermitidos = 0;
        negados = 0;
        bytesNegados = 0;
        links = 0;
        erros = 0;
        vistos = 0;
        cortado = false;

        if (pastas != null) {
            for (File pasta : pastas) {
                if (cortado) {
                    break;
                }
                if (pasta != null && pasta.isDirectory() && !ehLink(pasta)) {
                    percorrer(pasta, 0);
                }
            }
        }

        Collections.sort(permitidos, new Comparator<Item>() {
            @Override
            public int compare(Item a, Item b) {
                if (a.bytes == b.bytes) {
                    return 0;
                }
                return a.bytes < b.bytes ? 1 : -1;
            }
        });

        return new Resultado(Collections.unmodifiableList(permitidos), bytesPermitidos,
                negados, bytesNegados, links, erros, cortado);
    }

    private void percorrer(File pasta, int profundidade) {
        File[] filhos = pasta.listFiles();
        if (filhos == null) {
            erros++;
            return;
        }
        for (File filho : filhos) {
            if (vistos >= LIMITE_ARQUIVOS) {
                cortado = true;
                return;
            }
            if (ehLink(filho)) {
                links++;
                continue;
            }
            if (filho.isDirectory()) {
                if (profundidade < LIMITE_PROFUNDIDADE) {
                    percorrer(filho, profundidade + 1);
                    if (cortado) {
                        return;
                    }
                } else {
                    cortado = true;
                    return;
                }
            } else if (filho.isFile()) {
                vistos++;
                long bytes = filho.length();
                if (politica.avaliar(filho) == PoliticaCaminhos.Decisao.PERMITIDO) {
                    permitidos.add(new Item(filho.getPath(), bytes));
                    bytesPermitidos += bytes;
                } else {
                    negados++;
                    bytesNegados += bytes;
                }
            }
        }
    }

    // Link simbólico: o caminho real é diferente do caminho esperado.
    // Em caso de erro, trata como link e ignora (lado seguro).
    private static boolean ehLink(File f) {
        try {
            File pai = f.getParentFile();
            if (pai == null) {
                return true;
            }
            File esperado = new File(pai.getCanonicalFile(), f.getName());
            return !esperado.getPath().equals(f.getCanonicalPath());
        } catch (IOException e) {
            return true;
        }
    }
}
