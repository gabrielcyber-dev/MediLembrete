package com.example.medilembrete;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.medilembrete.data.AppDatabase;
import com.example.medilembrete.data.entity.Horario;
import com.example.medilembrete.data.entity.Medicamento;

import java.util.Calendar;
import java.util.List;

public final class AgendadorNotificacoes {
    private AgendadorNotificacoes() {}

    public static void reagendarTodos(Context context) {
        AppDatabase.executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(context);
            Calendar agora = Calendar.getInstance();
            long inicio = inicioDia(agora);
            Calendar fimCal = (Calendar) agora.clone();
            fimCal.add(Calendar.DAY_OF_MONTH, 2);
            List<Medicamento> medicamentos =
                    db.medicamentoDao().listarAtivosNoPeriodoSync(inicio, fimCal.getTimeInMillis());

            for (Medicamento medicamento : medicamentos) {
                List<Horario> horarios =
                        db.horarioDao().listarPorMedicamentoSync(medicamento.getId());
                for (Horario horario : horarios) {
                    try {
                        String[] partes = horario.getHorario().split(":");
                        Calendar proximo = (Calendar) agora.clone();
                        proximo.set(Calendar.HOUR_OF_DAY, Integer.parseInt(partes[0]));
                        proximo.set(Calendar.MINUTE, Integer.parseInt(partes[1]));
                        proximo.set(Calendar.SECOND, 0);
                        proximo.set(Calendar.MILLISECOND, 0);
                        if (proximo.getTimeInMillis() <= System.currentTimeMillis()) {
                            proximo.add(Calendar.DAY_OF_MONTH, 1);
                        }
                        if (medicamento.getDataFim() != null &&
                                proximo.getTimeInMillis() > medicamento.getDataFim()) continue;
                        agendar(context, medicamento, horario.getHorario(), proximo.getTimeInMillis());
                    } catch (Exception ignorado) { }
                }
            }
        });
    }

    static void agendar(Context context, Medicamento medicamento, String horario, long instante) {
        Intent intent = new Intent(context, LembreteReceiver.class);
        intent.putExtra("medicamento_id", medicamento.getId());
        intent.putExtra("nome", medicamento.getNome());
        intent.putExtra("dosagem", medicamento.getDosagem() == null ? "" : medicamento.getDosagem());
        intent.putExtra("horario", horario);
        int requestCode = (medicamento.getId() + "|" + horario).hashCode();
        PendingIntent pending = PendingIntent.getBroadcast(context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarm.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, instante, pending);
            } else {
                alarm.set(AlarmManager.RTC_WAKEUP, instante, pending);
            }
        }
    }

    private static long inicioDia(Calendar c) {
        Calendar dia = (Calendar) c.clone();
        dia.set(Calendar.HOUR_OF_DAY, 0);
        dia.set(Calendar.MINUTE, 0);
        dia.set(Calendar.SECOND, 0);
        dia.set(Calendar.MILLISECOND, 0);
        return dia.getTimeInMillis();
    }
}