package com.printxpress.android.data.model;

public class Delivery {
    private String id;
    private String name;
    private String account;
    private String date;
    private Boolean homeDelivery;
    private Boolean pickupOption;

    public Delivery() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean getHomeDelivery() {
        return homeDelivery;
    }

    public void setHomeDelivery(Boolean homeDelivery) {
        this.homeDelivery = homeDelivery;
    }

    public Boolean getPickupOption() {
        return pickupOption;
    }

    public void setPickupOption(Boolean pickupOption) {
        this.pickupOption = pickupOption;
    }
}
