package edu.sdu.sensumbosted.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.sdu.sensumbosted.AuditAction;
import edu.sdu.sensumbosted.entity.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataService {

    private final JdbcTemplate jdbc;

    public DataService() {
        HikariConfig conf = new HikariConfig("database.properties");
        conf.setMaximumPoolSize(2);
        jdbc = new JdbcTemplate(new HikariDataSource(conf));
    }

    public void create(Department department) {
        jdbc.update("INSERT INTO department VALUES(?, ?);", ps -> {
            ps.setString(1, department.getIdString());
            ps.setString(2, department.getName());
        });
    }

    public void create(Manager manager) {
        jdbc.update("INSERT INTO managers VALUES(?, ?, ?, ?);", ps -> {
            ps.setString(1, manager.getIdString());
            ps.setString(2, manager.getDepartment().getIdString());
            ps.setString(3, manager.getName());
            ps.setInt(4, manager.getAuth().ordinal());
        });
    }

    public void create(Patient patient) {
        jdbc.update("INSERT INTO patients VALUES(?, ?, ?, ?, ?, ?);", ps -> {
            ps.setString(1, patient.getIdString());
            ps.setString(2, patient.getDepartment().getIdString());
            ps.setString(3, patient.getName());
            ps.setBoolean(4, patient.isEnrolled());
            ps.setString(5, null); // TODO
            ps.setString(6, null); // TODO
        });
    }

    public void create(Practitioner practitioner) {
        jdbc.update("INSERT INTO practitioners VALUES(?, ?, ?);", ps -> {
            ps.setString(1, practitioner.getIdString());
            ps.setString(2, practitioner.getDepartment().getIdString());
            ps.setString(3, practitioner.getName());
        });
    }

    public void update(Department department) {
        jdbc.update("UPDATE department SET name = ? WHERE id = ?;", varargs(
                department.getName(),
                department.getId()
        ));
    }

    public void update(Manager manager) {
        jdbc.update("UPDATE managers SET department = ?, name = ?, auth = ? WHERE id = ?;", varargs(
                manager.getDepartment().getId(),
                manager.getName(),
                manager.getAuth().ordinal(),
                manager.getId()
        ));
    }

    public void update(Patient patient) {
        jdbc.update("UPDATE patients SET department = ?, name = ?, enrolled = ?, diary = ?, calendar = ? WHERE id = ?;", varargs(
                patient.getDepartment().getId(),
                patient.getName(),
                patient.isEnrolled(),
                null,// TODO
                null,
                patient.getId()
        ));
    }

    public void update(Practitioner practitioner) {
        jdbc.update("UPDATE managers SET department = ?, name = ? WHERE id = ?;", varargs(
                practitioner.getDepartment().getId(),
                practitioner.getName(),
                practitioner.getId()
        ));
    }

    /**
     * Create a new relation between a practitioner and a patient
     */
    public void associate(Practitioner practitioner, Patient patient) {
        jdbc.update("INSERT INTO practitionerpatientrelation VALUES (?, ?);", ps -> {
            ps.setString(1, practitioner.getIdString());
            ps.setString(2, patient.getIdString());
        });
    }

    public void disassociate(Practitioner practitioner, Patient patient) {
        jdbc.update("DELETE FROM practitionerpatientrelation WHERE practitioner = ? AND patient = ?;", ps -> {
            ps.setString(1, practitioner.getIdString());
            ps.setString(2, patient.getIdString());
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

    /**
     * Convenience class to preserve my sanity.
     * <p>
     * This is at the cost of strong typing.
     */
    private class VarargSetter implements PreparedStatementSetter {

        private Object[] args;

        VarargSetter(Object... args) {
            this.args = args;
        }

        @Override
        public void setValues(PreparedStatement ps) throws SQLException {
            int i = 1;
            for (Object o : args) {
                ps.setObject(i++, o);
            }
        }
    }

    /**
     * Convenience function
     */
    private VarargSetter varargs(Object... args) {
        return new VarargSetter(args);
    }

}
