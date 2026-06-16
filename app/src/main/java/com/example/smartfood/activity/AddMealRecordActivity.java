package com.example.smartfood.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter01.R;
import com.example.smartfood.entity.FoodNutritionEntity;
import com.example.smartfood.entity.MealRecordEntity;
import com.example.smartfood.repository.DataCallback;
import com.example.smartfood.repository.HealthRepository;
import com.example.smartfood.util.BottomSheetSelectUtil;
import com.example.smartfood.util.DateUtil;
import com.example.smartfood.util.FoodCatalog;
import com.example.smartfood.util.SessionManager;
import com.example.smartfood.util.SmartToast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddMealRecordActivity extends AppCompatActivity {
    public static final String EXTRA_RECORD_ID = "meal_record_id";
    private static final String[] MEAL_TYPES = {"早餐", "午餐", "晚餐", "加餐"};

    private TextView titleText;
    private TextView saveButton;
    private EditText dateEdit;
    private EditText amountEdit;
    private TextView mealTypeView;
    private TextView foodCategoryView;
    private TextView foodView;
    private TextView previewText;
    private HealthRepository repository;
    private final List<FoodNutritionEntity> nutritionList = new ArrayList<>();
    private long userId;
    private long editingRecordId = -1;
    private MealRecordEntity editingRecord;
    private String selectedMealType = MEAL_TYPES[0];
    private String selectedFoodCategory = "全部分类";
    private String selectedFoodName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal_record);
        repository = new HealthRepository(this);
        userId = new SessionManager(this).getCurrentUserId();
        editingRecordId = getIntent().getLongExtra(EXTRA_RECORD_ID, -1);
        bindViews();
        setupStaticFields();
        loadFoodOptions();
    }

    private void bindViews() {
        titleText = findViewById(R.id.tv_meal_form_title);
        dateEdit = findViewById(R.id.et_meal_date);
        amountEdit = findViewById(R.id.et_amount_gram);
        mealTypeView = findViewById(R.id.sp_meal_type);
        foodCategoryView = findViewById(R.id.sp_food_category);
        foodView = findViewById(R.id.sp_food_name);
        previewText = findViewById(R.id.tv_nutrition_preview);
        saveButton = findViewById(R.id.btn_save_meal);
        if (editingRecordId > 0) {
            titleText.setText("编辑饮食记录");
            saveButton.setText("保存修改");
        }
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMealRecord();
            }
        });
    }

    private void setupStaticFields() {
        dateEdit.setText(DateUtil.getToday());
        amountEdit.setText("100");
        selectedMealType = MEAL_TYPES[0];
        mealTypeView.setText(selectedMealType);
        selectedFoodCategory = "全部分类";
        foodCategoryView.setText(selectedFoodCategory);
        mealTypeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetSelectUtil.show(AddMealRecordActivity.this, "选择餐次",
                        Arrays.asList(MEAL_TYPES), selectedMealType,
                        new BottomSheetSelectUtil.OnItemSelectedListener() {
                            @Override
                            public void onSelected(String value) {
                                selectedMealType = value;
                                mealTypeView.setText(value);
                            }
                        });
            }
        });
        foodCategoryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetSelectUtil.show(AddMealRecordActivity.this, "选择食物分类",
                        getFoodCategories(), selectedFoodCategory,
                        new BottomSheetSelectUtil.OnItemSelectedListener() {
                            @Override
                            public void onSelected(String value) {
                                selectedFoodCategory = value;
                                foodCategoryView.setText(value);
                                ensureSelectedFoodInCategory();
                                updateNutritionPreview();
                            }
                        });
            }
        });
        foodView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetSelectUtil.show(AddMealRecordActivity.this, "选择食物",
                        getFilteredFoodNames(), selectedFoodName,
                        new BottomSheetSelectUtil.OnItemSelectedListener() {
                            @Override
                            public void onSelected(String value) {
                                selectedFoodName = value;
                                foodView.setText(value);
                                updateNutritionPreview();
                            }
                        });
            }
        });
        amountEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateNutritionPreview();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void loadFoodOptions() {
        repository.getFoodNutritionList(new DataCallback<List<FoodNutritionEntity>>() {
            @Override
            public void onResult(final List<FoodNutritionEntity> data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        nutritionList.clear();
                        nutritionList.addAll(data);
                        if (selectedFoodName == null && !nutritionList.isEmpty()) {
                            ensureSelectedFoodInCategory();
                        }
                        updateNutritionPreview();
                        loadEditingRecordIfNeeded();
                    }
                });
            }
        });
    }

    private List<String> getFoodCategories() {
        List<String> categories = new ArrayList<>();
        categories.add("全部分类");
        categories.addAll(Arrays.asList(FoodCatalog.CATEGORIES));
        return categories;
    }

    private List<String> getFilteredFoodNames() {
        List<String> names = new ArrayList<>();
        for (FoodNutritionEntity item : nutritionList) {
            if ("全部分类".equals(selectedFoodCategory)
                    || selectedFoodCategory.equals(FoodCatalog.getCategoryByName(item.foodName))) {
                names.add(item.foodName);
            }
        }
        return names;
    }

    private void ensureSelectedFoodInCategory() {
        List<String> names = getFilteredFoodNames();
        if (names.isEmpty()) {
            selectedFoodName = null;
            foodView.setText("暂无可选食物");
            return;
        }
        if (selectedFoodName == null || !names.contains(selectedFoodName)) {
            selectedFoodName = names.get(0);
        }
        foodView.setText(selectedFoodName);
    }

    private void loadEditingRecordIfNeeded() {
        if (editingRecordId <= 0) {
            return;
        }
        repository.getMealRecordById(editingRecordId, new DataCallback<MealRecordEntity>() {
            @Override
            public void onResult(final MealRecordEntity data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (data == null) {
                            SmartToast.show(AddMealRecordActivity.this, "饮食记录不存在");
                            finish();
                            return;
                        }
                        editingRecord = data;
                        applyEditingRecord(data);
                    }
                });
            }
        });
    }

    private void applyEditingRecord(MealRecordEntity record) {
        dateEdit.setText(record.mealDate);
        amountEdit.setText(formatAmount(record.amountGram));
        selectedMealType = record.mealType;
        selectedFoodName = record.foodName;
        selectedFoodCategory = FoodCatalog.getCategoryByName(record.foodName);
        mealTypeView.setText(selectedMealType);
        foodCategoryView.setText(selectedFoodCategory);
        foodView.setText(selectedFoodName);
        updateNutritionPreview();
    }

    private void updateNutritionPreview() {
        FoodNutritionEntity food = getSelectedFood();
        double amount = getAmount();
        if (food == null) {
            previewText.setText("请选择食物");
            return;
        }
        double ratio = amount / 100.0;
        previewText.setText("营养预估（本系统营养数据仅用于课程设计演示和日常饮食参考，不作为医学诊断依据）\n"
                + "热量：" + Math.round(food.calories * ratio) + " kcal\n"
                + "蛋白质：" + round1(food.protein * ratio) + " g\n"
                + "脂肪：" + round1(food.fat * ratio) + " g\n"
                + "碳水：" + round1(food.carbohydrate * ratio) + " g\n"
                + "膳食纤维：" + round1(food.fiber * ratio) + " g");
    }

    private void saveMealRecord() {
        FoodNutritionEntity food = getSelectedFood();
        String mealDate = dateEdit.getText().toString().trim();
        if (food == null) {
            SmartToast.show(this, "请选择食物");
            return;
        }
        if (TextUtils.isEmpty(mealDate)) {
            SmartToast.show(this, "日期不能为空");
            return;
        }
        double amount = getAmount();
        if (amount <= 0) {
            SmartToast.show(this, "食用重量必须大于 0");
            return;
        }

        MealRecordEntity record = new MealRecordEntity(userId, mealDate, selectedMealType, food.foodName, amount, DateUtil.getToday());
        if (editingRecord != null) {
            record.id = editingRecord.id;
            record.createdAt = editingRecord.createdAt;
            repository.updateMealRecord(record, new DataCallback<Boolean>() {
                @Override
                public void onResult(Boolean data) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SmartToast.show(AddMealRecordActivity.this, "饮食记录已修改");
                            finish();
                        }
                    });
                }
            });
            return;
        }

        repository.addMealRecord(record, new DataCallback<Long>() {
            @Override
            public void onResult(Long data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SmartToast.show(AddMealRecordActivity.this, "饮食记录已保存");
                        finish();
                    }
                });
            }
        });
    }

    private FoodNutritionEntity getSelectedFood() {
        if (selectedFoodName == null) {
            return null;
        }
        for (FoodNutritionEntity item : nutritionList) {
            if (selectedFoodName.equals(item.foodName)) {
                return item;
            }
        }
        return null;
    }

    private double getAmount() {
        try {
            return Double.parseDouble(amountEdit.getText().toString().trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private double round1(double value) {
        return Math.round(value * 10) / 10.0;
    }

    private String formatAmount(double amount) {
        if (amount == Math.floor(amount)) {
            return String.valueOf((int) amount);
        }
        return String.valueOf(amount);
    }
}
