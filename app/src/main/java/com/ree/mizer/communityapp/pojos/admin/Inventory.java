package com.ree.mizer.communityapp.pojos.admin;

public class Inventory {
    String id, description;
    int total;

    public Inventory() {
    }

    public Inventory(String id, String description, int total) {
        this.id = id;
        this.description = description;
        this.total = total;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
