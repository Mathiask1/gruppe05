package edu.sdu.sensumbosted.presentation;

import edu.sdu.sensumbosted.entity.AuthLevel;
import javafx.util.StringConverter;

public class AuthLevelStringConverter extends StringConverter<AuthLevel> {
    @Override
    public String toString(AuthLevel level) {
        return level.getUiName();
    }

    @Override
    public AuthLevel fromString(String string) {
        throw new UnsupportedOperationException();
    }
}
