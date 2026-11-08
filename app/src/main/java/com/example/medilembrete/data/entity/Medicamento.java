package com.example.medilembrete.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Um medicamento cadastrado pelo usuário (Tela 2 - Cadastro de Medicamento).
 * Os horários de administração ficam em {@link Horario}, associados pelo id
 * deste medicamento.
 */
@Entity(tableName = "medicamentos")
public class Medicamento {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    @ColumnInfo(name = "nome")
    private String nome;

    @ColumnInfo(name = "dosagem")
    private String dosagem; // ex: "50 mg", "10 ml"

    @ColumnInfo(name = "quantidade_por_dose")
    private int quantidadePorDose; // ex: 1 comprimido, 2 gotas

    @ColumnInfo(name = "data_inicio")
    private long dataInicio; // epoch millis - início do tratamento

    @ColumnInfo(name = "data_fim")
    private Long dataFim; // epoch millis - fim do tratamento; null = uso contínuo

    @ColumnInfo(name = "observacoes")
    private String observacoes;

    public Medicamento(@NonNull String nome, String dosagem, int quantidadePorDose,
                        long dataInicio, Long dataFim, String observacoes) {
        this.nome = nome;
        this.dosagem = dosagem;
        this.quantidadePorDose = quantidadePorDose;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.observacoes = observacoes;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getNome() {
        return nome;
    }

    public void setNome(@NonNull String nome) {
        this.nome = nome;
    }

    public String getDosagem() {
        return dosagem;
    }

    public void setDosagem(String dosagem) {
        this.dosagem = dosagem;
    }

    public int getQuantidadePorDose() {
        return quantidadePorDose;
    }

    public void setQuantidadePorDose(int quantidadePorDose) {
        this.quantidadePorDose = quantidadePorDose;
    }

    public long getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(long dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Long getDataFim() {
        return dataFim;
    }

    public void setDataFim(Long dataFim) {
        this.dataFim = dataFim;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
