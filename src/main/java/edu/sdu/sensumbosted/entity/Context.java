package edu.sdu.sensumbosted.entity;

import edu.sdu.sensumbosted.AuditAction;
import edu.sdu.sensumbosted.data.DataService;

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
        return auth.ordinal() >= getAuth().ordinal();
    }

    public void assertMinimum(AuditAction action, AuthLevel auth) {
        if (checkMinimum(auth)) return;
        throw new SensumAccessException(this, action);
    }

    /**
     * Assert that the current user has at least a certain AuthLevel. Throws exception otherwise.
     * The action will be logged.
     */
    public void assertAndLog(AuditAction action, AuthLevel auth) {
        assertMinimum(action, auth);
        data.log(this, action);
    }

    /**
     * Assert that the current user has at least a certain AuthLevel. Throws exception otherwise.
     * The callback will be run afterwards.
     * The action will be logged.
     */
    public void assertAndLog(AuditAction action, AuthLevel auth, Runnable runnable) {
        assertMinimum(action, auth);
        runnable.run();
        data.log(this, action);
    }

}
