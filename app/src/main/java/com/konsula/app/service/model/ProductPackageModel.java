package com.konsula.app.service.model;

/**
 * Created by SamuelSonny on 16-Apr-16.
 */
public class ProductPackageModel {
    private String name;
    private String description;
    private double price;
    private int amount;

    public ProductPackageModel() {
    }

    public ProductPackageModel(String name, String description, double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
