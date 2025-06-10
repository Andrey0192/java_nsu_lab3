import Config.Config;
import Gui.FactoryFrame;
import model.*;
import supplier.SupplierWorker;
import threadpool.Controller;
import threadpool.ThreadPool;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Main {
    public static void main(String[] args) {
        Config config = new Config();





        int lowAuto = 50;

        int bodeDealy =100;
        int engineDelay = 500;
        int accessoriesDelay = 500;

        int dealersDelay = 500;


        Storage<Body> bodyStorage = new Storage<>(config.getInt("StorageBodySize" ,100));
        Storage<Engine> engineStorage = new Storage<>(config.getInt("StorageEngineSize" ,100));
        Storage<Accessories> accessoriesStorage = new Storage<>(config.getInt("StorageAccessoriesSize" ,100));

        Storage<Auto> autoStorage = new Storage<>(config.getInt("StorageAutoSize" ,100));

        SupplierWorker<Body> bodySupplierWorker = new SupplierWorker<>(bodyStorage ,Body::new, bodeDealy);
        SupplierWorker<Engine> engineSupplierWorker = new SupplierWorker<Engine>(engineStorage,Engine::new,engineDelay);


        List<Thread> workersThreads = new ArrayList<>();

        for (int i = 0; i < config.getInt("AccessoriesSuppliers", 10); i++) {
            Thread thread  = new Thread(new SupplierWorker<>(accessoriesStorage,Accessories::new,accessoriesDelay),
                    "Accessories № " + i);
            thread.start();
            workersThreads.add(thread);
        }


        ThreadPool threadPool = new ThreadPool(config.getInt("Workers" ,100));

        Thread controller =new Thread(
        new Controller(bodyStorage,engineStorage,
                accessoriesStorage,autoStorage,threadPool,lowAuto) ,
                "controller"
        );
        controller.start();

        List<Thread> dealersThreads = new ArrayList<>();


        for (int i = 0; i < config.getInt("Dealers",10); i++) {
            Thread dealer = new Thread(
            new Dealer(autoStorage ,dealersDelay , true, "./sales.log"),
                    "dealer № "+ i
            );
            dealer.start();
            dealersThreads.add(dealer);
        }


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            bodySupplierWorker.stop();
            engineSupplierWorker.stop();
            workersThreads.forEach(Thread::interrupt);

            controller.interrupt();

            dealersThreads.forEach(Thread::interrupt);

            threadPool.shutdown();

            try {
                bodySupplierWorker.getThread().join();
                engineSupplierWorker.getThread().join();
                for (Thread t : workersThreads) {
                    t.join();
                }
                controller.join();
                for (Thread d : dealersThreads) {
                    d.join();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }));


        SwingUtilities.invokeLater(() -> {
        });







    }
}
