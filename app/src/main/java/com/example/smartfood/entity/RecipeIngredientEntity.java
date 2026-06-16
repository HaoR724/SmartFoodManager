package com.example.smartfood.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipe_ingredient", indices = {@Index("recipeId"), @Index("ingredientName")})
public class RecipeIngredientEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public long recipeId;
    public String ingredientName;
    public double amount;
    public String unit;

    public RecipeIngredientEntity(long recipeId, String ingredientName, double amount, String unit) {
        this.recipeId = recipeId;
        this.ingredientName = ingredientName;
        this.amount = amount;
        this.unit = unit;
    }
}
