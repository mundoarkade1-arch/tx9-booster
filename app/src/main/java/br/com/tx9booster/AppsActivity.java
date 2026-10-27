package br.com.tx9booster;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Tela APLICATIVOS. Só LÊ a lista de apps e classifica.
 * Não apaga, não desativa e não para nada. Apertar OK em um app abre a tela
 * de informações do próprio Android, onde quem decide é o dono.
 */
public class AppsActivity extends Activity {

    private static final class Linha {
        String nome;
        String pacote;
        String tag;
        boolean usuario;
    }

    private final List<Linha> todas = new ArrayList<Linha>();
    private final List<Linha> visiveis = new ArrayList<Linha>();
    private boolean soUsuario = true;

    private Button btnFiltro;
    private TextView txtTotal;
    private ListView lista;
    private ArrayAdapter<String> adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps);

        btnFiltro = (Button) findViewById(R.id.btnFiltroApps);
        txtTotal = (TextView) findViewById(R.id.txtTotalApps);
        lista = (ListView) findViewById(R.id.listaApps);

        adaptador = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, new ArrayList<String>()) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                tv.setTextColor(0xFFFFFFFF);
                tv.setTextSize(20);
                tv.setPadding(16, 12, 16, 12);
                return tv;
            }
        };
        lista.setAdapter(adaptador);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < visiveis.size()) {
                    abrirInformacoes(visiveis.get(position).pacote);
                }
            }
        });

        btnFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soUsuario = !soUsuario;
                atualizarLista();
            }
        });

        carregar();
    }

    private void carregar() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Linha> lidas = lerApps();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isFinishing()) {
                            return;
                        }
                        todas.clear();
                        todas.addAll(lidas);
                        atualizarLista();
                        btnFiltro.requestFocus();
                    }
                });
            }
        }).start();
    }

    private List<Linha> lerApps() {
        List<Linha> saida = new ArrayList<Linha>();
        PackageManager pm = getPackageManager();

        String home = null;
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo ri = pm.resolveActivity(i, PackageManager.MATCH_DEFAULT_ONLY);
        if (ri != null && ri.activityInfo != null) {
            home = ri.activityInfo.packageName;
        }

        List<ApplicationInfo> apps = pm.getInstalledApplications(0);
        for (ApplicationInfo a : apps) {
            Linha l = new Linha();
            l.pacote = a.packageName;
            CharSequence rotulo = pm.getApplicationLabel(a);
            l.nome = rotulo == null ? a.packageName : rotulo.toString();

            boolean sistema = (a.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
            boolean atualizadoSistema = (a.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;

            if (sistema || atualizadoSistema) {
                l.usuario = false;
                if (ehCritico(a.packageName, home)) {
                    l.tag = getString(R.string.apps_tag_critico);
                } else if (atualizadoSistema) {
                    l.tag = getString(R.string.apps_tag_analisar);
                } else {
                    l.tag = getString(R.string.apps_tag_sistema);
                }
            } else {
                l.usuario = true;
                l.tag = getString(R.string.apps_tag_usuario);
            }
            saida.add(l);
        }

        Collections.sort(saida, new Comparator<Linha>() {
            @Override
            public int compare(Linha x, Linha y) {
                return x.nome.toLowerCase(Locale.getDefault())
                        .compareTo(y.nome.toLowerCase(Locale.getDefault()));
            }
        });
        return saida;
    }

    // Críticos: o que o Android e o TV Box precisam para funcionar.
    private boolean ehCritico(String pacote, String home) {
        if (pacote.equals(getPackageName())) {
            return true;
        }
        if (home != null && pacote.equals(home)) {
            return true;
        }
        return pacote.equals("android")
                || pacote.startsWith("android.")
                || pacote.startsWith("com.android.")
                || pacote.startsWith("com.google.android.gms")
                || pacote.startsWith("com.google.android.gsf")
                || pacote.equals("com.android.vending")
                || pacote.startsWith("com.amlogic.")
                || pacote.startsWith("com.droidlogic");
    }

    private void atualizarLista() {
        visiveis.clear();
        adaptador.clear();
        for (Linha l : todas) {
            if (soUsuario && !l.usuario) {
                continue;
            }
            visiveis.add(l);
            adaptador.add("[" + l.tag + "] " + l.nome + "\n" + l.pacote);
        }
        adaptador.notifyDataSetChanged();
        btnFiltro.setText(soUsuario ? R.string.apps_filtro_usuario : R.string.apps_filtro_todos);
        txtTotal.setText(getString(R.string.apps_total, visiveis.size()));
    }

    private void abrirInformacoes(String pacote) {
        try {
            Intent it = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + pacote));
            startActivity(it);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.apps_sem_tela, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, R.string.apps_sem_tela, Toast.LENGTH_SHORT).show();
        }
    }
}
