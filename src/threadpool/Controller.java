package threadpool;

import model.*;

public class Controller implements Runnable {
    private final Storage<Body> bodyStorage;
    private final Storage<Engine> engineStorage;
    private final Storage<Accessories> accessoriesStorage;
    private final Object controllerLock ;

    private final Storage<Auto> autoStorage;
    private final ThreadPool threadPool;

    private int lowAuto = 1;


    public Controller(Storage<Body> bodyStorage, Storage<Engine> engineStorage,
                      Storage<Accessories> accessoriesStorage, Object controllerLock,
                      Storage<Auto> autoStorage, ThreadPool threadPool  , int lowAuto) {
        this.bodyStorage = bodyStorage;
        this.engineStorage = engineStorage;
        this.accessoriesStorage = accessoriesStorage;
        this.autoStorage = autoStorage;
        this.threadPool = threadPool;
        this.lowAuto = lowAuto;
        this.controllerLock = controllerLock;

    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < lowAuto; i++) {
                threadPool.submit(new WorkerTask(bodyStorage , engineStorage ,accessoriesStorage, autoStorage));
            }

            while(!Thread.currentThread().isInterrupted()) {
                synchronized(controllerLock){
                    controllerLock.wait();
                }
                threadPool.submit(new WorkerTask(bodyStorage , engineStorage ,accessoriesStorage, autoStorage));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }
}
