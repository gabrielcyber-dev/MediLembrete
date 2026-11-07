package com.example.medilembrete;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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

public class MainActivity extends AppCompatActivity {
    private LinearLayout listaMedicamentos;
    private TextView txtTotalMedicamentos, txtQuantidadeMedicamentos, txtTomadosHoje, txtPendentesHoje, txtProximoLembrete;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listaMedicamentos=findViewById(R.id.listaMedicamentos);
        txtTotalMedicamentos=findViewById(R.id.txtTotalMedicamentos);
        txtQuantidadeMedicamentos=findViewById(R.id.txtQuantidadeMedicamentos);
        txtTomadosHoje=findViewById(R.id.txtTomadosHoje);
        txtPendentesHoje=findViewById(R.id.txtPendentesHoje);
        txtProximoLembrete=findViewById(R.id.txtProximoLembrete);
        atualizarCabecalho(findViewById(R.id.txtSaudacao),findViewById(R.id.txtDataAtual));
        AppDatabase db=AppDatabase.getInstance(this);
        db.medicamentoDao().listarTodos().observe(this,this::mostrarMedicamentos);
        Calendar inicio=Calendar.getInstance(); inicio.set(Calendar.HOUR_OF_DAY,0); inicio.set(Calendar.MINUTE,0); inicio.set(Calendar.SECOND,0); inicio.set(Calendar.MILLISECOND,0);
        long inicioDia=inicio.getTimeInMillis(); Calendar fim=(Calendar)inicio.clone(); fim.add(Calendar.DAY_OF_MONTH,1); long fimDia=fim.getTimeInMillis();
        db.registroDoseDao().contarPorStatusNoPeriodo(StatusDose.TOMADO,inicioDia,fimDia).observe(this,n->txtTomadosHoje.setText(String.valueOf(n==null?0:n)));
        db.registroDoseDao().contarPorStatusNoPeriodo(StatusDose.PENDENTE,inicioDia,fimDia).observe(this,n->txtPendentesHoje.setText(String.valueOf(n==null?0:n)));
        db.registroDoseDao().proximaDosePendente(System.currentTimeMillis()).observe(this,dose->{
            if(dose==null) txtProximoLembrete.setText("Nenhum lembrete programado.");
            else txtProximoLembrete.setText(new SimpleDateFormat("HH:mm",Locale.getDefault()).format(dose.getDataHoraProgramada()));
        });
        findViewById(R.id.btnAdicionar).setOnClickListener(v->startActivity(new Intent(this,CadastroMedicamentoActivity.class)));
        findViewById(R.id.menuMedicamentos).setOnClickListener(v->startActivity(new Intent(this,MeusMedicamentosActivity.class)));
        findViewById(R.id.menuHistorico).setOnClickListener(v->startActivity(new Intent(this,HistoricoActivity.class)));
    }
    private void mostrarMedicamentos(List<Medicamento> meds) {
        listaMedicamentos.removeAllViews(); int qtd=meds==null?0:meds.size();
        txtTotalMedicamentos.setText(qtd+" total"); txtQuantidadeMedicamentos.setText(String.valueOf(qtd));
        if(qtd==0){ TextView vazio=new TextView(this); vazio.setText("Nenhum medicamento cadastrado ainda. Adicione seu primeiro medicamento para começar."); vazio.setTextColor(Color.rgb(100,116,139)); vazio.setTextSize(14); vazio.setPadding(dp(18),dp(22),dp(18),dp(22)); listaMedicamentos.addView(vazio); return; }
        for(Medicamento m:meds) listaMedicamentos.addView(criarCartaoMedicamento(m));
    }
    private View criarCartaoMedicamento(Medicamento m) {
        LinearLayout card=new LinearLayout(this); card.setOrientation(LinearLayout.HORIZONTAL); card.setGravity(android.view.Gravity.CENTER_VERTICAL); card.setPadding(dp(14),dp(14),dp(12),dp(14));
        GradientDrawable bg=new GradientDrawable(); bg.setColor(Color.WHITE); bg.setCornerRadius(dp(16)); bg.setStroke(dp(1),Color.rgb(226,232,240)); card.setBackground(bg);
        LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2); cp.bottomMargin=dp(10); card.setLayoutParams(cp);
        TextView icon=new TextView(this); icon.setText("＋"); icon.setTextSize(23); icon.setTextColor(Color.parseColor(m.getCorIdentificacao())); icon.setGravity(android.view.Gravity.CENTER);
        GradientDrawable ib=new GradientDrawable(); ib.setColor(Color.parseColor(m.getCorIdentificacao()) & 0x22FFFFFF | 0x18000000); ib.setColor(Color.parseColor(m.getCorIdentificacao())); ib.setAlpha(28); ib.setCornerRadius(dp(13)); icon.setBackground(ib);
        LinearLayout.LayoutParams ip=new LinearLayout.LayoutParams(dp(52),dp(52)); card.addView(icon,ip);
        LinearLayout info=new LinearLayout(this); info.setOrientation(LinearLayout.VERTICAL); info.setPadding(dp(12),0,0,0); card.addView(info,new LinearLayout.LayoutParams(0,-2,1));
        TextView nome=new TextView(this); nome.setText(m.getNome()); nome.setTextColor(Color.rgb(15,23,42)); nome.setTextSize(16); nome.setTypeface(null,1); info.addView(nome);
        TextView dose=new TextView(this); dose.setText((m.getDosagem()==null?"":m.getDosagem())+" · "+m.getQuantidadePorDose()+" por dose"); dose.setTextColor(Color.rgb(100,116,139)); dose.setTextSize(13); dose.setPadding(0,dp(3),0,dp(5)); info.addView(dose);
        TextView hora=new TextView(this); hora.setText(m.getHorarioInicial()+"   ·   "+(m.getIntervaloHoras()==24?"1 vez ao dia":m.getIntervaloHoras()+" em "+m.getIntervaloHoras()+" horas")); hora.setTextColor(Color.rgb(26,79,191)); hora.setTextSize(12); info.addView(hora);
        TextView seta=new TextView(this); seta.setText("›"); seta.setTextSize(24); seta.setTextColor(Color.LTGRAY); card.addView(seta);
        card.setOnClickListener(v->{Intent i=new Intent(this,DetalheMedicamentoActivity.class); i.putExtra("medicamento_id",m.getId()); startActivity(i);});
        return card;
    }
    private void atualizarCabecalho(TextView saudacao,TextView data){Calendar c=Calendar.getInstance();int h=c.get(Calendar.HOUR_OF_DAY);saudacao.setText((h<12?"Bom dia":h<18?"Boa tarde":"Boa noite")+" 👋");data.setText(new SimpleDateFormat("EEEE, d 'de' MMMM",new Locale("pt","BR")).format(c.getTime()));}
    private int dp(int v){return (int)(v*getResources().getDisplayMetrics().density+0.5f);}
}