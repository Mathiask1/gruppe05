/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.sdu.sensumbosted.presentation;

import edu.sdu.sensumbosted.Main;
import edu.sdu.sensumbosted.entity.AuthLevel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author birke
 */
public class NewDepartmentController extends SensumController {
    @FXML
    private TextField departmentNameTextField;
    @FXML
    private Button newDepartmentButton;
    @FXML
    private Text errorMessageTxt;

    public NewDepartmentController(Main main) {
        super(main);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    private void newDepartmentButtonClicked(MouseEvent event) {
        if (departmentNameTextField.getLength() < 3) {
            errorMessageTxt.setText("Indsæt et navn! Mindst 3 tegn");
        } else if (main.getContext().checkMinimum(AuthLevel.SUPERUSER)) {
            main.newDepartment(main.getContext(), departmentNameTextField.getText());

            Stage stage = (Stage) departmentNameTextField.getScene().getWindow();
            stage.close();
        }
    }
}