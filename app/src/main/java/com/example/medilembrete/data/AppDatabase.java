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
 * Ponto único de acesso ao banco SQLite do app.
 *
 * Enquanto o app está em desenvolvimento, mudanças de esquema sobem a
 * "version" e o banco é recriado do zero (fallbackToDestructiveMigration),
 * o que evita que cada integrante precise desinstalar o app a cada ajuste
 * na modelagem. ANTES de o app ser usado por alguém de verdade, trocar isso
 * por Migrations, senão os dados do usuário serão apagados na atualização.
 */
@Database(
        entities = {Medicamento.class, Horario.class, RegistroDose.class},
        version = 2,
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
                    )
                            // apenas durante o desenvolvimento - ver comentario da classe
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instancia;
    }
}
