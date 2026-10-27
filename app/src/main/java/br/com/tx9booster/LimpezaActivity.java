package br.com.tx9booster;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Tela LIMPEZA AVANÇADA, modo de simulação.
 * Só mede e mostra o que seria apagado. NUNCA apaga nada.
 */
public class LimpezaActivity extends Activity {

    private static final int MAX_ITENS_NA_TELA = 10;

    private Button btnSimular;
    private TextView txtResultado;
    private boolean ocupado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_limpeza);

        btnSimular = (Button) findViewById(R.id.btnSimular);
        txtResultado = (TextView) findViewById(R.id.txtSimulResultado);

        btnSimular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simular();
            }
        });
    }

    private void simular() {
        if (ocupado) {
            return;
        }
        ocupado = true;
        btnSimular.setEnabled(false);
        txtResultado.setText(R.string.simul_analisando);

        final List<File> areas = new ArrayList<File>();
        File cacheInterno = getCacheDir();
        if (cacheInterno != null) {
            areas.add(cacheInterno);
        }
        File cacheExterno = getExternalCacheDir();
        if (cacheExterno != null) {
            areas.add(cacheExterno);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                String texto;
                try {
                    texto = analisar(areas);
                } catch (Exception e) {
                    texto = "Erro na análise: " + e.getClass().getSimpleName();
                }
                final String textoFinal = texto;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isFinishing()) {
                            return;
                        }
                        txtResultado.setText(textoFinal);
                        btnSimular.setEnabled(true);
                        btnSimular.requestFocus();
                        ocupado = false;
                    }
                });
            }
        }).start();
    }

    // Só lê e monta o texto. Não apaga nada.
    private String analisar(List<File> areas) {
        PoliticaCaminhos politica = new PoliticaCaminhos(areas);
        Analisador analisador = new Analisador(politica);

        StringBuilder sb = new StringBuilder();
        long totalGeral = 0;

        for (File area : areas) {
            Analisador.Resultado r = analisador.analisar(area);
            sb.append(getString(R.string.simul_area, area.getPath())).append("\n");
            if (!r.areaValida) {
                sb.append(getString(R.string.simul_area_invalida)).append("\n\n");
                continue;
            }
            totalGeral += r.totalBytes;
            sb.append(getString(R.string.simul_arquivos,
                    r.totalArquivos, formatar(r.totalBytes))).append("\n");
            sb.append(getString(R.string.simul_ignorados, r.ignorados)).append("\n");
            if (r.truncado) {
                sb.append(getString(R.string.simul_truncado)).append("\n");
            }
            if (!r.itens.isEmpty()) {
                sb.append(getString(R.string.simul_lista_titulo)).append("\n");
                int limite = Math.min(MAX_ITENS_NA_TELA, r.itens.size());
                for (int i = 0; i < limite; i++) {
                    Analisador.Item item = r.itens.get(i);
                    sb.append("- ").append(item.caminho)
                            .append(" (").append(formatar(item.bytes)).append(")\n");
                }
            }
            sb.append("\n");
        }

        sb.append(getString(R.string.simul_total, formatar(totalGeral)));
        return sb.toString();
    }

    private static String formatar(long bytes) {
        Locale pt = new Locale("pt", "BR");
        if (bytes < 1024) {
            return bytes + " bytes";
        }
        double kb = bytes / 1024.0;
        if (kb < 1024) {
            return String.format(pt, "%.1f KB", kb);
        }
        return String.format(pt, "%.1f MB", kb / 1024.0);
    }
}
