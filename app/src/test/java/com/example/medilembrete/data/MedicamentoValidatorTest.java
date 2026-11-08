package com.example.medilembrete.data;

import static org.junit.Assert.assertThrows;

import org.junit.Test;

public class MedicamentoValidatorTest {

    @Test
    public void validar_dadosValidos_naoLancaExcecao() {
        MedicamentoValidator.validar("Losartana", 1, 1000L, 2000L);
    }

    @Test
    public void validar_semDataFim_naoLancaExcecao() {
        // dataFim nula = uso contínuo, tratamento sem previsão de término
        MedicamentoValidator.validar("Losartana", 1, 1000L, null);
    }

    @Test
    public void validar_nomeVazio_lancaExcecao() {
        assertThrows(IllegalArgumentException.class,
                () -> MedicamentoValidator.validar("   ", 1, 1000L, null));
    }

    @Test
    public void validar_nomeNulo_lancaExcecao() {
        assertThrows(IllegalArgumentException.class,
                () -> MedicamentoValidator.validar(null, 1, 1000L, null));
    }

    @Test
    public void validar_quantidadeZeroOuNegativa_lancaExcecao() {
        assertThrows(IllegalArgumentException.class,
                () -> MedicamentoValidator.validar("Losartana", 0, 1000L, null));
        assertThrows(IllegalArgumentException.class,
                () -> MedicamentoValidator.validar("Losartana", -1, 1000L, null));
    }

    @Test
    public void validar_dataFimAntesDoInicio_lancaExcecao() {
        assertThrows(IllegalArgumentException.class,
                () -> MedicamentoValidator.validar("Losartana", 1, 2000L, 1000L));
    }
}
