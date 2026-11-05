package com.example.medilembrete.data;

import androidx.room.TypeConverter;

import com.example.medilembrete.data.entity.StatusDose;

/**
 * Ensina o Room a salvar/ler o enum {@link StatusDose} como texto no banco.
 */
public class Converters {

    @TypeConverter
    public static String fromStatusDose(StatusDose status) {
        return status == null ? null : status.name();
    }

    @TypeConverter
    public static StatusDose toStatusDose(String valor) {
        return valor == null ? null : StatusDose.valueOf(valor);
    }
}
