package com.example.medilembrete.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.medilembrete.data.entity.RegistroDose;
import com.example.medilembrete.data.entity.StatusDose;

import java.util.List;

@Dao
public interface RegistroDoseDao {

    @Insert
    long inserir(RegistroDose registro);

    /** Insere a dose somente se ela ainda não existir para o mesmo medicamento/instante. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long inserirSeNaoExistir(RegistroDose registro);

    @Update
    void atualizar(RegistroDose registro);

    @Query("SELECT * FROM registros_dose WHERE medicamento_id = :medicamentoId " +
            "ORDER BY data_hora_programada DESC")
    LiveData<List<RegistroDose>> historicoPorMedicamento(long medicamentoId);

    @Query("SELECT * FROM registros_dose ORDER BY data_hora_programada DESC")
    LiveData<List<RegistroDose>> historicoCompleto();

    @Query("SELECT * FROM registros_dose " +
            "WHERE data_hora_programada >= :inicio AND data_hora_programada < :fim " +
            "ORDER BY data_hora_programada ASC")
    LiveData<List<RegistroDose>> listarPorPeriodo(long inicio, long fim);

    @Query("SELECT COUNT(*) FROM registros_dose " +
            "WHERE status = :status AND data_hora_programada >= :inicio AND data_hora_programada < :fim")
    LiveData<Integer> contarPorStatusNoPeriodo(StatusDose status, long inicio, long fim);

    @Query("SELECT * FROM registros_dose " +
            "WHERE status = 'PENDENTE' AND data_hora_programada >= :agora " +
            "ORDER BY data_hora_programada ASC LIMIT 1")
    LiveData<RegistroDose> proximaDosePendente(long agora);

    @Query("SELECT * FROM registros_dose " +
            "WHERE status = 'PENDENTE' AND data_hora_programada < :limite " +
            "ORDER BY data_hora_programada ASC")
    List<RegistroDose> pendentesAtrasadas(long limite);

    /** Permite marcar como tomada uma dose pendente ou perdida, sem regravar uma já tomada. */
    @Query("UPDATE registros_dose SET status = 'TOMADO', data_hora_registro = :dataHoraRegistro " +
            "WHERE id = :registroId AND status != 'TOMADO'")
    int marcarComoTomada(long registroId, long dataHoraRegistro);

    /** Atualiza em lote as doses pendentes cujo horário programado já passou. */
    @Query("UPDATE registros_dose SET status = 'PERDIDO' " +
            "WHERE status = 'PENDENTE' AND data_hora_programada < :limite")
    int marcarPendentesAtrasadasComoPerdidas(long limite);
}
