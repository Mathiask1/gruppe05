package edu.sdu.sensumbosted.entity;

import edu.sdu.sensumbosted.AuditAction;
import edu.sdu.sensumbosted.data.DataService;
import org.jetbrains.annotations.Nullable;

public class Context {

    public final DataService data;
    private User user = null;

    public Context(DataService data) {
        this.data = data;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AuthLevel getAuth() {
        return user != null ? user.getAuth() : AuthLevel.NO_AUTH;
    }

    public boolean checkMinimum(AuthLevel auth) {
        return auth.ordinal() <= getAuth().ordinal();
    }

    public boolean checkAccess(@Nullable Department department) {
        return department == null
                || checkMinimum(AuthLevel.SUPERUSER)
                || (getUser() != null && getUser().getDepartment() == department);
    }

    public boolean checkMinimum(@Nullable Department department, AuthLevel auth) {
        return auth.ordinal() >= getAuth().ordinal() && checkAccess(department);
    }

    public void assertMinimum(@Nullable Department department, AuditAction action, AuthLevel auth) {
        if (checkMinimum(auth) && checkAccess(department)) return;
        throw new SensumAccessException(this, action);
    }

    /**
     * Assert that the current user has at least a certain AuthLevel. Throws exception otherwise.
     * The action will be logged.
     */
    public void assertAndLog(@Nullable Department department, AuditAction action, AuthLevel auth) {
        assertMinimum(department, action, auth);
        data.log(this, action);
    }

    /**
     * Assert that the current user has at least a certain AuthLevel. Throws exception otherwise.
     * The callback will be run afterwards.
     * The action will be logged.
     */
    public void assertAndLog(Department department, AuditAction action, AuthLevel auth, Runnable runnable) {
        assertMinimum(department, action, auth);
        runnable.run();
        data.log(this, action);
    }

}
