package ru.scapegoats.checkers.util.responsetypes;

public class SessionListResponse {
    String title;
    String creator;
    String minRate;
    String maxRate;
    String id;
    String sesid;

    public String getSesid() {
        return sesid;
    }

    public String getTitle() {
        return title;
    }

    public String getCreator() {
        return creator;
    }

    public String getMinRate() {
        return minRate;
    }

    public String getMaxRate() {
        return maxRate;
    }

    public String getId() {
        return id;
    }
}
