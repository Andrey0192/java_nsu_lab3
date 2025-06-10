package model;

import java.util.ArrayDeque;
import java.util.Deque;

public class Storage<T> {
    private final Deque<T> storage = new ArrayDeque<>();
    private final int capacity;
    public Storage(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void add(T t) throws InterruptedException {
        while (storage.size() == capacity) {
                wait();
        }
        storage.addLast(t);
        notifyAll();
    }

    public synchronized T take() throws InterruptedException{
        while(storage.isEmpty()){
            wait();
        }
        T t = storage.removeFirst();
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
