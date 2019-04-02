package edu.sdu.sensumbosted.entity;

import edu.sdu.sensumbosted.AuditAction;

public class SensumAccessException extends RuntimeException {

    private Context ctx;
    private AuditAction action;

    public SensumAccessException(Context ctx, AuditAction action) {
        super(ctx + " attempted to do " + action);
        this.ctx = ctx;
        this.action = action;
    }

    public SensumAccessException(Context ctx, AuditAction action, String s) {
        super(s);
        this.ctx = ctx;
        this.action = action;
    }
}
