package com.example.medilembrete;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public final class NotificacaoUtils {
    public static final String CHANNEL_ID = "medicamentos";
    private NotificacaoUtils() {}

    public static void criarCanal(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    CHANNEL_ID, "Lembretes de medicamentos",
                    NotificationManager.IMPORTANCE_HIGH);
            canal.setDescription("Avisos nos horários dos medicamentos");
            canal.enableVibration(true);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(canal);
        }
    }
}