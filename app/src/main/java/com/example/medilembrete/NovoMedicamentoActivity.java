package com.example.medilembrete;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.medilembrete.model.Medicamento;

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

        EditText edtHorarioInicial =
                findViewById(R.id.edtHorarioInicial);

        Spinner spinnerIntervalo =
                findViewById(R.id.spinnerIntervalo);

        EditText edtDuracao =
                findViewById(R.id.edtDuracao);

        CheckBox chkDuracaoIndefinida =
                findViewById(R.id.chkDuracaoIndefinida);

        Button btnSalvar =
                findViewById(R.id.btnSalvar);

        String[] intervalos = {
                "6 horas",
                "8 horas",
                "12 horas",
                "24 horas"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        intervalos
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerIntervalo.setAdapter(adapter);

        btnSalvar.setOnClickListener(v -> {

            String nome =
                    edtNomeMedicamento.getText().toString();

            String dosagem =
                    edtDosagem.getText().toString();

            String quantidade =
                    edtQuantidade.getText().toString();

            String horarioInicial =
                    edtHorarioInicial.getText().toString();

            String intervaloTexto =
                    spinnerIntervalo
                            .getSelectedItem()
                            .toString();

            int intervaloHoras =
                    Integer.parseInt(
                            intervaloTexto
                                    .replace(" horas", "")
                    );

            boolean duracaoIndefinida =
                    chkDuracaoIndefinida.isChecked();

            int duracaoDias = 0;

            if (!duracaoIndefinida) {

                String duracaoTexto =
                        edtDuracao
                                .getText()
                                .toString();

                duracaoDias =
                        Integer.parseInt(duracaoTexto);
            }

            Medicamento medicamento =
                    new Medicamento(
                            0,
                            nome,
                            dosagem,
                            quantidade,
                            horarioInicial,
                            intervaloHoras,
                            duracaoDias,
                            duracaoIndefinida
                    );

            Toast.makeText(
                    this,
                    "Medicamento criado: "
                            + medicamento.getNome(),
                    Toast.LENGTH_SHORT
            ).show();
        });
    }
}