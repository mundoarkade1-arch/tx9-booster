package br.com.tx9booster;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private Button btnAtualizar;
    private TextView txtAtualizacao;
    private boolean verificando = false;
    private boolean baixando = false;
    private String textoBase = "";

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

        findViewById(R.id.btnApps).setOnClickListener(emBreve);
        findViewById(R.id.btnHistorico).setOnClickListener(emBreve);

        findViewById(R.id.btnOtimizacao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, OtimizacaoActivity.class));
            }
        });

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

    // Baixa o APK dentro do próprio app e abre o instalador do Android.
    // Quem instala é o Android, com confirmação do dono.
    private void baixarEInstalar(String url) {
        if (baixando) {
            return;
        }
        if (url == null || !url.endsWith(".apk")) {
            acrescentarTexto(getString(R.string.inst_erro, "endereço sem arquivo .apk"));
            return;
        }

        baixando = true;
        btnAtualizar.setEnabled(false);
        textoBase = txtAtualizacao.getText().toString();
        txtAtualizacao.setText(textoBase + "\n" + getString(R.string.inst_baixando));
        txtAtualizacao.setVisibility(View.VISIBLE);

        InstaladorAtualizacao.baixarEInstalar(this, url, new InstaladorAtualizacao.Ouvinte() {
            @Override
            public void aoProgredir(int percentual) {
                if (isFinishing() || percentual < 0) {
                    return;
                }
                txtAtualizacao.setText(textoBase + "\n"
                        + getString(R.string.inst_baixando) + " " + percentual + "%");
            }

            @Override
            public void aoAbrirInstalador() {
                baixando = false;
                if (isFinishing()) {
                    return;
                }
                btnAtualizar.setEnabled(true);
                txtAtualizacao.setText(textoBase + "\n" + getString(R.string.inst_pronto));
            }

            @Override
            public void aoFalhar(String mensagem) {
                baixando = false;
                if (isFinishing()) {
                    return;
                }
                btnAtualizar.setEnabled(true);
                txtAtualizacao.setText(textoBase + "\n" + mensagem);
            }
        });
    }
}
