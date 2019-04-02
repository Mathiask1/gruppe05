package edu.sdu.sensumbosted.entity;

public abstract class User {

    private String name;

    User(String name) {
        this.name = name;
    }

    abstract AuthLevel getAuth();

    public String getName() {
        return name;
    }
}
