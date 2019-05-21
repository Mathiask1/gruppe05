/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.sdu.sensumbosted.presentation;

import edu.sdu.sensumbosted.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author birke
 */
public class NewDepartmentController extends SensumController {
    //@formatter:off
    @FXML private TextField departmentNameTextField;
    @FXML private Button submitButton;
    //@formatter:on

    public NewDepartmentController(Main main) {
        super(main);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        departmentNameTextField.textProperty().addListener((__, ___, newValue) ->
                submitButton.setDisable(newValue.length() < 3));
    }

    @FXML
    private void newDepartmentButtonClicked(MouseEvent event) {
        main.newDepartment(main.getContext(), departmentNameTextField.getText());

        Stage stage = (Stage) departmentNameTextField.getScene().getWindow();
        stage.close();
    }
}