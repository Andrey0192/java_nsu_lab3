import logging.Logger;
import model.Auto;
import model.Storage;

import java.util.concurrent.atomic.AtomicLong;

public class Dealer implements Runnable {
    private final Storage<Auto> autoStorage;
    private final AtomicLong delay;
    private final boolean logEnabled;
    private final Logger logger;

    public Dealer(Storage<Auto> autoStorage, long delay, boolean logEnabled, Logger logger) {
        this.autoStorage = autoStorage;
        this.delay       = new AtomicLong(delay);
        this.logEnabled = logEnabled;
        this.logger      = logger;
    }


    public void setDelay(long newDelay) {
        delay.set(newDelay);
    }

    public long getDelay() {
        return delay.get();
    }

    @Override
    public void run() {
        try {
            Auto auto = autoStorage.take();
            if (logEnabled) {

                logger.log(auto);
            }
            Thread.sleep(delay.get());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
