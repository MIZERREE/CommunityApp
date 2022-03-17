package com.ree.mizer.communityapp.pojos.admin;

public class Fine {
    String fineID, memberID, date, dueDate, amount, description, status;

    public Fine() {
    }

    public Fine(String fineID, String memberID, String date,String dueDate, String amount, String description, String status) {
        this.fineID = fineID;
        this.memberID = memberID;
        this.date = date;
        this.amount = amount;
        this.description = description;
        this.status = status;
        this.dueDate = dueDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getFineID() {
        return fineID;
    }

    public void setFineID(String fineID) {
        this.fineID = fineID;
    }

    public String getMemberID() {
        return memberID;
    }

    public void setMemberID(String memberID) {
        this.memberID = memberID;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
