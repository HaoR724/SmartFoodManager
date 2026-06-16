package com.example.smartfood.repository;

import android.content.Context;

import com.example.smartfood.database.AppDatabase;
import com.example.smartfood.entity.ShoppingItemEntity;

import java.util.List;

public class ShoppingRepository {
    private final AppDatabase db;

    public ShoppingRepository(Context context) {
        db = AppDatabase.getInstance(context);
    }

    public void getShoppingItems(final long userId, final DataCallback<List<ShoppingItemEntity>> callback) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                callback.onResult(db.shoppingDao().getByUser(userId));
            }
        });
    }

    public void addItems(final List<ShoppingItemEntity> items, final DataCallback<Boolean> callback) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                db.shoppingDao().insertAll(items);
                callback.onResult(true);
            }
        });
    }

    public void update(final ShoppingItemEntity item, final DataCallback<Boolean> callback) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                db.shoppingDao().update(item);
                callback.onResult(true);
            }
        });
    }
}
