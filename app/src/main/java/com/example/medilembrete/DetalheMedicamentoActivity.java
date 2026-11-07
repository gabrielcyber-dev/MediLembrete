package com.example.medilembrete;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import com.example.medilembrete.data.AppDatabase;
import com.example.medilembrete.data.entity.Medicamento;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DetalheMedicamentoActivity extends AppCompatActivity {
    private AppDatabase db;
    private long id;
    private Medicamento medicamentoAtual;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_detalhe_medicamento);
        id=getIntent().getLongExtra("medicamento_id",-1);
        db=AppDatabase.getInstance(this);
        findViewById(R.id.btnVoltarDetalhe).setOnClickListener(v->finish());

        db.medicamentoDao().buscarPorId(id).observe(this,m->{
            if(m!=null){medicamentoAtual=m;preencher(m);}
        });
        db.horarioDao().listarPorMedicamento(id).observe(this,hs->{
            TextView horarios=findViewById(R.id.txtHorariosDetalhe);
            if(hs==null||hs.isEmpty()){horarios.setText("");return;}
            StringBuilder texto=new StringBuilder("Horários: ");
            for(int i=0;i<hs.size();i++){
                if(i>0)texto.append("  ·  ");
                texto.append(hs.get(i).getHorario());
            }
            horarios.setText(texto.toString());
        });

        findViewById(R.id.btnEditarDetalhe).setOnClickListener(v->{
            Intent intent=new Intent(this,CadastroMedicamentoActivity.class);
            intent.putExtra("medicamento_id",id);
            startActivity(intent);
        });
        findViewById(R.id.btnExcluirDetalhe).setOnClickListener(v->
            new AlertDialog.Builder(this)
                .setTitle("Excluir medicamento?")
                .setMessage("Essa ação não poderá ser desfeita.")
                .setNegativeButton("Cancelar",null)
                .setPositiveButton("Excluir",(dialog,which)->AppDatabase.executor.execute(()->{
                    if(medicamentoAtual!=null)db.medicamentoDao().excluir(medicamentoAtual);
                    runOnUiThread(this::finish);
                })).show());
    }

    private void preencher(Medicamento m) {
        int cor;
        try{cor=Color.parseColor(m.getCorIdentificacao());}
        catch(Exception e){cor=Color.rgb(255,62,0);}
        findViewById(R.id.headerDetalhe).setBackgroundColor(cor);
        ((TextView)findViewById(R.id.txtNomeDetalhe)).setText(m.getNome());
        String unidade="aplicações";
        if("Comprimido".equals(m.getForma()))unidade=m.getQuantidadePorDose()==1?"comprimido":"comprimidos";
        else if("Cápsula".equals(m.getForma()))unidade=m.getQuantidadePorDose()==1?"cápsula":"cápsulas";
        else if("Líquido".equals(m.getForma()))unidade="ml";
        ((TextView)findViewById(R.id.txtDosagemDetalhe)).setText(m.getDosagem()+" — "+m.getQuantidadePorDose()+" "+unidade);
        ((TextView)findViewById(R.id.txtFormaDetalhe)).setText(m.getForma());
        ((TextView)findViewById(R.id.txtFrequenciaDetalhe)).setText(m.getIntervaloHoras()==24?"1 vez ao dia":m.getIntervaloHoras()+" em "+m.getIntervaloHoras()+" horas");
        ((TextView)findViewById(R.id.txtHorarioInicioDetalhe)).setText(m.getHorarioInicial());
        String duracao=m.getDataFim()==null?"Contínuo":(m.getDuracaoDias()==null
                ?new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault()).format(new java.util.Date(m.getDataFim()))
                :m.getDuracaoDias()+" dias");
        ((TextView)findViewById(R.id.txtDuracaoDetalhe)).setText(duracao);
        ((TextView)findViewById(R.id.txtObservacoesDetalhe)).setText(m.getObservacoes()==null||m.getObservacoes().isEmpty()?"Não há observações cadastradas.":m.getObservacoes());
    }
}
