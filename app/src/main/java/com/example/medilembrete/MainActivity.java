package com.example.medilembrete;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Carrega a tela inicial
        setContentView(R.layout.activity_main);

        // Encontra o botão pelo ID
        Button btnAdicionar = findViewById(R.id.btnAdicionar);

        // Quando o usuário clicar no botão
        btnAdicionar.setOnClickListener(v -> {

            // Cria uma intenção para abrir a tela de novo medicamento
            Intent intent = new Intent(
                    MainActivity.this,
                    NovoMedicamentoActivity.class
            );

            // Abre a nova tela
            startActivity(intent);
        });
    }
}
