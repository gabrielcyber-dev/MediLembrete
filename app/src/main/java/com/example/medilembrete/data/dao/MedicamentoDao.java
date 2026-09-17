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
}
