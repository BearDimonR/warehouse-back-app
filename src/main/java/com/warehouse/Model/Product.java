package com.warehouse.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private long id;
    private String name;
    private double price;
    private double amount;
    private double totalCost;
    private String measureName;
    private int groupId;
    private int manufacturerId;
    private String description;
}
