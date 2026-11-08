package com.example.medilembrete.data;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.medilembrete.data.dao.HorarioDao;
import com.example.medilembrete.data.dao.MedicamentoDao;
import com.example.medilembrete.data.dao.RegistroDoseDao;
import com.example.medilembrete.data.entity.Horario;
import com.example.medilembrete.data.entity.Medicamento;
import com.example.medilembrete.data.entity.RegistroDose;
import com.example.medilembrete.data.entity.StatusDose;

import java.util.ArrayList;
import java.util.List;

/**
 * Registro de doses: gera a agenda do dia a partir dos horários de cada
 * medicamento ativo e movimenta o status de cada dose.
 *
 * As leituras devolvem LiveData e podem ser chamadas direto da tela, que passa
 * a se atualizar sozinha quando o banco muda. As escritas rodam em background
 * e avisam pelo callback - use runOnUiThread para mexer na interface a partir
 * dele.
 *
 * Atende a Tela 1 (contadores e próximo lembrete), a Tela 5 (registro de dose),
 * a Tela 6 (histórico) e dá ao lembrete da Tela 7 o horário a ser agendado.
 */
public class RegistroDoseRepository {

    /** Chamado numa thread de background - use runOnUiThread para mexer na UI. */
    public interface Callback<T> {
        void aoConcluir(T resultado);
    }

    private final MedicamentoDao medicamentoDao;
    private final HorarioDao horarioDao;
    private final RegistroDoseDao registroDoseDao;

    public RegistroDoseRepository(@NonNull AppDatabase db) {
        this.medicamentoDao = db.medicamentoDao();
        this.horarioDao = db.horarioDao();
        this.registroDoseDao = db.registroDoseDao();
    }

    // ------------------------------------------------------------------
    // Leituras - para a tela observar
    // ------------------------------------------------------------------

    /** Doses programadas para o dia do instante informado, da mais cedo para a mais tarde. */
    public LiveData<List<RegistroDose>> dosesDoDia(long dia) {
        return registroDoseDao.listarPorPeriodo(
                GeradorDeDoses.inicioDoDia(dia), GeradorDeDoses.inicioDoDiaSeguinte(dia));
    }

    /** Quantas doses do dia já foram tomadas - contador "Tomados hoje" da Tela 1. */
    public LiveData<Integer> tomadasNoDia(long dia) {
        return contarNoDia(StatusDose.TOMADO, dia);
    }

    /** Quantas doses do dia continuam pendentes - contador "Pendentes" da Tela 1. */
    public LiveData<Integer> pendentesNoDia(long dia) {
        return contarNoDia(StatusDose.PENDENTE, dia);
    }

    /** Quantas doses do dia ficaram sem ser tomadas. */
    public LiveData<Integer> perdidasNoDia(long dia) {
        return contarNoDia(StatusDose.PERDIDO, dia);
    }

    private LiveData<Integer> contarNoDia(StatusDose status, long dia) {
        return registroDoseDao.contarPorStatusNoPeriodo(status,
                GeradorDeDoses.inicioDoDia(dia), GeradorDeDoses.inicioDoDiaSeguinte(dia));
    }

    /** Próxima dose ainda pendente a partir do instante informado, ou nulo se não houver. */
    public LiveData<RegistroDose> proximaDose(long agora) {
        return registroDoseDao.proximaDosePendente(agora);
    }

    /** Histórico completo, da dose mais recente para a mais antiga (Tela 6). */
    public LiveData<List<RegistroDose>> historico() {
        return registroDoseDao.historicoCompleto();
    }

    /** Histórico de um medicamento só (Tela 4 / Tela 6). */
    public LiveData<List<RegistroDose>> historicoDoMedicamento(long medicamentoId) {
        return registroDoseDao.historicoPorMedicamento(medicamentoId);
    }

    // ------------------------------------------------------------------
    // Escritas - rodam fora da main thread
    // ------------------------------------------------------------------

    /**
     * Cria as doses pendentes do dia para todos os medicamentos em tratamento,
     * combinando cada horário cadastrado com a data do dia.
     *
     * É seguro chamar quantas vezes quiser (ao abrir o app, por exemplo): uma
     * dose já existente é ignorada, e o status de quem já tomou não se perde.
     *
     * @param callback recebe quantas doses foram criadas nesta chamada
     */
    public void gerarDosesDoDia(long dia, Callback<Integer> callback) {
        AppDatabase.executor.execute(() -> {
            long inicioDoDia = GeradorDeDoses.inicioDoDia(dia);
            long fimDoDia = GeradorDeDoses.inicioDoDiaSeguinte(dia);
            int criadas = 0;

            for (Medicamento medicamento : medicamentoDao.listarAtivosNoPeriodoSync(inicioDoDia, fimDoDia)) {
                for (long instante : instantesValidosDoDia(medicamento.getId(), inicioDoDia)) {
                    // um tratamento pode comecar ou terminar no meio do dia
                    if (!GeradorDeDoses.tratamentoAtivoEm(
                            medicamento.getDataInicio(), medicamento.getDataFim(), instante)) {
                        continue;
                    }

                    long id = registroDoseDao.inserirSeNaoExistir(new RegistroDose(
                            medicamento.getId(), instante, null, StatusDose.PENDENTE));
                    if (id != -1) {
                        criadas++;
                    }
                }
            }

            if (callback != null) {
                callback.aoConcluir(criadas);
            }
        });
    }

    /**
     * Horários do medicamento convertidos nos instantes daquele dia. Um horário
     * gravado fora do formato "HH:mm" é ignorado, para não impedir a geração das
     * outras doses - a validação do formato é da tela de horários (Tela 3).
     */
    private List<Long> instantesValidosDoDia(long medicamentoId, long inicioDoDia) {
        List<String> horarios = new ArrayList<>();
        for (Horario horario : horarioDao.listarPorMedicamentoSync(medicamentoId)) {
            horarios.add(horario.getHorario());
        }

        List<Long> instantes = new ArrayList<>();
        for (String horario : horarios) {
            try {
                instantes.add(GeradorDeDoses.instanteDaDose(inicioDoDia, horario));
            } catch (IllegalArgumentException horarioInvalido) {
                // ignora apenas este horario
            }
        }
        return instantes;
    }

    /**
     * Registra a dose como tomada, guardando o instante do registro (Tela 5).
     * Uma dose perdida pode ser marcada como tomada depois - tomar atrasado
     * ainda conta -, mas uma dose já tomada não tem a hora regravada.
     *
     * @param callback recebe true se a dose mudou de status nesta chamada
     */
    public void marcarComoTomada(long registroId, long dataHoraRegistro, Callback<Boolean> callback) {
        AppDatabase.executor.execute(() -> {
            int alteradas = registroDoseDao.marcarComoTomada(registroId, dataHoraRegistro);
            if (callback != null) {
                callback.aoConcluir(alteradas > 0);
            }
        });
    }

    /**
     * Marca como perdidas as doses pendentes cujo horário já passou do limite.
     * Serve para o app não mostrar como "pendente" uma dose de ontem.
     *
     * @param callback recebe quantas doses passaram para PERDIDO
     */
    public void marcarAtrasadasComoPerdidas(long limite, Callback<Integer> callback) {
        AppDatabase.executor.execute(() -> {
            int alteradas = registroDoseDao.marcarPendentesAtrasadasComoPerdidas(limite);
            if (callback != null) {
                callback.aoConcluir(alteradas);
            }
        });
    }
}
