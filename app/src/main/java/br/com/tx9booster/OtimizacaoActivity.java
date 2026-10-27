package br.com.tx9booster;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Tela OTIMIZAÇÃO RÁPIDA.
 * Junta duas ações já seguras: apagar o cache do próprio app (ExclusorCache,
 * com a PoliticaCaminhos) e pedir ao Android para encerrar apps de USUÁRIO
 * em segundo plano. Nunca toca em apps do sistema nem no próprio app.
 * O relatório mostra só números medidos.
 */
public class OtimizacaoActivity extends Activity {

    private Button btnOtimizar;
    private TextView txtResultado;
    private boolean ocupado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otimizacao);

        btnOtimizar = (Button) findViewById(R.id.btnOtimizar);
        txtResultado = (TextView) findViewById(R.id.txtOtimResultado);

        btnOtimizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmar();
            }
        });
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

    // Pergunta antes. O foco começa em CANCELAR.
    private void confirmar() {
        if (ocupado || isFinishing()) {
            return;
        }
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle(R.string.otim_conf_titulo);
        b.setMessage(R.string.otim_conf_msg);
        b.setPositiveButton(R.string.otim_sim, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                otimizar();
            }
        });
        b.setNegativeButton(R.string.otim_nao, null);
        AlertDialog dialogo = b.create();
        dialogo.show();
        Button nao = dialogo.getButton(DialogInterface.BUTTON_NEGATIVE);
        if (nao != null) {
            nao.requestFocus();
        }
    }

    private void otimizar() {
        if (ocupado) {
            return;
        }
        ocupado = true;
        btnOtimizar.setEnabled(false);
        txtResultado.setText(R.string.otim_trabalhando);

        final List<File> areas = areasDoApp();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String texto;
                try {
                    texto = executar(areas);
                } catch (Exception e) {
                    texto = "Erro na otimização: " + e.getClass().getSimpleName();
                }
                final String textoFinal = texto;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isFinishing()) {
                            return;
                        }
                        txtResultado.setText(textoFinal);
                        btnOtimizar.setEnabled(true);
                        btnOtimizar.requestFocus();
                        ocupado = false;
                    }
                });
            }
        }).start();
    }

    // Mede ANTES, otimiza, espera um pouco, mede DEPOIS.
    private String executar(List<File> areas) {
        long ramTotal = ramTotal();
        long ramAntes = ramLivre();
        long discoAntes = discoLivre();

        // 1) Cache do próprio app (mesma lógica da tela Limpeza).
        PoliticaCaminhos politica = new PoliticaCaminhos(areas);
        ExclusorCache exclusor = new ExclusorCache(politica);
        int apagados = 0;
        long liberados = 0;
        int falhas = 0;
        for (File area : areas) {
            ExclusorCache.Resultado e = exclusor.apagar(area);
            if (e.areaValida) {
                apagados += e.arquivosApagados;
                liberados += e.bytesLiberados;
                falhas += e.falhas;
            }
        }

        // 2) Pedido ao Android para encerrar apps de usuário em segundo plano.
        int pedidos = encerrarAppsDeUsuario();

        // Dá um tempo para o Android atualizar os números.
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long ramDepois = ramLivre();
        long discoDepois = discoLivre();

        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.otim_relatorio_titulo)).append("\n\n");
        sb.append(getString(R.string.otim_ram_antes,
                formatar(ramAntes), formatar(ramTotal))).append("\n");
        sb.append(getString(R.string.otim_ram_depois, formatar(ramDepois))).append("\n");
        sb.append(getString(R.string.otim_ram_dif, formatarComSinal(ramDepois - ramAntes)))
                .append("\n\n");
        sb.append(getString(R.string.otim_disco_antes, formatar(discoAntes))).append("\n");
        sb.append(getString(R.string.otim_disco_depois, formatar(discoDepois))).append("\n\n");
        sb.append(getString(R.string.otim_cache, apagados, formatar(liberados), falhas))
                .append("\n");
        sb.append(getString(R.string.otim_apps, pedidos)).append("\n\n");
        sb.append(getString(R.string.otim_nota));
        return sb.toString();
    }

    // Só apps de usuário. Pula apps do sistema, o próprio app e a tela inicial (launcher).
    private int encerrarAppsDeUsuario() {
        PackageManager pm = getPackageManager();
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) {
            return 0;
        }

        String home = null;
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo ri = pm.resolveActivity(i, PackageManager.MATCH_DEFAULT_ONLY);
        if (ri != null && ri.activityInfo != null) {
            home = ri.activityInfo.packageName;
        }

        int pedidos = 0;
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);
        for (ApplicationInfo a : apps) {
            int sistema = ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;
            if ((a.flags & sistema) != 0) {
                continue;
            }
            if (a.packageName.equals(getPackageName())) {
                continue;
            }
            if (a.packageName.equals(home)) {
                continue;
            }
            try {
                am.killBackgroundProcesses(a.packageName);
                pedidos++;
            } catch (Exception e) {
                // Segue para o próximo.
            }
        }
        return pedidos;
    }

    private long ramLivre() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) {
            return 0;
        }
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return mi.availMem;
    }

    private long ramTotal() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) {
            return 0;
        }
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return mi.totalMem;
    }

    private long discoLivre() {
        try {
            StatFs s = new StatFs(Environment.getDataDirectory().getPath());
            return s.getAvailableBlocksLong() * s.getBlockSizeLong();
        } catch (Exception e) {
            return 0;
        }
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

    private static String formatarComSinal(long bytes) {
        if (bytes > 0) {
            return "+" + formatar(bytes);
        }
        if (bytes < 0) {
            return "-" + formatar(-bytes);
        }
        return formatar(0);
    }
}
