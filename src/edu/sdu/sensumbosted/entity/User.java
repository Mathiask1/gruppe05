package edu.sdu.sensumbosted.entity;

import edu.sdu.sensumbosted.Util;

public abstract class User {

    private String name;
    private String id;

    User(String name) {
        this.name = name;
        this.id = Util.newId();

    }

    abstract AuthLevel getAuth();

    public String getName() {
        return name;
    }
    public String getId() {
        return id;
    }
}
