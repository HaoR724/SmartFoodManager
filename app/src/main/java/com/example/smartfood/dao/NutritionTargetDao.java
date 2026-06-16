package com.example.smartfood.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.smartfood.entity.NutritionTargetEntity;

@Dao
public interface NutritionTargetDao {
    @Insert
    long insert(NutritionTargetEntity target);

    @Update
    void update(NutritionTargetEntity target);

    @Query("SELECT * FROM nutrition_target WHERE userId = :userId LIMIT 1")
    NutritionTargetEntity getByUser(long userId);
}
