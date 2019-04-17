package edu.sdu.sensumbosted.entity;

import edu.sdu.sensumbosted.AuditAction;

import java.util.ArrayList;
import java.util.List;

public class Practitioner extends User {

    private ArrayList<Patient> assigned = new ArrayList<>();

    Practitioner(Department department, String name) {
        super(department, name);
    }

    @Override
    AuthLevel getAuth() {
        return AuthLevel.PRACTITIONER;
    }

    public void assign(Context ctx, Patient patient) {
        ctx.assertAndLog(AuditAction.PRACTITIONER_ASSIGN,
                AuthLevel.CASEWORKER,
                () -> {
                    ctx.data.relate(this, patient);
                    this.assigned.add(patient);
                }
        );
    }

    public void unassign(Context ctx, Patient patient) {
        ctx.assertAndLog(AuditAction.PRACTITIONER_UNASSIGN,
                AuthLevel.CASEWORKER,
                () -> {
                    ctx.data.unrelate(this, patient);
                    this.assigned.remove(patient);
                }
        );
    }

    public List<Patient> getPatients(Context ctx) {
        ctx.assertAndLog(AuditAction.PRACTITIONER_READ_ASSIGNED, AuthLevel.CASEWORKER);
        return this.assigned;
    }

    @Override
    public String getSqlTable() { return "practitioners"; }

}