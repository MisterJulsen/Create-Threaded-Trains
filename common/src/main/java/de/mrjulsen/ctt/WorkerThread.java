package de.mrjulsen.ctt;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

public class WorkerThread {

    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
    private final Thread worker;

    public WorkerThread(String name) {
        worker = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Runnable task = taskQueue.take();
                    task.run();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, name);
        worker.start();
    }

    /** Übergibt eine Aufgabe an den Worker */
    public Future<?> submitTask(Runnable task) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        taskQueue.offer(() -> {
            try {
                task.run();
                future.complete(null);
            } catch (Throwable t) {
                future.completeExceptionally(t);
            }
        });

        return future;
    }

    public void shutdown() {
        worker.interrupt();
    }
}

