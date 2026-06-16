package com.example.smartfood.repository;

import android.content.Context;

import com.example.smartfood.database.AppDatabase;
import com.example.smartfood.database.DefaultDataSeeder;
import com.example.smartfood.entity.IngredientEntity;
import com.example.smartfood.model.RecipeRecommendResult;
import com.example.smartfood.util.RecipeRecommendUtil;

import java.util.List;

public class RecipeRepository {
    private final AppDatabase db;

    public RecipeRepository(Context context) {
        db = AppDatabase.getInstance(context);
    }

    public void getTopRecommendations(final long userId, final DataCallback<List<RecipeRecommendResult>> callback) {
        getRecommendations(userId, 20, callback);
    }

    public void getRecommendations(final long userId, final int limit, final DataCallback<List<RecipeRecommendResult>> callback) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                DefaultDataSeeder.seed(db);
                List<IngredientEntity> ingredients = db.ingredientDao().getByUser(userId);
                callback.onResult(RecipeRecommendUtil.calculateRecommendations(
                        ingredients,
                        db.recipeDao().getAll(),
                        db.recipeIngredientDao().getAll(),
                        limit
                ));
            }
        });
    }
}
