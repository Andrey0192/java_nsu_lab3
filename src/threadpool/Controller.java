package threadpool;

import model.*;

import java.util.concurrent.atomic.AtomicLong;

public class Controller implements Runnable {
    private final Storage<Body> bodyStorage;
    private final Storage<Engine> engineStorage;
    private final Storage<Accessories> accessoriesStorage;
    private final Object controllerLock ;

    private final Storage<Auto> autoStorage;
    private final ThreadPool threadPool;

    private int minCountAutoInStorage = 1;

    private AtomicLong producedCounter = new AtomicLong(0);


    public Controller(Storage<Body> bodyStorage, Storage<Engine> engineStorage,
                      Storage<Accessories> accessoriesStorage, Object controllerLock,
                      Storage<Auto> autoStorage, ThreadPool threadPool  , int minCountAutoInStorage) {
        this.bodyStorage           = bodyStorage;
        this.engineStorage         = engineStorage;
        this.accessoriesStorage    = accessoriesStorage;
        this.autoStorage           = autoStorage;
        this.threadPool            = threadPool;
        this.minCountAutoInStorage = minCountAutoInStorage;
        this.controllerLock        = controllerLock;

    }

    public long getProducedCounter() {
        return producedCounter.get();
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < minCountAutoInStorage; i++) {
                threadPool.submit(new WorkerTask(bodyStorage , engineStorage ,accessoriesStorage, autoStorage,producedCounter));
            }

            while(!Thread.currentThread().isInterrupted()) {
                synchronized(controllerLock){
                    controllerLock.wait();
                }
                threadPool.submit(new WorkerTask(bodyStorage , engineStorage ,accessoriesStorage, autoStorage,producedCounter));
            }
        } catch (InterruptedException _) {

        }

    }
}
