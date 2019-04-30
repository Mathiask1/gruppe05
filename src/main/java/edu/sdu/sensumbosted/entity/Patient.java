package edu.sdu.sensumbosted.entity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Patient extends User {

    private UUID id;
    private Map<Integer, String> diary;
    private List<Practitioner> assignees = null;
    private boolean enrolled;


    Patient(Department department, String name) {
        super(department, name);
    }

    /** DB access */
    public Patient(UUID id, Department department, String name, Map<Integer, String> diary, boolean enrolled) {
        super(department, name);
        this.id = id;
        this.diary = diary;
        this.enrolled = enrolled;
    }

    public void lateInit(List<Practitioner> assignees) {
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
