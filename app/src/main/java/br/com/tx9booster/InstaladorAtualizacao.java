package br.com.tx9booster;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Baixa o APK da atualização e abre o instalador do Android.
 *
 * Segurança:
 *  - só fala com endereços https;
 *  - só baixa arquivo .apk do próprio repositório (PREFIXO_DOWNLOAD);
 *  - confere que o arquivo baixado é um pacote (começa com "PK");
 *  - o Android só instala se a assinatura for a mesma do app instalado;
 *  - não registra endereços nem dados em log.
 */
public final class InstaladorAtualizacao {

    /** Andamento do download. Sempre chamado na thread principal. */
    public interface Ouvinte {
        /** De 0 a 100, ou -1 se o tamanho não for conhecido. */
        void aoProgredir(int percentual);

        /** O instalador do Android foi aberto. */
        void aoAbrirInstalador();

        void aoFalhar(String mensagem);
    }

    private static final String PREFIXO_DOWNLOAD =
            "https://github.com/mundoarkade1-arch/tx9-booster/releases/download/";

    private static final int TEMPO_CONEXAO_MS = 15000;
    private static final int TEMPO_LEITURA_MS = 30000;
    private static final int MAX_REDIRECIONAMENTOS = 5;
    private static final long MAX_BYTES = 150L * 1024L * 1024L;
    private static final String USER_AGENT = "TX9Booster/1.0 (Android)";

    private static final Handler PRINCIPAL = new Handler(Looper.getMainLooper());
    private static final AtomicBoolean OCUPADO = new AtomicBoolean(false);

    private InstaladorAtualizacao() {
    }

    public static void baixarEInstalar(final Context contexto, final String endereco,
                                       final Ouvinte ouvinte) {
        if (!OCUPADO.compareAndSet(false, true)) {
            return; // já existe um download em andamento
        }
        final Context app = contexto.getApplicationContext();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (endereco == null || !endereco.startsWith(PREFIXO_DOWNLOAD)
                            || !endereco.endsWith(".apk")) {
                        throw new IOException("endereco invalido");
                    }
                    final File apk = baixar(app, endereco, ouvinte);
                    PRINCIPAL.post(new Runnable() {
                        @Override
                        public void run() {
                            abrirInstalador(app, apk, ouvinte);
                        }
                    });
                } catch (Exception e) {
                    avisarFalha(ouvinte,
                            "Não foi possível baixar a atualização. Tente de novo mais tarde.");
                } finally {
                    OCUPADO.set(false);
                }
            }
        }).start();
    }

    private static File baixar(Context app, String endereco, final Ouvinte ouvinte)
            throws IOException {
        File pasta = new File(app.getCacheDir(), "updates");
        if (!pasta.exists() && !pasta.mkdirs()) {
            throw new IOException("pasta");
        }
        File[] antigos = pasta.listFiles();
        if (antigos != null) {
            for (File f : antigos) {
                //noinspection ResultOfMethodCallIgnored
                f.delete();
            }
        }
        File saida = new File(pasta, "TX9-Booster.apk");

        HttpURLConnection conexao = abrir(endereco);
        try {
            if (conexao.getResponseCode() != 200) {
                throw new IOException("resposta");
            }
            long total = conexao.getContentLength();
            if (total > MAX_BYTES) {
                throw new IOException("grande demais");
            }
            InputStream entrada = conexao.getInputStream();
            OutputStream arquivo = new FileOutputStream(saida);
            try {
                byte[] bloco = new byte[16 * 1024];
                long feito = 0;
                int ultimo = -2;
                int n;
                while ((n = entrada.read(bloco)) >= 0) {
                    feito += n;
                    if (feito > MAX_BYTES) {
                        throw new IOException("grande demais");
                    }
                    arquivo.write(bloco, 0, n);
                    final int percentual = total > 0 ? (int) (feito * 100L / total) : -1;
                    if (percentual != ultimo) {
                        ultimo = percentual;
                        PRINCIPAL.post(new Runnable() {
                            @Override
                            public void run() {
                                ouvinte.aoProgredir(percentual);
                            }
                        });
                    }
                }
            } finally {
                arquivo.close();
                entrada.close();
            }
        } finally {
            conexao.disconnect();
        }

        if (!pareceUmPacote(saida)) {
            //noinspection ResultOfMethodCallIgnored
            saida.delete();
            throw new IOException("arquivo estranho");
        }
        return saida;
    }

    /** Um APK é um arquivo zip: os dois primeiros bytes são "PK". */
    private static boolean pareceUmPacote(File arquivo) {
        try {
            InputStream in = new FileInputStream(arquivo);
            try {
                return in.read() == 'P' && in.read() == 'K';
            } finally {
                in.close();
            }
        } catch (IOException e) {
            return false;
        }
    }

    private static void abrirInstalador(Context app, File apk, Ouvinte ouvinte) {
        try {
            if (Build.VERSION.SDK_INT >= 26
                    && !app.getPackageManager().canRequestPackageInstalls()) {
                Intent config = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                        Uri.parse("package:" + app.getPackageName()));
                config.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                app.startActivity(config);
                ouvinte.aoFalhar("Permita instalar aplicativos por este app e tente de novo.");
                return;
            }
            Uri uri = FileProvider.getUriForFile(app,
                    app.getPackageName() + ".fileprovider", apk);
            Intent instalar = new Intent(Intent.ACTION_VIEW);
            instalar.setDataAndType(uri, "application/vnd.android.package-archive");
            instalar.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            app.startActivity(instalar);
            ouvinte.aoAbrirInstalador();
        } catch (ActivityNotFoundException e) {
            ouvinte.aoFalhar("Este aparelho não tem instalador de aplicativos.");
        } catch (Exception e) {
            ouvinte.aoFalhar("Não foi possível abrir o instalador.");
        }
    }

    private static void avisarFalha(final Ouvinte ouvinte, final String mensagem) {
        PRINCIPAL.post(new Runnable() {
            @Override
            public void run() {
                ouvinte.aoFalhar(mensagem);
            }
        });
    }

    /** Abre um endereço https seguindo redirecionamentos (só para https). */
    private static HttpURLConnection abrir(String endereco) throws IOException {
        URL url = new URL(endereco);
        for (int i = 0; i <= MAX_REDIRECIONAMENTOS; i++) {
            if (!"https".equals(url.getProtocol())) {
                throw new IOException("somente https");
            }
            HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
            conexao.setConnectTimeout(TEMPO_CONEXAO_MS);
            conexao.setReadTimeout(TEMPO_LEITURA_MS);
            conexao.setInstanceFollowRedirects(false);
            conexao.setRequestProperty("User-Agent", USER_AGENT);
            int codigo = conexao.getResponseCode();
            if (codigo == 301 || codigo == 302 || codigo == 303
                    || codigo == 307 || codigo == 308) {
                String destino = conexao.getHeaderField("Location");
                conexao.disconnect();
                if (destino == null || destino.isEmpty()) {
                    throw new IOException("redirecionamento sem destino");
                }
                url = new URL(url, destino);
                continue;
            }
            return conexao;
        }
        throw new IOException("redirecionamentos demais");
    }
}
