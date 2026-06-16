package com.example.smartfood.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.smartfood.dao.FoodNutritionDao;
import com.example.smartfood.dao.IngredientDao;
import com.example.smartfood.dao.MealRecordDao;
import com.example.smartfood.dao.NutritionTargetDao;
import com.example.smartfood.dao.RecipeDao;
import com.example.smartfood.dao.RecipeIngredientDao;
import com.example.smartfood.dao.ShoppingDao;
import com.example.smartfood.dao.UserDao;
import com.example.smartfood.entity.FoodNutritionEntity;
import com.example.smartfood.entity.IngredientEntity;
import com.example.smartfood.entity.MealRecordEntity;
import com.example.smartfood.entity.NutritionTargetEntity;
import com.example.smartfood.entity.RecipeEntity;
import com.example.smartfood.entity.RecipeIngredientEntity;
import com.example.smartfood.entity.ShoppingItemEntity;
import com.example.smartfood.entity.UserEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {
                UserEntity.class,
                IngredientEntity.class,
                RecipeEntity.class,
                RecipeIngredientEntity.class,
                ShoppingItemEntity.class,
                MealRecordEntity.class,
                FoodNutritionEntity.class,
                NutritionTargetEntity.class
        },
        version = 2,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public abstract UserDao userDao();
    public abstract IngredientDao ingredientDao();
    public abstract RecipeDao recipeDao();
    public abstract RecipeIngredientDao recipeIngredientDao();
    public abstract ShoppingDao shoppingDao();
    public abstract MealRecordDao mealRecordDao();
    public abstract FoodNutritionDao foodNutritionDao();
    public abstract NutritionTargetDao nutritionTargetDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "smart_food.db")
                            .addMigrations(MIGRATION_1_2)
                            .addCallback(roomCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    DefaultDataSeeder.seed(INSTANCE);
                }
            });
        }
    };

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE ingredient ADD COLUMN bestEatDate TEXT NOT NULL DEFAULT ''");
        }
    };
}
