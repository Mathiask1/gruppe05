package edu.sdu.sensumbosted.entity;

@SuppressWarnings("SpellCheckingInspection")
public enum AuthLevel {
    NO_AUTH("<ingen adgang>"),
    PATIENT("Patient"),
    PRACTITIONER("Praksiserende"),
    CASEWORKER("Sagsbehandler"),
    LOCAL_ADMIN("Lokal admin"),
    SUPERUSER("Superbruger");

    private final String uiName;

    AuthLevel(String uiName) {
        this.uiName = uiName;
    }

    public String getUiName() {
        return uiName;
    }
}
