package com.example.medilembrete;

public class Medicamento {

    private String nome;
    private String dosagem;
    private String quantidade;
    private String frequencia;
    private int duracao;

    public Medicamento(
            String nome,
            String dosagem,
            String quantidade,
            String frequencia,
            int duracao) {

        this.nome = nome;
        this.dosagem = dosagem;
        this.quantidade = quantidade;
        this.frequencia = frequencia;
        this.duracao = duracao;
    }

    public String getNome() {
        return nome;
    }

    public String getDosagem() {
        return dosagem;
    }

    public String getQuantidade() {
        return quantidade;
    }

    public String getFrequencia() {
        return frequencia;
    }

    public int getDuracao() {
        return duracao;
    }
}