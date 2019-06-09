package com.example.forgetMeNot.Inventory;

public class Item {
    private String name;
    private String expiry;
    private boolean purchase;

    public Item(String name, String expiry) {
        this.name = name;
        this.expiry = expiry;
        this.purchase = false;
    }

    public Item(String name, String expiry, boolean purchase) {
        this(name, expiry);
        this.purchase = purchase;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public boolean isPurchase() {
        return purchase;
    }

    public void setPurchase(boolean purchase) {
        this.purchase = purchase;
    }
}
