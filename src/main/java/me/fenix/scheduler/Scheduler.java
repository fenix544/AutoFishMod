package me.fenix.scheduler;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Scheduler {

    private final String name;
    private ExecutorService executor;

    public Scheduler(String name) {
        this.name = name;
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2,
                new ThreadFactoryBuilder().setNameFormat(name + "-%d").build());
    }

    public CompletableFuture<Void> runAsyncTask(Runnable runnable) {
        return CompletableFuture.supplyAsync(() -> {
            runnable.run();
            return null;
        }, this.executor);
    }

    public void shutdownTasks() {
        this.executor.shutdownNow();
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2,
                new ThreadFactoryBuilder().setNameFormat(this.name + "-%d").build());
    }
}
