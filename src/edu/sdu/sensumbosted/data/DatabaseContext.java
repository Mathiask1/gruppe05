package edu.sdu.sensumbosted.data;

import edu.sdu.sensumbosted.entity.AuthLevel;
import edu.sdu.sensumbosted.entity.Context;

/**
 * This is used by the data layer for the purpose of bypassing the permission system
 */
public final class DatabaseContext extends Context {
    DatabaseContext(DataService data) {
        super(data);
    }

    @Override
    public AuthLevel getAuth() {
        return AuthLevel.SUPERUSER;
    }
}
