package com.example.medilembrete;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Button btnAdicionar = findViewById(R.id.btnAdicionar);

        // Abre a Tela 2 - Cadastro de Medicamento, que grava no banco (Room).
        // A NovoMedicamentoActivity (Gabriel) segue no projeto e sera unificada
        // com esta tela quando o time definir o modelo unico de dados.
        btnAdicionar.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    CadastroMedicamentoActivity.class
            );

            startActivity(intent);
        });
    }
}
