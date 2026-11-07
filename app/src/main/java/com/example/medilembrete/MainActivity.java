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
                    } else {
                        String horario = new SimpleDateFormat("HH:mm", Locale.getDefault())
                                .format(proximaDose.getDataHoraProgramada());
                        txtProximoLembrete.setText("Próxima dose programada para " + horario);
                    }
                });

        findViewById(R.id.menuMedicamentos).setOnClickListener(v -> startActivity(new Intent(this, MeusMedicamentosActivity.class)));
        findViewById(R.id.menuHistorico).setOnClickListener(v -> startActivity(new Intent(this, HistoricoActivity.class)));

        Button btnAdicionar = findViewById(R.id.btnAdicionar);
        btnAdicionar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CadastroMedicamentoActivity.class);
            startActivity(intent);
        });
    }

    private void mostrarMedicamentos(List<Medicamento> medicamentos) {
        listaMedicamentos.removeAllViews();

        int quantidade = medicamentos == null ? 0 : medicamentos.size();
        txtTotalMedicamentos.setText(quantidade + " total");
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
        cartao.setOrientation(LinearLayout.HORIZONTAL);
        cartao.setGravity(android.view.Gravity.CENTER_VERTICAL);
        cartao.setPadding(dp(14), dp(14), dp(12), dp(14));
        android.graphics.drawable.GradientDrawable fundo = new android.graphics.drawable.GradientDrawable();
        fundo.setColor(Color.WHITE);
        fundo.setCornerRadius(dp(16));
        fundo.setStroke(dp(1), Color.rgb(226, 232, 240));
        cartao.setBackground(fundo);
        LinearLayout.LayoutParams parametros = new LinearLayout.LayoutParams(-1, -2);
        parametros.bottomMargin = dp(10);
        cartao.setLayoutParams(parametros);

        TextView icone = new TextView(this);
        String forma = medicamento.getForma() == null ? "" : medicamento.getForma();
        String simbolo = forma.equals("Comprimido") || forma.equals("Cápsula") ? "💊"
                : (forma.equals("Líquido") ? "🧪" : "🧴");
        icone.setText(simbolo);
        icone.setTextSize(25);
        icone.setTypeface(null, 1);
        icone.setTextColor(Color.parseColor(medicamento.getCorIdentificacao()));
        icone.setGravity(android.view.Gravity.CENTER);
        android.graphics.drawable.GradientDrawable fundoIcone = new android.graphics.drawable.GradientDrawable();
        fundoIcone.setColor(Color.parseColor(medicamento.getCorIdentificacao()));
        fundoIcone.setAlpha(30);
        fundoIcone.setCornerRadius(dp(13));
        icone.setBackground(fundoIcone);
        cartao.addView(icone, new LinearLayout.LayoutParams(dp(52), dp(52)));

        LinearLayout informacoes = new LinearLayout(this);
        informacoes.setOrientation(LinearLayout.VERTICAL);
        informacoes.setPadding(dp(12), 0, 0, 0);
        cartao.addView(informacoes, new LinearLayout.LayoutParams(0, -2, 1));

        TextView nome = new TextView(this);
        nome.setText(medicamento.getNome());
        nome.setTextColor(Color.rgb(15, 23, 42));
        nome.setTextSize(16);
        nome.setTypeface(null, 1);
        informacoes.addView(nome);

        String dosagem = medicamento.getDosagem() == null ? "" : medicamento.getDosagem();
        TextView detalhes = new TextView(this);
        detalhes.setText(dosagem + " · " + medicamento.getQuantidadePorDose() + " por dose");
        detalhes.setTextColor(Color.rgb(100, 116, 139));
        detalhes.setTextSize(13);
        detalhes.setPadding(0, dp(3), 0, dp(5));
        informacoes.addView(detalhes);

        TextView horario = new TextView(this);
        String frequencia = medicamento.getIntervaloHoras() == 24 ? "1 vez ao dia"
                : medicamento.getIntervaloHoras() + " em " + medicamento.getIntervaloHoras() + " horas";
        horario.setText(medicamento.getHorarioInicial() + "  ·  " + frequencia);
        horario.setTextColor(Color.rgb(26, 79, 191));
        horario.setTextSize(12);
        informacoes.addView(horario);

        TextView seta = new TextView(this);
        seta.setText("›");
        seta.setTextColor(Color.rgb(203, 213, 225));
        seta.setTextSize(25);
        cartao.addView(seta);

        cartao.setOnClickListener(v -> {
            Intent intent = new Intent(this, DetalheMedicamentoActivity.class);
            intent.putExtra("medicamento_id", medicamento.getId());
            startActivity(intent);
        });
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
