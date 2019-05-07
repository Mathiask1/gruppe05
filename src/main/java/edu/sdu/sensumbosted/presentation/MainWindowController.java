/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.sdu.sensumbosted.presentation;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import edu.sdu.sensumbosted.Main;
import edu.sdu.sensumbosted.data.DataService;
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
    private ChoiceBox<String> userSelectionMenu;
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // TODO
        userSelectionMenu.getItems().add("Test");
        userSelectionMenu.setValue("Test");
        users.setAll(main.getUsers(main.getContext()));

    }

    @FXML
    private void newUserClicked(MouseEvent event) throws IOException {
        Parent root1 = FXMLLoader.load(getClass().getResource("/views/NewUser.fxml"));
        Scene scene = new Scene(root1);
        Stage stage2 = new Stage();
        stage2.setTitle("FXML Welcome");
        stage2.setScene(scene);
        stage2.show();
    }

    @FXML
    private void newDepartmentClicked(MouseEvent event) {
    }

    @FXML
    private void selectUserClicked(MouseEvent event) {
    }

    @FXML
    private void newDiaryMouseClicked(MouseEvent event) {
    }



}
