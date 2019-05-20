package edu.sdu.sensumbosted.entity;

import edu.sdu.sensumbosted.AuditAction;
import edu.sdu.sensumbosted.Util;
import edu.sdu.sensumbosted.data.SqlTable;

import java.util.Optional;
import java.util.UUID;

public class Manager extends User {

    private AuthLevel authLevel;

    Manager(Department department, String name, AuthLevel authLevel) {
        super(Util.newId(), department, name);
        this.authLevel = authLevel;
    }

    public Manager(UUID id, Department department, String name, AuthLevel authLevel) {
        super(id, department, name);
        this.authLevel = authLevel;
    }

    @Override
    public AuthLevel getAuth() {
        return authLevel;
    }

    /**
     * Check if we can assign the auth level
     * @return An empty Optional if we are allowed, or a reason why we can't.
     */
    public Optional<String> canSetAuthLevel(Context ctx, AuthLevel newLevel) {
        if (newLevel == AuthLevel.PATIENT || newLevel == AuthLevel.PRACTITIONER)
            return Optional.of(newLevel + " should not be managed by this class");

        if (ctx.getAuth().ordinal() < authLevel.ordinal())
            return Optional.of("Cannot change auth level of more privileged user.");

        if (!ctx.checkMinimum(newLevel)) return Optional.of("Cannot change to auth level higher than your level.");

        // Admin and up and change perms
        if (!ctx.checkMinimum(getDepartment(), AuthLevel.LOCAL_ADMIN)) return Optional.of("Must be LOCAL_ADMIN or above");

        return Optional.empty();
    }

    public void setAuthLevel(Context ctx, AuthLevel newLevel) {
        canSetAuthLevel(ctx, newLevel).ifPresent(s -> {
            throw new SensumAccessException(ctx, AuditAction.MANAGER_AUTH_CHANGE, s);
        });

        ctx.data.log(ctx, AuditAction.MANAGER_AUTH_CHANGE, "Changing auth of " + this + " to " + newLevel);
        authLevel = newLevel;
        ctx.data.update(this);
    }

    @Override
    public SqlTable getSqlTable() {
        return SqlTable.MANAGER;
    }
}
