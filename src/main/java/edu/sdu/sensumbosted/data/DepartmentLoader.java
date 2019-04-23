package edu.sdu.sensumbosted.data;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import edu.sdu.sensumbosted.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

class DepartmentLoader {

    private static final Logger log = LoggerFactory.getLogger(DepartmentLoader.class);
    private final DataService data;

    DepartmentLoader(DataService data) {
        this.data = data;
    }

    HashMap<UUID, Department> loadDepartments() {
        log.info("Loading departments and their members");

        // Load departments
        List<Department> depsList = data.jdbc.query("SELECT * FROM departments;", (rs, rowNum) -> {
            Department department = new Department(getId(rs, "id"), rs.getString("name"));
            log.debug("Loaded {}", department);
            return department;
        });
        ImmutableMap<UUID, Department> departments = Maps.uniqueIndex(depsList, Department::getId);
        log.info("Loaded {} departments", departments.size());

        List<Manager> managers = data.jdbc.query("SELECT * FROM managers;", (rs, rowNum) -> {
            Manager manager = new Manager(
                    getDepartment(departments, rs),
                    rs.getString("name"),
                    AuthLevel.valueOf(rs.getString("auth"))
            );
            log.debug("Loaded {}", manager);
            return manager;
        });

        log.info("Loaded {} managers", managers.size());

        HashMap<UUID, List<Practitioner>> assigneeRelations = new HashMap<>();
        HashMap<UUID, List<Patient>> assignedRelations = new HashMap<>();

        List<Practitioner> practitioners = data.jdbc.query("SELECT * FROM practitioners;", (rs, rowNum) -> {
            Practitioner practitioner = new Practitioner(getDepartment(departments, rs), rs.getString("name"));
            log.info("Loaded {}", practitioner);
            return practitioner;
        });

        log.info("Loaded {} practitioners", practitioners.size());

        return new HashMap<>(departments);
    }

    private UUID getId(ResultSet rs, String label) throws SQLException {
        return UUID.fromString(rs.getString(label));
    }

    private Department getDepartment(Map<UUID, Department> departments, ResultSet rs) throws SQLException {
        return departments.get(UUID.fromString(rs.getString("department")));
    }

}
