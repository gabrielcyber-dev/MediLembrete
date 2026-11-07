package com.example.medilembrete.data;

import androidx.annotation.NonNull;
import com.example.medilembrete.data.entity.Horario;
import com.example.medilembrete.data.entity.Medicamento;
import java.util.List;

public class MedicamentoRepository {
    public interface Callback { void aoConcluir(long medicamentoId); }
    private final AppDatabase db;
    public MedicamentoRepository(@NonNull AppDatabase db) { this.db = db; }

    public void cadastrar(@NonNull Medicamento medicamento, Callback callback) {
        java.util.List<String> horarios = new java.util.ArrayList<>();
        String[] partes = medicamento.getHorarioInicial().split(":");
        int inicio = Integer.parseInt(partes[0]) * 60 + Integer.parseInt(partes[1]);
        int intervalo = medicamento.getIntervaloHoras() * 60;
        int quantidade = 24 / medicamento.getIntervaloHoras();
        for (int i = 0; i < quantidade; i++) {
            int total = (inicio + i * intervalo) % 1440;
            horarios.add(String.format(java.util.Locale.ROOT, "%02d:%02d", total / 60, total % 60));
        }
        cadastrar(medicamento, horarios, callback);
    }

    public void cadastrar(@NonNull Medicamento medicamento, @NonNull List<String> horarios, Callback callback) {
        MedicamentoValidator.validar(medicamento.getNome(), medicamento.getQuantidadePorDose(),
                medicamento.getDataInicio(), medicamento.getDataFim());
        if (horarios == null || horarios.isEmpty()) throw new IllegalArgumentException("Selecione um horário inicial.");
        AppDatabase.executor.execute(() -> {
            final long[] id = new long[1];
            db.runInTransaction(() -> {
                id[0] = db.medicamentoDao().inserir(medicamento);
                for (String hora : horarios) db.horarioDao().inserir(new Horario(id[0], hora));
            });
            if (callback != null) callback.aoConcluir(id[0]);
        });
    }
}
