package threadpool;

import model.*;

import java.util.concurrent.atomic.AtomicLong;


public class WorkerTask implements Task{

    Storage<Body> bodyStorage;
    Storage<Engine> engineStorage;
    Storage<Accessories> accessoriesStorage;
    private final AtomicLong producedCounter ;


    Storage<Auto> autoStorage;

    WorkerTask(Storage<Body> bodyStorage, Storage<Engine> engineStorage,
               Storage<Accessories> accessoriesStorage, Storage<Auto> autoStorage, AtomicLong producedCounter) {
        this.bodyStorage        = bodyStorage;
        this.engineStorage      = engineStorage;
        this.accessoriesStorage = accessoriesStorage;
        this.autoStorage        = autoStorage;
        this.producedCounter = producedCounter;
    }


    @Override
    public void run() {
        try {
            Body body               = bodyStorage.take();
            Engine engine           = engineStorage.take();
            Accessories accessories = accessoriesStorage.take();

            Auto auto = new Auto(body, engine, accessories);
            autoStorage.add(auto);
            producedCounter.incrementAndGet();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
