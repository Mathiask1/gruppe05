package edu.sdu.sensumbosted.entity;

import edu.sdu.sensumbosted.AuditAction;
import edu.sdu.sensumbosted.Util;

import java.util.Map;

public class Department {
    private String id;
    private String name;
    private Map<String, User> members;

    public Department (String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void newManager(Context ctx, String name, AuthLevel auth) {
        ctx.assertAndLog(AuditAction.USER_CREATE,AuthLevel.LOCAL_ADMIN);
        User manager = new Manager(name, auth);
        members.put(manager.getId(),manager);
    }

    public void newPractitioner(Context ctx, String name) {
        ctx.assertAndLog(AuditAction.USER_CREATE,AuthLevel.LOCAL_ADMIN);
        User practitioner = new Practitioner(name);
        members.put(practitioner.getId(),practitioner);
    }

    public void newPatient(Context ctx, String name) {
        ctx.assertAndLog(AuditAction.USER_CREATE,AuthLevel.CASEWORKER);
        User patient = new Patient(name);
        members.put(patient.getId(),patient);
    }

    public void deleteUser(Context ctx, User user) {
        ctx.assertAndLog(AuditAction.USER_DELETE,AuthLevel.LOCAL_ADMIN);
        members.remove(user);
    }

    public User getUser(Context ctx, String id) {
        ctx.assertAndLog(AuditAction.PRACTITIONER_READ_ASSIGNED,AuthLevel.PRACTITIONER);
        return members.get(id);
    }

    public Manager getManager(Context ctx, String id) {
        ctx.assertAndLog(AuditAction.MANAGER_READ,AuthLevel.PATIENT);
        return (Manager) members.get(id);
    }

    public Practitioner getPractitioner(Context ctx, String id) {
        ctx.assertAndLog(AuditAction.PRACTITIONER_READ,AuthLevel.PATIENT);
        return (Practitioner) members.get(id);
    }

    public Patient getPatient(Context ctx, String id) {
        ctx.assertAndLog(AuditAction.PATIENT_READ,AuthLevel.PATIENT);
        return (Patient) members.get(id);
    }

    // skal implementeres
    public void onUpdate(User user) {

    }
}
