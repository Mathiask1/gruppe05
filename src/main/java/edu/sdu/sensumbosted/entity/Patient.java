package edu.sdu.sensumbosted.entity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Patient extends User {

    private UUID id;
    private Map<Integer, String> diary;
    private List<CalendarEntry> calendar;
    private List<Practitioner> assignees = null;
    private boolean enrolled;


    Patient(Department department, String name) {
        super(department, name);
    }

    /** DB access */
    public Patient(UUID id, Department department, String name, Map<Integer, String> diary, boolean enrolled) {
        super(department, name);
        this.id = id;
        this.diary = diary;
        this.enrolled = enrolled;
    }

    public void lateInit(List<Practitioner> assignees) {
        if (assignees == null) throw new IllegalStateException("Assignees are already initialized");
        this.assignees = assignees;
    }

    @Override
    AuthLevel getAuth() {
        return AuthLevel.PATIENT;
    }

    @Override
    public String getSqlTable() { return "patients"; }

    public boolean isEnrolled() {
        return enrolled;
    }

    /** DB access */
    public void postInit(JSONObject diary, JSONArray calendar) {
        // TODO
    }

    public JSONObject getDiaryJson() {
        JSONObject json = new JSONObject();
        diary.forEach((i, str) -> json.put(i.toString(), str));
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
