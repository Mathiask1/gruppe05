package edu.sdu.sensumbosted.entity;

import edu.sdu.sensumbosted.AuditAction;
import edu.sdu.sensumbosted.Util;

import java.util.UUID;

public class Manager extends User {

    public static final String SQL_TABLE = "managers";
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

    public void setAuthLevel(Context ctx, AuthLevel newLevel) {
        if (newLevel == AuthLevel.PATIENT || newLevel == AuthLevel.PRACTITIONER)
            throw new IllegalArgumentException(newLevel + " should not be managed by this class");

        if (ctx.getAuth().ordinal() < authLevel.ordinal())
            throw new SensumAccessException(ctx, AuditAction.MANAGER_AUTH_CHANGE,
                    "Cannot change auth level of more privileged user.");

        if (!ctx.checkMinimum(newLevel)) throw new SensumAccessException(ctx, AuditAction.MANAGER_AUTH_CHANGE,
                "Cannot change to auth level higher than your level.");

        // Admin and up and change perms
        ctx.assertAndLog(getDepartment(), AuditAction.MANAGER_AUTH_CHANGE, AuthLevel.LOCAL_ADMIN, () -> {
            authLevel = newLevel;
            ctx.data.update(this);
        });
    }

    @Override
    public String getSqlTable() { return SQL_TABLE; }
}
