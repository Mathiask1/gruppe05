/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.sdu.sensumbosted.presentation;

import edu.sdu.sensumbosted.Main;
import edu.sdu.sensumbosted.entity.AuthLevel;
import edu.sdu.sensumbosted.entity.Patient;
import edu.sdu.sensumbosted.entity.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author birke
 */
public class MainWindowController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(MainWindowController.class);
    private Main main = Main.getInstance();
    @FXML private Button newUserButton;
    @FXML private Button newDepartmentbutton;
    @FXML private Text userName;
    @FXML private Text userRole;
    @FXML private Text userDepartment;
    @FXML private Tab diaryTab;
    @FXML private Tab calendarTab;
    @FXML private Tab administrationTab;
    @FXML private ChoiceBox<User> userSelectionMenu;
    @FXML private ListView<User> userList;
    @FXML private Button selectUser;
    @FXML private TextArea diaryTextArea;
    @FXML private Button newDiaryButton;
    @FXML private TextArea newDiaryEntryTxtArea;
    @FXML private ListView<?> departmentListView;
    @FXML private Text currentUserTxtField;

    private final ObservableList<User> users = FXCollections.observableArrayList();
    private final ObservableList<User> usersSelectionList = FXCollections.observableArrayList();

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
        try {
            if (userSelectionMenu.getValue() == null) {
                log.warn("No user selected!");
            } else {
                main.getContext().setUser(userSelectionMenu.getValue());
            }
            refresh();

        } catch (RuntimeException e) {
            log.error("Exception after UI click", e);
        }

    }

    @FXML
    private void newDiaryMouseClicked(MouseEvent event) {
        try {
            Patient patient = (Patient) userList.getSelectionModel().getSelectedItem();

            patient.newDiaryEntry(main.getContext(), newDiaryEntryTxtArea.getText());

            diaryTextArea.setText(patient.getDiary());

        } catch (Exception e) {
            log.error("User not a patient!", e);
        }

    }

    /*
    Refresh information on screen
     */
    public void refresh() {
        usersSelectionList.setAll(main.getUsers(main.getContext()));
        userSelectionMenu.setItems(usersSelectionList);

        try {
            userName.setText(main.getContext().getUser().getName());
            userRole.setText(main.getContext().getUser().getAuth().toString());
            userDepartment.setText(main.getContext().getUser().getDepartment().toString());

            if (!main.getContext().checkMinimum(AuthLevel.PATIENT)) {
                users.setAll(main.getUsers(main.getContext()));
                userList.setItems(users);
            } else {
                users.setAll(main.getContext().getUser());
                userList.setItems(users);
            }
        } catch (Exception e) {
            log.error("No active user!", e);
        }

        if (main.getContext().getUser() == null) {
            currentUserTxtField.setText("No current user!");
        } else {
            currentUserTxtField.setText(main.getContext().getUser().getName());
        }
    }

    @FXML
    private void userListViewClicked(MouseEvent event) {
        try {
            User user = userList.getSelectionModel().getSelectedItem();
            userName.setText(user.getName());
            userRole.setText(user.getAuth().toString());
            userDepartment.setText(user.getDepartment().toString());

            if (userList.getSelectionModel().getSelectedItem().getAuth() == AuthLevel.PATIENT) {
                Patient patient = (Patient) userList.getSelectionModel().getSelectedItem();

                diaryTextArea.setText(patient.getDiary());
            } else {
                diaryTextArea.setText("User is not a patient!");
            }

        } catch (Exception e) {
            log.error("User not a patient!", e);
        }
    }
}
