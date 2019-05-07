/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.sdu.sensumbosted.presentation;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

import edu.sdu.sensumbosted.Main;
import edu.sdu.sensumbosted.entity.AuthLevel;
import edu.sdu.sensumbosted.entity.Department;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author birke
 */
public class NewUserController implements Initializable {
    private Main main = Main.getInstance();

    @FXML
    private TextField userNameTextField;
    @FXML
    private TextField departmentIDTextField;
    @FXML
    private Button newUserButton;
    @FXML
    private ChoiceBox<String> userRole;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        if (Main.getInstance().getContext().checkMinimum(AuthLevel.SUPERUSER)) {
            userRole.getItems().setAll("Patient","Læge","Sagsbehandler","Lokal Admin","Superbruger");
        } else if (Main.getInstance().getContext().checkMinimum(AuthLevel.LOCAL_ADMIN)) {
            userRole.getItems().setAll("Patient","Læge","Sagsbehandler","Lokal Admin");
        } else if (Main.getInstance().getContext().checkMinimum(AuthLevel.CASEWORKER)) {
            userRole.getItems().setAll("Patient");
        }
    }

    @FXML
    private void newUserButtonClicked(MouseEvent event) {
        UUID uuid;
        try {
            uuid = UUID.fromString(departmentIDTextField.getText());
        } catch (IllegalStateException e) {
            // TODO
            return;
        }

        Department department = main.getDepartments().get(uuid);
        if (department == null) {
            // TODO
            return;
        }
        // TODO
        //department.newPatient(main.getContext(), userNameTextField.getText());
    }
    
}
