import Config.Config;
import Gui.FactoryFrame;
import model.*;
import supplier.SupplierWorker;
import threadpool.Controller;
import threadpool.ThreadPool;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Config config = new Config();

        Storage<Body> bodyStorage = new Storage<>(config.getInt("StorageBodySize" ,100));
        Storage<Engine> engineStorage = new Storage<>(config.getInt("StorageEngineSize" ,100));
        Storage<Accessories> accessoriesStorage = new Storage<>(config.getInt("StorageAccessoriesSize" ,100));

        Storage<Auto> autoStorage = new Storage<>(config.getInt("StorageAutoSize" ,100));

        SupplierWorker<Body> bodySupplierWorker = new SupplierWorker<>(bodyStorage ,Body::new, config.getInt("BodyDelay" ,100));

        SupplierWorker<Engine> engineSupplierWorker = new SupplierWorker<>(engineStorage,Engine::new, config.getInt("EngineDelay" ,100));

        List<SupplierWorker<Accessories>> accessoriesSupplierWorkers = new ArrayList<>();

        for (int i = 0; i < config.getInt("AccessoriesSuppliers", 10); i++) {
            SupplierWorker<Accessories> accessoriesSupplierWorker = new SupplierWorker<>(accessoriesStorage,Accessories::new, config.getInt("AccessoriesSuppliers", i));
            accessoriesSupplierWorkers.add(accessoriesSupplierWorker);
        }


        ThreadPool threadPool = new ThreadPool(config.getInt("Workers" ,100));

        Thread controller =new Thread(
        new Controller(bodyStorage,engineStorage,
                accessoriesStorage,autoStorage,threadPool, config.getInt("LowAuto" ,50)) ,
                "controller"
        );
        controller.start();

        List<Dealer> dealers = new ArrayList<>();


        for (int i = 0; i < config.getInt("Dealers",10); i++) {
            Dealer dealer = new Dealer(
                    autoStorage ,
                    config.getInt("DealersDelay" ,100) ,
                    true,
                    "./sales.log");
            dealers.add(dealer);
            new Thread(dealer ,"dealer № "+i+1).start();
        }

        SwingUtilities.invokeLater(() ->

         new FactoryFrame(config,
                 bodySupplierWorker,bodyStorage,
                 engineSupplierWorker,engineStorage,
                 accessoriesSupplierWorkers,accessoriesStorage,
                 autoStorage,
                 threadPool,dealers
                        ).setVisible(true)
        );


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            bodySupplierWorker.stop();
            engineSupplierWorker.stop();
            accessoriesSupplierWorkers.forEach(SupplierWorker::stop);

            controller.interrupt();

            dealers.forEach(Dealer::stop);

            threadPool.shutdown();

            try {
                bodySupplierWorker.getThread().join();
                engineSupplierWorker.getThread().join();
                for (SupplierWorker<Accessories> t : accessoriesSupplierWorkers) {
                    t.stop();
                }
                controller.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }));


//        SwingUtilities.invokeLater(() -> {
//        });







    }
}
