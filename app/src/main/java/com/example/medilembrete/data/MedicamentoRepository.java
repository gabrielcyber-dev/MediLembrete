package com.example.medilembrete.data;

import androidx.annotation.NonNull;

import com.example.medilembrete.data.dao.MedicamentoDao;
import com.example.medilembrete.data.entity.Medicamento;

/**
 * Implementação do cadastro e armazenamento dos medicamentos (Tela 2):
 * valida e grava um {@link Medicamento} fora da main thread. A configuração
 * dos horários (Tela 3) é feita depois, num passo separado, sobre o id
 * retornado aqui.
 */
public class MedicamentoRepository {

    /** Chamado numa thread de background - use runOnUiThread para mexer na UI. */
    public interface Callback {
        void aoConcluir(long medicamentoId);
    }

    private final MedicamentoDao medicamentoDao;

    public MedicamentoRepository(@NonNull AppDatabase db) {
        this.medicamentoDao = db.medicamentoDao();
    }

    public void cadastrar(@NonNull Medicamento medicamento, Callback callback) {
        MedicamentoValidator.validar(
                medicamento.getNome(),
                medicamento.getQuantidadePorDose(),
                medicamento.getDataInicio(),
                medicamento.getDataFim()
        );

        AppDatabase.executor.execute(() -> {
            long id = medicamentoDao.inserir(medicamento);
            if (callback != null) {
                callback.aoConcluir(id);
            }
        });
    }
}
