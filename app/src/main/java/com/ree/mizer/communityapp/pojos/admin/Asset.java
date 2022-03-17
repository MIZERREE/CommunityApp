package com.ree.mizer.communityapp.pojos.admin;

public class Asset {
    private String assetID, userID, issueDate, name, address, description, pictureURI, extra, status;

    public Asset() {
    }

    public Asset(String assetID, String userID, String issueDate, String name, String address, String description, String pictureURI, String extra, String status) {
        this.assetID = assetID;
        this.userID = userID;
        this.issueDate = issueDate;
        this.name = name;
        this.address = address;
        this.description = description;
        this.pictureURI = pictureURI;
        this.extra = extra;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssetID() {
        return assetID;
    }

    public void setAssetID(String assetID) {
        this.assetID = assetID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
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

    public String getPictureURI() {
        return pictureURI;
    }

    public void setPictureURI(String pictureURI) {
        this.pictureURI = pictureURI;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        return "Asset{" +
                "assetID='" + assetID + '\'' +
                ", userID='" + userID + '\'' +
                ", issueDate='" + issueDate + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", description='" + description + '\'' +
                ", pictureURI='" + pictureURI + '\'' +
                ", extra='" + extra + '\'' +
                '}';
    }
}
