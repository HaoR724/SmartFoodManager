package com.example.smartfood.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.smartfood.entity.UserEntity;

@Dao
public interface UserDao {
    @Insert
    long insert(UserEntity user);

    @Query("SELECT * FROM user WHERE username = :username LIMIT 1")
    UserEntity findByUsername(String username);

    @Query("SELECT * FROM user WHERE username = :username AND password = :password LIMIT 1")
    UserEntity login(String username, String password);

    @Query("SELECT * FROM user WHERE id = :id LIMIT 1")
    UserEntity findById(long id);

    @Query("SELECT COUNT(*) FROM user")
    int count();
}
