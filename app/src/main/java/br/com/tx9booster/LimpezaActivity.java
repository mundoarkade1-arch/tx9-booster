package br.com.tx9booster;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Tela LIMPEZA AVANÇADA.
 * SIMULAR só mede e mostra o que seria apagado. NUNCA apaga nada.
 * APAGAR CACHE DO APP só funciona depois de uma simulação, pede confirmação
 * e apaga somente o cache do próprio app, passando pela PoliticaCaminhos.
 */
public class LimpezaActivity extends Activity {

    private static final int MAX_ITENS_NA_TELA = 10;

    private Button btnSimular;
    private Button btnApagar;
    private TextView txtResultado;
    private boolean ocupado = false;
    // Total em bytes da última simulação (ou do "depois" da última limpeza).
    private volatile long ultimoTotal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_limpeza);

        btnSimular = (Button) findViewById(R.id.btnSimular);
        btnApagar = (Button) findViewById(R.id.btnApagar);
        txtResultado = (TextView) findViewById(R.id.txtSimulResultado);
        definirApagar(false);

        btnSimular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simular();
            }
        });

        btnApagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarApagar();
            }
        });
    }

    private void definirApagar(boolean ligado) {
        btnApagar.setEnabled(ligado);
        btnApagar.setAlpha(ligado ? 1.0f : 0.4f);
    }

    // Só o cache interno e o cache externo do próprio app.
    private List<File> areasDoApp() {
        List<File> areas = new ArrayList<File>();
        File cacheInterno = getCacheDir();
        if (cacheInterno != null) {
            areas.add(cacheInterno);
        }
        File cacheExterno = getExternalCacheDir();
        if (cacheExterno != null) {
            areas.add(cacheExterno);
        }
        return areas;
    }

    private void simular() {
        if (ocupado) {
            return;
        }
        ocupado = true;
        btnSimular.setEnabled(false);
        definirApagar(false);
        ultimoTotal = 0;
        txtResultado.setText(R.string.simul_analisando);

        final List<File> areas = areasDoApp();

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
                        definirApagar(ultimoTotal > 0);
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

        ultimoTotal = totalGeral;
        sb.append(getString(R.string.simul_total, formatar(totalGeral)));
        return sb.toString();
    }

    // Pergunta antes de apagar. O foco começa em CANCELAR, para não apagar sem querer.
    private void confirmarApagar() {
        if (ocupado || isFinishing()) {
            return;
        }
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle(R.string.exc_confirma_titulo);
        b.setMessage(getString(R.string.exc_confirma_msg, formatar(ultimoTotal)));
        b.setPositiveButton(R.string.exc_confirma_sim, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                apagar();
            }
        });
        b.setNegativeButton(R.string.exc_confirma_nao, null);
        AlertDialog dialogo = b.create();
        dialogo.show();
        Button nao = dialogo.getButton(DialogInterface.BUTTON_NEGATIVE);
        if (nao != null) {
            nao.requestFocus();
        }
    }

    private void apagar() {
        if (ocupado) {
            return;
        }
        ocupado = true;
        btnSimular.setEnabled(false);
        definirApagar(false);
        txtResultado.setText(R.string.exc_apagando);

        final List<File> areas = areasDoApp();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String texto;
                try {
                    texto = apagarEMedir(areas);
                } catch (Exception e) {
                    texto = "Erro ao apagar: " + e.getClass().getSimpleName();
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
                        definirApagar(ultimoTotal > 0);
                        btnSimular.requestFocus();
                        ocupado = false;
                    }
                });
            }
        }).start();
    }

    // Mede ANTES, apaga, mede DEPOIS. Só números medidos.
    private String apagarEMedir(List<File> areas) {
        PoliticaCaminhos politica = new PoliticaCaminhos(areas);
        Analisador analisador = new Analisador(politica);
        ExclusorCache exclusor = new ExclusorCache(politica);

        int areasValidas = 0;
        boolean limite = false;

        long antesBytes = 0;
        int antesArquivos = 0;
        for (File area : areas) {
            Analisador.Resultado r = analisador.analisar(area);
            if (r.areaValida) {
                areasValidas++;
                antesBytes += r.totalBytes;
                antesArquivos += r.totalArquivos;
                if (r.truncado) {
                    limite = true;
                }
            }
        }

        int apagados = 0;
        long liberados = 0;
        int falhas = 0;
        int ignorados = 0;
        for (File area : areas) {
            ExclusorCache.Resultado e = exclusor.apagar(area);
            if (e.areaValida) {
                apagados += e.arquivosApagados;
                liberados += e.bytesLiberados;
                falhas += e.falhas;
                ignorados += e.ignorados;
                if (e.truncado) {
                    limite = true;
                }
            }
        }

        long depoisBytes = 0;
        int depoisArquivos = 0;
        for (File area : areas) {
            Analisador.Resultado r = analisador.analisar(area);
            if (r.areaValida) {
                depoisBytes += r.totalBytes;
                depoisArquivos += r.totalArquivos;
                if (r.truncado) {
                    limite = true;
                }
            }
        }
        ultimoTotal = depoisBytes;

        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.exc_relatorio_titulo)).append("\n\n");
        if (areasValidas == 0) {
            sb.append(getString(R.string.exc_nenhuma_area));
            return sb.toString();
        }
        sb.append(getString(R.string.exc_antes, antesArquivos, formatar(antesBytes))).append("\n");
        sb.append(getString(R.string.exc_apagados, apagados, formatar(liberados))).append("\n");
        sb.append(getString(R.string.exc_depois, depoisArquivos, formatar(depoisBytes))).append("\n");
        sb.append(getString(R.string.exc_falhas, falhas)).append("\n");
        sb.append(getString(R.string.exc_ignorados, ignorados));
        if (limite) {
            sb.append("\n").append(getString(R.string.exc_limite));
        }
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
