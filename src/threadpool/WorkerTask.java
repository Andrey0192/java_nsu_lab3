package threadpool;

import model.*;

import java.util.concurrent.atomic.AtomicLong;


public class WorkerTask implements Task{

    Storage<Body> bodyStorage;
    Storage<Engine> engineStorage;
    Storage<Accessories> accessoriesStorage;
    private final AtomicLong produced = new AtomicLong();


    Storage<Auto> autoStorage;

    WorkerTask(Storage<Body> bodyStorage, Storage<Engine> engineStorage,
               Storage<Accessories> accessoriesStorage, Storage<Auto> autoStorage) {
        this.bodyStorage = bodyStorage;
        this.engineStorage = engineStorage;
        this.accessoriesStorage = accessoriesStorage;
        this.autoStorage = autoStorage;
    }


    @Override
    public void run() {
        try {
            Body body = bodyStorage.take();
            Engine engine = engineStorage.take();
            Accessories accessories = accessoriesStorage.take();
            Auto auto = new Auto(body, engine, accessories);
            autoStorage.add(auto);
            produced.incrementAndGet();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    public long getProduced() {
        return produced.get();
    }
}
