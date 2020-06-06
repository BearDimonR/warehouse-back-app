package com.warehouse.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
