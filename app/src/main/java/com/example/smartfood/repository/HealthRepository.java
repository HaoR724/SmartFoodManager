package com.example.smartfood.repository;

import android.content.Context;

import com.example.smartfood.database.AppDatabase;
import com.example.smartfood.database.DefaultDataSeeder;
import com.example.smartfood.entity.FoodNutritionEntity;
import com.example.smartfood.entity.MealRecordEntity;
import com.example.smartfood.entity.NutritionTargetEntity;
import com.example.smartfood.model.NutritionAnalyzeResult;
import com.example.smartfood.util.DateUtil;
import com.example.smartfood.util.NutritionUtil;

import java.util.ArrayList;
import java.util.List;

public class HealthRepository {
    private static final int MONTH_DAYS = 30;
    private final AppDatabase db;

    public HealthRepository(Context context) {
        db = AppDatabase.getInstance(context);
    }

    public void analyzeToday(final long userId, final DataCallback<NutritionAnalyzeResult> callback) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                DefaultDataSeeder.seed(db);
                callback.onResult(NutritionUtil.analyzeTodayNutrition(
                        db.mealRecordDao().getByDate(userId, DateUtil.getToday()),
                        db.foodNutritionDao().getAll(),
                        db.nutritionTargetDao().getByUser(userId)
                ));
            }
        });
    }

    public void getTodayRecords(final long userId, final DataCallback<List<MealRecordEntity>> callback) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                callback.onResult(db.mealRecordDao().getByDate(userId, DateUtil.getToday()));
            }
        });
    }

    public void analyzeRecentMonth(final long userId, final DataCallback<NutritionAnalyzeResult> callback) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                DefaultDataSeeder.seed(db);
                String endDate = DateUtil.getToday();
                String startDate = DateUtil.addDays(endDate, -MONTH_DAYS + 1);
                callback.onResult(NutritionUtil.analyzePeriodNutrition(
                        db.mealRecordDao().getByDateRange(userId, startDate, endDate),
                        db.foodNutritionDao().getAll(),
                        db.nutritionTargetDao().getByUser(userId),
                        MONTH_DAYS,
                        startDate,
                        endDate
                ));
            }
        });
    }

    public void getRecentMonthRecords(final long userId, final DataCallback<List<MealRecordEntity>> callback) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                String endDate = DateUtil.getToday();
                String startDate = DateUtil.addDays(endDate, -MONTH_DAYS + 1);
                callback.onResult(db.mealRecordDao().getByDateRange(userId, startDate, endDate));
            }
        });
    }

    public void addMealRecord(final MealRecordEntity record, final DataCallback<Long> callback) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                callback.onResult(db.mealRecordDao().insert(record));
            }
        });
    }

    public void getMealRecordById(final long recordId, final DataCallback<MealRecordEntity> callback) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                callback.onResult(db.mealRecordDao().findById(recordId));
            }
        });
    }

    public void updateMealRecord(final MealRecordEntity record, final DataCallback<Boolean> callback) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                db.mealRecordDao().update(record);
                callback.onResult(true);
            }
        });
    }

    public void deleteMealRecord(final MealRecordEntity record, final DataCallback<Boolean> callback) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                db.mealRecordDao().delete(record);
                callback.onResult(true);
            }
        });
    }

    public void addRecentMonthSampleRecords(final long userId, final DataCallback<Integer> callback) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                DefaultDataSeeder.seed(db);
                String endDate = DateUtil.getToday();
                String startDate = DateUtil.addDays(endDate, -MONTH_DAYS + 1);
                int existingDays = db.mealRecordDao().countMealDays(userId, startDate, endDate);
                if (existingDays >= MONTH_DAYS) {
                    callback.onResult(0);
                    return;
                }
                List<MealRecordEntity> records = buildMonthSampleRecords(userId, endDate);
                int inserted = 0;
                for (MealRecordEntity record : records) {
                    List<MealRecordEntity> dayRecords = db.mealRecordDao().getByDate(userId, record.mealDate);
                    if (dayRecords.isEmpty()) {
                        db.mealRecordDao().insert(record);
                        inserted++;
                    } else if (countRecordsForDate(records, record.mealDate) > dayRecords.size()
                            && !containsMeal(dayRecords, record.mealType, record.foodName)) {
                        db.mealRecordDao().insert(record);
                        inserted++;
                    }
                }
                callback.onResult(inserted);
            }
        });
    }

    public void getFoodNutritionList(final DataCallback<List<FoodNutritionEntity>> callback) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                DefaultDataSeeder.seed(db);
                callback.onResult(db.foodNutritionDao().getAll());
            }
        });
    }

    private List<MealRecordEntity> buildMonthSampleRecords(long userId, String endDate) {
        List<MealRecordEntity> result = new ArrayList<>();
        String[][] plans = {
                {"早餐", "牛奶", "250"}, {"午餐", "鸡胸肉", "120"}, {"晚餐", "西兰花", "180"}, {"加餐", "苹果", "150"},
                {"早餐", "鸡蛋", "100"}, {"午餐", "牛肉", "120"}, {"晚餐", "胡萝卜", "160"}, {"加餐", "酸奶", "180"},
                {"早餐", "燕麦", "80"}, {"午餐", "虾仁", "120"}, {"晚餐", "西红柿", "180"}, {"加餐", "香蕉", "120"},
                {"早餐", "全麦面包", "90"}, {"午餐", "豆腐", "180"}, {"晚餐", "菠菜", "160"}, {"加餐", "橙子", "150"}
        };
        for (int day = MONTH_DAYS - 1; day >= 0; day--) {
            String date = DateUtil.addDays(endDate, -day);
            int offset = (MONTH_DAYS - 1 - day) % 4;
            for (int meal = 0; meal < 4; meal++) {
                String[] item = plans[offset * 4 + meal];
                result.add(new MealRecordEntity(userId, date, item[0], item[1], Double.parseDouble(item[2]), DateUtil.getToday()));
            }
        }
        return result;
    }

    private int countRecordsForDate(List<MealRecordEntity> records, String date) {
        int count = 0;
        for (MealRecordEntity record : records) {
            if (date.equals(record.mealDate)) {
                count++;
            }
        }
        return count;
    }

    private boolean containsMeal(List<MealRecordEntity> records, String mealType, String foodName) {
        for (MealRecordEntity record : records) {
            if (mealType.equals(record.mealType) && foodName.equals(record.foodName)) {
                return true;
            }
        }
        return false;
    }
}
