package com.example.smartfood.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.smartfood.entity.ShoppingItemEntity;

import java.util.List;

@Dao
public interface ShoppingDao {
    @Insert
    long insert(ShoppingItemEntity item);

    @Insert
    void insertAll(List<ShoppingItemEntity> items);

    @Update
    void update(ShoppingItemEntity item);

    @Delete
    void delete(ShoppingItemEntity item);

    @Query("SELECT * FROM shopping_item WHERE userId = :userId ORDER BY status ASC, createdAt DESC")
    List<ShoppingItemEntity> getByUser(long userId);

    @Query("DELETE FROM shopping_item WHERE userId = :userId AND status = 1")
    void clearPurchased(long userId);
}
