package org.acme;

public class Entity {

    public String id;
    public String name;
    public Ref ref;


    public Entity(String id, String name, Ref ref) {
        this.id = id;
        this.name = name;
        this.ref = ref;
    }
}
