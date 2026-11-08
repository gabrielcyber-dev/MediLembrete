package com.example.medilembrete;

import android.content.Context;
import android.content.Intent;

import com.example.medilembrete.data.AppDatabase;
import com.example.medilembrete.data.entity.Medicamento;

import java.util.Calendar;

final class AgendadorNotificacaoProxima {
    private AgendadorNotificacaoProxima() {}

    static void reagendar(Context context, Intent intent) {
        long id = intent.getLongExtra("medicamento_id", -1);
        String horario = intent.getStringExtra("horario");
        if (id < 0 || horario == null) return;

        AppDatabase.executor.execute(() -> {
            Medicamento medicamento = AppDatabase.getInstance(context)
                    .medicamentoDao().buscarPorIdSync(id);
            if (medicamento == null) return; // foi excluído
            Calendar proximo = Calendar.getInstance();
            String[] partes = horario.split(":");
            try {
                proximo.set(Calendar.HOUR_OF_DAY, Integer.parseInt(partes[0]));
                proximo.set(Calendar.MINUTE, Integer.parseInt(partes[1]));
                proximo.set(Calendar.SECOND, 0);
                proximo.set(Calendar.MILLISECOND, 0);
                proximo.add(Calendar.DAY_OF_MONTH, 1);
            } catch (Exception e) { return; }
            if (medicamento.getDataFim() != null &&
                    proximo.getTimeInMillis() > medicamento.getDataFim()) return;

            Intent novo = new Intent(context, LembreteReceiver.class);
            novo.putExtra("medicamento_id", id);
            novo.putExtra("nome", medicamento.getNome());
            novo.putExtra("dosagem", medicamento.getDosagem() == null ? "" : medicamento.getDosagem());
            novo.putExtra("horario", horario);
            int requestCode = (id + "|" + horario).hashCode();
            android.app.PendingIntent pi = android.app.PendingIntent.getBroadcast(context,
                    requestCode, novo, android.app.PendingIntent.FLAG_UPDATE_CURRENT |
                            android.app.PendingIntent.FLAG_IMMUTABLE);
            android.app.AlarmManager am = (android.app.AlarmManager)
                    context.getSystemService(Context.ALARM_SERVICE);
            if (am != null) am.setAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP,
                    proximo.getTimeInMillis(), pi);
        });
    }
}