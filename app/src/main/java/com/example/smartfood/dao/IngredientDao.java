package com.example.smartfood.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.smartfood.entity.IngredientEntity;

import java.util.List;

@Dao
public interface IngredientDao {
    @Insert
    long insert(IngredientEntity ingredient);

    @Insert
    void insertAll(List<IngredientEntity> ingredients);

    @Update
    void update(IngredientEntity ingredient);

    @Delete
    void delete(IngredientEntity ingredient);

    @Query("SELECT * FROM ingredient WHERE id = :id LIMIT 1")
    IngredientEntity findById(long id);

    @Query("SELECT * FROM ingredient WHERE userId = :userId ORDER BY expireDate ASC")
    List<IngredientEntity> getByUser(long userId);

    @Query("SELECT * FROM ingredient WHERE userId = :userId AND name LIKE '%' || :keyword || '%' ORDER BY expireDate ASC")
    List<IngredientEntity> search(long userId, String keyword);

    @Query("SELECT * FROM ingredient WHERE userId = :userId AND category = :category ORDER BY expireDate ASC")
    List<IngredientEntity> getByCategory(long userId, String category);

    @Query("SELECT COUNT(*) FROM ingredient WHERE userId = :userId")
    int countByUser(long userId);

    @Query("SELECT COUNT(*) FROM ingredient")
    int countAll();
}
