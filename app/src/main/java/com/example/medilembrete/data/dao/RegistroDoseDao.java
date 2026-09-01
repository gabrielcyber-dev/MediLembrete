package com.example.medilembrete.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.medilembrete.data.entity.RegistroDose;

import java.util.List;

@Dao
public interface RegistroDoseDao {

    @Insert
    long inserir(RegistroDose registro);

    @Update
    void atualizar(RegistroDose registro);

    @Query("SELECT * FROM registros_dose WHERE medicamento_id = :medicamentoId ORDER BY data_hora_registro DESC")
    LiveData<List<RegistroDose>> historicoPorMedicamento(long medicamentoId);

    @Query("SELECT * FROM registros_dose ORDER BY data_hora_registro DESC")
    LiveData<List<RegistroDose>> historicoCompleto();
}
