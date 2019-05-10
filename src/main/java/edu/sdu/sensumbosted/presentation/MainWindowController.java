/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.sdu.sensumbosted.presentation;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import edu.sdu.sensumbosted.Main;
import edu.sdu.sensumbosted.entity.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author birke
 */
public class MainWindowController implements Initializable {

    private Main main = Main.getInstance();
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
    private ChoiceBox<User> userSelectionMenu;
    @FXML
    private ListView<User> userList;

    private final ObservableList<User> users = FXCollections.observableArrayList();

    @FXML
    private Button selectUser;
    
    @FXML
    private TextArea diaryTextArea;
    @FXML
    private Button newDiaryButton;
    @FXML
    private TextArea newDiaryEntryTxtArea;
    @FXML
    private ListView<?> departmentListView;


    ArrayList<User> userArray = new ArrayList<>();
    @FXML
    private Text currentUserTxtField;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        refresh();
    }

    @FXML
    private void newUserClicked(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/views/NewUser.fxml"));
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("FXML Welcome");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void newDepartmentClicked(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/views/NewDepartment.fxml"));
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("FXML Welcome");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void selectUserClicked(MouseEvent event) {
        // TODO
        // Frederik, jeg skal have din hjælp!
        try {
            if (userSelectionMenu.getValue() == null) {
                System.out.println("No user selected!");
            } else {
                main.getContext().setUser(userSelectionMenu.getValue());
            }
            refresh();

        } catch (Exception e) {
            e.getStackTrace();
        }

    }

    @FXML
    private void newDiaryMouseClicked(MouseEvent event) {
        try {
            Patient patient = (Patient) userList.getSelectionModel().getSelectedItem();
            

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void refresh() {
        users.setAll(main.getUsers(main.getContext()));
        userSelectionMenu.setItems(users);
        userList.setItems(users);
        if (main.getContext().getUser() == null) {
            currentUserTxtField.setText("No current user!");
        } else {
            currentUserTxtField.setText(main.getContext().getUser().getName());
        }

    }

    @FXML
    private void userListViewClicked(MouseEvent event) {
        User user = userList.getSelectionModel().getSelectedItem();
        userName.setText(user.getName());
        userRole.setText(user.getAuth().toString());
        userDepartment.setText(user.getDepartment().toString());

        Patient patient = (Patient) userList.getSelectionModel().getSelectedItem();

        diaryTextArea.setText(patient.getDiaryJson().toString());
    }

}
