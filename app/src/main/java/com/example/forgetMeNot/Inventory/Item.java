package com.example.forgetMeNot.Inventory;

import java.util.Date;

public class Item {
    private String name;
    private Date expiry;
    private boolean purchase;

    public Item(String name, Date expiry, boolean purchase) {
        this.name = name;
        this.expiry = expiry;
        this.purchase = purchase;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getExpiry() {
        return expiry;
    }

    public void setExpiry(Date expiry) {
        this.expiry = expiry;
    }

    public boolean isPurchase() {
        return purchase;
    }

    public void setPurchase(boolean purchase) {
        this.purchase = purchase;
    }
}
