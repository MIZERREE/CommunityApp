package com.ree.mizer.communityapp.pojos.admin;

public class Funeral {
    String funeralID,userId,name, address, whenHappened, whenBurial, contacts, extras, announcer;

    public Funeral() {
    }

    public Funeral(String funeralID, String userId, String name, String address, String whenHappened, String whenBurial, String contacts, String extras, String announcer) {
        this.funeralID = funeralID;
        this.userId = userId;
        this.name = name;
        this.address = address;
        this.whenHappened = whenHappened;
        this.whenBurial = whenBurial;
        this.contacts = contacts;
        this.extras = extras;
        this.announcer = announcer;
    }

    public String getAnnouncer() {
        return announcer;
    }

    public void setAnnouncer(String announcer) {
        this.announcer = announcer;
    }

    public String getFuneralID() {
        return funeralID;
    }

    public void setFuneralID(String funeralID) {
        this.funeralID = funeralID;
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

    public String getWhenHappened() {
        return whenHappened;
    }

    public void setWhenHappened(String whenHappened) {
        this.whenHappened = whenHappened;
    }

    public String getWhenBurial() {
        return whenBurial;
    }

    public void setWhenBurial(String whenBurial) {
        this.whenBurial = whenBurial;
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
}
