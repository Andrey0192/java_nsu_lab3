package model;

import java.util.ArrayDeque;
import java.util.Deque;

public class Storage<T> {
    private final Deque<T> storage = new ArrayDeque<>();
    private final int capacity;
    public Storage(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void add(T t) {
        while (storage.size() == capacity) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        storage.addLast(t);
        notifyAll();
    }

    public synchronized T take(){
        while(storage.isEmpty()){
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

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
