package supplier;

import model.Storage;
import util.IdGenerator;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;


public class SupplierWorker<T> implements Runnable{
    private final Storage<T> storage;
    private final Supplier<T> factory;
    private AtomicLong delay;
    private final Thread thread;
    private final AtomicLong produced ;

    private final int id;
    public SupplierWorker(Storage<T> storage, Supplier<T> factory, long delay) {
        this.storage  = storage;
        this.factory  = factory;
        id            = IdGenerator.getId();
        produced      = new AtomicLong();
        this.delay    = new AtomicLong(delay);
        this.thread   = new Thread(this,"SupplierWorker № "+ id +" : " + storage.getClass().getSimpleName() );
    }

    public void start(){
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
//            thread.start(); //так можно делать?
            while(!thread.isInterrupted()){
                Thread.sleep(delay.get());
                T item = factory.get();
                storage.add(item);
                produced.incrementAndGet();
            }
        } catch (InterruptedException _) {
        }
    }

    public void stop() {
        thread.interrupt();
    }

    public long getProduced() {
        return produced.get();
    }

    public Thread getThread() {
        return thread;
    }

}