package edu.sdu.sensumbosted.data;

import java.util.UUID;

public interface DataEntity {
    UUID getId();
    SqlTable getSqlTable();
}
