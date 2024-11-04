package ru.otus.multithreading;

import ru.otus.multithreading.thread.ThreadPool;

public class Main {

    public static void main(String[] args) {
        ThreadPool pool = new ThreadPool(3);

        for (int i = 0; i < 10; i++) {
            int taskId = i;

            Runnable task = () -> {
                System.out.println("Executing Task №" + taskId);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            };

            System.out.println("Task №" + taskId + " hash = task" + task.hashCode());
            pool.execute(task);
        }

        pool.shutdown();
    }

}