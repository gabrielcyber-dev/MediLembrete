package com.example.medilembrete.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.medilembrete.LiveDataTestUtil;
import com.example.medilembrete.data.entity.Medicamento;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@RunWith(AndroidJUnit4.class)
public class MedicamentoRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;
    private MedicamentoRepository repository;

    @Before
    public void criarBancoEmMemoria() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        repository = new MedicamentoRepository(db);
    }

    @After
    public void fecharBanco() {
        db.close();
    }

    @Test
    public void cadastrar_gravaMedicamentoEChamaCallbackComOId() throws InterruptedException {
        Medicamento medicamento = new Medicamento("Losartana", "50 mg", 1,
                1_700_000_000_000L, null, null);

        CountDownLatch latch = new CountDownLatch(1);
        AtomicLong idGerado = new AtomicLong(-1);

        repository.cadastrar(medicamento, id -> {
            idGerado.set(id);
            latch.countDown();
        });

        assertTrue("callback não foi chamado a tempo", latch.await(2, TimeUnit.SECONDS));

        Medicamento salvo = LiveDataTestUtil.getOrAwaitValue(
                db.medicamentoDao().buscarPorId(idGerado.get()));

        assertEquals("Losartana", salvo.getNome());
        assertNull(salvo.getDataFim());
    }

    @Test(expected = IllegalArgumentException.class)
    public void cadastrar_nomeVazio_lancaExcecaoAntesDeGravar() {
        Medicamento invalido = new Medicamento("", "50 mg", 1, 1_700_000_000_000L, null, null);
        repository.cadastrar(invalido, id -> { });
    }
}
