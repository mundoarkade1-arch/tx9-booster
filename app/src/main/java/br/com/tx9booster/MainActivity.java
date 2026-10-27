package br.com.tx9booster;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

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
    }
}
