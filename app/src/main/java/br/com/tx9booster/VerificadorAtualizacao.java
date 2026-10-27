package br.com.tx9booster;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Verifica se existe uma versão mais nova do app na Release do GitHub.
 * Só consulta. Não grava arquivos, não baixa e não instala nada.
 * Só aceita endereços https do repositório tx9-booster.
 * Esta classe não usa nada do Android, para poder ser testada no GitHub.
 */
public final class VerificadorAtualizacao {

    public static final String URL_API =
            "https://api.github.com/repos/mundoarkade1-arch/tx9-booster/releases/latest";
    public static final String PREFIXO_CONFIAVEL =
            "https://github.com/mundoarkade1-arch/tx9-booster/";

    private static final int TEMPO_LIMITE_MS = 8000;
    private static final int MAX_BYTES = 262144;

    private static final Pattern TAG = Pattern.compile("\"tag_name\"\\s*:\\s*\"([^\"\\\\]*)\"");
    private static final Pattern TAG_VALIDA = Pattern.compile("[0-9A-Za-z._-]{1,40}");
    private static final Pattern APK = Pattern.compile(
            "\"browser_download_url\"\\s*:\\s*\"("
                    + Pattern.quote(PREFIXO_CONFIAVEL + "releases/download/")
                    + "[^\"\\s]+\\.apk)\"");

    public enum Estado {
        NOVA_VERSAO,
        ATUALIZADO,
        ERRO
    }

    /** Resultado da verificação. */
    public static final class Resultado {
        public final Estado estado;
        public final String versaoRemota;
        public final String urlDownload;
        public final String mensagem;

        private Resultado(Estado estado, String versaoRemota, String urlDownload, String mensagem) {
            this.estado = estado;
            this.versaoRemota = versaoRemota;
            this.urlDownload = urlDownload;
            this.mensagem = mensagem;
        }

        static Resultado nova(String versao, String url) {
            return new Resultado(Estado.NOVA_VERSAO, versao, url, null);
        }

        static Resultado atualizado(String versao) {
            return new Resultado(Estado.ATUALIZADO, versao, null, null);
        }

        static Resultado erro(String mensagem) {
            return new Resultado(Estado.ERRO, null, null, mensagem);
        }
    }

    private VerificadorAtualizacao() {
    }

    /** Consulta o GitHub. Deve ser chamado fora da thread da tela. */
    public static Resultado verificar(String versaoLocal) {
        HttpURLConnection conexao = null;
        try {
            conexao = (HttpURLConnection) new URL(URL_API).openConnection();
            conexao.setConnectTimeout(TEMPO_LIMITE_MS);
            conexao.setReadTimeout(TEMPO_LIMITE_MS);
            conexao.setInstanceFollowRedirects(false);
            conexao.setRequestProperty("User-Agent", "tx9-booster");
            conexao.setRequestProperty("Accept", "application/vnd.github+json");

            int codigo = conexao.getResponseCode();
            if (codigo == 404) {
                return Resultado.erro("Ainda não há versão publicada.");
            }
            if (codigo != 200) {
                return Resultado.erro("O GitHub respondeu com o código " + codigo + ".");
            }
            String corpo = ler(conexao.getInputStream());
            return interpretar(corpo, versaoLocal);
        } catch (IOException e) {
            return Resultado.erro("Sem conexão com o GitHub.");
        } catch (RuntimeException e) {
            return Resultado.erro("Não foi possível verificar.");
        } finally {
            if (conexao != null) {
                conexao.disconnect();
            }
        }
    }

    /** Lê a resposta do GitHub e decide se há versão nova. Sem rede. */
    static Resultado interpretar(String json, String versaoLocal) {
        if (json == null) {
            return Resultado.erro("Resposta vazia do GitHub.");
        }
        Matcher m = TAG.matcher(json);
        if (!m.find()) {
            return Resultado.erro("Resposta inesperada do GitHub.");
        }
        String tag = m.group(1);
        if (!TAG_VALIDA.matcher(tag).matches() || partes(tag) == null) {
            return Resultado.erro("Número de versão inválido na Release.");
        }
        if (!ehMaisNova(tag, versaoLocal)) {
            return Resultado.atualizado(tag);
        }
        Matcher a = APK.matcher(json);
        String url = a.find()
                ? a.group(1)
                : PREFIXO_CONFIAVEL + "releases/tag/" + tag;
        return Resultado.nova(tag, url);
    }

    /** true só se a versão remota for estritamente maior que a local. */
    static boolean ehMaisNova(String remota, String local) {
        int[] a = partes(remota);
        int[] b = partes(local);
        if (a == null || b == null) {
            return false;
        }
        int n = Math.max(a.length, b.length);
        for (int i = 0; i < n; i++) {
            int x = i < a.length ? a[i] : 0;
            int y = i < b.length ? b[i] : 0;
            if (x > y) {
                return true;
            }
            if (x < y) {
                return false;
            }
        }
        return false;
    }

    // "v0.3.0" ou "0.3.0" vira {0, 3, 0}. Qualquer coisa estranha vira null.
    static int[] partes(String versao) {
        if (versao == null) {
            return null;
        }
        String s = versao.trim();
        if (s.startsWith("v") || s.startsWith("V")) {
            s = s.substring(1);
        }
        int hifen = s.indexOf('-');
        if (hifen >= 0) {
            s = s.substring(0, hifen);
        }
        if (s.isEmpty()) {
            return null;
        }
        String[] p = s.split("\\.");
        if (p.length > 4) {
            return null;
        }
        int[] r = new int[p.length];
        for (int i = 0; i < p.length; i++) {
            if (!p[i].matches("[0-9]{1,6}")) {
                return null;
            }
            r[i] = Integer.parseInt(p[i]);
        }
        return r;
    }

    private static String ler(InputStream entrada) throws IOException {
        try {
            ByteArrayOutputStream saida = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int total = 0;
            int n;
            while ((n = entrada.read(buffer)) != -1) {
                total += n;
                if (total > MAX_BYTES) {
                    throw new IOException("Resposta grande demais");
                }
                saida.write(buffer, 0, n);
            }
            return new String(saida.toByteArray(), StandardCharsets.UTF_8);
        } finally {
            entrada.close();
        }
    }
}
