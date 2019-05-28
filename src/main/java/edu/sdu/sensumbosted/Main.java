package edu.sdu.sensumbosted;

import edu.sdu.sensumbosted.data.DataService;
import edu.sdu.sensumbosted.entity.AuthLevel;
import edu.sdu.sensumbosted.entity.Context;
import edu.sdu.sensumbosted.entity.Department;
import edu.sdu.sensumbosted.entity.User;
import edu.sdu.sensumbosted.presentation.ControllerLauncher;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.*;
import java.util.stream.Collectors;

public class Main extends Application {

    private final HashMap<UUID, Department> departments;
    private final DataService data = new DataService();
    private final Context context = new Context(data);
    private final SystemContext systemContext = new SystemContext(data);
    public final ControllerLauncher launcher = new ControllerLauncher(this);

    public static void main(String[] args) { launch(args); }

    public Main() {
        departments = data.loadDepartments(systemContext);
    }

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(launcher.launchMain());
        stage.setTitle("Sensum Bosted");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

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
    public Set<User> getUsers(Context ctx) {
        Set<User> set = departments.values().stream()
                .flatMap(department -> department.getVisibleUsers(ctx).stream())
                .collect(Collectors.toSet());
        ctx.data.log(ctx, AuditAction.DEPARTMENT_USERS_READ, String.format("Current user can see %d users", set.size()));
        return set;
    }

    public Context getContext() {
        return context;
    }

    public Map<UUID, Department> getDepartments() {
        return Collections.unmodifiableMap(departments);
    }

    public SystemContext getSystemContext() {
        return systemContext;
    }

    public ControllerLauncher getControllerLauncher() {
        return launcher;
    }
}
