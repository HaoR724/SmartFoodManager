package com.example.smartfood.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "food_nutrition", indices = {@Index(value = "foodName", unique = true)})
public class FoodNutritionEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String foodName;
    public double calories;
    public double protein;
    public double fat;
    public double carbohydrate;
    public double fiber;
    public double calcium;
    public double iron;
    @ColumnInfo(name = "vitamin_c")
    public double vitaminC;

    public FoodNutritionEntity(String foodName, double calories, double protein, double fat, double carbohydrate,
                               double fiber, double calcium, double iron, double vitaminC) {
        this.foodName = foodName;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbohydrate = carbohydrate;
        this.fiber = fiber;
        this.calcium = calcium;
        this.iron = iron;
        this.vitaminC = vitaminC;
    }
}
