package com.example.medilembrete;
import androidx.appcompat.app.AppCompatActivity; import android.os.Bundle; import android.widget.TextView; import android.widget.Button; import android.view.View; import android.graphics.Color; import android.graphics.drawable.GradientDrawable; import android.widget.LinearLayout; import android.content.DialogInterface; import androidx.appcompat.app.AlertDialog;
import com.example.medilembrete.data.AppDatabase; import com.example.medilembrete.data.entity.Medicamento; import com.example.medilembrete.data.entity.Horario; import java.text.SimpleDateFormat; import java.util.List; import java.util.Locale;
public class DetalheMedicamentoActivity extends AppCompatActivity {
 private AppDatabase db; private long id; private Medicamento medicamentoAtual;
 @Override protected void onCreate(Bundle b){super.onCreate(b);setContentView(R.layout.activity_detalhe_medicamento); id=getIntent().getLongExtra("medicamento_id",-1); db=AppDatabase.getInstance(this);
 findViewById(R.id.btnVoltarDetalhe).setOnClickListener(v->finish());
 db.medicamentoDao().buscarPorId(id).observe(this,m->{if(m!=null){medicamentoAtual=m;preencher(m);}});
 db.horarioDao().listarPorMedicamento(id).observe(this,hs->{StringBuilder s=new StringBuilder("Horários: "); if(hs!=null)for(int i=0;i<hs.size();i++){if(i>0)s.append("  ·  ");s.append(hs.get(i).getHorario());} ((TextView)findViewById(R.id.txtHorariosDetalhe)).setText(hs==null||hs.isEmpty()?"":android.text.TextUtils.join("   ",hs.stream().map(Horario::getHorario).toArray()));});
 findViewById(R.id.btnExcluirDetalhe).setOnClickListener(v->new AlertDialog.Builder(this).setTitle("Excluir medicamento?").setMessage("Essa ação não poderá ser desfeita.").setNegativeButton("Cancelar",null).setPositiveButton("Excluir",(d,w)->AppDatabase.executor.execute(()->{if(medicamentoAtual!=null)db.medicamentoDao().excluir(medicamentoAtual); runOnUiThread(this::finish);})).show());
 }
 private void preencher(Medicamento m){
  int cor;
  try { cor=Color.parseColor(m.getCorIdentificacao()); } catch(Exception e) { cor=Color.rgb(255,62,0); }
  findViewById(R.id.headerDetalhe).setBackgroundColor(cor);
  ((TextView)findViewById(R.id.txtNomeDetalhe)).setText(m.getNome());
  ((TextView)findViewById(R.id.txtDosagemDetalhe)).setText(m.getDosagem()+" — "+m.getQuantidadePorDose()+" por dose");
  ((TextView)findViewById(R.id.txtFormaDetalhe)).setText(m.getForma());
  ((TextView)findViewById(R.id.txtFrequenciaDetalhe)).setText(m.getIntervaloHoras()==24?"1 vez ao dia":m.getIntervaloHoras()+" em "+m.getIntervaloHoras()+" horas");
  ((TextView)findViewById(R.id.txtHorarioInicioDetalhe)).setText(m.getHorarioInicial());
  ((TextView)findViewById(R.id.txtDuracaoDetalhe)).setText(m.getDataFim()==null?"Contínuo":new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault()).format(new java.util.Date(m.getDataFim())));
  ((TextView)findViewById(R.id.txtObservacoesDetalhe)).setText(m.getObservacoes()==null||m.getObservacoes().isEmpty()?"Não há observações cadastradas.":m.getObservacoes());
 }
}