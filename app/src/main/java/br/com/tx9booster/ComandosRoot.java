package br.com.tx9booster;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public final class ComandosRoot {

    static final String TESTAR_ROOT = "TESTAR_ROOT";
    static final String LISTAR_DESATIVADOS = "LISTAR_DESATIVADOS";
    static final String DESATIVAR_PACOTE = "DESATIVAR_PACOTE";
    static final String REATIVAR_PACOTE = "REATIVAR_PACOTE";
    static final String LER_SYSFS = "LER_SYSFS";

    private static final Pattern PACOTE = Pattern.compile(
            "[A-Za-z0-9_]+(?:\\.[A-Za-z0-9_]+)+");
    private static final Map<String, String> CAMINHOS_LEITURA;

    static {
        Map<String, String> caminhos = new HashMap<String, String>();
        caminhos.put("swappiness", "/proc/sys/vm/swappiness");
        caminhos.put("min_free_kbytes", "/proc/sys/vm/min_free_kbytes");
        caminhos.put("zram_disksize", "/sys/block/zram0/disksize");
        caminhos.put("zram_initstate", "/sys/block/zram0/initstate");
        CAMINHOS_LEITURA = Collections.unmodifiableMap(caminhos);
    }

    private ComandosRoot() {
    }

    public static Comando testarRoot() {
        return new Comando(TESTAR_ROOT, "", "id", "", 15000L);
    }

    public static Comando listarPacotesDesativados() {
        return new Comando(LISTAR_DESATIVADOS, "", "pm list packages -d", "", 15000L);
    }

    public static Comando desativarPacote(String pacote) {
        validarPacote(pacote);
        return new Comando(DESATIVAR_PACOTE, pacote,
                "pm disable-user --user 0 " + pacote,
                "pm enable --user 0 " + pacote, 15000L);
    }

    public static Comando reativarPacote(String pacote) {
        validarPacote(pacote);
        return new Comando(REATIVAR_PACOTE, pacote,
                "pm enable --user 0 " + pacote,
                "pm disable-user --user 0 " + pacote, 15000L);
    }

    public static Comando lerSysfs(String chave) {
        String caminho = CAMINHOS_LEITURA.get(chave);
        if (caminho == null) {
            throw new IllegalArgumentException("Chave de leitura não permitida.");
        }
        return new Comando(LER_SYSFS, chave, "cat " + caminho, "", 5000L);
    }

    static Comando reverterPacote(String acaoOriginal, String pacote,
                                  String comandoInverso) {
        validarPacote(pacote);
        Comando esperado;
        if (DESATIVAR_PACOTE.equals(acaoOriginal)) {
            esperado = reativarPacote(pacote);
        } else if (REATIVAR_PACOTE.equals(acaoOriginal)) {
            esperado = desativarPacote(pacote);
        } else {
            throw new IllegalArgumentException("Ação sem reversão de pacote disponível.");
        }
        if (!esperado.linha.equals(comandoInverso)) {
            throw new IllegalArgumentException("Comando inverso não corresponde à ação.");
        }
        return esperado;
    }

    static void validarPacote(String pacote) {
        if (pacote == null || pacote.length() > 223 || !PACOTE.matcher(pacote).matches()) {
            throw new IllegalArgumentException("Nome de pacote inválido.");
        }
    }

    static final class Comando {
        final String tipo;
        final String alvo;
        final String linha;
        final String inverso;
        final long timeoutMillis;

        private Comando(String tipo, String alvo, String linha, String inverso,
                        long timeoutMillis) {
            this.tipo = tipo;
            this.alvo = alvo;
            this.linha = linha;
            this.inverso = inverso;
            this.timeoutMillis = timeoutMillis;
        }

        String linha() {
            return linha;
        }

        String tipo() {
            return tipo;
        }

        String alvo() {
            return alvo;
        }

        String inverso() {
            return inverso;
        }

        long timeoutMillis() {
            return timeoutMillis;
        }
    }
}