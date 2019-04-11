package edu.sdu.sensumbosted.entity;

import edu.sdu.sensumbosted.data.DataEntity;

import java.util.UUID;

public abstract class User implements DataEntity  {

    private UUID id;
    private Department department;
    private String name;

    User(Department department, String name) {
        id = UUID.randomUUID();
        this.department = department;
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

    public Department getDepartment() {
        return department;
    }
}
