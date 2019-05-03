package edu.sdu.sensumbosted.entity;

import edu.sdu.sensumbosted.AuditAction;

import java.util.List;

public class Practitioner extends User {

    private List<Patient> assigned = null;

    public Practitioner(Department department, String name) {
        super(department, name);
    }

    public void lateInit(List<Patient> assigned) {
        if (assigned == null) throw new IllegalStateException("Assigned are already initialized");
        this.assigned = assigned;
    }

    @Override
    AuthLevel getAuth() {
        return AuthLevel.PRACTITIONER;
    }

    public void assign(Context ctx, Patient patient) {
        ctx.assertAndLog(getDepartment(), AuditAction.PRACTITIONER_ASSIGN, AuthLevel.CASEWORKER, () -> {
            ctx.data.associate(this, patient);
            this.assigned.add(patient);
        });
    }

    public void unassign(Context ctx, Patient patient) {
        ctx.assertAndLog(getDepartment(), AuditAction.PRACTITIONER_UNASSIGN, AuthLevel.CASEWORKER, () -> {
            ctx.data.disassociate(this, patient);
            this.assigned.remove(patient);
        });
    }

    public List<Patient> getPatients(Context ctx) {
        ctx.assertAndLog(getDepartment(), AuditAction.PRACTITIONER_READ_ASSIGNED, AuthLevel.CASEWORKER);
        return this.assigned;
    }

    @Override
    public String getSqlTable() {
        return "practitioners";
    }

}