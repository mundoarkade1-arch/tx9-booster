package br.com.tx9booster;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Locale;

public class DiagnosticoActivity extends Activity {

    private static final String[] CAMINHOS_SU = {
            "/system/bin/su",
            "/system/xbin/su",
            "/sbin/su",
            "/su/bin/su",
            "/vendor/bin/su"
    };

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Locale br = new Locale("pt", "BR");
    private TextView txtInfo;
    private TextView txtRoot;
    private Button btnTestarRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnostico);

        txtInfo = (TextView) findViewById(R.id.txtInfo);
        txtRoot = (TextView) findViewById(R.id.txtRoot);
        btnTestarRoot = (Button) findViewById(R.id.btnTestarRoot);

        txtInfo.setText(montarInfo());
        txtRoot.setText(montarRootPassivo());

        btnTestarRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testarRoot();
            }
        });
    }

    private String montarInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Fabricante: ").append(Build.MANUFACTURER).append("\n");
        sb.append("Modelo: ").append(Build.MODEL).append("\n");
        sb.append("Placa: ").append(Build.BOARD).append("\n");
        sb.append("Android: ").append(Build.VERSION.RELEASE)
                .append(" (API ").append(Build.VERSION.SDK_INT).append(")\n");
        sb.append("Kernel: ").append(System.getProperty("os.version")).append("\n");
        sb.append("ABIs aceitas: ").append(listarAbis()).append("\n");

        try {
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);
            long usada = mi.totalMem - mi.availMem;
            sb.append("RAM total: ").append(emMb(mi.totalMem)).append("\n");
            sb.append("RAM disponível: ").append(emMb(mi.availMem)).append("\n");
            sb.append("RAM em uso: ").append(emMb(usada)).append("\n");
            sb.append("Pouca memória (aviso do sistema): ")
                    .append(mi.lowMemory ? "sim" : "não").append("\n");
        } catch (Exception e) {
            sb.append("RAM: não foi possível ler (").append(e.getMessage()).append(")\n");
        }

        try {
            StatFs st = new StatFs(Environment.getDataDirectory().getPath());
            long total = st.getTotalBytes();
            long livre = st.getAvailableBytes();
            sb.append("Armazenamento total: ").append(emGb(total)).append("\n");
            sb.append("Armazenamento livre: ").append(emGb(livre)).append("\n");
            sb.append("Armazenamento usado: ").append(emGb(total - livre)).append("\n");
        } catch (Exception e) {
            sb.append("Armazenamento: não foi possível ler (").append(e.getMessage()).append(")\n");
        }

        return sb.toString();
    }

    private String listarAbis() {
        String[] abis = Build.SUPPORTED_ABIS;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < abis.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(abis[i]);
        }
        return sb.toString();
    }

    private String emMb(long bytes) {
        return String.format(br, "%.0f MB", bytes / 1048576.0);
    }

    private String emGb(long bytes) {
        return String.format(br, "%.1f GB", bytes / 1073741824.0);
    }

    private String montarRootPassivo() {
        String achado = null;
        for (String caminho : CAMINHOS_SU) {
            if (new File(caminho).exists()) {
                achado = caminho;
                break;
            }
        }
        StringBuilder sb = new StringBuilder();
        if (achado != null) {
            sb.append("Arquivo su encontrado em: ").append(achado).append("\n");
        } else {
            sb.append("Nenhum arquivo su encontrado nos locais comuns.\n");
        }
        sb.append("Isso só mostra se o arquivo existe. Para saber se o app tem permissão de root, use o botão abaixo.");
        return sb.toString();
    }

    private void testarRoot() {
        btnTestarRoot.setEnabled(false);
        txtRoot.setText("Testando... Se aparecer um pedido de permissão, aceite. Aguarde até 15 segundos.");
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String resultado = executarTesteRoot();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isFinishing()) {
                            return;
                        }
                        txtRoot.setText(resultado + "\n\n" + montarRootPassivo());
                        btnTestarRoot.setEnabled(true);
                        btnTestarRoot.requestFocus();
                    }
                });
            }
        }).start();
    }

    private String executarTesteRoot() {
        Process p = null;
        try {
            p = new ProcessBuilder("su", "-c", "id").redirectErrorStream(true).start();
            long limite = System.currentTimeMillis() + 15000;
            Integer codigo = null;
            while (codigo == null && System.currentTimeMillis() < limite) {
                try {
                    codigo = p.exitValue();
                } catch (IllegalThreadStateException aindaRodando) {
                    Thread.sleep(100);
                }
            }
            if (codigo == null) {
                p.destroy();
                return "Resultado: tempo esgotado (15 segundos). O pedido de permissão não foi respondido.";
            }
            String saida = lerSaida(p);
            if (codigo == 0 && saida.contains("uid=0")) {
                return "Resultado: ROOT FUNCIONANDO. O app recebeu permissão de root.";
            }
            return "Resultado: sem permissão de root. Código de saída: " + codigo
                    + ". Saída: " + saida;
        } catch (Exception e) {
            return "Resultado: não foi possível executar o su (" + e.getMessage() + ").";
        } finally {
            if (p != null) {
                p.destroy();
            }
        }
    }

    private String lerSaida(Process p) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader leitor = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String linha;
            while ((linha = leitor.readLine()) != null && sb.length() < 500) {
                sb.append(linha).append(" ");
            }
            leitor.close();
        } catch (Exception ignorado) {
            // sem saída para mostrar
        }
        return sb.toString().trim();
    }
}
