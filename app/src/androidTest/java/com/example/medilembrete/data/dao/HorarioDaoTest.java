package com.example.medilembrete.data.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.medilembrete.LiveDataTestUtil;
import com.example.medilembrete.data.AppDatabase;
import com.example.medilembrete.data.entity.Horario;
import com.example.medilembrete.data.entity.Medicamento;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class HorarioDaoTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;
    private MedicamentoDao medicamentoDao;
    private HorarioDao horarioDao;

    @Before
    public void criarBancoEmMemoria() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        medicamentoDao = db.medicamentoDao();
        horarioDao = db.horarioDao();
    }

    @After
    public void fecharBanco() {
        db.close();
    }

    @Test
    public void listarPorMedicamento_retornaHorariosEmOrdemCrescente() throws InterruptedException {
        long medicamentoId = medicamentoDao.inserir(
                new Medicamento("Losartana", "50 mg", 1, 0L, null, null));

        horarioDao.inserir(new Horario(medicamentoId, "20:00"));
        horarioDao.inserir(new Horario(medicamentoId, "08:00"));

        List<Horario> horarios = LiveDataTestUtil.getOrAwaitValue(
                horarioDao.listarPorMedicamento(medicamentoId));

        assertEquals(2, horarios.size());
        assertEquals("08:00", horarios.get(0).getHorario());
        assertEquals("20:00", horarios.get(1).getHorario());
    }

    @Test
    public void excluirMedicamento_removeSeusHorariosEmCascata() throws InterruptedException {
        long medicamentoId = medicamentoDao.inserir(
                new Medicamento("Vitamina D", "2000 UI", 1, 0L, null, null));
        horarioDao.inserir(new Horario(medicamentoId, "09:00"));

        Medicamento medicamento = LiveDataTestUtil.getOrAwaitValue(
                medicamentoDao.buscarPorId(medicamentoId));
        medicamentoDao.excluir(medicamento);

        List<Horario> horarios = LiveDataTestUtil.getOrAwaitValue(
                horarioDao.listarPorMedicamento(medicamentoId));
        assertTrue(horarios.isEmpty());
    }
}
