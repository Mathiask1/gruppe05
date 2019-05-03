package edu.sdu.sensumbosted.data;

import edu.sdu.sensumbosted.AuditAction;
import edu.sdu.sensumbosted.entity.AuthLevel;
import edu.sdu.sensumbosted.entity.Context;
import edu.sdu.sensumbosted.entity.Department;
import edu.sdu.sensumbosted.entity.User;

/**
 * This is used by the data layer for the purpose of bypassing the permission system
 */
public final class DatabaseContext extends Context {
    DatabaseContext(DataService data) {
        super(data);
    }

    @Override
    public AuthLevel getAuth() {
        return AuthLevel.SUPERUSER;
    }

    @Override
    public void assertAndLog(Department department, AuditAction action, AuthLevel auth) {}

    @Override
    public boolean checkMinimum(AuthLevel auth) { return true; }

    @Override
    public void assertAndLog(Department department, AuditAction action, AuthLevel auth, Runnable runnable) {
        runnable.run();
    }

    @Override
    public void assertMinimum(Department department, AuditAction action, AuthLevel auth) { }

    @Override
    public void setUser(User user) {
        throw new UnsupportedOperationException("Attempt to set the user in the database context");
    }

    @Override
    public User getUser() {
        return null;
    }
}
