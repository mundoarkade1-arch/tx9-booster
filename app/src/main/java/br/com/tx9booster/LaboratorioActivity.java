package br.com.tx9booster;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class LaboratorioActivity extends Activity {

    private static final String PREFERENCIAS = "laboratorio";
    private final Handler handler = new Handler(Looper.getMainLooper());
    private TextView txtRoot;
    private Button btnTestarRoot;
    private Switch[] interruptores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laboratorio);

        txtRoot = (TextView) findViewById(R.id.txtLaboratorioRoot);
        btnTestarRoot = (Button) findViewById(R.id.btnTestarRootLaboratorio);
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
                            txtRoot.setText(R.string.lab_root_ok);
                            habilitarInterruptores(true);
                        } else {
                            txtRoot.setText(R.string.lab_root_negado);
                            habilitarInterruptores(false);
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
}