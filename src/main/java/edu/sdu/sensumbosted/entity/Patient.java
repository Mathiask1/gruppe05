package edu.sdu.sensumbosted.entity;

import java.util.List;
import java.util.Map;

public class Patient extends User {

    private Map<Integer, String> diary;
    private List<String> asignees;
    private boolean enrolled;

    Patient(String name) {
        super(name);
    }

    @Override
    AuthLevel getAuth() {
        return AuthLevel.PATIENT;
    }

    @Override
    public String getSqlTable() { return "patients"; }
}
