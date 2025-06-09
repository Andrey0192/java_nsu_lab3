import logging.DillerLogger;
import model.Auto;
import model.Storage;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

public class Dealer implements Runnable {
    private final Storage<Auto> autoStorage;
    private final AtomicLong delay;
    private final boolean logEnabled;
    private final DillerLogger dillerLogger;
    private final String filePath;

    public Dealer(Storage<Auto> autoStorage, long delay, boolean logEnabled, String filePath) {
        this.autoStorage = autoStorage;
        this.delay       = new AtomicLong(delay);
        this.logEnabled = logEnabled;
        this.filePath = filePath;
        try {
            this.dillerLogger = new DillerLogger("DillerLogger" , filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

                dillerLogger.log(auto , DillerLogger.Level.INFO , "execute...");
            }
            Thread.sleep(delay.get());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
