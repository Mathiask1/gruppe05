package edu.sdu.sensumbosted.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.sdu.sensumbosted.AuditAction;
import edu.sdu.sensumbosted.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

public class DataService {

    final JdbcTemplate jdbc;
    private final DepartmentLoader departmentLoader;
    private static final Logger log = LoggerFactory.getLogger(DataService.class);
    final DatabaseContext context = new DatabaseContext(this);

    public DataService() {
        HikariConfig conf = new HikariConfig("database.properties");
        conf.validate();
        log.info("Database config passed validation");
        conf.setMaximumPoolSize(2);
        jdbc = new JdbcTemplate(new HikariDataSource(conf));
        departmentLoader = new DepartmentLoader(this);
    }

    public HashMap<UUID, Department> loadDepartments() {
        return departmentLoader.loadDepartments();
    }

    public void create(Department department) {
        jdbc.update("INSERT INTO departments VALUES(?, ?);", varargs(
                department.getId(),
                department.getName()
        ));
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
            ps.setString(5, patient.getDiaryJson().toString());
            ps.setString(6, patient.getCalendarJson().toString());
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
        jdbc.update("UPDATE departments SET name = ? WHERE id = ?;", varargs(
                department.getName(),
                department.getId()
        ));
    }

    public void update(Manager manager) {
        jdbc.update("UPDATE managers SET department = ?, name = ?, auth = ? WHERE id = ?;", varargs(
                manager.getDepartment().getId(),
                manager.getName(),
                manager.getAuth().toString(),
                manager.getId()
        ));
    }

    public void update(Patient patient) {
        jdbc.update("UPDATE patients SET department = ?, name = ?, enrolled = ?, diary = ?, calendar = ? WHERE id = ?;", varargs(
                patient.getDepartment().getId(),
                patient.getName(),
                patient.isEnrolled(),
                patient.getDiaryJson().toString(),
                patient.getCalendarJson().toString(),
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
        @SuppressWarnings("SqlResolve")
        int changed = jdbc.update("DELETE FROM ? WHERE id = ?;", varargs(
                entity.getSqlTable(),
                entity.getId())
        );
        return changed > 0;
    }

    public void log(Context ctx, AuditAction action) {
        log(ctx, action, "");
    }

    public void log(Context ctx, AuditAction action, String description) {
        log.info("{} {} {}", ctx.getUser(), action, description);
        jdbc.update("INSERT INTO audit (time, actor, action, description) VALUES(?, ?, ?, ?);", varargs(
                Instant.now(), ctx.getUser().getId(), action.toString(), description
        ));
    }

    /**
     * Convenience class to preserve my sanity.
     * <p>
     * This is at the cost of strong typing.
     */
    class VarargSetter implements PreparedStatementSetter {

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
    VarargSetter varargs(Object... args) {
        return new VarargSetter(args);
    }

}
