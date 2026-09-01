package com.example.medilembrete.data.dao;

import static org.junit.Assert.assertEquals;

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

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class RegistroDoseDaoTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;
    private MedicamentoDao medicamentoDao;
    private RegistroDoseDao registroDoseDao;

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

    @Test
    public void inserir_persisteStatusCorretamenteViaTypeConverter() throws InterruptedException {
        long medicamentoId = medicamentoDao.inserir(
                new Medicamento("Losartana", "50 mg", 1, 0L, null, null));

        registroDoseDao.inserir(new RegistroDose(
                medicamentoId, "08:00", 1_700_000_500_000L, StatusDose.TOMADO));

        List<RegistroDose> historico = LiveDataTestUtil.getOrAwaitValue(
                registroDoseDao.historicoPorMedicamento(medicamentoId));

        assertEquals(1, historico.size());
        assertEquals(StatusDose.TOMADO, historico.get(0).getStatus());
        assertEquals("08:00", historico.get(0).getHorarioProgramado());
    }

    @Test
    public void historicoCompleto_incluiRegistrosDeMaisDeUmMedicamento() throws InterruptedException {
        long idA = medicamentoDao.inserir(new Medicamento("Losartana", "50 mg", 1, 0L, null, null));
        long idB = medicamentoDao.inserir(new Medicamento("Metformina", "850 mg", 1, 0L, null, null));

        registroDoseDao.inserir(new RegistroDose(idA, "08:00", 100L, StatusDose.TOMADO));
        registroDoseDao.inserir(new RegistroDose(idB, "12:00", null, StatusDose.PENDENTE));

        List<RegistroDose> historico = LiveDataTestUtil.getOrAwaitValue(
                registroDoseDao.historicoCompleto());

        assertEquals(2, historico.size());
    }
}
