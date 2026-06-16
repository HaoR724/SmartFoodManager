package com.example.smartfood.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.smartfood.entity.RecipeEntity;

import java.util.List;

@Dao
public interface RecipeDao {
    @Insert
    long insert(RecipeEntity recipe);

    @Query("SELECT * FROM recipe ORDER BY id ASC")
    List<RecipeEntity> getAll();

    @Query("SELECT * FROM recipe WHERE id = :id LIMIT 1")
    RecipeEntity findById(long id);

    @Query("SELECT * FROM recipe WHERE name LIKE '%' || :keyword || '%' ORDER BY id ASC")
    List<RecipeEntity> search(String keyword);

    @Query("SELECT * FROM recipe WHERE category = :category ORDER BY id ASC")
    List<RecipeEntity> getByCategory(String category);

    @Query("SELECT COUNT(*) FROM recipe")
    int count();
}
