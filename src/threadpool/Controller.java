package threadpool;

import model.*;

public class Controller implements Runnable {
    Storage<Body> bodyStorage;
    Storage<Engine> engineStorage;
    Storage<Accessories> accessoriesStorage;


    final Storage<Auto> autoStorage;
    ThreadPool threadPool;

    private int lowAuto = 1;


    public Controller(Storage<Body> bodyStorage, Storage<Engine> engineStorage,
                      Storage<Accessories> accessoriesStorage,
                      Storage<Auto> autoStorage, ThreadPool threadPool  , int lowAuto) {
        this.bodyStorage = bodyStorage;
        this.engineStorage = engineStorage;
        this.accessoriesStorage = accessoriesStorage;
        this.autoStorage = autoStorage;
        this.threadPool = threadPool;
        this.lowAuto = lowAuto;

    }

    @Override
    public void run() {
        try {
            while(!Thread.interrupted()) {
                synchronized(autoStorage){
                    while (autoStorage.size() > lowAuto){
                        autoStorage.wait();
                    }
                    int count =  autoStorage.getCapacity() - autoStorage.size();
                    for (int i = 0; i < count; i++){
                        threadPool.submit(new WorkerTask(bodyStorage , engineStorage ,accessoriesStorage, autoStorage));
                    }
                    autoStorage.wait();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }
}
