package model;


import java.util.LinkedList;
import java.util.Queue;

public class Storage<T> {
    private final Queue<T> storage = new LinkedList<>();
    private final int capacity;

    public Storage(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void add(T t) throws InterruptedException {
        while (storage.size() >= capacity) {
                wait();
        }
        storage.add(t);
        notifyAll();
    }

    public synchronized T take() throws InterruptedException{
        while(storage.isEmpty()){
            wait();
        }
        T t = storage.remove();
        notifyAll();
        return t;
    }


    public synchronized int size(){
        return storage.size();
    }
    public int getCapacity(){
        return capacity;
    }

}
