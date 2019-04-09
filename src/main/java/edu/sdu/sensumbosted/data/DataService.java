package edu.sdu.sensumbosted.data;

import edu.sdu.sensumbosted.AuditAction;
import edu.sdu.sensumbosted.entity.Context;

public class DataService {

    /*private final javax.sql.DataSource dataSource;
    private final JdbcTemplate departments;

    public DataService()  {
        dataSource = new PGSimpleDataSource();
        // TODO
        departments = new JdbcTemplate(dataSource);
    }*/

    public void log(Context ctx, AuditAction action) {
        log(ctx, action, "");
    }

    public void log(Context ctx, AuditAction action, String description) {
        System.out.println(ctx.getUser() + " " + action + " " + description);
    }

}
