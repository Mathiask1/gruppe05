/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.sdu.sensumbosted.presentation;

import edu.sdu.sensumbosted.Main;
import edu.sdu.sensumbosted.data.DataEntity;
import edu.sdu.sensumbosted.entity.*;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    @FXML private Button newUserButton;
    @FXML private Button newDepartmentButton;
    @FXML private ChoiceBox<DataEntity> userSelectionMenu;
    @FXML private Button selectUserButton;
    @FXML private ListView<User> userList;
    @FXML private ListView<Department> departmentListView;
    @FXML private ChoiceBox<AuthLevel> selectUserRoleChoiceBox;
    @FXML private ListView<Practitioner> relationsListView;
    @FXML private Button deleteUserButton;
    @FXML private Button changeRoleButton;
    @FXML private Tab adminTab;
    @FXML private Tab diaryTab;
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
        userSelectionMenu.setConverter(StringConverters.USER_SELECT);
        userSelectionMenu.setOnAction(event ->
                selectUserButton.setDisable(!(userSelectionMenu.getValue() instanceof User))
        );
        selectUserRoleChoiceBox.setConverter(StringConverters.AUTH_LEVEL);
        selectUserRoleChoiceBox.setItems(selectableLevels);
        relationsListView.setCellFactory(CheckBoxListCell.forListView(
                this::createAssignmentCheckbox,
                StringConverters.PRACTITIONER)
        );
        relationsListView.setItems(assignablePractitioners);
        userList.setCellFactory(TextFieldListCell.forListView(StringConverters.USER_WITH_ROLE));
        departmentListView.setCellFactory(TextFieldListCell.forListView(StringConverters.DEPARTMENTS));
        refresh();
    }

    @FXML
    private void newUserClicked(MouseEvent event) {
        main.launcher.launchNewUserDialog();
    }

    @FXML
    private void newDepartmentClicked(MouseEvent event) {
        main.launcher.launchNewDepartmentDialog();
    }

    @FXML
    private void selectUserClicked(MouseEvent event) {
        main.getContext().setUser((User) userSelectionMenu.getValue());
        refresh();
        refreshUser();
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
    void refresh() {
        Set<User> users = main.getUsers(main.getSystemContext());
        usersSelectionList.setAll(users);
        userSelectionMenu.setItems(StringConverters.USER_SELECT.withDepartments(users));
        userSelectionMenu.setValue(main.getContext().getUser());

        User user = main.getContext().getUser();

        newDepartmentButton.setDisable(!main.getContext().checkMinimum(AuthLevel.SUPERUSER));
        newUserButton.setDisable(!main.getContext().checkMinimum(AuthLevel.CASEWORKER));

        if (user != null) {
            this.users.setAll(main.getUsers(main.getContext()));
            userList.setItems(this.users);

            currentUserTxtField.setText(user.getName());
        } else {
            currentUserTxtField.setText("Ingen bruger valgt");
        }

        departmentObservableList.setAll(main.getDepartments().values());
        departmentListView.setItems(departmentObservableList);
        refreshAdminPanel();
    }

    private void refreshUser() {
        User user = main.getContext().getUser();

        if (user == null) {
            currentUserTxtField.setText("Ingen bruger valgt");
            return;
        }

        if (main.getContext().checkMinimum(AuthLevel.PRACTITIONER)) {
            userName.setText(user.getName() + ": " + user.getId());
        } else {
            userName.setText(user.getName());
        }
        userRole.setText(user.getAuth().getUiName());
        userDepartment.setText(user.getDepartment().getName());
    }

    private void refreshAdminPanel() {
        Context ctx = main.getContext();
        if (selectedUser == null || !ctx.checkMinimum(AuthLevel.CASEWORKER)) {
            adminTab.setDisable(true);
            diaryTab.getTabPane().getSelectionModel().select(diaryTab);
            return;
        }
        adminTab.setDisable(false);

        boolean mayDelete = ctx.checkMinimum(selectedUser.getAuth())
                && ctx.checkMinimum(AuthLevel.LOCAL_ADMIN);
        boolean mayChangeRole = mayDelete && ctx.checkMinimum(selectedUser.getAuth());
        deleteUserButton.setDisable(!mayDelete);
        changeRoleButton.setDisable(!mayChangeRole);

        if (selectedUser instanceof Manager) {
            Manager manager = (Manager) selectedUser;
            List<AuthLevel> levels = Arrays.stream(AuthLevel.values())
                    .filter(authLevel -> !manager.canSetAuthLevel(ctx, authLevel).isPresent())
                    .collect(Collectors.toList());
            selectableLevels.setAll(levels);
        } else {
            selectableLevels.clear();
        }

        if (selectedUser instanceof Patient && ctx.checkMinimum(AuthLevel.CASEWORKER)) {
            Set<Practitioner> practitioners = getUser().getDepartment().getPractitioners(ctx);
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
                userName.setText(selectedUser.getName() + ": " + selectedUser.getId());
                userRole.setText(selectedUser.getAuth().getUiName());
                userDepartment.setText(selectedUser.getDepartment().getName());
            } else {
                userName.setText("Ingen bruger valgt");
                userRole.setText("");
                userDepartment.setText("");
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
