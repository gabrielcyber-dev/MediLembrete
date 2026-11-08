package com.example.medilembrete.data;

import android.content.Context;
import android.database.Cursor;
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

    /*
     * As versões 2 e 3 circularam com esquemas diferentes entre as branches:
     * uma delas já possuía os campos visuais e a outra ainda não. A migração
     * verifica cada coluna antes de criá-la, evitando tanto perda de dados
     * quanto "duplicate column" em instalações que já possuem os campos.
     */
    private static void garantirColunasDaInterface(SupportSQLiteDatabase db) {
        adicionarColunaSeAusente(db, "forma",
                "ALTER TABLE medicamentos ADD COLUMN forma TEXT NOT NULL DEFAULT 'Comprimido'");
        adicionarColunaSeAusente(db, "cor_identificacao",
                "ALTER TABLE medicamentos ADD COLUMN cor_identificacao TEXT NOT NULL DEFAULT '#FF3E00'");
        adicionarColunaSeAusente(db, "horario_inicial",
                "ALTER TABLE medicamentos ADD COLUMN horario_inicial TEXT NOT NULL DEFAULT '08:00'");
        adicionarColunaSeAusente(db, "intervalo_horas",
                "ALTER TABLE medicamentos ADD COLUMN intervalo_horas INTEGER NOT NULL DEFAULT 24");
        adicionarColunaSeAusente(db, "duracao_dias",
                "ALTER TABLE medicamentos ADD COLUMN duracao_dias INTEGER");
    }

    private static void adicionarColunaSeAusente(
            SupportSQLiteDatabase db, String coluna, String sql) {
        boolean existe = false;
        try (Cursor cursor = db.query("PRAGMA table_info(medicamentos)")) {
            int indiceNome = cursor.getColumnIndex("name");
            while (cursor.moveToNext()) {
                if (coluna.equals(cursor.getString(indiceNome))) {
                    existe = true;
                    break;
                }
            }
        }
        if (!existe) db.execSQL(sql);
    }

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override public void migrate(@NonNull SupportSQLiteDatabase db) {
            garantirColunasDaInterface(db);
        }
    };

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override public void migrate(@NonNull SupportSQLiteDatabase db) {
            garantirColunasDaInterface(db);
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
