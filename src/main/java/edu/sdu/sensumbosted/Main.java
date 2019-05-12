package edu.sdu.sensumbosted;

import edu.sdu.sensumbosted.data.DataService;
import edu.sdu.sensumbosted.entity.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Main extends Application {

    private final HashMap<UUID, Department> departments;
    private final DataService data = new DataService();
    private final Context context = new Context(data);
    private static Main instance = null;
    private final SystemContext systemContext = new SystemContext(data);

    public static void main(String[] args) { launch(args); }

    public Main() {
        instance = this;
        departments = data.loadDepartments(systemContext);

    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/views/MainWindow.fxml"));

        Scene scene = new Scene(root);

        stage.setMinWidth(640);
        stage.setMinHeight(480);
        stage.setTitle("Sensum Bosted");
        stage.setScene(scene);
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
        return departments.values().stream()
                .flatMap(department -> department.getVisibleUsers(ctx).stream())
                .collect(Collectors.toSet());
    }

    public static Main getInstance() {
        return instance;
    }

    public Context getContext() {
        return context;
    }

    public HashMap<UUID, Department> getDepartments() {
        return departments;
    }
}
