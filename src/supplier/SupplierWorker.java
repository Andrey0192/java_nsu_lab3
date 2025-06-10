package supplier;

import model.Storage;
import util.IdGenerator;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;


public class SupplierWorker<T> implements Runnable{
    private final Storage<T> storage;
    private final Supplier<T> factory;
    private AtomicLong delay;
    private final Thread thread;
    private final int id;
    public SupplierWorker(Storage<T> storage, Supplier<T> factory, long delay) {
        this.storage  = storage;
        this.factory = factory;
        id = IdGenerator.getId();
        this.delay    = new AtomicLong(delay);
        this.thread   = new Thread(this,"SupplierWorker № "+ id +" : " + storage.getClass().getSimpleName() );
        thread.start();
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
            thread.interrupt();
        }
    }

    public void stop() {
        thread.interrupt();
    }

    public Thread getThread() {
        return thread;
    }
}
