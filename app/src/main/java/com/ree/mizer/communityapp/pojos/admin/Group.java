package com.ree.mizer.communityapp.pojos.admin;

public class Group {

    private String groupID, groupName, dateCreated, status;

    public Group() {
    }

    public Group(String groupID, String groupName, String dateCreated, String status) {
        this.groupID = groupID;
        this.groupName = groupName;
        this.dateCreated = dateCreated;
        this.status = status;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
