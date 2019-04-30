package edu.sdu.sensumbosted.entity;
import edu.sdu.sensumbosted.AuditAction;
import edu.sdu.sensumbosted.Util;


import java.time.Instant;
import java.time.LocalDate;
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

    public void editDiary(Context ctx, long diaryId, String diaryEntry) {
        ctx.assertAndLog(AuditAction.DIARY_WRITE, AuthLevel.PRACTITIONER);
        diary.put(diaryId, diaryEntry);
    }

    public void newDiaryEntry(Context ctx, String diaryEntry) {
        ctx.assertAndLog(AuditAction.DIARY_WRITE, AuthLevel.PRACTITIONER);
        LocalDate localDate = LocalDate.now();
        long diaryId = TimeUnit.DAYS.toMillis(localDate.toEpochDay());
        diary.put(diaryId, diaryEntry);
    }
}
