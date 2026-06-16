package com.example.smartfood.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "user", indices = {@Index(value = "username", unique = true)})
public class UserEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String username;
    // 课程设计演示中明文保存；真实项目应使用加盐哈希等方式加密存储。
    public String password;
    public String nickname;
    public String createdAt;

    public UserEntity(String username, String password, String nickname, String createdAt) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.createdAt = createdAt;
    }
}
