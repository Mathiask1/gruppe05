package edu.sdu.sensumbosted.entity;

import edu.sdu.sensumbosted.data.DataEntity;

import java.util.UUID;

public abstract class User implements DataEntity  {

    private UUID id;
    private String name;

    User(String name) {
        id = UUID.randomUUID();
        this.name = name;
    }

    abstract AuthLevel getAuth();

    public String getName() {
        return name;
    }

    @Override
    public UUID getId() {
        return id;
    }
}
