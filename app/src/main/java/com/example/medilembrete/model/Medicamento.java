package com.example.medilembrete.model;

public class Medicamento {

    private int id;
    private String nome;
    private String dosagem;
    private int intervalo;
    private int duracao;

    public Medicamento(int id, String nome, String dosagem, int intervalo, int duracao) {
        this.id = id;
        this.nome = nome;
        this.dosagem = dosagem;
        this.intervalo = intervalo;
        this.duracao = duracao;
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

    public int getIntervalo() {
        return intervalo;
    }

    public void setIntervalo(int intervalo) {
        this.intervalo = intervalo;
    }

    public int getDuracao() {
        return duracao;
    }

    public void setDuracao(int duracao) {
        this.duracao = duracao;
    }
}