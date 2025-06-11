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

        SupplierWorker<Body> bodySupplierWorker = new SupplierWorker<>(bodyStorage ,Body::new, config.getInt("BodySupplyDelay" ,100));
        bodySupplierWorker.start();
        SupplierWorker<Engine> engineSupplierWorker = new SupplierWorker<>(engineStorage,Engine::new, config.getInt("EngineDelay" ,100));
        engineSupplierWorker.start();
        List<SupplierWorker<Accessories>> accessoriesSupplierWorkers = new ArrayList<>();
        for (int i = 0; i < config.getInt("AccessoriesSuppliers", 10); i++) {
            SupplierWorker<Accessories> accessoriesSupplierWorker = new SupplierWorker<>(
                    accessoriesStorage,Accessories::new,
                    config.getInt("AccessoriesDelay", 100)
            );
            accessoriesSupplierWorkers.add(accessoriesSupplierWorker);
            accessoriesSupplierWorker.start();
        }

        //Для связи потока контроллера и диллеров
        Object controllerLock = new Object();

        ThreadPool threadPool = new ThreadPool(config.getInt("Workers" ,100));

        Controller controller = new Controller(bodyStorage, engineStorage,
                accessoriesStorage, controllerLock,
                autoStorage, threadPool,
                config.getInt("minCountAutoInStorage", 50));

        Thread thread =new Thread(controller ,"controller"
        );
        thread.start();

        List<Dealer> dealers = new ArrayList<>();

        for (int i = 0; i < config.getInt("Dealers",10); i++) {
            Dealer dealer = new Dealer(
                    autoStorage ,
                    config.getInt("DealersDelay" ,100) ,
                    config.getBoolean("LogSale", true), "./sales.log",
                    controllerLock);
            dealers.add(dealer);
            new Thread(dealer ,"dealer № "+i+1).start();
        }


        SwingUtilities.invokeLater(() ->
         new FactoryFrame(config,
                 bodySupplierWorker,bodyStorage,
                 engineSupplierWorker,engineStorage,
                 accessoriesSupplierWorkers,accessoriesStorage,
                 controller, autoStorage,
                 threadPool,dealers
                        ).setVisible(true)
        );

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            bodySupplierWorker.stop();
            engineSupplierWorker.stop();
            accessoriesSupplierWorkers.forEach(SupplierWorker::stop);
            dealers.forEach(Dealer::stop);
            threadPool.shutdown();

        }));

    }
}
