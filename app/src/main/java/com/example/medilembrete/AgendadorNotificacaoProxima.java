package com.example.medilembrete;

import android.content.Context;

import com.example.medilembrete.data.entity.Medicamento;

import java.util.Calendar;

final class AgendadorNotificacaoProxima {
    private AgendadorNotificacaoProxima() {}
    static void reagendar(Context context, android.content.Intent intent) {
        long id = intent.getLongExtra("medicamento_id", -1);
        String nome = intent.getStringExtra("nome");
        String dosagem = intent.getStringExtra("dosagem");
        String horario = intent.getStringExtra("horario");
        if (id < 0 || horario == null) return;
        Calendar proximo = Calendar.getInstance();
        String[] p = horario.split(":");
        try {
            proximo.set(Calendar.HOUR_OF_DAY, Integer.parseInt(p[0]));
            proximo.set(Calendar.MINUTE, Integer.parseInt(p[1]));
            proximo.set(Calendar.SECOND, 0);
            proximo.set(Calendar.MILLISECOND, 0);
            proximo.add(Calendar.DAY_OF_MONTH, 1);
        } catch (Exception e) { return; }

        // Reutiliza o mesmo PendingIntent; mantém o lembrete diário até o tratamento ser alterado.
        android.content.Intent novo = new android.content.Intent(context, LembreteReceiver.class);
        novo.putExtra("medicamento_id", id);
        novo.putExtra("nome", nome);
        novo.putExtra("dosagem", dosagem);
        novo.putExtra("horario", horario);
        int requestCode = (id + "|" + horario).hashCode();
        android.app.PendingIntent pi = android.app.PendingIntent.getBroadcast(context, requestCode, novo,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE);
        android.app.AlarmManager am = (android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am != null) am.setAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, proximo.getTimeInMillis(), pi);
    }
}