package com.example.medilembrete;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class NovoMedicamentoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_novo_medicamento);

        EditText edtNomeMedicamento =
                findViewById(R.id.edtNomeMedicamento);

        EditText edtDosagem =
                findViewById(R.id.edtDosagem);

        EditText edtQuantidade =
                findViewById(R.id.edtQuantidade);

        EditText edtDuracao =
                findViewById(R.id.edtDuracao);

        Spinner spinnerFrequencia =
                findViewById(R.id.spinnerFrequencia);

        Button btnSalvar =
                findViewById(R.id.btnSalvar);

        btnSalvar.setOnClickListener(v -> {

            String nome =
                    edtNomeMedicamento.getText().toString();

            String dosagem =
                    edtDosagem.getText().toString();

            String quantidade =
                    edtQuantidade.getText().toString();

            String frequencia =
                    spinnerFrequencia.getSelectedItem().toString();

            String duracaoTexto =
                    edtDuracao.getText().toString();

            int duracao =
                    Integer.parseInt(duracaoTexto);

            Medicamento medicamento = new Medicamento(
                    nome,
                    dosagem,
                    quantidade,
                    frequencia,
                    duracao
            );

            Toast.makeText(
                    this,
                    "Medicamento criado: " + medicamento.getNome(),
                    Toast.LENGTH_SHORT
            ).show();
        });
    }
}