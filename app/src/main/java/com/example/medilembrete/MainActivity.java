package com.example.medilembrete;

import android.content.Intent; // necessário para abrir outra Activity
import android.os.Bundle;
import android.widget.Button; // necessário para criar botões

import androidx.appcompat.app.AppCompatActivity;

// cria uma classe chamada "Main Activity" que herda AppCompatActivity
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // carrega a parte visual

        Button btnAdicionar = findViewById(R.id.btnAdicionar); // chama o botão via id
        btnAdicionar.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CadastroMedicamentoActivity.class));
        });
    }

    
}
