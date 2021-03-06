package edu.sdu.sensumbosted.presentation;

import edu.sdu.sensumbosted.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ControllerLauncher {

    private final Main main;
    private MainWindowController mainWindowController;

    public ControllerLauncher(Main main) {
        this.main = main;
    }

    public Parent launchMain() {
        return launch("/views/MainWindow.fxml");
    }

    void launchNewUserDialog() {
        Parent parent = launch("/views/NewUser.fxml");
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setTitle("New user");
        stage.setScene(scene);
        stage.show();
    }

    void launchNewDepartmentDialog() {
        Parent parent = launch("/views/NewDepartment.fxml");
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setTitle("New department");
        stage.setScene(scene);
        stage.show();
    }

    void refreshMain() {
        mainWindowController.refresh();
    }

    private Parent launch(String path) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(path));
            fxmlLoader.setControllerFactory(this::controllerBuilder);
            Parent parent = fxmlLoader.load();
            SensumController sensumController = fxmlLoader.getController();
            if (sensumController instanceof MainWindowController) {
                mainWindowController = (MainWindowController) sensumController;
            }
            sensumController.onShow();
            return parent;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Instantiates any SensumControllers with the instance of the main class using reflection.
     * Any other controllers will be instantiated like JavaFX normally would -- with a public no-arg constructor.
     */
    private Object controllerBuilder(Class<?> clazz) {
        try {
            if (SensumController.class.isAssignableFrom(clazz))
                return clazz.getConstructor(Main.class).newInstance(main);
            else return clazz.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) { throw new RuntimeException(e); }
    }

}
