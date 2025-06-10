package Gui;

import Config.Config;
import model.*;
import supplier.SupplierWorker;
import threadpool.ThreadPool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class FactoryFrame extends JFrame {

    private final Storage<Body> bodyStorage;
    private final Storage<Engine> engineStorage;
    private final Storage<Accessories> accessoriesStorage;
    private final Storage<Auto> autoStorage;

    private final SupplierWorker<Body> bodyWorker;
    private final SupplierWorker<Engine> engineWorker;
    private final List<SupplierWorker<Accessories>> accessoriesWorkers;



    private final JLabel lblBodyStorage         = new JLabel();
    private final JLabel lblEngineStorage       = new JLabel();
    private final JLabel lblAccessoriesStorage  = new JLabel();
    private final JLabel lblAutoStorage         = new JLabel();
//    private final JLabel lblAutoProduced        = new JLabel();
//    private final JLabel lblBodyProduced        = new JLabel();
//    private final JLabel lblEngineProduced      = new JLabel();
//    private final JLabel lblAccessoriesProduced = new JLabel();

    private final ThreadPool threadPool;
    private final List<Dealer> dealers;

    public FactoryFrame(Config config ,
                        SupplierWorker<Body> bodyWorker , Storage<Body> bodyStorage,
                        SupplierWorker<Engine> engineWorker , Storage<Engine> engineStorage,
                        List<SupplierWorker<Accessories>> accessoriesWorkers, Storage<Accessories> accessoriesStorage,
                        Storage<Auto> autoStorage,
                        ThreadPool threadPool, List<Dealer> dealers) {

        super("Factory");

        this.bodyStorage =bodyStorage;
        this.engineStorage =engineStorage;
        this.accessoriesStorage = accessoriesStorage;
        this.autoStorage = autoStorage;

        this.bodyWorker = bodyWorker;
        this.engineWorker = engineWorker;
        this.accessoriesWorkers = accessoriesWorkers;

        this.threadPool = threadPool;
        this.dealers = dealers;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 300);
        setLocationRelativeTo(null); //окно в центре
        setLayout(new BorderLayout());

        // ========== 1) Панель управления слайдерами ==========
        JPanel controls = new JPanel(new GridLayout(4,2,5,10));

        JSlider bodySlider = new JSlider(0, 2000, (int)config.getInt("BodySupplyDelay" ,50));
        JSlider engineSlider = new JSlider(0, 2000, (int)config.getInt("BodySupplyDelay" ,50));
        JSlider accessoriesSlider = new JSlider(0, 2000, (int)config.getInt("BodySupplyDelay" ,50));
        JSlider dealerSlider = new JSlider(0, 2000, (int)config.getInt("BodySupplyDelay" ,50));


        controls.setBorder(BorderFactory.createTitledBorder("Delays (ms)"));
        controls.add(new JLabel("Body supplier "));             controls.add(bodySlider);
        controls.add(new JLabel("Body engineSlider "));         controls.add(engineSlider);
        controls.add(new JLabel("Body accessoriesSlider "));    controls.add(accessoriesSlider);
        controls.add(new JLabel("Body dealerSlider "));         controls.add(dealerSlider);




        add(controls, BorderLayout.NORTH);

        bodySlider.addChangeListener(e -> {
            bodyWorker.setDelay(bodySlider.getValue());
        });
        engineSlider.addChangeListener(e -> {
            engineWorker.setDelay(engineSlider.getValue());
        });
        accessoriesSlider.addChangeListener(e -> {
            accessoriesWorkers.forEach(dealer ->
            {dealer.setDelay(accessoriesSlider.getValue());});
        });
        dealerSlider.addChangeListener(e -> {
            dealers.forEach(dealer -> dealer.setDelay(dealerSlider.getValue()));
        });

        // ========== 2) Панель статистики ==========
        JPanel status = new JPanel(new GridLayout(4,1,5,10));
        status.setBorder(BorderFactory.createTitledBorder("Factory Status"));

        status.add(lblBodyStorage);
        status.add(lblEngineStorage);
        status.add(lblAccessoriesStorage);
        status.add(lblAutoStorage);


        add(status, BorderLayout.CENTER);

        // ========== 3) Таймер для обновления UI ==========
        new Timer(100, e -> updateStats()).start();
        // ========== 4) Закрытие всех потоков ==========
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                bodyWorker         .stop();
                engineWorker       .stop();
                accessoriesWorkers .forEach(SupplierWorker::stop);
                dealers            .forEach(Dealer::stop);
                threadPool         .shutdown();
            }
        });
    }

        private void updateStats() {

            lblBodyStorage.setText("Bodies in storage: " +
                    bodyStorage.size() + " / " + bodyStorage.getCapacity() );
            lblEngineStorage.setText("Engine in storage: " +
                    engineStorage.size() + " / " + engineStorage.getCapacity() );
            lblAccessoriesStorage.setText("Accessories in storage: " +
                    accessoriesStorage.size() + " / " + accessoriesStorage.getCapacity() );
            lblAutoStorage.setText("Auto in storage: " +
                    autoStorage.size() + " / " + autoStorage.getCapacity() );


        };
}
