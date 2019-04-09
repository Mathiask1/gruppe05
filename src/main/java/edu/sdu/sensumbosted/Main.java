package edu.sdu.sensumbosted;

import edu.sdu.sensumbosted.data.DataService;
import edu.sdu.sensumbosted.entity.AuthLevel;
import edu.sdu.sensumbosted.entity.Context;
import edu.sdu.sensumbosted.entity.Department;

import java.util.ArrayList;

public class Main {

    private final ArrayList<Department> departments = new ArrayList<>();
    private final DataService data = new DataService();
    private final Context context = new Context(data);

    public static void main(String[] args) { new Main(); }

    public void newDepartment(Context ctx, String name) {
        ctx.assertAndLog(AuditAction.DEPARTMENT_CREATE, AuthLevel.SUPERUSER, () -> {
            // TODO
        });
    }

}
