package edu.sdu.sensumbosted.entity;

import java.util.Map;
import java.util.UUID;

public class Department {
    private UUID id;
    private String name;
    private Map<String, User> members;

    public UUID getId() {
        return id;
    }
}
