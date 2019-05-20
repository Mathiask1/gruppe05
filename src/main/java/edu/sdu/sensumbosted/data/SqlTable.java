package edu.sdu.sensumbosted.data;

/**
 * This serves as a guard against SQL injection, as we need to concatenate the table name
 */
public enum SqlTable {
    DEPARTMENT("departments"),
    MANAGER("managers"),
    PRACTITIONER("practitioners"),
    PATIENT("patients");

    private final String tableName;

    SqlTable(String tableName) {

        this.tableName = tableName;
    }

    /**
     * @return a table name which is guarded against SQL injection when used for concatenation
     */
    public String getTableName() {
        return tableName;
    }
}
