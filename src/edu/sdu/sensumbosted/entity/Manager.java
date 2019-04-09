package edu.sdu.sensumbosted.entity;

public class Manager extends User {

    private AuthLevel authLevel;

    Manager(String name,String id, AuthLevel authLevel) {
        super(name,id);
        this.authLevel = authLevel;
    }

    @Override
    AuthLevel getAuth() {
        return authLevel;
    }
}
