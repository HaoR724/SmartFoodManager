package com.example.smartfood.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.smartfood.entity.FoodNutritionEntity;

import java.util.List;

@Dao
public interface FoodNutritionDao {
    @Insert
    void insertAll(List<FoodNutritionEntity> foods);

    @Query("SELECT * FROM food_nutrition WHERE foodName = :foodName LIMIT 1")
    FoodNutritionEntity findByName(String foodName);

    @Query("SELECT * FROM food_nutrition WHERE foodName LIKE '%' || :keyword || '%' ORDER BY foodName ASC LIMIT 30")
    List<FoodNutritionEntity> search(String keyword);

    @Query("SELECT * FROM food_nutrition ORDER BY foodName ASC")
    List<FoodNutritionEntity> getAll();

    @Query("SELECT COUNT(*) FROM food_nutrition")
    int count();
}
