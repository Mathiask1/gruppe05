/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.sdu.sensumbosted.presentation;

import edu.sdu.sensumbosted.Main;
import edu.sdu.sensumbosted.entity.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * FXML Controller class
 *
 * @author birke
 */
public class MainWindowController extends SensumController {

    private static final Logger log = LoggerFactory.getLogger(MainWindowController.class);
    //@formatter:off

    @FXML private Text userName;
    @FXML private Text userRole;
    @FXML private Text userDepartment;
    @FXML private TextArea diaryTextArea;
    @FXML private TextArea newDiaryEntryTxtArea;
    @FXML private Text currentUserTxtField;
    @FXML private ChoiceBox<User> userSelectionMenu;
    @FXML private ListView<User> userList;
    @FXML private ListView<Department> departmentListView;
    @FXML private ChoiceBox<AuthLevel> selectUserRoleChoiceBox;
    @FXML private ChoiceBox<User> assignPractitionerChoicebox;

    //@formatter:on

    private final ObservableList<Department> departmentObservableList = FXCollections.observableArrayList();
    private final ObservableList<User> users = FXCollections.observableArrayList();
    private final ObservableList<User> usersSelectionList = FXCollections.observableArrayList();
    private final ObservableList<AuthLevel> selectableLevels = FXCollections.observableArrayList();
    private User selectedUser = null;

    public MainWindowController(Main main) {
        super(main);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userSelectionMenu.setConverter(new UserStringConverter());
        assignPractitionerChoicebox.setConverter(new UserStringConverter());
        selectUserRoleChoiceBox.setConverter(new AuthLevelStringConverter());
        selectUserRoleChoiceBox.setItems(selectableLevels);
        refresh();
    }

    @FXML
    private void newUserClicked(MouseEvent event) throws IOException {
        main.launcher.launchNewUserDialog();
    }

    @FXML
    private void newDepartmentClicked(MouseEvent event) throws IOException {
        main.launcher.launchNewDepartmentDialog();
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

            patient.setTodaysDiaryEntry(main.getContext(), newDiaryEntryTxtArea.getText());
            diaryTextArea.setText(patient.getDiary(main.getContext()).toString());
            if (patient.getTodaysDiaryEntry(main.getContext()).isPresent()) {
                newDiaryEntryTxtArea.setText(patient.getTodaysDiaryEntry(main.getContext()).get());
            }

        } catch (RuntimeException e) {
            log.error("User not a patient!", e);
        }
    }

    /**
     * Refresh information in the main window.
     */
    public void refresh() {
        usersSelectionList.setAll(main.getUsers(main.getSystemContext()));
        userSelectionMenu.setItems(usersSelectionList);

        User user = main.getContext().getUser();

        if (user != null) {
            if (main.getContext().checkMinimum(AuthLevel.PRACTITIONER)) {
                userName.setText(user.getName() + ": " + user.getId());
            } else {
                userName.setText(user.getName());
            }
            userRole.setText(user.getAuth().toString());
            userDepartment.setText(user.getDepartment().toString());

            users.setAll(main.getUsers(main.getContext()));
            userList.setItems(users);

            currentUserTxtField.setText(user.getName());
        } else {
            currentUserTxtField.setText("No current user!");
        }
        departmentObservableList.setAll(main.getDepartments().values());
        departmentListView.setItems(departmentObservableList);
        updateAdminPanel();
    }

    private void updateAdminPanel() {
        if (selectedUser instanceof Manager) {
            Manager manager = (Manager) selectedUser;
            List<AuthLevel> levels = Arrays.stream(AuthLevel.values())
                    .filter(authLevel -> !manager.canSetAuthLevel(main.getContext(), authLevel).isPresent())
                    .collect(Collectors.toList());
            selectableLevels.setAll(levels);
        } else {
            selectableLevels.clear();
        }
    }

    @FXML
    private void userListViewClicked(MouseEvent event) {
        try {
            selectedUser = userList.getSelectionModel().getSelectedItem();

            if (selectedUser != null) {
                if (main.getContext().checkMinimum(AuthLevel.PRACTITIONER)) {
                    userName.setText(selectedUser.getName() + ": " + selectedUser.getId().toString());
                } else {
                    userName.setText(selectedUser.getName());
                }
                userRole.setText(selectedUser.getAuth().toString());
                userDepartment.setText(selectedUser.getDepartment().toString());
            }

            if (selectedUser instanceof Patient) {
                if (((Patient) selectedUser).getDiary(main.getContext()) == null) {
                    diaryTextArea.setText("Ingen dagbog!");
                } else {
                    diaryTextArea.setText(((Patient) selectedUser).getDiary(main.getContext()).toString());
                    if (((Patient) selectedUser).getTodaysDiaryEntry(main.getContext()).isPresent()) {
                        newDiaryEntryTxtArea.setText(((Patient) selectedUser).getTodaysDiaryEntry(main.getContext()).get());
                    }
                }
            } else {
                diaryTextArea.setText("Denne bruger er ikke en patient");
            }
            updateAdminPanel();
        } catch (RuntimeException e) {
            log.error("Error clicking on user listview", e);
        }
    }

    @FXML
    private void deleteUserButtonClicked(MouseEvent event) {
        selectedUser.getDepartment().deleteUser(main.getContext(), selectedUser);
        selectedUser = null;
        refresh();
    }

    @FXML
    private void changeRoleButtonClicked(MouseEvent event) {
        Manager manager = (Manager) selectedUser;
        manager.canSetAuthLevel(main.getContext(), selectUserRoleChoiceBox.getValue());
        refresh();
    }

    @FXML
    private void assignPractitionerMouseClicked(MouseEvent event) {
    }
}
