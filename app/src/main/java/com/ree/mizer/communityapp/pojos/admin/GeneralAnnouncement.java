package com.ree.mizer.communityapp.pojos.admin;

public class GeneralAnnouncement {
    private String generalID,date, heading, announcement, announcer;

    public GeneralAnnouncement() {
    }

    public GeneralAnnouncement(String generalID, String date,String heading, String announcement, String announcer) {
        this.generalID = generalID;
        this.heading = heading;
        this.date = date;
        this.announcement = announcement;
        this.announcer = announcer;
    }

    public String getAnnouncer() {
        return announcer;
    }

    public void setAnnouncer(String announcer) {
        this.announcer = announcer;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGeneralID() {
        return generalID;
    }

    public void setGeneralID(String generalID) {
        this.generalID = generalID;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }
}
