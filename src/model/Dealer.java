package model;

import logging.DillerLogger;
import util.IdGenerator;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

public class Dealer implements Runnable {
    private final Storage<Auto> autoStorage;
    private final AtomicLong delay;
    private final boolean logEnabled;
    private final DillerLogger dillerLogger;
    private final int dealerId;
    private boolean running = true;
    private final Object controllerLock;

    public Dealer(Storage<Auto> autoStorage, long delay, boolean logEnabled, String filePath, Object controllerLock) {
        this.autoStorage = autoStorage;
        this.delay       = new AtomicLong(delay);
        this.logEnabled = logEnabled;
        this.controllerLock = controllerLock;
        dealerId = IdGenerator.getId();
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
            while (running) {
                Thread.sleep(delay.get());

                Auto auto = autoStorage.take();

                if (logEnabled) {
                    dillerLogger.log(auto , DillerLogger.Level.INFO , "execute: " +  dealerId );
                }
                synchronized (controllerLock) {
                    controllerLock.notify();
                }
            }
        } catch (InterruptedException e) {
            return;
        } finally {
            if (logEnabled) {
                dillerLogger.close();
            }
        }
    }
    public void stop() {
        running = false;
        Thread.currentThread().interrupt();
    }

}
