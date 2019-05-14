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
public class MainWindowController extends SensumController {

    private static final Logger log = LoggerFactory.getLogger(MainWindowController.class);

    //@formatter:off
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
    //@formatter:on

    private final ObservableList<User> userObservableList = FXCollections.observableArrayList();

    @FXML
    private Button selectUser;
    @FXML
    private TextArea diaryTextArea;
    @FXML
    private Button newDiaryButton;
    @FXML
    private TextArea newDiaryEntryTxtArea;

    private final ObservableList<Department> departmentObservableList = FXCollections.observableArrayList();
    @FXML
    private ListView<Department> departmentListView;


    ArrayList<User> userArray = new ArrayList<>();
    @FXML
    private Text currentUserTxtField;
    private final ObservableList<User> users = FXCollections.observableArrayList();
    private final ObservableList<User> usersSelectionList = FXCollections.observableArrayList();

    public MainWindowController(Main main) {
        super(main);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userSelectionMenu.setConverter(new UserStringConverter());
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

        } catch (RuntimeException e) {
            log.error("User not a patient!", e);
        }

    }

    /*
    Refresh information in the main window.
     */
    public void refresh() {
        userObservableList.setAll(main.getUsers(main.getContext()));
        userSelectionMenu.setItems(userObservableList);
        userList.setItems(userObservableList);

        departmentObservableList.setAll(main.getDepartments().values());
        departmentListView.setItems(departmentObservableList);


        if (main.getContext().getUser() == null) {
            currentUserTxtField.setText("No current user!");
    /**
     * Refresh information on screen
     */
	 
    private void refresh() {
        usersSelectionList.setAll(main.getUsers(main.getContext()));
        userSelectionMenu.setItems(usersSelectionList);

        User user = main.getContext().getUser();

        if (user != null) {
            userName.setText(user.getName());
            userRole.setText(user.getAuth().toString());
            userDepartment.setText(user.getDepartment().toString());

            if (!main.getContext().checkMinimum(AuthLevel.PATIENT)) {
                users.setAll(main.getUsers(main.getContext()));
                userList.setItems(users);
            } else {
                users.setAll(user);
                userList.setItems(users);
            }

            currentUserTxtField.setText(user.getName());
        } else {
            currentUserTxtField.setText("No current user!");
        }
    }

    @FXML
    private void userListViewClicked(MouseEvent event) {
        try {
            User user = userList.getSelectionModel().getSelectedItem();
            userName.setText(user.getName());
            userRole.setText(user.getAuth().toString());
            userDepartment.setText(user.getDepartment().toString());

            if (user instanceof Patient) {
                diaryTextArea.setText(((Patient) user).getDiary());
            } else {
                diaryTextArea.setText("User is not a patient!");
            }

        } catch (RuntimeException e) {
            log.error("User not a patient!", e);
        }
    }
}
