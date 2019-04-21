package edu.sdu.sensumbosted.entity;

import edu.sdu.sensumbosted.data.DataEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Department implements DataEntity {

    Department(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.members = new HashMap<>();
    }

    /** DB access */
    public Department(UUID id, String name, Map<UUID, User> members) {
        this.id = id;
        this.name = name;
        this.members = members;
    }

    private UUID id;
    private String name;
    private Map<UUID, User> members;

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getSqlTable() { return "departments"; }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Department)) return false;

        Department that = (Department) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
