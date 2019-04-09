package edu.sdu.sensumbosted.entity;
import edu.sdu.sensumbosted.AuditAction;
import edu.sdu.sensumbosted.Util;


import java.util.List;
import java.util.Map;

public class Patient extends User {

    private Map<Integer, String> diary;
    private List<String> asignees;
    private boolean enrolled;
    private String id;

    Patient(String name) {
        super(name);

    }

    @Override
    AuthLevel getAuth() {
        return AuthLevel.PATIENT;
    }

    public Map<Integer, String> getDiary(Context ctx) {
        ctx.assertAndLog(AuditAction.DIARY_READ, AuthLevel.PRACTITIONER);
        return this.diary;
    }

    // SKAL IMPLEMENTERES
    public void setDiary(Context ctx, String diaryEntry) {
        ctx.assertAndLog(AuditAction.DIARY_WRITE, AuthLevel.PRACTITIONER);

    }

}
