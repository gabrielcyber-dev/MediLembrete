package com.example.medilembrete.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.medilembrete.LiveDataTestUtil;
import com.example.medilembrete.data.dao.HorarioDao;
import com.example.medilembrete.data.dao.MedicamentoDao;
import com.example.medilembrete.data.dao.RegistroDoseDao;
import com.example.medilembrete.data.entity.Horario;
import com.example.medilembrete.data.entity.Medicamento;
import com.example.medilembrete.data.entity.RegistroDose;
import com.example.medilembrete.data.entity.StatusDose;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(AndroidJUnit4.class)
public class RegistroDoseRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;
    private MedicamentoDao medicamentoDao;
    private HorarioDao horarioDao;
    private RegistroDoseDao registroDoseDao;
    private RegistroDoseRepository repository;

    private long hoje;
    private long umDia;

    @Before
    public void criarBancoEmMemoria() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        medicamentoDao = db.medicamentoDao();
        horarioDao = db.horarioDao();
        registroDoseDao = db.registroDoseDao();
        repository = new RegistroDoseRepository(db);

        hoje = System.currentTimeMillis();
        umDia = TimeUnit.DAYS.toMillis(1);
    }

    @After
    public void fecharBanco() {
        db.close();
    }

    /** Espera o callback do repositório, que roda numa thread de background. */
    private <T> T esperar(Escrita<T> escrita) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<T> resultado = new AtomicReference<>();

        escrita.executar(valor -> {
            resultado.set(valor);
            latch.countDown();
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new RuntimeException("Tempo esgotado esperando o callback do repositório");
        }
        return resultado.get();
    }

    private interface Escrita<T> {
        void executar(RegistroDoseRepository.Callback<T> callback);
    }

    /** Gera as doses do dia e devolve quantas foram criadas. */
    private int gerarDoses(long dia) throws InterruptedException {
        return this.<Integer>esperar(cb -> repository.gerarDosesDoDia(dia, cb));
    }

    /** Marca a dose como tomada e devolve se ela mudou de status. */
    private boolean marcarTomada(long registroId, long instante) throws InterruptedException {
        return this.<Boolean>esperar(cb -> repository.marcarComoTomada(registroId, instante, cb));
    }

    /** Marca as atrasadas como perdidas e devolve quantas mudaram. */
    private int marcarPerdidas(long limite) throws InterruptedException {
        return this.<Integer>esperar(cb -> repository.marcarAtrasadasComoPerdidas(limite, cb));
    }

    private long medicamentoComHorarios(String nome, Long dataFim, String... horarios) {
        long id = medicamentoDao.inserir(
                new Medicamento(nome, "50 mg", 1, hoje - umDia, dataFim, null));
        for (String horario : horarios) {
            horarioDao.inserir(new Horario(id, horario));
        }
        return id;
    }

    @Test
    public void gerarDosesDoDia_criaUmaDosePendenteParaCadaHorario() throws InterruptedException {
        medicamentoComHorarios("Losartana", null, "08:00", "20:00");

        int criadas = gerarDoses(hoje);

        assertEquals(2, criadas);
        List<RegistroDose> doses = LiveDataTestUtil.getOrAwaitValue(repository.dosesDoDia(hoje));
        assertEquals(2, doses.size());
        assertEquals(StatusDose.PENDENTE, doses.get(0).getStatus());
        assertTrue("a primeira dose do dia vem antes da segunda",
                doses.get(0).getDataHoraProgramada() < doses.get(1).getDataHoraProgramada());
    }

    @Test
    public void gerarDosesDoDia_chamadaDuasVezesNaoDuplicaNemPerdeOQueFoiTomado()
            throws InterruptedException {
        medicamentoComHorarios("Losartana", null, "08:00", "20:00");
        gerarDoses(hoje);

        List<RegistroDose> doses = LiveDataTestUtil.getOrAwaitValue(repository.dosesDoDia(hoje));
        marcarTomada(doses.get(0).getId(), hoje);

        int criadasNaSegundaVez = gerarDoses(hoje);

        assertEquals("nenhuma dose nova deve ser criada", 0, criadasNaSegundaVez);
        List<RegistroDose> depois = LiveDataTestUtil.getOrAwaitValue(repository.dosesDoDia(hoje));
        assertEquals(2, depois.size());
        assertEquals("a dose ja tomada tem que continuar tomada",
                StatusDose.TOMADO, depois.get(0).getStatus());
    }

    @Test
    public void gerarDosesDoDia_ignoraMedicamentoComTratamentoEncerrado() throws InterruptedException {
        medicamentoComHorarios("Em uso", null, "08:00");
        medicamentoComHorarios("Encerrado ontem", hoje - umDia, "09:00");

        int criadas = gerarDoses(hoje);

        assertEquals(1, criadas);
    }

    @Test
    public void gerarDosesDoDia_ignoraHorarioComFormatoInvalidoESalvaOsDemais()
            throws InterruptedException {
        medicamentoComHorarios("Losartana", null, "08:00", "25:99", "20:00");

        int criadas = gerarDoses(hoje);

        assertEquals("o horario invalido nao pode impedir os outros", 2, criadas);
    }

    @Test
    public void gerarDosesDoDia_medicamentoSemHorarioCadastradoNaoGeraDose() throws InterruptedException {
        medicamentoComHorarios("Sem horario", null);

        int criadas = gerarDoses(hoje);

        assertEquals(0, criadas);
    }

    @Test
    public void gerarDosesDoDia_geraSomenteParaODiaPedido() throws InterruptedException {
        medicamentoComHorarios("Losartana", null, "08:00");

        gerarDoses(hoje);
        gerarDoses(hoje + umDia);

        assertEquals(1, LiveDataTestUtil.getOrAwaitValue(repository.dosesDoDia(hoje)).size());
        assertEquals(1, LiveDataTestUtil.getOrAwaitValue(repository.dosesDoDia(hoje + umDia)).size());
        assertEquals("as duas doses coexistem no historico",
                2, LiveDataTestUtil.getOrAwaitValue(repository.historico()).size());
    }

    @Test
    public void marcarComoTomada_atualizaOsContadoresDoDia() throws InterruptedException {
        medicamentoComHorarios("Losartana", null, "08:00", "20:00");
        gerarDoses(hoje);
        List<RegistroDose> doses = LiveDataTestUtil.getOrAwaitValue(repository.dosesDoDia(hoje));

        boolean mudou = marcarTomada(doses.get(0).getId(), hoje);

        assertTrue(mudou);
        assertEquals(1, (int) LiveDataTestUtil.getOrAwaitValue(repository.tomadasNoDia(hoje)));
        assertEquals(1, (int) LiveDataTestUtil.getOrAwaitValue(repository.pendentesNoDia(hoje)));
    }

    @Test
    public void marcarComoTomada_avisaQuandoNaoMudouNada() throws InterruptedException {
        medicamentoComHorarios("Losartana", null, "08:00");
        gerarDoses(hoje);
        List<RegistroDose> doses = LiveDataTestUtil.getOrAwaitValue(repository.dosesDoDia(hoje));
        marcarTomada(doses.get(0).getId(), hoje);

        boolean mudouDeNovo = marcarTomada(doses.get(0).getId(), hoje);

        assertFalse("marcar a mesma dose de novo nao deve alterar nada", mudouDeNovo);
    }

    @Test
    public void marcarAtrasadasComoPerdidas_moveSoAsPendentesComHorarioPassado()
            throws InterruptedException {
        long medicamentoId = medicamentoComHorarios("Losartana", null, "08:00", "20:00");
        gerarDoses(hoje - umDia); // doses de ontem
        gerarDoses(hoje);         // doses de hoje

        int perdidas = marcarPerdidas(GeradorDeDoses.inicioDoDia(hoje));

        assertEquals("as duas doses de ontem viram perdidas", 2, perdidas);
        assertEquals("as de hoje continuam pendentes",
                2, (int) LiveDataTestUtil.getOrAwaitValue(repository.pendentesNoDia(hoje)));
        assertEquals(2, (int) LiveDataTestUtil.getOrAwaitValue(repository.perdidasNoDia(hoje - umDia)));
        assertEquals(4, LiveDataTestUtil.getOrAwaitValue(
                repository.historicoDoMedicamento(medicamentoId)).size());
    }

    @Test
    public void proximaDose_retornaAPrimeiraPendenteDepoisDoInstanteInformado()
            throws InterruptedException {
        medicamentoComHorarios("Losartana", null, "08:00", "14:00", "20:00");
        gerarDoses(hoje);
        long meioDia = GeradorDeDoses.instanteDaDose(hoje, "12:00");

        RegistroDose proxima = LiveDataTestUtil.getOrAwaitValue(repository.proximaDose(meioDia));

        assertNotNull(proxima);
        assertEquals(GeradorDeDoses.instanteDaDose(hoje, "14:00"), proxima.getDataHoraProgramada());
    }

    @Test
    public void excluirMedicamento_apagaAsDosesGeradasParaEle() throws InterruptedException {
        long medicamentoId = medicamentoComHorarios("Losartana", null, "08:00");
        gerarDoses(hoje);

        Medicamento medicamento = LiveDataTestUtil.getOrAwaitValue(
                medicamentoDao.buscarPorId(medicamentoId));
        medicamentoDao.excluir(medicamento);

        assertTrue(LiveDataTestUtil.getOrAwaitValue(repository.historico()).isEmpty());
        assertEquals(0, registroDoseDao.pendentesAtrasadas(Long.MAX_VALUE).size());
    }
}
