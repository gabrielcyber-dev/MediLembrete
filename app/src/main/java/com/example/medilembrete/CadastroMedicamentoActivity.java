package com.example.medilembrete;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.medilembrete.data.AppDatabase;
import com.example.medilembrete.data.MedicamentoRepository;
import com.example.medilembrete.data.entity.Medicamento;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CadastroMedicamentoActivity extends AppCompatActivity {
    private EditText etNome, etDosagem, etQuantidade, etDuracaoDias, etObservacoes;
    private TextView tvHorarioInicial, tvUnidadeQuantidade;
    private LinearLayout tvPreviaHorarios;
    private RadioGroup rgForma, rgFrequencia, rgCor;
    private CheckBox cbContinuo;
    private Button btnSalvar;
    private String horarioInicial = "08:00";
    private MedicamentoRepository repository;

    private final String[] cores = {"#FF3E00", "#7902E8", "#10F0FF", "#8EEB10", "#FF980F"};
    private final String[] formas = {"Comprimido", "Cápsula", "Líquido", "Loção", "Spray"};
    private final int[] intervalos = {6, 8, 12, 24};

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_medicamento);
        etNome=findViewById(R.id.etNome); etDosagem=findViewById(R.id.etDosagem);
        etQuantidade=findViewById(R.id.etQuantidade); etDuracaoDias=findViewById(R.id.etDuracaoDias);
        etObservacoes=findViewById(R.id.etObservacoes); rgForma=findViewById(R.id.rgForma);
        rgFrequencia=findViewById(R.id.rgFrequencia); rgCor=findViewById(R.id.rgCor);
        tvHorarioInicial=findViewById(R.id.tvHorarioInicial); tvPreviaHorarios=findViewById(R.id.tvPreviaHorarios);
        tvUnidadeQuantidade=findViewById(R.id.tvUnidadeQuantidade); cbContinuo=findViewById(R.id.cbContinuo);
        btnSalvar=findViewById(R.id.btnSalvar);
        repository=new MedicamentoRepository(AppDatabase.getInstance(this));

        tvHorarioInicial.setOnClickListener(v -> abrirSeletorHorario());
        rgForma.setOnCheckedChangeListener((g,id) -> atualizarUnidade());
        rgFrequencia.setOnCheckedChangeListener((g,id) -> atualizarPrevia());
        cbContinuo.setOnCheckedChangeListener((b,checked) -> {
            etDuracaoDias.setEnabled(!checked);
            if (checked) etDuracaoDias.setText("");
        });
        atualizarUnidade(); atualizarPrevia();
        btnSalvar.setOnClickListener(v -> salvar());
        findViewById(R.id.btnVoltar).setOnClickListener(v -> finish());
    }

    private void abrirSeletorHorario() {
        Calendar c=Calendar.getInstance();
        new TimePickerDialog(this,(view,hour,minute)->{
            horarioInicial=String.format(Locale.getDefault(),"%02d:%02d",hour,minute);
            tvHorarioInicial.setText(horarioInicial); atualizarPrevia();
        },c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),true).show();
    }

    private int intervaloSelecionado() {
        int id=rgFrequencia.getCheckedRadioButtonId();
        if(id==R.id.freq6)return 6; if(id==R.id.freq8)return 8;
        if(id==R.id.freq12)return 12; return 24;
    }
    private String formaSelecionada() {
        int id=rgForma.getCheckedRadioButtonId();
        if(id==R.id.formaCapsula)return formas[1]; if(id==R.id.formaLiquido)return formas[2];
        if(id==R.id.formaLocao)return formas[3]; if(id==R.id.formaSpray)return formas[4];
        return formas[0];
    }
    private String unidadeSelecionada() {
        String f=formaSelecionada();
        if(f.equals("Comprimido"))return "comprimidos";
        if(f.equals("Cápsula"))return "cápsulas";
        if(f.equals("Líquido"))return "ml";
        if(f.equals("Loção"))return "aplicações";
        return "aplicações";
    }
    private void atualizarUnidade() {
        if(tvUnidadeQuantidade!=null) tvUnidadeQuantidade.setText("Quantidade por dose ("+unidadeSelecionada()+") *");
    }
    private String corSelecionada() {
        int id=rgCor.getCheckedRadioButtonId();
        if(id==R.id.corRoxo)return cores[1]; if(id==R.id.corCiano)return cores[2];
        if(id==R.id.corVerde)return cores[3]; if(id==R.id.corLaranjaClaro)return cores[4];
        return cores[0];
    }
    private List<String> calcularHorarios() {
        List<String> lista=new ArrayList<>();
        String[] partes=horarioInicial.split(":");
        int minutoInicial=Integer.parseInt(partes[0])*60+Integer.parseInt(partes[1]);
        int intervalo=intervaloSelecionado()*60;
        int quantidade=24/intervaloSelecionado();
        for(int i=0;i<quantidade;i++){
            int total=(minutoInicial+i*intervalo)%1440;
            lista.add(String.format(Locale.getDefault(),"%02d:%02d",total/60,total%60));
        }
        return lista;
    }
    private void atualizarPrevia() {
        if (tvPreviaHorarios == null) return;
        tvPreviaHorarios.removeAllViews();
        List<String> horarios = calcularHorarios();
        for (String horario : horarios) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setGravity(android.view.Gravity.CENTER);
            card.setPadding(dp(8), dp(10), dp(8), dp(10));
            GradientDrawable fundo = new GradientDrawable();
            fundo.setColor(Color.WHITE);
            fundo.setCornerRadius(dp(12));
            fundo.setStroke(dp(1), Color.rgb(219, 229, 246));
            card.setBackground(fundo);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dp(58), 1f);
            params.setMargins(dp(3), 0, dp(3), 0);
            tvPreviaHorarios.addView(card, params);

            TextView hora = new TextView(this);
            hora.setText(horario);
            hora.setTextColor(Color.rgb(26, 79, 191));
            hora.setTextSize(15);
            hora.setTypeface(null, 1);
            card.addView(hora);
        }
    }

    private int dp(int valor) {
        return (int) (valor * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void salvar() {
        String nome=etNome.getText().toString().trim();
        String dosagem=etDosagem.getText().toString().trim();
        String quantidadeTexto=etQuantidade.getText().toString().trim();
        String duracaoTexto=etDuracaoDias.getText().toString().trim();
        if(nome.isEmpty()){etNome.setError("Informe o nome");return;}
        if(dosagem.isEmpty()){etDosagem.setError("Informe a dosagem");return;}
        int quantidade;
        try { quantidade=Integer.parseInt(quantidadeTexto); if(quantidade<=0)throw new NumberFormatException(); }
        catch(NumberFormatException e){etQuantidade.setError("Informe uma quantidade inteira maior que zero");return;}
        Integer dias=null; Long dataFim=null; long inicio=System.currentTimeMillis();
        if(!cbContinuo.isChecked()){
            try { dias=Integer.parseInt(duracaoTexto); if(dias<=0)throw new NumberFormatException(); }
            catch(NumberFormatException e){etDuracaoDias.setError("Informe a duração em dias ou marque contínua");return;}
            dataFim=inicio+TimeUnit.DAYS.toMillis(dias);
        }
        String observacoes=etObservacoes.getText().toString().trim();
        Medicamento med=new Medicamento(nome,dosagem,quantidade,inicio,dataFim,
                observacoes.isEmpty()?null:observacoes,formaSelecionada(),corSelecionada(),
                horarioInicial,intervaloSelecionado(),dias);
        btnSalvar.setEnabled(false);
        try {
            repository.cadastrar(med,calcularHorarios(),id->runOnUiThread(()->{
                Toast.makeText(this,"Medicamento salvo!",Toast.LENGTH_SHORT).show();
                AgendadorNotificacoes.reagendarTodos(this);
                setResult(RESULT_OK); finish();
            }));
        } catch(IllegalArgumentException e) {
            btnSalvar.setEnabled(true); Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
}
