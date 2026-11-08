package com.example.medilembrete.data.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.medilembrete.LiveDataTestUtil;
import com.example.medilembrete.data.AppDatabase;
import com.example.medilembrete.data.entity.Medicamento;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class MedicamentoDaoTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;
    private MedicamentoDao dao;

    @Before
    public void criarBancoEmMemoria() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        dao = db.medicamentoDao();
    }

    @After
    public void fecharBanco() {
        db.close();
    }

    @Test
    public void inserirEBuscarPorId_retornaOMesmoMedicamento() throws InterruptedException {
        Medicamento losartana = new Medicamento("Losartana", "50 mg", 1,
                1_700_000_000_000L, null, null);

        long id = dao.inserir(losartana);
        Medicamento encontrado = LiveDataTestUtil.getOrAwaitValue(dao.buscarPorId(id));

        assertEquals("Losartana", encontrado.getNome());
        assertEquals("50 mg", encontrado.getDosagem());
        assertEquals(1, encontrado.getQuantidadePorDose());
        assertNull(encontrado.getDataFim());
    }

    @Test
    public void listarTodos_ordenaPorNomeAlfabeticamente() throws InterruptedException {
        dao.inserir(new Medicamento("Vitamina D", "2000 UI", 1, 0L, null, null));
        dao.inserir(new Medicamento("Amoxicilina", "500 mg", 1, 0L, null, null));

        List<Medicamento> todos = LiveDataTestUtil.getOrAwaitValue(dao.listarTodos());

        assertEquals(2, todos.size());
        assertEquals("Amoxicilina", todos.get(0).getNome());
        assertEquals("Vitamina D", todos.get(1).getNome());
    }

    @Test
    public void atualizar_persisteAsMudancas() throws InterruptedException {
        long id = dao.inserir(new Medicamento("Dipirona", "1g", 1, 0L, null, null));
        Medicamento medicamento = LiveDataTestUtil.getOrAwaitValue(dao.buscarPorId(id));

        medicamento.setDosagem("500 mg");
        dao.atualizar(medicamento);

        Medicamento atualizado = LiveDataTestUtil.getOrAwaitValue(dao.buscarPorId(id));
        assertEquals("500 mg", atualizado.getDosagem());
    }

    @Test
    public void excluir_removeMedicamentoDoBanco() throws InterruptedException {
        long id = dao.inserir(new Medicamento("Paracetamol", "750 mg", 1, 0L, null, null));
        Medicamento medicamento = LiveDataTestUtil.getOrAwaitValue(dao.buscarPorId(id));

        dao.excluir(medicamento);

        List<Medicamento> todos = LiveDataTestUtil.getOrAwaitValue(dao.listarTodos());
        assertTrue(todos.isEmpty());
    }
}
