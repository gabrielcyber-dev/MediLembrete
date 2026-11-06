package com.example.medilembrete;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medilembrete.data.AppDatabase;
import com.example.medilembrete.data.entity.Medicamento;
import com.example.medilembrete.data.entity.StatusDose;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Tela inicial do MediLembrete.
 * Os medicamentos e os contadores são carregados do Room, sem dados fictícios no XML.
 */
public class MainActivity extends AppCompatActivity {

    private LinearLayout listaMedicamentos;
    private TextView txtTotalMedicamentos;
    private TextView txtQuantidadeMedicamentos;
    private TextView txtTomadosHoje;
    private TextView txtPendentesHoje;
    private TextView txtProximoLembrete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listaMedicamentos = findViewById(R.id.listaMedicamentos);
        txtTotalMedicamentos = findViewById(R.id.txtTotalMedicamentos);
        txtQuantidadeMedicamentos = findViewById(R.id.txtQuantidadeMedicamentos);
        txtTomadosHoje = findViewById(R.id.txtTomadosHoje);
        txtPendentesHoje = findViewById(R.id.txtPendentesHoje);
        txtProximoLembrete = findViewById(R.id.txtProximoLembrete);

        TextView txtSaudacao = findViewById(R.id.txtSaudacao);
        TextView txtDataAtual = findViewById(R.id.txtDataAtual);
        atualizarCabecalho(txtSaudacao, txtDataAtual);

        AppDatabase db = AppDatabase.getInstance(this);

        // LiveData atualiza a tela automaticamente quando o banco muda.
        db.medicamentoDao().listarTodos().observe(this, this::mostrarMedicamentos);

        Calendar inicio = Calendar.getInstance();
        inicio.set(Calendar.HOUR_OF_DAY, 0);
        inicio.set(Calendar.MINUTE, 0);
        inicio.set(Calendar.SECOND, 0);
        inicio.set(Calendar.MILLISECOND, 0);
        long inicioDia = inicio.getTimeInMillis();

        Calendar fim = (Calendar) inicio.clone();
        fim.add(Calendar.DAY_OF_MONTH, 1);
        long fimDia = fim.getTimeInMillis();

        db.registroDoseDao()
                .contarPorStatusNoPeriodo(StatusDose.TOMADO, inicioDia, fimDia)
                .observe(this, quantidade -> txtTomadosHoje.setText(
                        String.valueOf(quantidade == null ? 0 : quantidade)));

        db.registroDoseDao()
                .contarPorStatusNoPeriodo(StatusDose.PENDENTE, inicioDia, fimDia)
                .observe(this, quantidade -> txtPendentesHoje.setText(
                        String.valueOf(quantidade == null ? 0 : quantidade)));

        db.registroDoseDao().proximaDosePendente(System.currentTimeMillis())
                .observe(this, proximaDose -> {
                    // A exibição completa do próximo lembrete depende da associação
                    // entre dose, medicamento e horário, que será implementada na etapa de agenda.
                    if (proximaDose == null) {
                        txtProximoLembrete.setText(
                                "Nenhum lembrete programado. Configure os horários dos medicamentos.");
                    }
                });

        Button btnAdicionar = findViewById(R.id.btnAdicionar);
        btnAdicionar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CadastroMedicamentoActivity.class);
            startActivity(intent);
        });
    }

    private void mostrarMedicamentos(List<Medicamento> medicamentos) {
        listaMedicamentos.removeAllViews();

        int quantidade = medicamentos == null ? 0 : medicamentos.size();
        txtTotalMedicamentos.setText(quantidade + (quantidade == 1 ? " total" : " total"));
        txtQuantidadeMedicamentos.setText(String.valueOf(quantidade));

        if (quantidade == 0) {
            TextView vazio = new TextView(this);
            vazio.setText("Você ainda não cadastrou medicamentos. Toque em “Adicionar medicamento” para começar.");
            vazio.setTextColor(Color.rgb(100, 116, 139));
            vazio.setTextSize(15);
            vazio.setPadding(16, 24, 16, 24);
            listaMedicamentos.addView(vazio);
            return;
        }

        for (Medicamento medicamento : medicamentos) {
            listaMedicamentos.addView(criarCartaoMedicamento(medicamento));
        }
    }

    private View criarCartaoMedicamento(Medicamento medicamento) {
        LinearLayout cartao = new LinearLayout(this);
        cartao.setOrientation(LinearLayout.VERTICAL);
        cartao.setPadding(dp(16), dp(14), dp(16), dp(14));
        cartao.setBackgroundResource(R.drawable.bg_card_medicamento);

        LinearLayout.LayoutParams parametros = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        parametros.bottomMargin = dp(12);
        cartao.setLayoutParams(parametros);

        TextView nome = new TextView(this);
        nome.setText(medicamento.getNome());
        nome.setTextColor(Color.rgb(30, 41, 59));
        nome.setTextSize(18);
        cartao.addView(nome);

        String dosagem = medicamento.getDosagem() == null
                ? "" : medicamento.getDosagem().trim();
        String quantidade = medicamento.getQuantidadePorDose() + " por dose";
        TextView detalhes = new TextView(this);
        detalhes.setText(dosagem.isEmpty() ? quantidade : dosagem + " — " + quantidade);
        detalhes.setTextColor(Color.rgb(30, 41, 59));
        detalhes.setTextSize(15);
        detalhes.setPadding(0, dp(8), 0, dp(4));
        cartao.addView(detalhes);

        TextView periodo = new TextView(this);
        if (medicamento.getDataFim() == null) {
            periodo.setText("Uso contínuo");
        } else {
            String dataFim = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(medicamento.getDataFim());
            periodo.setText("Tratamento até " + dataFim);
        }
        periodo.setTextColor(Color.rgb(46, 73, 255));
        periodo.setTextSize(14);
        cartao.addView(periodo);

        return cartao;
    }

    private void atualizarCabecalho(TextView saudacao, TextView data) {
        Calendar agora = Calendar.getInstance();
        int hora = agora.get(Calendar.HOUR_OF_DAY);
        String mensagem = hora < 12 ? "Bom dia" : (hora < 18 ? "Boa tarde" : "Boa noite");
        saudacao.setText(mensagem);
        data.setText(new SimpleDateFormat("EEEE, dd 'de' MMMM", new Locale("pt", "BR"))
                .format(agora.getTime()));
    }

    private int dp(int valor) {
        return (int) (valor * getResources().getDisplayMetrics().density + 0.5f);
    }
}
