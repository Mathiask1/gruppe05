package edu.sdu.sensumbosted.presentation;

import edu.sdu.sensumbosted.entity.User;
import javafx.util.StringConverter;

class UserStringConverter<U extends User> extends StringConverter<U> {

    private final boolean nameOnly;

    UserStringConverter() {
        this.nameOnly = false;
    }

    UserStringConverter(boolean nameOnly) {
        this.nameOnly = nameOnly;
    }

    @Override
    public String toString(User object) {
        if (nameOnly) return object.getName();
        return object.getName() + " (" + object.getAuth().name() + ")";
    }

    @Override
    public U fromString(String string) {
        return null;
    }
}
