package supplier;

import model.Storage;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;


public class SupplierWorker<T> implements Runnable{
    private final Storage<T> storage;
    private final Supplier<T> factory;
    private AtomicLong delay;
    private final Thread thread;
    public SupplierWorker(Storage<T> storage, Supplier<T> factory, long delay) {
        this.storage  = storage;
        this.factory = factory;
        this.delay    = new AtomicLong(delay);
        this.thread   = new Thread(this,"StorageWorker" + storage.getClass().getSimpleName() );
        this.thread.start();
    }

    public void setDelay(long newDelay) {
        delay.set(newDelay);
    }

    public long getDelay() {
        return delay.get();
    }

    public void run()  {
        try {
            while(!thread.isInterrupted()){
                T item = factory.get();
                storage.add(item);
                Thread.sleep(delay.get());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
