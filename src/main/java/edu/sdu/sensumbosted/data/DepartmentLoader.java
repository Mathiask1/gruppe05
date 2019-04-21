package edu.sdu.sensumbosted.data;

import edu.sdu.sensumbosted.entity.Department;
import edu.sdu.sensumbosted.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

class DepartmentLoader {

    private static final Logger log = LoggerFactory.getLogger(DepartmentLoader.class);
    private final DataService data;

    DepartmentLoader(DataService data) {
        this.data = data;
    }

    HashMap<UUID, Department> loadDepartments() {
        log.info("Loading departments");
        // We'll keep the user maps so that we can mutate them later
        HashMap<Department, HashMap<UUID, User>> departments = new HashMap<>();

        // Load departments
        data.jdbc.query("SELECT * FROM departments;", (rs, rowNum) -> {
            HashMap<UUID, User> users = new HashMap<>();
            Department department = new Department(getId(rs), rs.getString("name"), users);
            departments.put(department, users);
            log.debug("Loaded {}", department);
            return department;
        });

        // TODO members

        HashMap<UUID, Department> resultingMap = new HashMap<>();
        departments.keySet().forEach(dep -> resultingMap.put(dep.getId(), dep));
        log.info("Loaded {} departments", resultingMap.size());
        return resultingMap;
    }

    private UUID getId(ResultSet rs) throws SQLException {
        return UUID.fromString(rs.getString("id"));
    }

}
