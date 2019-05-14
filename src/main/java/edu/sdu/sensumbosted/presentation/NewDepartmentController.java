/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.sdu.sensumbosted.presentation;

import java.net.URL;
import java.util.ResourceBundle;

import edu.sdu.sensumbosted.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author birke
 */
public class NewDepartmentController implements Initializable {
    private Main main = Main.getInstance();

    @FXML
    private TextField departmentNameTextField;
    @FXML
    private Button newDepartmentButton;
    @FXML
    private Text errorMessageTxt;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    private void newDepartmentButtonClicked(MouseEvent event) {
        if (departmentNameTextField.getLength() <= 3) {
            errorMessageTxt.setText("Indsæt et navn! Mindst 3 tegn");
        } else {
            main.getInstance().newDepartment(main.getContext(), departmentNameTextField.getText());
            Stage stage = (Stage) departmentNameTextField.getScene().getWindow();
            stage.close();

        }
    }

}
