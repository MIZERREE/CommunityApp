package com.ree.mizer.communityapp.pojos.admin;

public class FinancialContribute {
    String financialID, contributorID, contributorName, date, amount, groupName, status;

    public FinancialContribute() {
    }

    public FinancialContribute(String financialID, String contributorID, String contributorName, String date, String amount, String groupName, String status) {
        this.financialID = financialID;
        this.contributorID = contributorID;
        this.contributorName = contributorName;
        this.date = date;
        this.amount = amount;
        this.groupName = groupName;
        this.status = status;
    }

    public String getFinancialID() {
        return financialID;
    }

    public void setFinancialID(String financialID) {
        this.financialID = financialID;
    }

    public String getContributorID() {
        return contributorID;
    }

    public void setContributorID(String contributorID) {
        this.contributorID = contributorID;
    }

    public String getContributorName() {
        return contributorName;
    }

    public void setContributorName(String contributorName) {
        this.contributorName = contributorName;
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

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
