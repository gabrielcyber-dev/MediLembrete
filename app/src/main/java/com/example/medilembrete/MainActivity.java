package com.example.medilembrete;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button; // necessário para criar botões
import android.widget.Toast; // cria mensagem temporária

// cria uma classe chamada "Main Activity" que herda Activity
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // carrega a parte visual
        
        Button btnAdicionar = findViewById(R.id.btnAdicionar); // chama o botão via id
        btnAdicionar.setOnClickListener(v -> {
            Toast.makeText(this, "Botão clicado!", Toast.LENGTH_SHORT).show();
        });
    }

    
}
