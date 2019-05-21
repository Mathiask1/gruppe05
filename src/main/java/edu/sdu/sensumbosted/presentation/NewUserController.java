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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author birke
 */

public class NewUserController extends SensumController {
    private static final Logger log = LoggerFactory.getLogger(NewUserController.class);

    //@formatter:off
    @FXML private TextField userNameTextField;
    @FXML private TextField departmentIDTextField;
    @FXML private ChoiceBox<String> userRole;
    @FXML private Text errorMessageDepartment;
    @FXML private Text errorMessageRole;
    //@formatter:on

    public NewUserController(Main main) {
        super(main);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) { }

    public void refresh() {
        if (main.getContext().checkMinimum(AuthLevel.SUPERUSER)) {
            userRole.getItems().setAll("Patient", "Læge", "Sagsbehandler", "Lokal Admin", "Superbruger");
        } else if (main.getContext().checkMinimum(AuthLevel.LOCAL_ADMIN)) {
            userRole.getItems().setAll("Patient", "Læge", "Sagsbehandler", "Lokal Admin");
        } else if (main.getContext().checkMinimum(AuthLevel.CASEWORKER)) {
            userRole.getItems().setAll("Patient");
        }
    }

    @Override
    public void onShow() {
        refresh();
    }

    @FXML
    private void newUserButtonClicked(MouseEvent event) {
        UUID uuid;
        errorMessageDepartment.setText("");
        errorMessageRole.setText("");

        try {
            uuid = UUID.fromString(departmentIDTextField.getText());

        } catch (IllegalStateException e) {
            log.error("Error clicking on New User button.", e);
            return;
        }

        Department department = main.getDepartments().get(uuid);

        if (department == null) {
            errorMessageDepartment.setText("Indtast venglist ID.");
            return;
        }

        if (userRole.getValue() == null) {
            errorMessageRole.setText("Vælg en rolle!");
            return;
        } else if (userRole.getValue() == "Patient") {
            department.newPatient(main.getContext(), userNameTextField.getText());
        } else if (userRole.getValue() == "Læge") {
            department.newPractitioner(main.getContext(), userNameTextField.getText());
        } else if (userRole.getValue() == "Sagsbehandler") {
            department.newManager(main.getContext(), userNameTextField.getText(), AuthLevel.CASEWORKER);
        } else if (userRole.getValue() == "Lokal Admin") {
            department.newManager(main.getContext(), userNameTextField.getText(), AuthLevel.LOCAL_ADMIN);
        } else if (userRole.getValue() == "Superbruger") {
            department.newManager(main.getContext(), userNameTextField.getText(), AuthLevel.SUPERUSER);
        }
        Stage closeWindow = (Stage) userNameTextField.getScene().getWindow();
        closeWindow.close();
    }
}
