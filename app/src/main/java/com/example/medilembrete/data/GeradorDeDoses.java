package com.example.medilembrete.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Cálculos de agenda de doses, sem depender do Android nem do banco
 * (por isso é testável na JVM, sem emulador).
 *
 * Os horários do medicamento são recorrentes ("08:00", "20:00") e não têm dia;
 * aqui eles são combinados com uma data para virar o instante exato em que a
 * dose deve ser tomada, que é o que a tabela registros_dose guarda.
 */
public final class GeradorDeDoses {

    private GeradorDeDoses() {
    }

    /** Início do dia (00:00:00.000) a que pertence o instante informado. */
    public static long inicioDoDia(long instante) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(instante);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    /** Início do dia seguinte - usado como limite superior (exclusivo) das consultas do dia. */
    public static long inicioDoDiaSeguinte(long instante) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(inicioDoDia(instante));
        c.add(Calendar.DAY_OF_MONTH, 1);
        return c.getTimeInMillis();
    }

    /**
     * Combina um dia com um horário "HH:mm" e devolve o instante correspondente.
     * Usa Calendar em vez de somar milissegundos para o resultado continuar
     * correto em dias com mudança de horário.
     *
     * @throws IllegalArgumentException se o horário não estiver em "HH:mm" válido
     */
    public static long instanteDaDose(long diaQualquer, String horarioHHmm) {
        if (horarioHHmm == null) {
            throw new IllegalArgumentException("Horário não informado");
        }

        String[] partes = horarioHHmm.trim().split(":");
        if (partes.length != 2) {
            throw new IllegalArgumentException("Horário inválido: " + horarioHHmm);
        }

        int hora;
        int minuto;
        try {
            hora = Integer.parseInt(partes[0]);
            minuto = Integer.parseInt(partes[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Horário inválido: " + horarioHHmm);
        }

        if (hora < 0 || hora > 23 || minuto < 0 || minuto > 59) {
            throw new IllegalArgumentException("Horário fora da faixa: " + horarioHHmm);
        }

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(inicioDoDia(diaQualquer));
        c.set(Calendar.HOUR_OF_DAY, hora);
        c.set(Calendar.MINUTE, minuto);
        return c.getTimeInMillis();
    }

    /**
     * Instantes das doses de um dia, em ordem crescente e sem repetição
     * (dois horários iguais cadastrados geram uma dose só).
     */
    public static List<Long> instantesDoDia(long diaQualquer, List<String> horariosHHmm) {
        List<Long> instantes = new ArrayList<>();
        if (horariosHHmm == null) {
            return instantes;
        }

        for (String horario : horariosHHmm) {
            long instante = instanteDaDose(diaQualquer, horario);
            if (!instantes.contains(instante)) {
                instantes.add(instante);
            }
        }

        Collections.sort(instantes);
        return instantes;
    }

    /**
     * Diz se o tratamento cobre o instante informado.
     * {@code dataFim} nula significa uso contínuo, sem data de término.
     */
    public static boolean tratamentoAtivoEm(long dataInicio, Long dataFim, long instante) {
        if (instante < dataInicio) {
            return false;
        }
        return dataFim == null || instante <= dataFim;
    }
}
