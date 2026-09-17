package com.example.medilembrete.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.medilembrete.data.entity.RegistroDose;
import com.example.medilembrete.data.entity.StatusDose;

import java.util.List;

@Dao
public interface RegistroDoseDao {

    @Insert
    long inserir(RegistroDose registro);

    @Update
    void atualizar(RegistroDose registro);

    @Query("SELECT * FROM registros_dose WHERE medicamento_id = :medicamentoId "
            + "ORDER BY data_hora_programada DESC")
    LiveData<List<RegistroDose>> historicoPorMedicamento(long medicamentoId);

    @Query("SELECT * FROM registros_dose ORDER BY data_hora_programada DESC")
    LiveData<List<RegistroDose>> historicoCompleto();

    /**
     * Doses programadas dentro de um intervalo, da mais antiga para a mais nova.
     * Usado para montar a agenda do dia (Tela 1) e o histórico por período
     * (Tela 6): basta passar o início e o fim do dia desejado.
     */
    @Query("SELECT * FROM registros_dose "
            + "WHERE data_hora_programada >= :inicio AND data_hora_programada < :fim "
            + "ORDER BY data_hora_programada ASC")
    LiveData<List<RegistroDose>> listarPorPeriodo(long inicio, long fim);

    /** Contador de um status num intervalo - alimenta "Tomados hoje" e "Pendentes" na Tela 1. */
    @Query("SELECT COUNT(*) FROM registros_dose "
            + "WHERE status = :status AND data_hora_programada >= :inicio AND data_hora_programada < :fim")
    LiveData<Integer> contarPorStatusNoPeriodo(StatusDose status, long inicio, long fim);

    /** Próxima dose ainda pendente a partir de um instante - card "Próximo lembrete" da Tela 1. */
    @Query("SELECT * FROM registros_dose "
            + "WHERE status = 'PENDENTE' AND data_hora_programada >= :agora "
            + "ORDER BY data_hora_programada ASC LIMIT 1")
    LiveData<RegistroDose> proximaDosePendente(long agora);

    /** Doses pendentes cujo horário já passou - usado para marcar as perdidas. */
    @Query("SELECT * FROM registros_dose "
            + "WHERE status = 'PENDENTE' AND data_hora_programada < :limite "
            + "ORDER BY data_hora_programada ASC")
    List<RegistroDose> pendentesAtrasadas(long limite);
}
