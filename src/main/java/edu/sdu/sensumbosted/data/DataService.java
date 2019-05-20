package edu.sdu.sensumbosted.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.sdu.sensumbosted.AuditAction;
import edu.sdu.sensumbosted.SystemContext;
import edu.sdu.sensumbosted.entity.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.lang.NonNull;
import org.springframework.jdbc.core.RowMapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DataService {

    final JdbcTemplate jdbc;
    private final DepartmentLoader departmentLoader;
    private static final Logger log = LoggerFactory.getLogger(DataService.class);
    SystemContext systemContext = null;

    public DataService() {
        HikariConfig conf = new HikariConfig("database.properties");
        conf.validate();
        log.info("Database config passed validation");
        conf.setMaximumPoolSize(2);
        jdbc = new JdbcTemplate(new HikariDataSource(conf));
        departmentLoader = new DepartmentLoader(this);
    }

    public HashMap<UUID, Department> loadDepartments(SystemContext systemContext) {
        this.systemContext = systemContext;
        return departmentLoader.loadDepartments();
    }

    public void create(Department department) {
        jdbc.update("INSERT INTO departments VALUES(?, ?);", varargs(
                department.getId(),
                department.getName()
        ));
    }

    public void create(Manager manager) {
        jdbc.update("INSERT INTO managers VALUES(?, ?, ?, ?);", varargs(
            manager.getId(),
            manager.getDepartment().getId(),
            manager.getName(),
            manager.getAuth().toString()
        ));
    }

    public void create(Patient patient) {
        jdbc.update("INSERT INTO patients VALUES(?, ?, ?, ?, ?, ?);", varargs(
            patient.getId(),
            patient.getDepartment().getId(),
            patient.getName(),
            patient.isEnrolled(),
            patient.getDiaryJson(),
            patient.getCalendarJson()
        ));
    }

    public void create(Practitioner practitioner) {
        jdbc.update("INSERT INTO practitioners VALUES(?, ?, ?);", varargs(
            practitioner.getId(),
            practitioner.getDepartment().getId(),
            practitioner.getName()
        ));
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
                patient.getDiaryJson(),
                patient.getCalendarJson(),
                patient.getId()
        ));
    }

    public void update(Practitioner practitioner) {
        jdbc.update("UPDATE practitioners SET department = ?, name = ? WHERE id = ?;", varargs(
                practitioner.getDepartment().getId(),
                practitioner.getName(),
                practitioner.getId()
        ));
    }

    /**
     * Create a new relation between a practitioner and a patient
     */
    public void associate(Practitioner practitioner, Patient patient) {
        jdbc.update("INSERT INTO practitionerpatientrelation VALUES (?, ?);", varargs(
                practitioner.getId(),
                patient.getId()
        ));
    }

    public void disassociate(Practitioner practitioner, Patient patient) {
        jdbc.update("DELETE FROM practitionerpatientrelation WHERE practitioner = ? AND patient = ?;", varargs(
                practitioner.getId(),
                patient.getId()
        ));
    }

    /**
     * @param entity the entity to delete
     * @return true if deleted
     */
    public boolean delete(DataEntity entity) {
        @SuppressWarnings("SqlResolve")
        int changed = jdbc.update("DELETE FROM " + entity.getSqlTable().getTableName() + " WHERE id = ?;", varargs(
                entity.getId())
        );
        return changed > 0;
    }

    /**
     * @param id the UUID of the entity to delete
     * @param table the table to delete from
     * @return true if deleted
     */
    public boolean delete(UUID id, String table) {
        @SuppressWarnings("SqlResolve")
        int changed = jdbc.update("DELETE FROM ? WHERE id = ?;", varargs(id, table));
        return changed > 0;
    }

    public List<Object> getRawUserRow(UUID id) {
        RowMapper<List<Object>> rowMapper = (rs, rowNum) -> {
            int i = 1;
            ArrayList<Object> list = new ArrayList<>();
            while (i < 100) {
                try {
                    list.add(rs.getObject(i++));
                } catch (SQLException e) {
                    break;
                }
            }
            return list;
        };
        List<List<Object>> row = jdbc.query("SELECT * FROM managers WHERE id = ?;", varargs(id), rowMapper);
        if (!row.isEmpty()) return row.get(0);

        row = jdbc.query("SELECT * FROM practitioners WHERE id = ?;", varargs(id), rowMapper);
        if (!row.isEmpty()) return row.get(0);

        row = jdbc.query("SELECT * FROM patients WHERE id = ?;", varargs(id), rowMapper);
        if (!row.isEmpty()) return row.get(0);
        return null;
    }

    public void log(Context ctx, AuditAction action) {
        log(ctx, action, "");
    }

    public void log(@NonNull Context ctx, @NonNull AuditAction action, @NonNull String description) {
        log.info("{} {} {}", ctx.getUser(), action, description);
        UUID userId = null;
        User user = ctx.getUser();
        if (user != null) userId = user.getId();
        jdbc.update("INSERT INTO audit (time, actor, action, description) VALUES(?, ?, ?, ?);", varargs(
                Instant.now(), userId, action.toString(), description
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
                if (o instanceof Instant) o = Timestamp.from((Instant) o);
                else if (o instanceof JSONObject || o instanceof JSONArray) {
                    PGobject pgo = new PGobject();
                    pgo.setType("json");
                    pgo.setValue(o.toString());
                    o = pgo;
                }
                ps.setObject(i++, o);
            }
        }
    }

    /**
     * Convenience function
     */
    @SuppressWarnings("WeakerAccess")
    VarargSetter varargs(Object... args) {
        return new VarargSetter(args);
    }

}
