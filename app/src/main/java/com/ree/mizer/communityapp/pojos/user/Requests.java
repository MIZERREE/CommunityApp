package com.ree.mizer.communityapp.pojos.user;

public class Requests {

    String requestID, requesterID, requester, heading, description, date, status;

    public Requests() {
    }

    public Requests(String requestID, String requesterID, String requester, String heading, String description, String date, String status) {
        this.requestID = requestID;
        this.requesterID = requesterID;
        this.requester = requester;
        this.heading = heading;
        this.description = description;
        this.date = date;
        this.status = status;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getRequesterID() {
        return requesterID;
    }

    public void setRequesterID(String requesterID) {
        this.requesterID = requesterID;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}
