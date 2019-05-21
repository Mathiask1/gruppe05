/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.sdu.sensumbosted.presentation;

import edu.sdu.sensumbosted.Main;
import edu.sdu.sensumbosted.entity.*;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
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
    @FXML private ListView<Practitioner> relationsListView;
    @FXML private Button deleteUserButton;
    @FXML public Tab adminTab;
    //@formatter:on

    private final ObservableList<Department> departmentObservableList = FXCollections.observableArrayList();
    private final ObservableList<User> users = FXCollections.observableArrayList();
    private final ObservableList<User> usersSelectionList = FXCollections.observableArrayList();
    private final ObservableList<AuthLevel> selectableLevels = FXCollections.observableArrayList();
    private final ObservableList<Practitioner> assignablePractitioners = FXCollections.observableArrayList();
    private User selectedUser = null;

    public MainWindowController(Main main) {
        super(main);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userSelectionMenu.setConverter(new UserStringConverter<>());
        selectUserRoleChoiceBox.setConverter(new AuthLevelStringConverter());
        selectUserRoleChoiceBox.setItems(selectableLevels);
        relationsListView.setCellFactory(CheckBoxListCell.forListView(
                this::createAssignmentCheckbox,
                new UserStringConverter<>(true))
        );
        relationsListView.setItems(assignablePractitioners);
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
        refreshAdminPanel();
    }

    private void refreshAdminPanel() {
        if (selectedUser == null) { //TODO
            adminTab.setDisable(true);
            return;
        }
        adminTab.setDisable(false);

        deleteUserButton.setDisable(!main.getContext().checkMinimum(selectedUser.getAuth()));

        if (selectedUser instanceof Manager) {
            Manager manager = (Manager) selectedUser;
            List<AuthLevel> levels = Arrays.stream(AuthLevel.values())
                    .filter(authLevel -> !manager.canSetAuthLevel(main.getContext(), authLevel).isPresent())
                    .collect(Collectors.toList());
            selectableLevels.setAll(levels);
        } else {
            selectableLevels.clear();
        }

        if (selectedUser instanceof Patient && main.getContext().checkMinimum(AuthLevel.CASEWORKER)) {
            Set<Practitioner> practitioners = getUser().getDepartment().getPractitioners(main.getContext());
            assignablePractitioners.setAll(practitioners);
        } else {
            assignablePractitioners.clear();
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
                Patient patient = (Patient) selectedUser;
                Map<LocalDate, String> diary = patient.getDiary(main.getContext());
                if (diary == null) {
                    diaryTextArea.setText("Ingen dagbog!");
                } else {
                    diaryTextArea.setText(diary.toString());
                    patient.getTodaysDiaryEntry(main.getContext()).ifPresent(newDiaryEntryTxtArea::setText);
                }
            } else {
                diaryTextArea.setText("Denne bruger er ikke en patient");
            }
            refreshAdminPanel();
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

    /**
     * Shorthand
     */
    private User getUser() {
        return main.getContext().getUser();
    }

    /**
     * @return an observable boolean which represents the practitioner's relation checkbox
     */
    private ObservableValue<Boolean> createAssignmentCheckbox(Practitioner practitioner) {
        //noinspection SuspiciousMethodCalls
        boolean assigned = practitioner.getPatients(main.getContext()).contains(selectedUser);
        SimpleBooleanProperty checkbox = new SimpleBooleanProperty(assigned);
        checkbox.addListener((__, ___, newValue) -> onPractitionerAssignmentChange(practitioner, newValue));
        return checkbox;
    }

    private void onPractitionerAssignmentChange(Practitioner practitioner, boolean assigned) {
        if (assigned) practitioner.assign(main.getContext(), (Patient) selectedUser);
        else practitioner.unassign(main.getContext(), (Patient) selectedUser);
    }
}
