package com.example.smartfood.repository;

import android.content.Context;

import com.example.smartfood.database.AppDatabase;
import com.example.smartfood.database.DefaultDataSeeder;
import com.example.smartfood.entity.UserEntity;
import com.example.smartfood.network.ApiClient;
import com.example.smartfood.util.DateUtil;

public class UserRepository {
    private final AppDatabase db;
    private final ApiClient apiClient;

    public UserRepository(Context context) {
        db = AppDatabase.getInstance(context);
        apiClient = new ApiClient();
    }

    public void login(final String username, final String password, final DataCallback<UserEntity> callback) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                DefaultDataSeeder.seed(db);
                ApiClient.ApiResponse response = apiClient.login(username, password);
                if (!response.success) {
                    callback.onResult(null);
                    return;
                }
                callback.onResult(syncLocalUser(response.username, password, response.nickname));
            }
        });
    }

    public void register(final String username, final String password, final String nickname, final DataCallback<String> callback) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                DefaultDataSeeder.seed(db);
                ApiClient.ApiResponse response = apiClient.register(username, password, nickname);
                if (response.success) {
                    syncLocalUser(response.username, password, response.nickname);
                }
                callback.onResult(response.message);
            }
        });
    }

    private UserEntity syncLocalUser(String username, String password, String nickname) {
        UserEntity localUser = db.userDao().findByUsername(username);
        if (localUser != null) {
            return localUser;
        }
        // 后端负责账号认证；本地保留用户镜像，供现有 Room 食材、菜谱、健康数据继续按 userId 关联。
        db.userDao().insert(new UserEntity(username, password, nickname, DateUtil.getToday()));
        return db.userDao().findByUsername(username);
    }
}
