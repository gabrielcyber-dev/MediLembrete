package com.example.medilembrete.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.medilembrete.data.entity.Medicamento;

import java.util.List;

@Dao
public interface MedicamentoDao {

    @Insert
    long inserir(Medicamento medicamento);

    @Update
    void atualizar(Medicamento medicamento);

    @Delete
    void excluir(Medicamento medicamento);

    @Query("SELECT * FROM medicamentos ORDER BY nome ASC")
    LiveData<List<Medicamento>> listarTodos();

    @Query("SELECT * FROM medicamentos WHERE id = :id")
    LiveData<Medicamento> buscarPorId(long id);

    @Query("SELECT * FROM medicamentos WHERE id = :id LIMIT 1")
    Medicamento buscarPorIdSync(long id);

    /** Medicamentos cujo período de tratamento cruza o dia consultado. */
    @Query("SELECT * FROM medicamentos " +
            "WHERE data_inicio < :fim AND (data_fim IS NULL OR data_fim >= :inicio) " +
            "ORDER BY nome ASC")
    List<Medicamento> listarAtivosNoPeriodoSync(long inicio, long fim);
}
