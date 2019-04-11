package edu.sdu.sensumbosted.entity;

import edu.sdu.sensumbosted.data.DataEntity;

import java.util.Map;
import java.util.UUID;

public class Department implements DataEntity {
    private UUID id;
    private String name;
    private Map<String, User> members;

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getSqlTable() { return "departments"; }

    public String getName() {
        return name;
    }
}
