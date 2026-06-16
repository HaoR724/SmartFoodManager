package com.example.smartfood.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.smartfood.entity.MealRecordEntity;

import java.util.List;

@Dao
public interface MealRecordDao {
    @Insert
    long insert(MealRecordEntity record);

    @Delete
    void delete(MealRecordEntity record);

    @Update
    void update(MealRecordEntity record);

    @Query("SELECT * FROM meal_record WHERE id = :id LIMIT 1")
    MealRecordEntity findById(long id);

    @Query("SELECT * FROM meal_record WHERE userId = :userId AND mealDate = :date ORDER BY createdAt DESC")
    List<MealRecordEntity> getByDate(long userId, String date);

    @Query("SELECT * FROM meal_record WHERE userId = :userId AND mealDate BETWEEN :startDate AND :endDate ORDER BY mealDate DESC, createdAt DESC")
    List<MealRecordEntity> getByDateRange(long userId, String startDate, String endDate);

    @Query("SELECT COUNT(DISTINCT mealDate) FROM meal_record WHERE userId = :userId AND mealDate BETWEEN :startDate AND :endDate")
    int countMealDays(long userId, String startDate, String endDate);

    @Query("SELECT * FROM meal_record WHERE userId = :userId ORDER BY mealDate DESC, createdAt DESC")
    List<MealRecordEntity> getByUser(long userId);
}
