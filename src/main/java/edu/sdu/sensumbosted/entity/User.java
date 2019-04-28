package edu.sdu.sensumbosted.entity;

import edu.sdu.sensumbosted.Util;
import edu.sdu.sensumbosted.data.DataEntity;
import java.util.UUID;

public abstract class User implements DataEntity  {

    private UUID id;
    private Department department;
    private String name;

    User(Department department, String name) {
        this.department = department;
        this.name = name;
        this.id = Util.newId();
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

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
