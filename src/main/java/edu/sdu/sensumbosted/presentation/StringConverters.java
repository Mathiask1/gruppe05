package edu.sdu.sensumbosted.presentation;

import edu.sdu.sensumbosted.data.DataEntity;
import edu.sdu.sensumbosted.entity.AuthLevel;
import edu.sdu.sensumbosted.entity.Department;
import edu.sdu.sensumbosted.entity.Practitioner;
import edu.sdu.sensumbosted.entity.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StringConverters {

    static final StringConverter<AuthLevel> AUTH_LEVEL = fromFunction(AuthLevel::getUiName);
    static final StringConverter<User> USER = fromFunction(User::getName);
    static final StringConverter<Practitioner> PRACTITIONER = fromFunction(User::getName);
    static final StringConverter<User> USER_WITH_ROLE = fromFunction(user -> String.format(
            "%s (%s)",
            user.getName(),
            user.getAuth().getUiName())
    );
    static final UserSelectConverter USER_SELECT = new UserSelectConverter();
    static final StringConverter<Department> DEPARTMENTS = fromFunction(Department::getName);

    private static <E> StringConverter<E> fromFunction(Function<E, String> func) {
        return new StringConverter<E>() {
            @Override
            public String toString(E object) { return func.apply(object); }
            @Override
            public E fromString(String string) { throw new UnsupportedOperationException(); }
        };
    }

    public static class UserSelectConverter extends StringConverter<DataEntity> {

        /**
         * Returns a mixed set with departments and users
         */
        ObservableList<DataEntity> withDepartments(Collection<User> users) {
            final Department[] lastDepartment = {null}; // Array gets around lambda scope restriction
            List<DataEntity> list = users.stream()
                    .sorted(Comparator.comparing(o -> o.getDepartment().getName()))
                    .flatMap(user -> {
                        if (user.getDepartment() != lastDepartment[0]) {
                            lastDepartment[0] = user.getDepartment();
                            return Stream.of(user.getDepartment(), user);
                        }
                        return Stream.of(user);
                    }).collect(Collectors.toList());
            return FXCollections.observableArrayList(list);
        }

        @Override
        public String toString(DataEntity entity) {
            if (entity instanceof User) {
                User user = (User) entity;
                return user.getName() + " (" + user.getAuth().getUiName() + ")";
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
}
