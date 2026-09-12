package com.example.medilembrete;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Button btnAdicionar = findViewById(R.id.btnAdicionar);

        btnAdicionar.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    NovoMedicamentoActivity.class
            );

            startActivity(intent);
        });
    }
}
