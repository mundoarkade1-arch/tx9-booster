package br.com.tx9booster;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Regra de segurança da limpeza: decide se um caminho pode ser apagado.
 * Só é permitido apagar o que está DENTRO de uma área autorizada.
 * A lista proibida sempre vence a lista permitida.
 * Esta classe só avalia caminhos. Ela nunca apaga nada.
 */
public final class PoliticaCaminhos {

    public enum Decisao {
        PERMITIDO,
        NEGADO_PROIBIDO,
        NEGADO_FORA_DA_AREA,
        NEGADO_RAIZ_DA_AREA,
        NEGADO_ERRO
    }

    // Esses caminhos e tudo que está dentro deles nunca podem ser apagados.
    private static final String[] PROIBIDAS_RECURSIVAS = {
            "/system", "/vendor", "/product", "/metadata", "/boot", "/dev", "/proc", "/sys",
            "/cache", "/efs", "/persist", "/oem", "/sbin", "/etc",
            "/data/system", "/data/misc", "/data/app", "/data/dalvik-cache",
            "/data/vendor", "/data/property", "/data/adb", "/data/media"
    };

    // Esses caminhos, exatamente eles, nunca podem ser apagados nem usados como área.
    private static final String[] PROIBIDAS_EXATAS = {
            "/", "/data", "/storage", "/storage/emulated", "/storage/emulated/0",
            "/sdcard", "/mnt", "/mnt/sdcard"
    };

    private final List<String> raizesPermitidas;

    public PoliticaCaminhos(List<File> permitidas) {
        List<String> raizes = new ArrayList<String>();
        for (File f : permitidas) {
            String c = canonico(f);
            if (c != null && raizSegura(c)) {
                raizes.add(c);
            }
        }
        raizesPermitidas = Collections.unmodifiableList(raizes);
    }

    public Decisao avaliar(File alvo) {
        String c = canonico(alvo);
        if (c == null) {
            return Decisao.NEGADO_ERRO;
        }
        for (String p : PROIBIDAS_RECURSIVAS) {
            if (dentro(c, p)) {
                return Decisao.NEGADO_PROIBIDO;
            }
        }
        for (String p : PROIBIDAS_EXATAS) {
            if (c.equals(p)) {
                return Decisao.NEGADO_PROIBIDO;
            }
        }
        for (String raiz : raizesPermitidas) {
            if (c.equals(raiz)) {
                return Decisao.NEGADO_RAIZ_DA_AREA;
            }
            if (c.startsWith(raiz + "/")) {
                return Decisao.PERMITIDO;
            }
        }
        return Decisao.NEGADO_FORA_DA_AREA;
    }

    public boolean podeApagar(File alvo) {
        return avaliar(alvo) == Decisao.PERMITIDO;
    }

    public List<String> getRaizesPermitidas() {
        return raizesPermitidas;
    }

    // Uma área autorizada nunca pode ser proibida, nem conter um caminho proibido.
    private static boolean raizSegura(String c) {
        for (String p : PROIBIDAS_EXATAS) {
            if (dentro(p, c)) {
                return false;
            }
        }
        for (String p : PROIBIDAS_RECURSIVAS) {
            if (dentro(c, p) || dentro(p, c)) {
                return false;
            }
        }
        return true;
    }

    private static boolean dentro(String caminho, String base) {
        if (base.equals("/")) {
            return true;
        }
        return caminho.equals(base) || caminho.startsWith(base + "/");
    }

    // Resolve ".." e links simbólicos. Se der erro, devolve null e o caminho é negado.
    private static String canonico(File f) {
        if (f == null) {
            return null;
        }
        try {
            return f.getCanonicalPath();
        } catch (IOException e) {
            return null;
        }
    }
}
