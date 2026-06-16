package com.example.smartfood.model;

public class MissingIngredient {
    public String name;
    public double amount;
    public String unit;

    public MissingIngredient(String name, double amount, String unit) {
        this.name = name;
        this.amount = amount;
        this.unit = unit;
    }
}
