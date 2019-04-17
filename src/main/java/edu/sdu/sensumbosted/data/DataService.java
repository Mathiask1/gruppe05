package edu.sdu.sensumbosted.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.sdu.sensumbosted.AuditAction;
import edu.sdu.sensumbosted.entity.*;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class DataService {

    private final JdbcTemplate jdbc;

    public DataService()  {
        HikariConfig conf = new HikariConfig("database.properties");
        conf.setMaximumPoolSize(2);
        jdbc = new JdbcTemplate(new HikariDataSource(conf));
    }

    public void create(Department department) {
        jdbc.update("INSERT INTO department VALUES(?, ?)", ps -> {
            ps.setString(1, department.getId().toString());
            ps.setString(2, department.getName());
        });
    }

    public void create(Manager manager) {
        jdbc.update("INSERT INTO managers VALUES(?, ?, ?, ?)", ps -> {
            ps.setString(1, manager.getId().toString());
            ps.setString(2, manager.getDepartment().getId().toString());
            ps.setString(3, manager.getName());
            ps.setInt(4, manager.getAuth().ordinal());
        });
    }

    public void create(Patient patient) {
        jdbc.update("INSERT INTO patients VALUES(?, ?, ?, ?, ?, ?)", ps -> {
            ps.setString(1, patient.getId().toString());
            ps.setString(2, patient.getDepartment().getId().toString());
            ps.setString(3, patient.getName());
            ps.setBoolean(4, patient.isEnrolled());
            ps.setString(5, null); // TODO
            ps.setString(6, null); // TODO
        });
    }

    public void create(Practitioner practitioner) {
        jdbc.update("INSERT INTO practitioners VALUES(?, ?, ?)", ps -> {
            ps.setString(1, practitioner.getId().toString());
            ps.setString(2, practitioner.getDepartment().getId().toString());
            ps.setString(3, practitioner.getName());
        });
    }

    /**
     * @param entity the entity to delete
     * @return true if deleted
     */
    public boolean delete(DataEntity entity) {
        //noinspection SqlResolve
        int changed = jdbc.update("DELETE FROM " + entity.getSqlTable() + " WHERE id = '" + entity.getId() + "';");
        return changed > 0;
    }

    public void log(Context ctx, AuditAction action) {
        log(ctx, action, "");
    }

    public void log(Context ctx, AuditAction action, String description) {
        System.out.println(ctx.getUser() + " " + action + " " + description);
    }

}
