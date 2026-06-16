package com.example.smartfood.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "ingredient", indices = {@Index("userId"), @Index("name"), @Index("category"), @Index("expireDate")})
public class IngredientEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public long userId;
    public String name;
    public String category;
    public double quantity;
    public String unit;
    public String buyDate;
    public String bestEatDate;
    public String expireDate;
    public String storagePlace;
    public String note;
    public String createdAt;

    public IngredientEntity(long userId, String name, String category, double quantity, String unit,
                            String buyDate, String bestEatDate, String expireDate, String storagePlace, String note, String createdAt) {
        this.userId = userId;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.unit = unit;
        this.buyDate = buyDate;
        this.bestEatDate = bestEatDate;
        this.expireDate = expireDate;
        this.storagePlace = storagePlace;
        this.note = note;
        this.createdAt = createdAt;
    }
}
