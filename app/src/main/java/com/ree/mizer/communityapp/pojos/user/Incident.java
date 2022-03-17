package com.ree.mizer.communityapp.pojos.user;

public class Incident {

    String incidentID, userID,date, description, location, status;

    public Incident() {
    }

    public Incident(String incidentID, String userID, String date, String description, String location, String status) {
        this.incidentID = incidentID;
        this.userID = userID;
        this.date = date;
        this.description = description;
        this.location = location;
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIncidentID() {
        return incidentID;
    }

    public void setIncidentID(String incidentID) {
        this.incidentID = incidentID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
