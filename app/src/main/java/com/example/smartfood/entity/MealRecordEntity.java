package com.example.smartfood.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "meal_record", indices = {@Index("userId"), @Index("mealDate"), @Index("foodName")})
public class MealRecordEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public long userId;
    public String mealDate;
    public String mealType;
    public String foodName;
    public double amountGram;
    public String createdAt;

    public MealRecordEntity(long userId, String mealDate, String mealType, String foodName, double amountGram, String createdAt) {
        this.userId = userId;
        this.mealDate = mealDate;
        this.mealType = mealType;
        this.foodName = foodName;
        this.amountGram = amountGram;
        this.createdAt = createdAt;
    }
}
