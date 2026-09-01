package com.example.medilembrete.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.example.medilembrete.data.entity.StatusDose;

import org.junit.Test;

/**
 * Teste unitário puro (JVM, sem Android/emulador) da conversão de
 * {@link StatusDose} usada pelo Room para gravar o enum como texto.
 */
public class ConvertersTest {

    @Test
    public void fromStatusDose_convertePraNomeDoEnum() {
        assertEquals("TOMADO", Converters.fromStatusDose(StatusDose.TOMADO));
    }

    @Test
    public void fromStatusDose_nuloRetornaNulo() {
        assertNull(Converters.fromStatusDose(null));
    }

    @Test
    public void toStatusDose_convertePraOEnumCorreto() {
        assertEquals(StatusDose.PENDENTE, Converters.toStatusDose("PENDENTE"));
    }

    @Test
    public void toStatusDose_nuloRetornaNulo() {
        assertNull(Converters.toStatusDose(null));
    }
}
