package br.com.tx9booster;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends Activity {

    private static final String NOME_ARQUIVO = "tx9-booster-atualizacao.apk";

    private Button btnAtualizar;
    private TextView txtAtualizacao;
    private boolean verificando = false;
    private boolean baixando = false;
    private boolean receptorAtivo = false;
    private long idDownload = -1;

    // Avisa quando o download termina.
    private final BroadcastReceiver receptor = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (id != idDownload) {
                return;
            }
            terminarDownload(id);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View.OnClickListener emBreve = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, R.string.em_breve, Toast.LENGTH_SHORT).show();
            }
        };

        findViewById(R.id.btnOtimizacao).setOnClickListener(emBreve);
        findViewById(R.id.btnApps).setOnClickListener(emBreve);
        findViewById(R.id.btnHistorico).setOnClickListener(emBreve);

        findViewById(R.id.btnDiagnostico).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DiagnosticoActivity.class));
            }
        });

        findViewById(R.id.btnLimpeza).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LimpezaActivity.class));
            }
        });

        btnAtualizar = (Button) findViewById(R.id.btnAtualizar);
        txtAtualizacao = (TextView) findViewById(R.id.txtAtualizacao);

        btnAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarAtualizacao(false);
            }
        });

        // Verificação silenciosa ao abrir o app (só uma vez, não a cada giro de tela).
        if (savedInstanceState == null) {
            verificarAtualizacao(true);
        }
    }

    @Override
    protected void onDestroy() {
        pararReceptor();
        super.onDestroy();
    }

    private String versaoInstalada() {
        try {
            String v = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            return v == null ? "" : v;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    // silencioso = true: só mostra algo se houver versão nova.
    // silencioso = false: botão apertado, sempre mostra uma resposta.
    private void verificarAtualizacao(final boolean silencioso) {
        if (verificando || baixando) {
            return;
        }
        verificando = true;
        final String local = versaoInstalada();

        if (!silencioso) {
            btnAtualizar.setEnabled(false);
            txtAtualizacao.setText(R.string.atu_verificando);
            txtAtualizacao.setVisibility(View.VISIBLE);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                VerificadorAtualizacao.Resultado r;
                try {
                    r = VerificadorAtualizacao.verificar(local);
                } catch (Exception e) {
                    r = null;
                }
                final VerificadorAtualizacao.Resultado resultado = r;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isFinishing()) {
                            return;
                        }
                        mostrarResultado(resultado, local, silencioso);
                        btnAtualizar.setEnabled(true);
                        verificando = false;
                    }
                });
            }
        }).start();
    }

    private void mostrarResultado(VerificadorAtualizacao.Resultado r, String local,
                                  boolean silencioso) {
        if (r == null) {
            if (!silencioso) {
                txtAtualizacao.setText(getString(R.string.atu_erro, ""));
                txtAtualizacao.setVisibility(View.VISIBLE);
            } else {
                txtAtualizacao.setVisibility(View.GONE);
            }
            return;
        }

        if (r.estado == VerificadorAtualizacao.Estado.NOVA_VERSAO) {
            txtAtualizacao.setText(getString(R.string.atu_nova, r.versaoRemota, local)
                    + "\n" + getString(R.string.atu_endereco, r.urlDownload));
            txtAtualizacao.setVisibility(View.VISIBLE);
            if (!silencioso) {
                baixarEInstalar(r.urlDownload);
            }
        } else if (silencioso) {
            txtAtualizacao.setVisibility(View.GONE);
        } else if (r.estado == VerificadorAtualizacao.Estado.ATUALIZADO) {
            txtAtualizacao.setText(getString(R.string.atu_atualizado, local));
            txtAtualizacao.setVisibility(View.VISIBLE);
        } else {
            txtAtualizacao.setText(getString(R.string.atu_erro, r.mensagem));
            txtAtualizacao.setVisibility(View.VISIBLE);
        }
    }

    private void acrescentarTexto(String texto) {
        txtAtualizacao.setText(txtAtualizacao.getText() + "\n" + texto);
        txtAtualizacao.setVisibility(View.VISIBLE);
    }

    // Baixa o APK pelo gerenciador de downloads do Android e depois chama o instalador.
    // Quem instala é o Android, com confirmação do dono.
    private void baixarEInstalar(String url) {
        if (baixando) {
            return;
        }
        if (url == null || !url.endsWith(".apk")) {
            acrescentarTexto(getString(R.string.inst_erro, "endereço sem arquivo .apk"));
            return;
        }

        if (Build.VERSION.SDK_INT >= 26 && !getPackageManager().canRequestPackageInstalls()) {
            acrescentarTexto(getString(R.string.inst_sem_permissao));
            try {
                startActivity(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                        Uri.parse("package:" + getPackageName())));
            } catch (ActivityNotFoundException e) {
                // Sem tela de configuração: o texto acima já explica o que fazer.
            }
            return;
        }

        DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        File pasta = getExternalFilesDir(null);
        if (dm == null || pasta == null) {
            acrescentarTexto(getString(R.string.inst_erro, "sem gerenciador de downloads"));
            return;
        }

        File antigo = new File(pasta, NOME_ARQUIVO);
        if (antigo.exists()) {
            antigo.delete();
        }

        try {
            DownloadManager.Request pedido = new DownloadManager.Request(Uri.parse(url));
            pedido.setTitle("TX9 Booster");
            pedido.setMimeType("application/vnd.android.package-archive");
            pedido.setDestinationInExternalFilesDir(this, null, NOME_ARQUIVO);
            idDownload = dm.enqueue(pedido);
            registerReceiver(receptor, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            receptorAtivo = true;
            baixando = true;
            acrescentarTexto(getString(R.string.inst_baixando));
        } catch (Exception e) {
            acrescentarTexto(getString(R.string.inst_erro, e.getClass().getSimpleName()));
        }
    }

    private void pararReceptor() {
        if (receptorAtivo) {
            try {
                unregisterReceiver(receptor);
            } catch (IllegalArgumentException e) {
                // Já estava desligado.
            }
            receptorAtivo = false;
        }
    }

    private void terminarDownload(long id) {
        pararReceptor();
        baixando = false;

        DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (dm == null) {
            acrescentarTexto(getString(R.string.inst_erro, "sem gerenciador de downloads"));
            return;
        }

        int status = -1;
        int motivo = -1;
        Cursor c = dm.query(new DownloadManager.Query().setFilterById(id));
        try {
            if (c != null && c.moveToFirst()) {
                status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                motivo = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        if (status != DownloadManager.STATUS_SUCCESSFUL) {
            acrescentarTexto(getString(R.string.inst_erro, "código " + motivo));
            return;
        }

        Uri arquivo = dm.getUriForDownloadedFile(id);
        if (arquivo == null) {
            acrescentarTexto(getString(R.string.inst_erro, "arquivo não encontrado"));
            return;
        }

        acrescentarTexto(getString(R.string.inst_pronto));
        try {
            Intent instalar = new Intent(Intent.ACTION_VIEW);
            instalar.setDataAndType(arquivo, "application/vnd.android.package-archive");
            instalar.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(instalar);
        } catch (ActivityNotFoundException e) {
            acrescentarTexto(getString(R.string.inst_recusada, "sem instalador"));
        } catch (SecurityException e) {
            acrescentarTexto(getString(R.string.inst_recusada, "sem permissão"));
        }
    }
}
