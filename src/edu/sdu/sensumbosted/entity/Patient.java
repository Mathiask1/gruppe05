package edu.sdu.sensumbosted.entity;

import java.util.List;
import java.util.Map;

public class Patient extends User {

    private Map<Integer, String> diary;
    private List<String> asignees;
    private boolean enrolled;

    Patient(String name, String id) {
        super(name,id);
    }

    @Override
    AuthLevel getAuth() {
        return AuthLevel.PATIENT;
    }
}
