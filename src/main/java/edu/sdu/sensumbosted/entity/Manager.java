package edu.sdu.sensumbosted.entity;

public class Manager extends User {

    private AuthLevel authLevel;

    Manager(Department department, String name, AuthLevel authLevel) {
        super(department, name);
        this.authLevel = authLevel;
    }

    @Override
    public AuthLevel getAuth() {
        return authLevel;
    }

    @Override
    public String getSqlTable() { return "managers"; }
}
