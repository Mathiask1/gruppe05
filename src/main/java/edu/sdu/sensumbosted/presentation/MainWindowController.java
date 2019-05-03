/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.sdu.sensumbosted.presentation;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author birke
 */
public class MainWindowController implements Initializable {

    @FXML
    private Button newUserButton;
    @FXML
    private Button newDepartmentbutton;
    @FXML
    private Text userName;
    @FXML
    private Text userRole;
    @FXML
    private Text userDepartment;
    @FXML
    private Tab diaryTab;
    @FXML
    private Tab calendarTab;
    @FXML
    private Tab administrationTab;
    @FXML
    private ChoiceBox<String> userSelectionMenu;
    @FXML
    private ListView<?> userList;
    @FXML
    private Button selectUser;
    @FXML
    private ListView<?> diaryList;
    @FXML
    private TextArea diaryTextArea;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        userSelectionMenu.getItems().add("Test");
        userSelectionMenu.setValue("Test");
    }    

    @FXML
    private void newUserClicked(MouseEvent event) {
    }

    @FXML
    private void newDepartmentClicked(MouseEvent event) {
    }

    @FXML
    private void selectUserClicked(MouseEvent event) {
    }
    
}
