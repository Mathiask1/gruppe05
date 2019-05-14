package edu.sdu.sensumbosted.entity;

import edu.sdu.sensumbosted.AuditAction;
import edu.sdu.sensumbosted.data.DataEntity;

import java.util.*;
import java.util.stream.Collectors;

public class Department implements DataEntity {

    public static final String SQL_TABLE = "departments";

    public Department(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.members = new HashMap<>();
    }

    /**
     * DB access
     */
    public Department(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    private final UUID id;
    private String name;
    private Map<UUID, User> members = null;

    public void lateInit(Map<UUID, User> members) {
        if (members == null) throw new IllegalStateException("Members are already initialized");
        this.members = members;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getSqlTable() {
        return SQL_TABLE;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Department)) return false;

        Department that = (Department) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public Manager newManager(Context ctx, String name, AuthLevel auth) {
        ctx.assertAndLog(this, AuditAction.USER_CREATE, AuthLevel.LOCAL_ADMIN);
        Manager manager = new Manager(this, name, auth);
        ctx.data.create(manager);
        members.put(manager.getId(), manager);
        return manager;
    }

    public Practitioner newPractitioner(Context ctx, String name) {
        ctx.assertAndLog(this, AuditAction.USER_CREATE, AuthLevel.LOCAL_ADMIN);
        Practitioner practitioner = new Practitioner(this, name);
        members.put(practitioner.getId(), practitioner);
        ctx.data.create(practitioner);
        return practitioner;
    }

    public Patient newPatient(Context ctx, String name) {
        ctx.assertAndLog(this, AuditAction.USER_CREATE, AuthLevel.CASEWORKER);
        Patient patient = new Patient(this, name);
        members.put(patient.getId(), patient);
        ctx.data.create(patient);
        return patient;
    }

    public void deleteUser(Context ctx, User user) {
        if (this != user.getDepartment()) throw new IllegalArgumentException("Mismatching departments");
        ctx.assertAndLog(this, AuditAction.USER_DELETE, AuthLevel.LOCAL_ADMIN);
        members.remove(user.getId());
        ctx.data.delete(user);
    }

    public User getUser(Context ctx, UUID id) {
        ctx.assertAndLog(this, AuditAction.PRACTITIONER_READ_ASSIGNED, AuthLevel.PRACTITIONER);
        return members.get(id);
    }

    public Manager getManager(Context ctx, UUID id) {
        ctx.assertAndLog(this, AuditAction.MANAGER_READ, AuthLevel.PRACTITIONER);
        return (Manager) members.get(id);
    }

    public Practitioner getPractitioner(Context ctx, UUID id) {
        ctx.assertAndLog(this, AuditAction.PRACTITIONER_READ, AuthLevel.PRACTITIONER);
        return (Practitioner) members.get(id);
    }

    public Patient getPatient(Context ctx, UUID id) {
        ctx.assertAndLog(this, AuditAction.PATIENT_READ, AuthLevel.PRACTITIONER);
        return (Patient) members.get(id);
    }

    public List<User> getUsers(Context ctx) {
        ctx.assertAndLog(this, AuditAction.DEPARTMENT_USERS_READ, AuthLevel.PRACTITIONER);
        return new ArrayList<>(members.values());
    }

    public Set<User> getVisibleUsers(Context ctx) {
        User thisUser = ctx.getUser();

        // Only superusers can see all departments
        if (!ctx.checkAccess(this)) return Collections.emptySet();

        Set<User> set = members.values().stream().filter(user -> {
            // Patients should be able to see who are assigned to them
            if (thisUser instanceof Patient && user instanceof Practitioner) {
                return ((Patient) thisUser).getAssignees(ctx).contains(user);
            } else {
                return ctx.checkMinimum(AuthLevel.PRACTITIONER);
            }
        }).collect(Collectors.toSet());

        ctx.data.log(ctx, AuditAction.DEPARTMENT_USERS_READ);
        return set;
    }

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
