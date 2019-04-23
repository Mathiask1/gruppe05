package edu.sdu.sensumbosted.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Patient extends User {

    private Map<Integer, String> diary;
    private List<Practitioner> assignees = null;
    private boolean enrolled;

    Patient(Department department, String name) {
        super(department, name);
    }

    public void lateInit(ArrayList<Practitioner> assignees) {
        if (assignees == null) throw new IllegalStateException("Assignees are already initialized");
        this.assignees = assignees;
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
