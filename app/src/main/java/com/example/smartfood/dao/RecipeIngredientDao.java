package com.example.smartfood.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.smartfood.entity.RecipeIngredientEntity;

import java.util.List;

@Dao
public interface RecipeIngredientDao {
    @Insert
    void insertAll(List<RecipeIngredientEntity> recipeIngredients);

    @Query("SELECT * FROM recipe_ingredient WHERE recipeId = :recipeId")
    List<RecipeIngredientEntity> getByRecipe(long recipeId);

    @Query("SELECT * FROM recipe_ingredient")
    List<RecipeIngredientEntity> getAll();
}
