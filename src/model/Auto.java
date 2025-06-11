package model;

import util.IdGenerator;

public class Auto  {
    private final int id;
    private final Body body;
    private final Engine engine;
    private final Accessories accessories;


    public Auto(Body body, Engine engine, Accessories accessories) {
        this.body = body;
        this.engine = engine;
        this.accessories = accessories;
        this.id = IdGenerator.getId();
    }
    public int getId() {
        return id;
    }
    public Body getBody() {
        return body;
    }

    public Engine getEngine() {
        return engine;
    }
    public Accessories getAccessories() {
        return accessories;
    }





    @Override
    public String toString() {
        return "Auto{" +
                "id=" + id +
                ", body=" + body +
                ", engine=" + engine +
                ", accessories=" + accessories +
                '}';
    }
}
