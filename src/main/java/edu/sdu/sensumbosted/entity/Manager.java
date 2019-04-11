package edu.sdu.sensumbosted.entity;

public class Manager extends User {

    private AuthLevel authLevel;

    Manager(String name, AuthLevel authLevel) {
        super(name);
        this.authLevel = authLevel;
    }

    @Override
    AuthLevel getAuth() {
        return authLevel;
    }

    @Override
    public String getSqlTable() { return "managers"; }
}
