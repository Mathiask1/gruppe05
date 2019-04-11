package edu.sdu.sensumbosted.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.sdu.sensumbosted.AuditAction;
import edu.sdu.sensumbosted.entity.Context;
import org.postgresql.Driver;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.DriverManager;

public class DataService {

    private final DataSource dataSource;
    private final JdbcTemplate departments;

    public DataService()  {
        HikariConfig conf = new HikariConfig("database.properties");
        conf.setMaximumPoolSize(2);
        dataSource = new HikariDataSource(conf);
        departments = new JdbcTemplate(dataSource);
    }

    public void put(DataEntity entity) {

    }

    public void delete(DataEntity entity) {

    }

    public void log(Context ctx, AuditAction action) {
        log(ctx, action, "");
    }

    public void log(Context ctx, AuditAction action, String description) {
        System.out.println(ctx.getUser() + " " + action + " " + description);
    }

}
