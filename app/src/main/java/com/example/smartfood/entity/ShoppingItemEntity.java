package com.example.smartfood.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "shopping_item", indices = {@Index("userId"), @Index("status")})
public class ShoppingItemEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public long userId;
    public String name;
    public double quantity;
    public String unit;
    public int status;
    public Long sourceRecipeId;
    public String createdAt;

    public ShoppingItemEntity(long userId, String name, double quantity, String unit, int status, Long sourceRecipeId, String createdAt) {
        this.userId = userId;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.status = status;
        this.sourceRecipeId = sourceRecipeId;
        this.createdAt = createdAt;
    }
}
