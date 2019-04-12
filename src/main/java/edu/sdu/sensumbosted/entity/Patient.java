package edu.sdu.sensumbosted.entity;
import edu.sdu.sensumbosted.AuditAction;
import edu.sdu.sensumbosted.Util;


import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Patient extends User {

    private Map<Long, String> diary;
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

    public Map<Long, String> getDiary(Context ctx) {
        ctx.assertAndLog(AuditAction.DIARY_READ, AuthLevel.PRACTITIONER);
        return this.diary;
    }

    public void setDiary(Context ctx, String diaryEntry) {
        ctx.assertAndLog(AuditAction.DIARY_WRITE, AuthLevel.PRACTITIONER);
        long diaryId = TimeUnit.DAYS.toMillis(1);
        diary.put(diaryId, diaryEntry);
    }
}
