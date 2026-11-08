package com.example.medilembrete;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class LembreteReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        NotificacaoUtils.criarCanal(context);
        long medicamentoId = intent.getLongExtra("medicamento_id", -1);
        String nome = intent.getStringExtra("nome");
        String dosagem = intent.getStringExtra("dosagem");
        String horario = intent.getStringExtra("horario");

        Intent abrir = new Intent(context, MainActivity.class);
        abrir.putExtra("medicamento_id", medicamentoId);
        abrir.putExtra("abrir_lembrete", true);
        abrir.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent toque = PendingIntent.getActivity(context, (int) medicamentoId,
                abrir, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        String texto = (dosagem == null || dosagem.trim().isEmpty()) ? "Está na hora de tomar seu medicamento"
                : "Dosagem: " + dosagem;
        NotificationCompat.Builder notificacao = new NotificationCompat.Builder(context, NotificacaoUtils.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_medication)
                .setContentTitle("Hora do medicamento: " + (nome == null ? "MedLembrete" : nome))
                .setContentText(texto)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(texto + " • Horário: " + horario))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(true)
                .setContentIntent(toque);

        try {
            NotificationManagerCompat.from(context).notify((int) (medicamentoId * 31 + (horario == null ? 0 : horario.hashCode())),
                    notificacao.build());
        } catch (SecurityException permissaoNegada) { }
        AgendadorNotificacaoProxima.reagendar(context, intent);
    }
}