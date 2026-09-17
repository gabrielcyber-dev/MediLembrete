package com.example.medilembrete.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Registro de uma dose (tomada, perdida ou pendente) referente a um horário
 * programado de um medicamento. Alimenta a Tela 5 (Registro de Dose) e a
 * Tela 6 (Histórico).
 */
@Entity(
        tableName = "registros_dose",
        foreignKeys = @ForeignKey(
                entity = Medicamento.class,
                parentColumns = "id",
                childColumns = "medicamento_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("medicamento_id")}
)
public class RegistroDose {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "medicamento_id")
    private long medicamentoId;

    @NonNull
    @ColumnInfo(name = "horario_programado")
    private String horarioProgramado; // "HH:mm" a que este registro se refere

    @ColumnInfo(name = "data_hora_registro")
    private Long dataHoraRegistro; // epoch millis de quando foi marcado como tomado; null se pendente

    @NonNull
    @ColumnInfo(name = "status")
    private StatusDose status;

    public RegistroDose(long medicamentoId, @NonNull String horarioProgramado,
                         Long dataHoraRegistro, @NonNull StatusDose status) {
        this.medicamentoId = medicamentoId;
        this.horarioProgramado = horarioProgramado;
        this.dataHoraRegistro = dataHoraRegistro;
        this.status = status;
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
    public String getHorarioProgramado() {
        return horarioProgramado;
    }

    public void setHorarioProgramado(@NonNull String horarioProgramado) {
        this.horarioProgramado = horarioProgramado;
    }

    public Long getDataHoraRegistro() {
        return dataHoraRegistro;
    }

    public void setDataHoraRegistro(Long dataHoraRegistro) {
        this.dataHoraRegistro = dataHoraRegistro;
    }

    @NonNull
    public StatusDose getStatus() {
        return status;
    }

    public void setStatus(@NonNull StatusDose status) {
        this.status = status;
    }
}
