package threadpool;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class ThreadPool {
    private  final Deque<Task> tasks = new ArrayDeque<>();
    private  final List<Thread> workers = new ArrayList<>();
    private volatile boolean running = true;

    public ThreadPool(int nWorkers) {
        for (int i = 0; i < nWorkers; i++) {
            Thread worker = new Thread(this::taskLoop , "worker" + i + " from " + nWorkers  + " .\n");
            workers.add(worker);
            worker.start();
        }
    }

    public  void submit(Task task) {
        synchronized (tasks) {
            tasks.addLast(task);
            tasks.notify();
        }
    }


    private void taskLoop(){
        try {
            while (true) {
                Task task;
                synchronized (tasks) {
                    while (tasks.isEmpty() && running) {
                        try {
                            tasks.wait();
                        } catch (InterruptedException _) {
                        }
                    }
                    if (!running && tasks.isEmpty()) {
                        break;
                    }
                    task = tasks.removeFirst();
                }
                try {
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }
        } finally {
            System.out.println("Thread " + Thread.currentThread().getName() + " is ended.");
        }

    }

    public  int queueSize(){
        synchronized(tasks) {
            return tasks.size();
        }
    }

    public void shutDownNow(){
        running = false;
        synchronized(tasks) {
            tasks.notifyAll();
        }
        // если workers выполняли run()
        workers.forEach(Thread::interrupt);

    }


    //Ждем завершения workers run()
    public  void shutdown() {
        running = false;
        synchronized(tasks) {
            tasks.notifyAll();
        }
    }
}



