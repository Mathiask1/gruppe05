/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.sdu.sensumbosted.presentation;

import edu.sdu.sensumbosted.Main;
import edu.sdu.sensumbosted.entity.AuthLevel;
import edu.sdu.sensumbosted.entity.Department;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

import static edu.sdu.sensumbosted.entity.AuthLevel.*;

/**
 * FXML Controller class
 *
 * @author birke
 */

public class NewUserController extends SensumController {
    private static final Logger log = LoggerFactory.getLogger(NewUserController.class);

    //@formatter:off
    @FXML private Button submitButton;
    @FXML private TextField userNameTextField;
    @FXML private ChoiceBox<Department> departmentChoiceBox;
    @FXML private ChoiceBox<AuthLevel> userRole;
    //@formatter:on

    public NewUserController(Main main) {
        super(main);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userNameTextField.textProperty().addListener((__, ___, ____) -> validate());
        departmentChoiceBox.setOnAction(__ -> validate());
        userRole.setConverter(new AuthLevelConverter());
    }

    public void refresh() {
        if (main.getContext().checkMinimum(AuthLevel.SUPERUSER)) {
            userRole.getItems().setAll(PATIENT, PRACTITIONER, CASEWORKER, LOCAL_ADMIN, SUPERUSER);
        } else if (main.getContext().checkMinimum(AuthLevel.LOCAL_ADMIN)) {
            userRole.getItems().setAll(PATIENT, PRACTITIONER, CASEWORKER, LOCAL_ADMIN);
        } else if (main.getContext().checkMinimum(AuthLevel.CASEWORKER)) {
            userRole.getItems().setAll(PATIENT);
        } else {
            log.warn("User should not have permission to view this dialog!");
        }
        if (userRole.getValue() == null) userRole.setValue(PATIENT);

        if (main.getContext().checkMinimum(AuthLevel.SUPERUSER)) {
            departmentChoiceBox.setDisable(false);
            departmentChoiceBox.getItems().setAll(main.getDepartments().values());
        } else {
            departmentChoiceBox.setDisable(true);
            Department department = main.getContext().getUser().getDepartment();
            departmentChoiceBox.setValue(department);
            departmentChoiceBox.getItems().setAll(department);
        }
    }

    @Override
    public void onShow() {
        refresh();
    }

    private void validate() {
        submitButton.setDisable(userNameTextField.getText().length() < 3 || departmentChoiceBox.getValue() == null);
    }

    @FXML
    private void newUserButtonClicked(MouseEvent event) {
        Department department = departmentChoiceBox.getValue();

        switch (userRole.getValue()) {
            case PATIENT:
                department.newPatient(main.getContext(), userNameTextField.getText());
                return;
            case PRACTITIONER:
                department.newPractitioner(main.getContext(), userNameTextField.getText());
                break;
            case CASEWORKER:
                department.newManager(main.getContext(), userNameTextField.getText(), AuthLevel.CASEWORKER);
                break;
            case LOCAL_ADMIN:
                department.newManager(main.getContext(), userNameTextField.getText(), AuthLevel.LOCAL_ADMIN);
                break;
            case SUPERUSER:
                department.newManager(main.getContext(), userNameTextField.getText(), AuthLevel.SUPERUSER);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + userRole.getValue());
        }

        Stage closeWindow = (Stage) userNameTextField.getScene().getWindow();
        closeWindow.close();
        main.getControllerLauncher().refreshMain();
    }

    private class AuthLevelConverter extends StringConverter<AuthLevel> {
        @Override
        public String toString(AuthLevel object) {
            return object.getUiName();
        }

        @Override
        public AuthLevel fromString(String string) {
            return AuthLevel.valueOf(string);
        }
    }
}
