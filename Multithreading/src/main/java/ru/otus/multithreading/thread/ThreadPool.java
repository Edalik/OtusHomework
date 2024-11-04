package ru.otus.multithreading.thread;

import java.util.LinkedList;

public class ThreadPool {

    private final CustomThread[] threads;

    private volatile boolean isShutdown;

    private final LinkedList<Runnable> tasks;

    public ThreadPool(Integer capacity) {
        this.threads = new CustomThread[capacity];
        this.tasks = new LinkedList<>();
        this.isShutdown = false;

        for (int i = 0; i < capacity; i++) {
            threads[i] = new CustomThread("Thread №" + i);
            threads[i].start();
        }
    }

    public synchronized void execute(Runnable task) {
        if (isShutdown) {
            throw new IllegalStateException("ThreadPool is shut down.");
        }

        tasks.add(task);

        notify();
    }

    public synchronized void shutdown() {
        isShutdown = true;
        System.out.println("Shutting down");

        notifyAll();
    }

    private class CustomThread extends Thread {

        private final String name;

        public CustomThread(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            while (true) {
                Runnable task;
                synchronized (ThreadPool.this) {
                    while (tasks.isEmpty()) {
                        if (isShutdown) {
                            System.out.println(name + " shutting down");
                            return;
                        }

                        try {
                            System.out.println(name + " waits");
                            ThreadPool.this.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }

                    task = tasks.poll();
                }

                System.out.println(name + " runs task" + task.hashCode());
                task.run();
            }
        }

    }

}