package br.com.tx9booster;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LaboratorioActivity extends Activity {

    private static final String PREFERENCIAS = "laboratorio";
    private static final int PEDIDO_ARMAZENAMENTO = 4101;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private TextView txtRoot;
    private TextView txtInventario;
    private Button btnTestarRoot;
    private Button btnInventario;
    private Switch[] interruptores;
    private boolean rootConfirmado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laboratorio);

        txtRoot = (TextView) findViewById(R.id.txtLaboratorioRoot);
        btnTestarRoot = (Button) findViewById(R.id.btnTestarRootLaboratorio);
        btnInventario = (Button) findViewById(R.id.btnInventarioPacotes);
        txtInventario = (TextView) findViewById(R.id.txtInventarioPacotes);
        interruptores = new Switch[] {
                (Switch) findViewById(R.id.switchDebloat),
                (Switch) findViewById(R.id.switchLimpezaRoot),
                (Switch) findViewById(R.id.switchMemoria),
                (Switch) findViewById(R.id.switchDesempenho),
                (Switch) findViewById(R.id.switchBoot)
        };
        carregarPreferencias();
        btnTestarRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewClicado) {
                testarRoot();
            }
        });
        btnInventario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewClicado) {
                solicitarArmazenamentoOuGerar();
            }
        });
        testarRoot();
    }

    private void carregarPreferencias() {
        final SharedPreferences preferencias = getSharedPreferences(PREFERENCIAS, MODE_PRIVATE);
        final String[] chaves = {
                "debloat", "limpeza_root", "memoria", "desempenho", "boot"
        };
        for (int indice = 0; indice < interruptores.length; indice++) {
            final Switch interruptor = interruptores[indice];
            final String chave = chaves[indice];
            interruptor.setChecked(preferencias.getBoolean(chave, false));
            interruptor.setEnabled(false);
            interruptor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    preferencias.edit().putBoolean(chave, isChecked).apply();
                }
            });
        }
    }

    private void testarRoot() {
        btnTestarRoot.setEnabled(false);
        btnInventario.setEnabled(false);
        txtRoot.setText(R.string.lab_testando);
        habilitarInterruptores(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final RootShell.Resultado resultado = new RootShell()
                        .executar(ComandosRoot.testarRoot());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isFinishing()) {
                            return;
                        }
                        btnTestarRoot.setEnabled(true);
                        if (resultado.ok) {
                            rootConfirmado = true;
                            txtRoot.setText(R.string.lab_root_ok);
                            habilitarInterruptores(true);
                            btnInventario.setEnabled(true);
                        } else {
                            rootConfirmado = false;
                            txtRoot.setText(R.string.lab_root_negado);
                            habilitarInterruptores(false);
                            btnInventario.setEnabled(false);
                        }
                    }
                });
            }
        }).start();
    }

    private void habilitarInterruptores(boolean habilitar) {
        for (Switch interruptor : interruptores) {
            interruptor.setEnabled(habilitar);
        }
    }

    private void solicitarArmazenamentoOuGerar() {
        if (!rootConfirmado) {
            txtInventario.setText(R.string.lab_inventario_sem_root);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            txtInventario.setText(R.string.lab_inventario_permissao);
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PEDIDO_ARMAZENAMENTO);
            return;
        }
        gerarInventario();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != PEDIDO_ARMAZENAMENTO) {
            return;
        }
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            gerarInventario();
        } else {
            txtInventario.setText(R.string.lab_inventario_permissao);
        }
    }

    private void gerarInventario() {
        btnInventario.setEnabled(false);
        txtInventario.setText(R.string.lab_inventario_gerando);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final File arquivo;
                try {
                    arquivo = criarArquivoInventario();
                } catch (Exception excecao) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!isFinishing()) {
                                txtInventario.setText(getString(
                                        R.string.lab_inventario_falha, mensagem(excecao)));
                                btnInventario.setEnabled(rootConfirmado);
                            }
                        }
                    });
                    return;
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!isFinishing()) {
                            txtInventario.setText(getString(R.string.lab_inventario_salvo,
                                    arquivo.getAbsolutePath()));
                            btnInventario.setEnabled(rootConfirmado);
                        }
                    }
                });
            }
        }, "tx9-inventario").start();
    }

    private File criarArquivoInventario() throws IOException {
        RootShell rootShell = new RootShell();
        RootShell.Resultado testeRoot = rootShell.executar(ComandosRoot.testarRoot());
        if (!testeRoot.ok) {
            throw new IOException(getString(R.string.lab_inventario_sem_root));
        }

        List<RootShell.Resultado> resultados = new ArrayList<RootShell.Resultado>();
        for (ComandosRoot.Comando comando : ComandosRoot.comandosInventario()) {
            resultados.add(rootShell.executar(comando));
        }

        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            throw new IOException("O armazenamento compartilhado não está disponível.");
        }
        File downloads = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);
        if ((!downloads.exists() && !downloads.mkdirs()) || !downloads.isDirectory()) {
            throw new IOException("Não foi possível abrir a pasta Download.");
        }

        Date agora = new Date();
        String nome = "tx9-booster-inventario-"
                + new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(agora) + ".txt";
        File arquivo = new File(downloads, nome);
        String dataHora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US)
                .format(agora);
        String relatorio = RelatorioInventario.formatar(dataHora, resultados);
        Writer escritor = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(arquivo), "UTF-8"));
        try {
            escritor.write(relatorio);
        } finally {
            escritor.close();
        }
        return arquivo;
    }

    private static String mensagem(Exception excecao) {
        return excecao.getMessage() == null
                ? excecao.getClass().getSimpleName() : excecao.getMessage();
    }
}