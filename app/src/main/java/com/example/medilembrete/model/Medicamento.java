package com.example.medilembrete.model;

public class Medicamento {

    private int id;
    private String nome;
    private String dosagem;
    private String quantidade;
    private String horarioInicial;
    private int intervaloHoras;
    private int duracaoDias;
    private boolean duracaoIndefinida;

    public Medicamento(
            int id,
            String nome,
            String dosagem,
            String quantidade,
            String horarioInicial,
            int intervaloHoras,
            int duracaoDias,
            boolean duracaoIndefinida) {

        this.id = id;
        this.nome = nome;
        this.dosagem = dosagem;
        this.quantidade = quantidade;
        this.horarioInicial = horarioInicial;
        this.intervaloHoras = intervaloHoras;
        this.duracaoDias = duracaoDias;
        this.duracaoIndefinida = duracaoIndefinida;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDosagem() {
        return dosagem;
    }

    public void setDosagem(String dosagem) {
        this.dosagem = dosagem;
    }

    public String getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(String quantidade) {
        this.quantidade = quantidade;
    }

    public String getHorarioInicial() {
        return horarioInicial;
    }

    public void setHorarioInicial(String horarioInicial) {
        this.horarioInicial = horarioInicial;
    }

    public int getIntervaloHoras() {
        return intervaloHoras;
    }

    public void setIntervaloHoras(int intervaloHoras) {
        this.intervaloHoras = intervaloHoras;
    }

    public int getDuracaoDias() {
        return duracaoDias;
    }

    public void setDuracaoDias(int duracaoDias) {
        this.duracaoDias = duracaoDias;
    }

    public boolean isDuracaoIndefinida() {
        return duracaoIndefinida;
    }

    public void setDuracaoIndefinida(boolean duracaoIndefinida) {
        this.duracaoIndefinida = duracaoIndefinida;
    }
}