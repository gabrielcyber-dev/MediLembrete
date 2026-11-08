package com.example.medilembrete.data;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.medilembrete.data.dao.HorarioDao;
import com.example.medilembrete.data.dao.MedicamentoDao;
import com.example.medilembrete.data.dao.RegistroDoseDao;
import com.example.medilembrete.data.entity.Horario;
import com.example.medilembrete.data.entity.Medicamento;
import com.example.medilembrete.data.entity.RegistroDose;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Medicamento.class, Horario.class, RegistroDose.class}, version = 4, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MedicamentoDao medicamentoDao();
    public abstract HorarioDao horarioDao();
    public abstract RegistroDoseDao registroDoseDao();
    private static volatile AppDatabase instancia;
    public static final ExecutorService executor = Executors.newFixedThreadPool(4);

    // Bancos da versão 2 recebem os campos adicionados na primeira evolução da interface.
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE medicamentos ADD COLUMN forma TEXT NOT NULL DEFAULT 'Comprimido'");
            db.execSQL("ALTER TABLE medicamentos ADD COLUMN cor_identificacao TEXT NOT NULL DEFAULT '#FF3E00'");
            db.execSQL("ALTER TABLE medicamentos ADD COLUMN horario_inicial TEXT NOT NULL DEFAULT '08:00'");
            db.execSQL("ALTER TABLE medicamentos ADD COLUMN intervalo_horas INTEGER NOT NULL DEFAULT 24");
            db.execSQL("ALTER TABLE medicamentos ADD COLUMN duracao_dias INTEGER");
        }
    };

    // A master anterior usava o esquema 3 sem os campos de apresentação da interface.
    // Esta migração preserva os medicamentos já cadastrados ao atualizar para a versão 4.
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE medicamentos ADD COLUMN forma TEXT NOT NULL DEFAULT 'Comprimido'");
            db.execSQL("ALTER TABLE medicamentos ADD COLUMN cor_identificacao TEXT NOT NULL DEFAULT '#FF3E00'");
            db.execSQL("ALTER TABLE medicamentos ADD COLUMN horario_inicial TEXT NOT NULL DEFAULT '08:00'");
            db.execSQL("ALTER TABLE medicamentos ADD COLUMN intervalo_horas INTEGER NOT NULL DEFAULT 24");
            db.execSQL("ALTER TABLE medicamentos ADD COLUMN duracao_dias INTEGER");
        }
    };

    public static AppDatabase getInstance(@NonNull Context context) {
        if (instancia == null) synchronized (AppDatabase.class) {
            if (instancia == null) instancia = Room.databaseBuilder(
                    context.getApplicationContext(), AppDatabase.class, "medilembrete_db")
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                    .build();
        }
        return instancia;
    }
}
