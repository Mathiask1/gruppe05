package edu.sdu.sensumbosted.entity;

public abstract class User {

    private String name;
    private String id;

    User(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public User(String name) {
    }

    abstract AuthLevel getAuth();

    public String getName() {
        return name;
    }
    public String getId() {
        return id;
    }
}
