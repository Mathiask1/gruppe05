package edu.sdu.sensumbosted;

import edu.sdu.sensumbosted.data.DataService;
import edu.sdu.sensumbosted.entity.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.management.relation.Role;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Main extends Application {

    private final HashMap<UUID, Department> departments;
    private final DataService data = new DataService();
    private final Context context = new Context(data);
    private static Main instance = null;

    public static void main(String[] args) { launch(args); }

    public Main() {
        instance = this;
        departments = data.loadDepartments();

    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/views/MainWindow.fxml"));

        Scene scene = new Scene(root);

        stage.setTitle("FXML Welcome");
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
