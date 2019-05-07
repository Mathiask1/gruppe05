package edu.sdu.sensumbosted;

import edu.sdu.sensumbosted.data.DataService;
import edu.sdu.sensumbosted.entity.*;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Main {

    private final HashMap<UUID, Department> departments;
    private final DataService data = new DataService();
    private final Context context = new Context(data);
    private final SystemContext systemContext = new SystemContext(data);

    public static void main(String[] args) { new Main(); }

    private Main() {
        departments = data.loadDepartments(systemContext);
    }

    public void newDepartment(Context ctx, String name) {
        ctx.assertAndLog(null, AuditAction.DEPARTMENT_CREATE, AuthLevel.SUPERUSER, () -> {
            Department department = new Department(name);
            data.create(department);
            departments.put(department.getId(), department);
        });
    }

    /**
     * @return a set of users that the current user may see
     */
    public Set<User> getUsers(Context context) {
        User thisUser = context.getUser();
        return departments.values().stream()
                // Only superusers can see all departments
                .filter(department -> {
                   if (context.checkMinimum(AuthLevel.SUPERUSER)) return true;
                   else return thisUser.getDepartment() == department;
                }).flatMap(department -> department.getUsers(context).stream())
                .filter(user -> {
                    // Patients should be able to see who are assigned to them
                    if (thisUser instanceof Patient && user instanceof Practitioner) {
                        return ((Patient) thisUser).getAssignees(context).contains(user);
                    } else return context.checkMinimum(AuthLevel.CASEWORKER);
                }).collect(Collectors.toSet());
    }

}
