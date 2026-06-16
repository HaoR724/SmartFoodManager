package com.example.smartfood.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "nutrition_target", indices = {@Index(value = "userId", unique = true)})
public class NutritionTargetEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public long userId;
    public double caloriesTarget;
    public double proteinTarget;
    public double fatTarget;
    public double carbohydrateTarget;
    public double fiberTarget;
    public double calciumTarget;
    public double ironTarget;
    public double vitaminCTarget;

    public NutritionTargetEntity(long userId, double caloriesTarget, double proteinTarget, double fatTarget,
                                 double carbohydrateTarget, double fiberTarget, double calciumTarget,
                                 double ironTarget, double vitaminCTarget) {
        this.userId = userId;
        this.caloriesTarget = caloriesTarget;
        this.proteinTarget = proteinTarget;
        this.fatTarget = fatTarget;
        this.carbohydrateTarget = carbohydrateTarget;
        this.fiberTarget = fiberTarget;
        this.calciumTarget = calciumTarget;
        this.ironTarget = ironTarget;
        this.vitaminCTarget = vitaminCTarget;
    }
}
