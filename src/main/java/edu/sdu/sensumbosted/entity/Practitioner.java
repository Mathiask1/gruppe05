package edu.sdu.sensumbosted.entity;

import edu.sdu.sensumbosted.AuditAction;
import edu.sdu.sensumbosted.Util;
import edu.sdu.sensumbosted.data.SqlTable;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Practitioner extends User {

    private List<Patient> assigned = null;

    Practitioner(Department department, String name) {
        super(Util.newId(), department, name);
    }

    public Practitioner(UUID id, Department department, String name) {
        super(id, department, name);
    }

    public void lateInit(List<Patient> assigned) {
        if (assigned == null) throw new IllegalStateException("Assigned are already initialized");
        this.assigned = assigned;
    }

    /**
     * @return if the user is properly initialized by the DB
     */
    public boolean isInitialized() {
        return assigned != null;
    }

    @Override
    public AuthLevel getAuth() {
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
        if (ctx.getUser() == this) return assigned;
        ctx.assertAndLog(getDepartment(), AuditAction.PRACTITIONER_READ_ASSIGNED, AuthLevel.CASEWORKER);
        return Collections.unmodifiableList(this.assigned);
    }

    @Override
    public SqlTable getSqlTable() {
        return SqlTable.PRACTITIONER;
    }

}