package com.example.medilembrete.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "medicamentos")
public class Medicamento {
    @PrimaryKey(autoGenerate = true) private long id;
    @NonNull @ColumnInfo(name = "nome") private String nome;
    @ColumnInfo(name = "dosagem") private String dosagem;
    @ColumnInfo(name = "quantidade_por_dose") private int quantidadePorDose;
    @ColumnInfo(name = "data_inicio") private long dataInicio;
    @ColumnInfo(name = "data_fim") private Long dataFim;
    @ColumnInfo(name = "observacoes") private String observacoes;
    @NonNull @ColumnInfo(name = "forma", defaultValue = "'Comprimido'") private String forma;
    @NonNull @ColumnInfo(name = "cor_identificacao", defaultValue = "'#FF3E00'") private String corIdentificacao;
    @NonNull @ColumnInfo(name = "horario_inicial", defaultValue = "'08:00'") private String horarioInicial;
    @ColumnInfo(name = "intervalo_horas", defaultValue = "24") private int intervaloHoras;
    @ColumnInfo(name = "duracao_dias") private Integer duracaoDias;

    public Medicamento(@NonNull String nome, String dosagem, int quantidadePorDose,
            long dataInicio, Long dataFim, String observacoes, @NonNull String forma,
            @NonNull String corIdentificacao, @NonNull String horarioInicial,
            int intervaloHoras, Integer duracaoDias) {
        this.nome = nome; this.dosagem = dosagem; this.quantidadePorDose = quantidadePorDose;
        this.dataInicio = dataInicio; this.dataFim = dataFim; this.observacoes = observacoes;
        this.forma = forma; this.corIdentificacao = corIdentificacao;
        this.horarioInicial = horarioInicial; this.intervaloHoras = intervaloHoras;
        this.duracaoDias = duracaoDias;
    }
    // Construtor legado para chamadas existentes; o Room deve ignorá-lo.
    @Ignore
    public Medicamento(@NonNull String nome, String dosagem, int quantidadePorDose,
            long dataInicio, Long dataFim, String observacoes) {
        this(nome, dosagem, quantidadePorDose, dataInicio, dataFim, observacoes,
                "Comprimido", "#FF3E00", "08:00", 24, null);
    }
    public long getId(){return id;} public void setId(long v){id=v;}
    @NonNull public String getNome(){return nome;} public void setNome(@NonNull String v){nome=v;}
    public String getDosagem(){return dosagem;} public void setDosagem(String v){dosagem=v;}
    public int getQuantidadePorDose(){return quantidadePorDose;} public void setQuantidadePorDose(int v){quantidadePorDose=v;}
    public long getDataInicio(){return dataInicio;} public void setDataInicio(long v){dataInicio=v;}
    public Long getDataFim(){return dataFim;} public void setDataFim(Long v){dataFim=v;}
    public String getObservacoes(){return observacoes;} public void setObservacoes(String v){observacoes=v;}
    @NonNull public String getForma(){return forma;} public void setForma(@NonNull String v){forma=v;}
    @NonNull public String getCorIdentificacao(){return corIdentificacao;} public void setCorIdentificacao(@NonNull String v){corIdentificacao=v;}
    @NonNull public String getHorarioInicial(){return horarioInicial;} public void setHorarioInicial(@NonNull String v){horarioInicial=v;}
    public int getIntervaloHoras(){return intervaloHoras;} public void setIntervaloHoras(int v){intervaloHoras=v;}
    public Integer getDuracaoDias(){return duracaoDias;} public void setDuracaoDias(Integer v){duracaoDias=v;}
}
