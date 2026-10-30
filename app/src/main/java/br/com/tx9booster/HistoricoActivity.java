package br.com.tx9booster;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoricoActivity extends Activity {

    private Historico historico;
    private ListView lista;
    private TextView txtEstado;
    private Button btnReverter;
    private ArrayAdapter<String> adaptador;
    private final List<Historico.Registro> registrosVisiveis =
            new ArrayList<Historico.Registro>();
    private int posicaoSelecionada = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        historico = new Historico(getFilesDir());
        lista = (ListView) findViewById(R.id.listaHistorico);
        txtEstado = (TextView) findViewById(R.id.txtHistoricoEstado);
        btnReverter = (Button) findViewById(R.id.btnReverterHistorico);
        Button btnAtualizar = (Button) findViewById(R.id.btnAtualizarHistorico);

        adaptador = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_activated_1, new ArrayList<String>()) {
            @Override
            public View getView(int position, View convertView,
                                android.view.ViewGroup parent) {
                TextView linha = (TextView) super.getView(position, convertView, parent);
                linha.setTextColor(0xFFFFFFFF);
                linha.setTextSize(18f);
                linha.setPadding(16, 16, 16, 16);
                linha.setMinHeight(96);
                return linha;
            }
        };
        lista.setAdapter(adaptador);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                posicaoSelecionada = position;
                lista.setItemChecked(position, true);
                atualizarBotaoReverter();
            }
        });
        btnReverter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarReversao();
            }
        });
        btnAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carregarHistorico(null);
            }
        });
        carregarHistorico(null);
    }

    private void carregarHistorico(String mensagem) {
        try {
            List<Historico.Registro> todos = historico.listar();
            List<Historico.Registro> reversos = new ArrayList<Historico.Registro>(todos);
            Collections.reverse(reversos);
            registrosVisiveis.clear();
            adaptador.clear();
            for (Historico.Registro registro : reversos) {
                if ("RESULTADO".equals(registro.acao)) {
                    continue;
                }
                registrosVisiveis.add(registro);
                adaptador.add(textoRegistro(registro, todos));
            }
            posicaoSelecionada = -1;
            lista.clearChoices();
            atualizarBotaoReverter();
            if (mensagem != null) {
                txtEstado.setText(mensagem);
            } else if (registrosVisiveis.isEmpty()) {
                txtEstado.setText(R.string.hist_vazio);
            } else {
                txtEstado.setText(getString(R.string.hist_total, registrosVisiveis.size()));
            }
        } catch (IOException e) {
            adaptador.clear();
            registrosVisiveis.clear();
            btnReverter.setEnabled(false);
            txtEstado.setText(getString(R.string.hist_erro_leitura, mensagem(e)));
        }
    }

    private String textoRegistro(Historico.Registro registro,
                                List<Historico.Registro> todos) {
        String acao;
        if (ComandosRoot.DESATIVAR_PACOTE.equals(registro.acao)) {
            acao = "Desativar aplicativo";
        } else if (ComandosRoot.REATIVAR_PACOTE.equals(registro.acao)) {
            acao = "Reativar aplicativo";
        } else {
            acao = "Reversão";
        }
        String alvo = "REVERSAO".equals(registro.acao)
                ? getString(R.string.hist_acao_sem_alvo, acao, registro.alvo)
                : getString(R.string.hist_acao_reversivel, acao, registro.alvo);
        return getString(R.string.hist_item, registro.dataHora, alvo,
                registro.valorOriginal, registro.valorOriginal, registro.valorNovo,
                historico.resultadoAtual(registro, todos));
    }

    private void atualizarBotaoReverter() {
        boolean habilitado = false;
        if (posicaoSelecionada >= 0 && posicaoSelecionada < registrosVisiveis.size()) {
            Historico.Registro registro = registrosVisiveis.get(posicaoSelecionada);
            try {
                List<Historico.Registro> todos = historico.listar();
                String resultado = historico.resultadoAtual(registro, todos);
                habilitado = "OK".equals(resultado)
                        && (ComandosRoot.DESATIVAR_PACOTE.equals(registro.acao)
                        || ComandosRoot.REATIVAR_PACOTE.equals(registro.acao))
                        && !historico.foiRevertido(registro, todos);
            } catch (IOException ignorado) {
                habilitado = false;
            }
        }
        btnReverter.setEnabled(habilitado);
        if (!habilitado && posicaoSelecionada < 0) {
            txtEstado.setText(R.string.hist_sem_selecao);
        }
    }

    private void confirmarReversao() {
        if (posicaoSelecionada < 0 || posicaoSelecionada >= registrosVisiveis.size()) {
            txtEstado.setText(R.string.hist_sem_selecao);
            return;
        }
        final Historico.Registro registro = registrosVisiveis.get(posicaoSelecionada);
        AlertDialog dialogo = new AlertDialog.Builder(this)
                .setTitle(R.string.hist_confirmar_titulo)
                .setMessage(R.string.hist_confirmar_msg)
                .setPositiveButton(R.string.hist_confirmar,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                executarReversao(registro);
                            }
                        })
                .setNegativeButton(R.string.hist_cancelar, null)
                .create();
        dialogo.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE).requestFocus();
            }
        });
        dialogo.show();
        dialogo.getButton(DialogInterface.BUTTON_NEGATIVE).requestFocus();
    }

    private void executarReversao(final Historico.Registro registro) {
        btnReverter.setEnabled(false);
        txtEstado.setText(R.string.hist_trabalhando);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String resultado;
                try {
                    Reversor.ResultadoReversao r = new Reversor(
                            new RootShell(), historico).reverter(registro);
                    resultado = r.mensagem;
                } catch (IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!isFinishing()) {
                                carregarHistorico(getString(R.string.hist_erro_leitura,
                                        mensagem(e)));
                            }
                        }
                    });
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isFinishing()) {
                            carregarHistorico(resultado);
                        }
                    }
                });
            }
        }).start();
    }

    private static String mensagem(Exception e) {
        return e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
    }
}