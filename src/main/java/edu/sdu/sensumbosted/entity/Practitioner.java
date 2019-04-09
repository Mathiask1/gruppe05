package edu.sdu.sensumbosted.entity;

import edu.sdu.sensumbosted.AuditAction;

import java.util.ArrayList;
import java.util.List;

public class Practitioner extends User {

    private ArrayList<Patient> assigned = new ArrayList<>();

    Practitioner(String name) {
        super(name);
    }

    @Override
    AuthLevel getAuth() {
        return AuthLevel.PRACTITIONER;
    }
    public void assign(Context ctx, Patient patient) {
        ctx.assertAndLog(AuditAction.PRACTITIONER_ASSIGN,
                AuthLevel.CASEWORKER,
                () -> this.assigned.add(patient)
        );
    }

    public void unassign(Context ctx, Patient patient) {
        ctx.assertAndLog(AuditAction.PRACTITIONER_UNASSIGN,
                AuthLevel.CASEWORKER,
                () -> this.assigned.remove(patient)
        );
    }

    public List<Patient> getPatients(Context ctx) {
        ctx.assertAndLog(AuditAction.PRACTITIONER_READ_ASSIGNED, AuthLevel.CASEWORKER);
        return this.assigned;
    }

}