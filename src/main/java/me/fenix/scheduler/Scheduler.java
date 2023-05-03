package me.fenix.scheduler;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Scheduler {

    private final Executor executor;

    public Scheduler(String name) {
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2,
                new ThreadFactoryBuilder().setNameFormat(name + "-%d").build());
    }

    public CompletableFuture<Void> runAsyncTask(Runnable runnable) {
        return CompletableFuture.supplyAsync(() -> {
            runnable.run();
            return null;
        }, executor);
    }

    public void shutdownTasks() {
        if (executor instanceof ExecutorService) {
            ((ExecutorService) executor).shutdownNow();
        }
    }
}
