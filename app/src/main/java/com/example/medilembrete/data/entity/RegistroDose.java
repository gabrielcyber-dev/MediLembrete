package com.example.medilembrete.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Registro de uma dose (tomada, perdida ou pendente) referente a um horário
 * programado de um medicamento. Alimenta a Tela 5 (Registro de Dose), a
 * Tela 6 (Histórico), os contadores da Tela 1 e o lembrete da Tela 7.
 *
 * O horário programado é guardado como data e hora completas (epoch millis),
 * e não como "HH:mm": sem a data não seria possível diferenciar a dose das
 * 08:00 de hoje da de ontem, nem contar as doses de um dia específico.
 */
@Entity(
        tableName = "registros_dose",
        foreignKeys = @ForeignKey(
                entity = Medicamento.class,
                parentColumns = "id",
                childColumns = "medicamento_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {
                @Index("medicamento_id"),
                // as consultas por dia/periodo e a busca da proxima dose
                // filtram e ordenam por esta coluna
                @Index("data_hora_programada")
        }
)
public class RegistroDose {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "medicamento_id")
    private long medicamentoId;

    @ColumnInfo(name = "data_hora_programada")
    private long dataHoraProgramada; // epoch millis - dia e hora em que a dose deveria ser tomada

    @ColumnInfo(name = "data_hora_registro")
    private Long dataHoraRegistro; // epoch millis de quando foi marcado como tomado; null se ainda pendente

    @NonNull
    @ColumnInfo(name = "status")
    private StatusDose status;

    public RegistroDose(long medicamentoId, long dataHoraProgramada,
                         Long dataHoraRegistro, @NonNull StatusDose status) {
        this.medicamentoId = medicamentoId;
        this.dataHoraProgramada = dataHoraProgramada;
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

    public long getDataHoraProgramada() {
        return dataHoraProgramada;
    }

    public void setDataHoraProgramada(long dataHoraProgramada) {
        this.dataHoraProgramada = dataHoraProgramada;
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
