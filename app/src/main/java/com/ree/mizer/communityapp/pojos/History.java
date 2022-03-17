package com.ree.mizer.communityapp.pojos;

public class History {
    String userID,date, time, activity;

    public History() {
    }

    public History(String userID, String date, String time, String activity) {
        this.userID = userID;
        this.date = date;
        this.time = time;
        this.activity = activity;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }
}
