package com.ree.mizer.communityapp.pojos.admin;

public class Meeting {
    String meetingID,userId,name, address, description, contacts, extras;

    public Meeting() {
    }

    public Meeting(String meetingID, String userId, String name, String address, String description, String contacts, String extras) {
        this.meetingID = meetingID;
        this.userId = userId;
        this.name = name;
        this.address = address;
        this.description = description;
        this.contacts = contacts;
        this.extras = extras;
    }

    public String getMeetingID() {
        return meetingID;
    }

    public void setMeetingID(String meetingID) {
        this.meetingID = meetingID;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    @Override
    public String toString() {
        return "Meeting{" +
                "meetingID='" + meetingID + '\'' +
                ", userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", description='" + description + '\'' +
                ", contacts='" + contacts + '\'' +
                ", extras='" + extras + '\'' +
                '}';
    }
}
