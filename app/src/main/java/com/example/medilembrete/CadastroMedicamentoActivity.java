package com.example.medilembrete;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medilembrete.data.AppDatabase;
import com.example.medilembrete.data.MedicamentoRepository;
import com.example.medilembrete.data.entity.Medicamento;

import java.util.concurrent.TimeUnit;

/**
 * Tela 2 - Cadastro de Medicamento (rascunho funcional).
 * Nome, dosagem, quantidade por dose e período do tratamento são salvos
 * aqui; os horários de administração ficam para a Tela 3 (Configuração
 * de Horários), num passo posterior sobre o id retornado.
 */
public class CadastroMedicamentoActivity extends AppCompatActivity {

    private EditText etNome;
    private EditText etDosagem;
    private EditText etQuantidade;
    private EditText etDuracaoDias;
    private EditText etObservacoes;
    private Button btnSalvar;

    private MedicamentoRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_medicamento);

        etNome = findViewById(R.id.etNome);
        etDosagem = findViewById(R.id.etDosagem);
        etQuantidade = findViewById(R.id.etQuantidade);
        etDuracaoDias = findViewById(R.id.etDuracaoDias);
        etObservacoes = findViewById(R.id.etObservacoes);
        btnSalvar = findViewById(R.id.btnSalvar);

        repository = new MedicamentoRepository(AppDatabase.getInstance(this));

        btnSalvar.setOnClickListener(v -> salvar());
    }

    private void salvar() {
        String nome = etNome.getText().toString().trim();
        String dosagem = etDosagem.getText().toString().trim();
        String quantidadeTexto = etQuantidade.getText().toString().trim();
        String duracaoTexto = etDuracaoDias.getText().toString().trim();
        String observacoes = etObservacoes.getText().toString().trim();

        int quantidadePorDose;
        try {
            quantidadePorDose = quantidadeTexto.isEmpty() ? 0 : Integer.parseInt(quantidadeTexto);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Quantidade por dose inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        long dataInicio = System.currentTimeMillis();
        Long dataFim = null;
        if (!duracaoTexto.isEmpty()) {
            try {
                int duracaoDias = Integer.parseInt(duracaoTexto);
                dataFim = dataInicio + TimeUnit.DAYS.toMillis(duracaoDias);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Duração do tratamento inválida", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Medicamento medicamento = new Medicamento(nome, dosagem, quantidadePorDose,
                dataInicio, dataFim, observacoes.isEmpty() ? null : observacoes);

        btnSalvar.setEnabled(false);
        try {
            repository.cadastrar(medicamento, id -> runOnUiThread(() -> {
                Toast.makeText(this, "Medicamento salvo!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }));
        } catch (IllegalArgumentException e) {
            // validação falhou antes de tentar gravar - reabilita o botão
            btnSalvar.setEnabled(true);
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
