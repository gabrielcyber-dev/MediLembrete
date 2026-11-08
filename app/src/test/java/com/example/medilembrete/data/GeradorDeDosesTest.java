package com.example.medilembrete.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GeradorDeDosesTest {

    /** Um instante conhecido: 17/09/2026 as 15:42:33.123, na hora local. */
    private long instanteDeReferencia() {
        Calendar c = Calendar.getInstance();
        c.set(2026, Calendar.SEPTEMBER, 17, 15, 42, 33);
        c.set(Calendar.MILLISECOND, 123);
        return c.getTimeInMillis();
    }

    private Calendar comoCalendar(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        return c;
    }

    @Test
    public void inicioDoDia_zeraHoraMinutoSegundoEMilissegundo() {
        Calendar c = comoCalendar(GeradorDeDoses.inicioDoDia(instanteDeReferencia()));

        assertEquals(2026, c.get(Calendar.YEAR));
        assertEquals(Calendar.SEPTEMBER, c.get(Calendar.MONTH));
        assertEquals(17, c.get(Calendar.DAY_OF_MONTH));
        assertEquals(0, c.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, c.get(Calendar.MINUTE));
        assertEquals(0, c.get(Calendar.SECOND));
        assertEquals(0, c.get(Calendar.MILLISECOND));
    }

    @Test
    public void inicioDoDiaSeguinte_avancaUmDiaEContinuaZerado() {
        Calendar c = comoCalendar(GeradorDeDoses.inicioDoDiaSeguinte(instanteDeReferencia()));

        assertEquals(18, c.get(Calendar.DAY_OF_MONTH));
        assertEquals(0, c.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, c.get(Calendar.MINUTE));
    }

    @Test
    public void inicioDoDia_eInicioDoDiaSeguinte_delimitamODiaInteiro() {
        long inicio = GeradorDeDoses.inicioDoDia(instanteDeReferencia());
        long fim = GeradorDeDoses.inicioDoDiaSeguinte(instanteDeReferencia());

        // o intervalo [inicio, fim) tem que conter o instante de referencia
        assertTrue(instanteDeReferencia() >= inicio);
        assertTrue(instanteDeReferencia() < fim);
    }

    @Test
    public void instanteDaDose_combinaODiaComOHorarioInformado() {
        Calendar c = comoCalendar(GeradorDeDoses.instanteDaDose(instanteDeReferencia(), "08:05"));

        assertEquals(17, c.get(Calendar.DAY_OF_MONTH));
        assertEquals(8, c.get(Calendar.HOUR_OF_DAY));
        assertEquals(5, c.get(Calendar.MINUTE));
        assertEquals(0, c.get(Calendar.SECOND));
        assertEquals(0, c.get(Calendar.MILLISECOND));
    }

    @Test
    public void instanteDaDose_aceitaMeiaNoiteEUltimoMinutoDoDia() {
        Calendar meiaNoite = comoCalendar(GeradorDeDoses.instanteDaDose(instanteDeReferencia(), "00:00"));
        Calendar fimDoDia = comoCalendar(GeradorDeDoses.instanteDaDose(instanteDeReferencia(), "23:59"));

        assertEquals(0, meiaNoite.get(Calendar.HOUR_OF_DAY));
        assertEquals(23, fimDoDia.get(Calendar.HOUR_OF_DAY));
        assertEquals(59, fimDoDia.get(Calendar.MINUTE));
    }

    @Test
    public void instanteDaDose_recusaHorarioMalFormatadoOuForaDaFaixa() {
        String[] invalidos = {null, "", "8h", "08", "08:00:00", "24:00", "08:60", "-1:00", "ab:cd"};

        for (String invalido : invalidos) {
            try {
                GeradorDeDoses.instanteDaDose(instanteDeReferencia(), invalido);
                fail("deveria ter recusado o horário: " + invalido);
            } catch (IllegalArgumentException esperado) {
                // ok - é o comportamento esperado
            }
        }
    }

    @Test
    public void instantesDoDia_retornaEmOrdemCrescente() {
        List<Long> instantes = GeradorDeDoses.instantesDoDia(
                instanteDeReferencia(), Arrays.asList("20:00", "08:00", "14:30"));

        assertEquals(3, instantes.size());
        assertEquals(8, comoCalendar(instantes.get(0)).get(Calendar.HOUR_OF_DAY));
        assertEquals(14, comoCalendar(instantes.get(1)).get(Calendar.HOUR_OF_DAY));
        assertEquals(20, comoCalendar(instantes.get(2)).get(Calendar.HOUR_OF_DAY));
    }

    @Test
    public void instantesDoDia_naoRepeteHorarioDuplicado() {
        List<Long> instantes = GeradorDeDoses.instantesDoDia(
                instanteDeReferencia(), Arrays.asList("08:00", "08:00", "20:00"));

        assertEquals(2, instantes.size());
    }

    @Test
    public void instantesDoDia_semHorariosRetornaListaVazia() {
        assertTrue(GeradorDeDoses.instantesDoDia(instanteDeReferencia(), null).isEmpty());
        assertTrue(GeradorDeDoses.instantesDoDia(instanteDeReferencia(), Arrays.<String>asList()).isEmpty());
    }

    @Test
    public void tratamentoAtivoEm_usoContinuoNuncaTermina() {
        long inicio = instanteDeReferencia();
        long doisAnosDepois = inicio + TimeUnit.DAYS.toMillis(730);

        assertTrue(GeradorDeDoses.tratamentoAtivoEm(inicio, null, inicio));
        assertTrue(GeradorDeDoses.tratamentoAtivoEm(inicio, null, doisAnosDepois));
    }

    @Test
    public void tratamentoAtivoEm_foraDoPeriodoRetornaFalso() {
        long inicio = instanteDeReferencia();
        long fim = inicio + TimeUnit.DAYS.toMillis(7);

        assertFalse("antes do inicio",
                GeradorDeDoses.tratamentoAtivoEm(inicio, fim, inicio - 1));
        assertTrue("no inicio",
                GeradorDeDoses.tratamentoAtivoEm(inicio, fim, inicio));
        assertTrue("no meio",
                GeradorDeDoses.tratamentoAtivoEm(inicio, fim, inicio + TimeUnit.DAYS.toMillis(3)));
        assertTrue("no ultimo instante",
                GeradorDeDoses.tratamentoAtivoEm(inicio, fim, fim));
        assertFalse("depois do fim",
                GeradorDeDoses.tratamentoAtivoEm(inicio, fim, fim + 1));
    }
}
