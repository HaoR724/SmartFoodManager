package com.example.smartfood.repository;

import android.content.Context;

import com.example.smartfood.database.AppDatabase;
import com.example.smartfood.entity.IngredientEntity;

import java.util.List;

public class IngredientRepository {
    private final AppDatabase db;

    public IngredientRepository(Context context) {
        db = AppDatabase.getInstance(context);
    }

    public void getIngredients(final long userId, final DataCallback<List<IngredientEntity>> callback) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                callback.onResult(db.ingredientDao().getByUser(userId));
            }
        });
    }

    public void findById(final long id, final DataCallback<IngredientEntity> callback) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                callback.onResult(db.ingredientDao().findById(id));
            }
        });
    }

    public void insert(final IngredientEntity ingredient, final DataCallback<Long> callback) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                callback.onResult(db.ingredientDao().insert(ingredient));
            }
        });
    }

    public void update(final IngredientEntity ingredient, final DataCallback<Boolean> callback) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                db.ingredientDao().update(ingredient);
                callback.onResult(true);
            }
        });
    }

    public void delete(final IngredientEntity ingredient, final DataCallback<Boolean> callback) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                db.ingredientDao().delete(ingredient);
                callback.onResult(true);
            }
        });
    }
}
