package com.example.medilembrete.data.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.medilembrete.LiveDataTestUtil;
import com.example.medilembrete.data.AppDatabase;
import com.example.medilembrete.data.entity.Medicamento;
import com.example.medilembrete.data.entity.RegistroDose;
import com.example.medilembrete.data.entity.StatusDose;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class RegistroDoseDaoTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;
    private MedicamentoDao medicamentoDao;
    private RegistroDoseDao registroDoseDao;

    /** Hoje na hora cheia informada - base fixa para os testes nao dependerem da hora em que rodam. */
    private long hojeAs(int hora) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hora);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    private long inicioDoDia() {
        return hojeAs(0);
    }

    private long inicioDoDiaSeguinte() {
        return inicioDoDia() + TimeUnit.DAYS.toMillis(1);
    }

    @Before
    public void criarBancoEmMemoria() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        medicamentoDao = db.medicamentoDao();
        registroDoseDao = db.registroDoseDao();
    }

    @After
    public void fecharBanco() {
        db.close();
    }

    private long inserirMedicamento(String nome) {
        return medicamentoDao.inserir(new Medicamento(nome, "50 mg", 1, 0L, null, null));
    }

    @Test
    public void inserir_persisteStatusCorretamenteViaTypeConverter() throws InterruptedException {
        long medicamentoId = inserirMedicamento("Losartana");
        long programada = hojeAs(8);

        registroDoseDao.inserir(new RegistroDose(
                medicamentoId, programada, programada + 60_000L, StatusDose.TOMADO));

        List<RegistroDose> historico = LiveDataTestUtil.getOrAwaitValue(
                registroDoseDao.historicoPorMedicamento(medicamentoId));

        assertEquals(1, historico.size());
        assertEquals(StatusDose.TOMADO, historico.get(0).getStatus());
        assertEquals(programada, historico.get(0).getDataHoraProgramada());
    }

    @Test
    public void historicoCompleto_incluiRegistrosDeMaisDeUmMedicamento() throws InterruptedException {
        long idA = inserirMedicamento("Losartana");
        long idB = inserirMedicamento("Metformina");

        registroDoseDao.inserir(new RegistroDose(idA, hojeAs(8), hojeAs(8), StatusDose.TOMADO));
        registroDoseDao.inserir(new RegistroDose(idB, hojeAs(12), null, StatusDose.PENDENTE));

        List<RegistroDose> historico = LiveDataTestUtil.getOrAwaitValue(
                registroDoseDao.historicoCompleto());

        assertEquals(2, historico.size());
    }

    @Test
    public void mesmoHorarioEmDiasDiferentes_geraRegistrosDistintos() throws InterruptedException {
        long medicamentoId = inserirMedicamento("Losartana");
        long hoje = hojeAs(8);
        long ontem = hoje - TimeUnit.DAYS.toMillis(1);

        registroDoseDao.inserir(new RegistroDose(medicamentoId, ontem, ontem, StatusDose.TOMADO));
        registroDoseDao.inserir(new RegistroDose(medicamentoId, hoje, null, StatusDose.PENDENTE));

        List<RegistroDose> historico = LiveDataTestUtil.getOrAwaitValue(
                registroDoseDao.historicoPorMedicamento(medicamentoId));

        // as duas doses sao das 08:00, mas de dias diferentes: precisam coexistir
        assertEquals(2, historico.size());
        assertEquals(hoje, historico.get(0).getDataHoraProgramada());
        assertEquals(ontem, historico.get(1).getDataHoraProgramada());
    }

    @Test
    public void listarPorPeriodo_trazSomenteAsDosesDoDia() throws InterruptedException {
        long medicamentoId = inserirMedicamento("Losartana");
        long ontem = hojeAs(8) - TimeUnit.DAYS.toMillis(1);
        long amanha = hojeAs(8) + TimeUnit.DAYS.toMillis(1);

        registroDoseDao.inserir(new RegistroDose(medicamentoId, ontem, ontem, StatusDose.TOMADO));
        registroDoseDao.inserir(new RegistroDose(medicamentoId, hojeAs(20), null, StatusDose.PENDENTE));
        registroDoseDao.inserir(new RegistroDose(medicamentoId, hojeAs(8), hojeAs(8), StatusDose.TOMADO));
        registroDoseDao.inserir(new RegistroDose(medicamentoId, amanha, null, StatusDose.PENDENTE));

        List<RegistroDose> doHoje = LiveDataTestUtil.getOrAwaitValue(
                registroDoseDao.listarPorPeriodo(inicioDoDia(), inicioDoDiaSeguinte()));

        assertEquals(2, doHoje.size());
        // ordenadas da mais cedo para a mais tarde
        assertEquals(hojeAs(8), doHoje.get(0).getDataHoraProgramada());
        assertEquals(hojeAs(20), doHoje.get(1).getDataHoraProgramada());
    }

    @Test
    public void contarPorStatusNoPeriodo_contaSomenteOStatusEOPeriodoPedidos() throws InterruptedException {
        long medicamentoId = inserirMedicamento("Losartana");
        long ontem = hojeAs(8) - TimeUnit.DAYS.toMillis(1);

        registroDoseDao.inserir(new RegistroDose(medicamentoId, hojeAs(8), hojeAs(8), StatusDose.TOMADO));
        registroDoseDao.inserir(new RegistroDose(medicamentoId, hojeAs(12), hojeAs(12), StatusDose.TOMADO));
        registroDoseDao.inserir(new RegistroDose(medicamentoId, hojeAs(20), null, StatusDose.PENDENTE));
        registroDoseDao.inserir(new RegistroDose(medicamentoId, ontem, ontem, StatusDose.TOMADO));

        int tomadosHoje = LiveDataTestUtil.getOrAwaitValue(registroDoseDao.contarPorStatusNoPeriodo(
                StatusDose.TOMADO, inicioDoDia(), inicioDoDiaSeguinte()));
        int pendentesHoje = LiveDataTestUtil.getOrAwaitValue(registroDoseDao.contarPorStatusNoPeriodo(
                StatusDose.PENDENTE, inicioDoDia(), inicioDoDiaSeguinte()));

        assertEquals(2, tomadosHoje);  // a dose de ontem nao entra na conta
        assertEquals(1, pendentesHoje);
    }

    @Test
    public void proximaDosePendente_retornaAMaisProximaEIgnoraPassadasETomadas()
            throws InterruptedException {
        long medicamentoId = inserirMedicamento("Losartana");

        registroDoseDao.inserir(new RegistroDose(medicamentoId, hojeAs(8), null, StatusDose.PENDENTE));
        registroDoseDao.inserir(new RegistroDose(medicamentoId, hojeAs(14), hojeAs(14), StatusDose.TOMADO));
        registroDoseDao.inserir(new RegistroDose(medicamentoId, hojeAs(18), null, StatusDose.PENDENTE));
        registroDoseDao.inserir(new RegistroDose(medicamentoId, hojeAs(22), null, StatusDose.PENDENTE));

        RegistroDose proxima = LiveDataTestUtil.getOrAwaitValue(
                registroDoseDao.proximaDosePendente(hojeAs(12)));

        assertNotNull(proxima);
        // ignora a das 08:00 (ja passou) e a das 14:00 (tomada)
        assertEquals(hojeAs(18), proxima.getDataHoraProgramada());
    }

    @Test
    public void proximaDosePendente_retornaNuloQuandoNaoHaDosePendenteAFrente()
            throws InterruptedException {
        long medicamentoId = inserirMedicamento("Losartana");

        registroDoseDao.inserir(new RegistroDose(medicamentoId, hojeAs(8), hojeAs(8), StatusDose.TOMADO));

        RegistroDose proxima = LiveDataTestUtil.getOrAwaitValue(
                registroDoseDao.proximaDosePendente(hojeAs(12)));

        assertNull(proxima);
    }

    @Test
    public void pendentesAtrasadas_trazSomenteAsPendentesComHorarioJaPassado() {
        long medicamentoId = inserirMedicamento("Losartana");

        registroDoseDao.inserir(new RegistroDose(medicamentoId, hojeAs(8), null, StatusDose.PENDENTE));
        registroDoseDao.inserir(new RegistroDose(medicamentoId, hojeAs(9), hojeAs(9), StatusDose.TOMADO));
        registroDoseDao.inserir(new RegistroDose(medicamentoId, hojeAs(20), null, StatusDose.PENDENTE));

        List<RegistroDose> atrasadas = registroDoseDao.pendentesAtrasadas(hojeAs(12));

        assertEquals(1, atrasadas.size());
        assertEquals(hojeAs(8), atrasadas.get(0).getDataHoraProgramada());
    }

    @Test
    public void excluirMedicamento_apagaOsRegistrosDeDoseEmCascata() throws InterruptedException {
        long medicamentoId = inserirMedicamento("Losartana");
        registroDoseDao.inserir(new RegistroDose(medicamentoId, hojeAs(8), null, StatusDose.PENDENTE));

        Medicamento medicamento = LiveDataTestUtil.getOrAwaitValue(
                medicamentoDao.buscarPorId(medicamentoId));
        medicamentoDao.excluir(medicamento);

        List<RegistroDose> historico = LiveDataTestUtil.getOrAwaitValue(
                registroDoseDao.historicoCompleto());

        assertEquals(0, historico.size());
    }
}
