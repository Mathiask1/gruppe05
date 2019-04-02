package edu.sdu.sensumbosted;

import edu.sdu.sensumbosted.entity.Context;

public class DataService {

    public void log(Context ctx, AuditAction action) {
        log(ctx, action, "");
    }

    public void log(Context ctx, AuditAction action, String description) {
        System.out.println(ctx.getUser() + " " + action + " " + description);
    }

}
