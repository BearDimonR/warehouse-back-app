package com.warehouse.Model;

import java.util.Objects;

public class Product {
    private int id;
    private String name;
    private double price;
    private double amount;
    private double totalCost;
    private String measureName;
    private int groupProductId;
    private int manufactureId;
    private String description;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public String getMeasureName() {
        return measureName;
    }

    public void setMeasureName(String measureName) {
        this.measureName = measureName;
    }

    public int getGroupProductId() {
        return groupProductId;
    }

    public void setGroupProductId(int groupProductId) {
        this.groupProductId = groupProductId;
    }

    public int getManufactureId() {
        return manufactureId;
    }

    public void setManufactureId(int manufactureId) {
        this.manufactureId = manufactureId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Product product = (Product) obj;
        return id == product.id &&
                Objects.equals(name, product.name) &&
                price ==product.price &&
                amount == product.amount &&
                totalCost == product.totalCost &&
                Objects.equals(measureName, product.measureName) &&
                groupProductId == product.groupProductId &&
                manufactureId == product.manufactureId &&
                Objects.equals(description, product.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, amount, totalCost, measureName, groupProductId, manufactureId, description);
    }

    public String toString() {
        return "Permission { \nid = " + id + ", " +
                "\nname = " + name + "" +
                ", \nprice = " + price +
                ", \namount = " + amount +
                "\n total_cost = " + totalCost +
                ", \nmeasureName = " + measureName  +
                ", \ngroupProductId = " + groupProductId +
                "\nmanufactureId = " + manufactureId  + "" +
                "\ndescription = " + description +
                "\n}\n";
    }



}
