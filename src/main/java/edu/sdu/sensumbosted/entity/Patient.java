package edu.sdu.sensumbosted.entity;

import edu.sdu.sensumbosted.AuditAction;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Instant;
import java.util.*;

public class Patient extends User {

    private UUID id;
    private HashMap<Date, String> diary = new HashMap<>();
    private ArrayList<CalendarEntry> calendar = new ArrayList<>();
    private ArrayList<Practitioner> assignees = new ArrayList<>();
    private boolean enrolled;
    public static final String SQL_TABLE = "patients";

    public Patient(Department department, String name) {
        super(department, name);
    }

    /** DB access */
    public Patient(UUID id, Department department, String name, JSONObject diary, JSONArray calendar, boolean enrolled) {
        super(department, name);
        this.id = id;
        this.enrolled = enrolled;
        parseJson(diary, calendar);
    }


    public Map<Date, String> getDiary(Context ctx) {
        ctx.assertAndLog(getDepartment(), AuditAction.DIARY_READ, AuthLevel.PRACTITIONER);
        return Collections.unmodifiableMap(diary);
    }

    public Optional<String> getTodaysDiaryEntry(Context ctx) {
        ctx.assertAndLog(getDepartment(), AuditAction.DIARY_READ, AuthLevel.PRACTITIONER);
        return Optional.ofNullable(diary.getOrDefault(Date.from(Instant.now()), null));
    }

    public void setTodaysDiaryEntry(Context ctx, String str) {
        ctx.assertAndLog(getDepartment(), AuditAction.DIARY_WRITE, AuthLevel.PRACTITIONER);
        diary.put(Date.from(Instant.now()), str);
        ctx.data.update(this);
    }

    private void parseJson(JSONObject diary, JSONArray calendar) {
        this.diary = new HashMap<>();
        this.calendar = new ArrayList<>();
        diary.toMap().forEach((s, o) -> this.diary.put(Date.from(Instant.parse(s)), (String) o));
        calendar.iterator().forEachRemaining(o -> new CalendarEntry((JSONObject) o));
    }

    public void lateInit(List<Practitioner> assignees) {
        if (assignees == null) throw new IllegalStateException("Assignees are already initialized");
        this.assignees = new ArrayList<>(assignees);
    }

    @Override
    public AuthLevel getAuth() {
        return AuthLevel.PATIENT;
    }

    @Override
    public String getSqlTable() { return SQL_TABLE; }

    public ArrayList<Practitioner> getAssignees(Context context) {
        if (context.getUser() == this) return assignees;
        context.assertAndLog(getDepartment(), AuditAction.PATIENT_READ, AuthLevel.PRACTITIONER);
        return assignees;
    }

    public boolean isEnrolled() {
        return enrolled;
    }

    public JSONObject getDiaryJson() {
        JSONObject json = new JSONObject();
        diary.forEach((i, str) -> json.put(i.toInstant().toString(), str));
        return json;
    }

    public JSONArray getCalendarJson() {
        JSONArray json = new JSONArray();
        calendar.forEach(entry -> json.put(entry.toJson()));
        return json;
    }

    public class CalendarEntry {
        private String title;
        private Long startTime;
        private Long endTime;

        public CalendarEntry(String title, Long startTime, Long endTime) {
            this.title = title;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public CalendarEntry(JSONObject json) {
            this.title = json.getString("title");
            this.startTime = json.getLong("startTime");
            this.endTime = json.getLong("startTime");
        }

        public String getTitle() {
            return title;
        }

        public Long getStartTime() {
            return startTime;
        }

        public Long getEndTime() {
            return endTime;
        }

        private JSONObject toJson() {
            JSONObject json = new JSONObject();
            json.put("title", title);
            json.put("startTime", startTime);
            json.put("endTime", endTime);
            return json;
        }
    }

}
