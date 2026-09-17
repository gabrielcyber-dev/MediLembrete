package com.example.medilembrete.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Um horário programado para um medicamento (Tela 3 - Configuração de Horários).
 * Um medicamento pode ter vários horários, ex: 08:00, 14:00, 20:00.
 */
@Entity(
        tableName = "horarios",
        foreignKeys = @ForeignKey(
                entity = Medicamento.class,
                parentColumns = "id",
                childColumns = "medicamento_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("medicamento_id")}
)
public class Horario {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "medicamento_id")
    private long medicamentoId;

    @NonNull
    @ColumnInfo(name = "horario")
    private String horario; // formato "HH:mm", ex: "08:00"

    public Horario(long medicamentoId, @NonNull String horario) {
        this.medicamentoId = medicamentoId;
        this.horario = horario;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMedicamentoId() {
        return medicamentoId;
    }

    public void setMedicamentoId(long medicamentoId) {
        this.medicamentoId = medicamentoId;
    }

    @NonNull
    public String getHorario() {
        return horario;
    }

    public void setHorario(@NonNull String horario) {
        this.horario = horario;
    }
}
