package edu.sdu.sensumbosted.entity;

import java.util.List;
import java.util.Map;

public class Patient extends User {

    private Map<Integer, String> diary;
    private List<String> asignees;
    private boolean enrolled;

    Patient(Department department, String name) {
        super(department, name);
    }

    @Override
    AuthLevel getAuth() {
        return AuthLevel.PATIENT;
    }

    @Override
    public String getSqlTable() { return "patients"; }

    public boolean isEnrolled() {
        return enrolled;
    }
}
