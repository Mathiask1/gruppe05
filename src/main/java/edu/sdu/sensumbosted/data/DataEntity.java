package edu.sdu.sensumbosted.data;

import java.util.UUID;

public interface DataEntity {
    UUID getId();
    String getSqlTable();
    default String getIdString() {
        return getId().toString();
    }
}
