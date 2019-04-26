package edu.sdu.sensumbosted;

import edu.sdu.sensumbosted.data.DataService;
import edu.sdu.sensumbosted.entity.AuthLevel;
import edu.sdu.sensumbosted.entity.Context;
import edu.sdu.sensumbosted.entity.Department;

import java.util.HashMap;
import java.util.UUID;

public class Main {

    private final HashMap<UUID, Department> departments;
    private final DataService data = new DataService();
    private final Context context = new Context(data);

    public static void main(String[] args) { new Main(); }

    private Main() {
        departments = data.loadDepartments();
    }

    public void newDepartment(Context ctx, String name) {
        ctx.assertAndLog(AuditAction.DEPARTMENT_CREATE, AuthLevel.SUPERUSER, () -> {
            Department department = new Department(name);
            data.create(department);
            departments.put(department.getId(), department);
        });
    }

}
