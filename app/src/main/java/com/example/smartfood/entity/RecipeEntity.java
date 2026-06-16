package com.example.smartfood.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipe", indices = {@Index("name"), @Index("category")})
public class RecipeEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String name;
    public String category;
    public String description;
    public String steps;
    public String imageName;
    public String createdAt;

    public RecipeEntity(String name, String category, String description, String steps, String imageName, String createdAt) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.steps = steps;
        this.imageName = imageName;
        this.createdAt = createdAt;
    }
}
