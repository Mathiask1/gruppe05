package edu.sdu.sensumbosted.presentation;

import edu.sdu.sensumbosted.data.DataEntity;
import edu.sdu.sensumbosted.entity.Department;
import edu.sdu.sensumbosted.entity.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserSelectConverter extends StringConverter<DataEntity> {

    /**
     * Returns a mixed set with departments and users
     */
    ObservableList<DataEntity> withDepartments(Collection<User> users) {
        final Department[] lastDepartment = {null}; // Array gets around lambda scope restriction
        Set<DataEntity> set = users.stream().sorted(Comparator.comparing(o -> o.getDepartment().getName()))
                .flatMap(user -> {
                    if (user.getDepartment() != lastDepartment[0]) {
                        lastDepartment[0] = user.getDepartment();
                        return Stream.of(user.getDepartment(), user);
                    }
                    return Stream.of(user);
                }).collect(Collectors.toSet());
        return FXCollections.observableArrayList(set);
    }

    @Override
    public String toString(DataEntity entity) {
        if (entity instanceof User) {
            User user = (User) entity;
            return user.getName() + " (" + user.getAuth().name() + ")";
        } else if (entity instanceof Department) {
            return "-- " + ((Department) entity).getName() + " --";
        }
        throw new IllegalArgumentException("Unhandled type");
    }

    @Override
    public DataEntity fromString(String string) {
        return null;
    }
}
