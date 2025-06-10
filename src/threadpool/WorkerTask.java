package threadpool;

import model.*;


public class WorkerTask implements Task{

    Storage<Body> bodyStorage;
    Storage<Engine> engineStorage;
    Storage<Accessories> accessoriesStorage;

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
            Body body = null;
            body = bodyStorage.take();
            Engine engine = engineStorage.take();
            Accessories accessories = accessoriesStorage.take();

            Auto auto = new Auto(body, engine, accessories);
            autoStorage.add(auto);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
