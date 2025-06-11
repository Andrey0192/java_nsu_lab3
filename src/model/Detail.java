package model;

import util.IdGenerator;

public abstract class Detail {
    protected final int id ;

    protected Detail() {
        this.id = IdGenerator.getId();
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [id=" + id + "]";
    }
}
