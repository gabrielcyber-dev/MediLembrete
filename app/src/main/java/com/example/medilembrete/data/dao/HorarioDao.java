package com.example.medilembrete.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.medilembrete.data.entity.Horario;

import java.util.List;

@Dao
public interface HorarioDao {

    @Insert
    long inserir(Horario horario);

    @Update
    void atualizar(Horario horario);

    @Delete
    void excluir(Horario horario);

    @Query("SELECT * FROM horarios WHERE medicamento_id = :medicamentoId ORDER BY horario ASC")
    LiveData<List<Horario>> listarPorMedicamento(long medicamentoId);
}
