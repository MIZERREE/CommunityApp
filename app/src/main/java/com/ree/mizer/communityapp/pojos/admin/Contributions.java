package com.ree.mizer.communityapp.pojos.admin;

public class Contributions {

    String id, contributorID, contributor, date, amount;

    public Contributions() {
    }

    public Contributions(String id, String contributorID, String contributor, String date, String amount) {
        this.id = id;
        this.contributorID = contributorID;
        this.contributor = contributor;
        this.date = date;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContributorID() {
        return contributorID;
    }

    public void setContributorID(String contributorID) {
        this.contributorID = contributorID;
    }

    public String getContributor() {
        return contributor;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
