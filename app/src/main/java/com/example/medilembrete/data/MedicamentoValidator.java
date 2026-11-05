package com.example.medilembrete.data;

/**
 * Validações mínimas antes de gravar um Medicamento no banco
 * (Tela 2 - Cadastro de Medicamento).
 */
public final class MedicamentoValidator {

    private MedicamentoValidator() {
    }

    public static void validar(String nome, int quantidadePorDose, long dataInicio, Long dataFim) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o nome do medicamento");
        }
        if (quantidadePorDose <= 0) {
            throw new IllegalArgumentException("A quantidade por dose deve ser maior que zero");
        }
        if (dataFim != null && dataFim < dataInicio) {
            throw new IllegalArgumentException("A data de fim do tratamento não pode ser antes do início");
        }
    }
}
