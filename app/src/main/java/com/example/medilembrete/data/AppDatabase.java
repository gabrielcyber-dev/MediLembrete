package com.example.medilembrete.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.medilembrete.data.dao.HorarioDao;
import com.example.medilembrete.data.dao.MedicamentoDao;
import com.example.medilembrete.data.dao.RegistroDoseDao;
import com.example.medilembrete.data.entity.Horario;
import com.example.medilembrete.data.entity.Medicamento;
import com.example.medilembrete.data.entity.RegistroDose;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Ponto único de acesso ao banco SQLite do app. Aumente "version" e forneça
 * uma Migration sempre que o esquema mudar depois que o app já estiver em uso
 * (sem isso, o Room apaga e recria o banco ao detectar uma versão diferente).
 */
@Database(
        entities = {Medicamento.class, Horario.class, RegistroDose.class},
        version = 1,
        exportSchema = false
)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {

    public abstract MedicamentoDao medicamentoDao();

    public abstract HorarioDao horarioDao();

    public abstract RegistroDoseDao registroDoseDao();

    private static volatile AppDatabase instancia;

    // Pool de threads para as operações de banco rodarem fora da main thread.
    public static final ExecutorService executor = Executors.newFixedThreadPool(4);

    public static AppDatabase getInstance(@NonNull Context context) {
        if (instancia == null) {
            synchronized (AppDatabase.class) {
                if (instancia == null) {
                    instancia = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "medilembrete_db"
                    ).build();
                }
            }
        }
        return instancia;
    }
}
