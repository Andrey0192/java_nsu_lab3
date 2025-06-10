package threadpool;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class ThreadPool {
    private  final Deque<Task> tasks = new ArrayDeque<Task>();
    private  final List<Thread> threads;
    private volatile boolean running = true;

    public ThreadPool(int nWorkers) {
        running = true;
        threads = new ArrayList<Thread>();
        for (int i = 0; i < nWorkers; i++) {
            Thread worker = new Thread(() -> {
                while(true) {
                    Task task;
                    synchronized (tasks) {
                        while (tasks.isEmpty() && running) {
                            try {
                                tasks.wait();
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                        }
                        if (!running && tasks.isEmpty()) {
                            break;
                        }
                        task = tasks.removeFirst();
                    }
                    task.run();
                }
            });
            worker.start();
            threads.add(worker);
        }
    }

    public  void submit(Task task) {
        synchronized (tasks) {
            tasks.addLast(task);
            tasks.notify();
        }
    }



    public  int queueSize(){
        synchronized(tasks) {
            return tasks.size();
        }
    }

    public  void shutdown() {
        running = false;
        synchronized(tasks) {
            tasks.notifyAll();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}



