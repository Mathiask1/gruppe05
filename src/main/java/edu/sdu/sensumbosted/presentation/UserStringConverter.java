package edu.sdu.sensumbosted.presentation;

import edu.sdu.sensumbosted.entity.User;
import javafx.util.StringConverter;

public class UserStringConverter extends StringConverter<User> {
    @Override
    public String toString(User object) {
        return object.getName() + " (" + object.getAuth().name() + ")";
    }

    @Override
    public User fromString(String string) {
        return null;
    }
}
