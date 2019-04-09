package edu.sdu.sensumbosted;

public enum AuditAction {
    DEPARTMENT_CREATE,
    DEPARTMENT_DELETE,
    USER_CREATE,
    USER_DELETE,
    LOGGED_IN,
    LOGGED_OUT,
    MANAGER_AUTH_CHANGE,
    PRACTITIONER_ASSIGN,
    PRACTITIONER_UNASSIGN,
    PRACTITIONER_READ_ASSIGNED,
    DIARY_READ,
    DIARY_WRITE,
    CALENDAR_READ,
    CALENDAR_WRITE,
    MANAGER_READ,
    PRACTITIONER_READ,
    PATIENT_READ;
}
