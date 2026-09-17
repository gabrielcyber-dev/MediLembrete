package com.example.medilembrete;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Observa um LiveData de forma síncrona nos testes, evitando esperas
 * manuais (Thread.sleep) para o valor do Room chegar.
 */
public final class LiveDataTestUtil {

    private LiveDataTestUtil() {
    }

    public static <T> T getOrAwaitValue(@NonNull LiveData<T> liveData) throws InterruptedException {
        AtomicReference<T> valorObtido = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Observer<T> observer = new Observer<T>() {
            @Override
            public void onChanged(T valor) {
                valorObtido.set(valor);
                latch.countDown();
                liveData.removeObserver(this);
            }
        };
        liveData.observeForever(observer);

        if (!latch.await(2, TimeUnit.SECONDS)) {
            throw new RuntimeException("Tempo esgotado esperando o valor do LiveData");
        }
        return valorObtido.get();
    }
}
