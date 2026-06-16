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
import com.example.smartfood.entity.IngredientEntity;
import com.example.smartfood.repository.DataCallback;
import com.example.smartfood.repository.IngredientRepository;
import com.example.smartfood.util.BottomSheetSelectUtil;
import com.example.smartfood.util.DateUtil;
import com.example.smartfood.util.FoodCatalog;
import com.example.smartfood.util.SessionManager;
import com.example.smartfood.util.ShelfLifeUtil;
import com.example.smartfood.util.SmartToast;

import java.util.Arrays;

public class AddIngredientActivity extends AppCompatActivity {
    public static final String EXTRA_INGREDIENT_ID = "ingredient_id";
    private static final String[] STORAGE_PLACES = {"冰箱", "冷冻", "常温"};

    private TextView nameView;
    private EditText quantityEdit;
    private EditText unitEdit;
    private EditText buyDateEdit;
    private EditText noteEdit;
    private TextView bestEatDateText;
    private TextView expireDateText;
    private TextView shelfLifeRuleText;
    private TextView categoryView;
    private TextView storageView;
    private IngredientRepository repository;
    private IngredientEntity editingIngredient;
    private long userId;
    private long ingredientId;
    private String selectedCategory = "叶菜类";
    private String selectedName;
    private String selectedStorage = "冰箱";
    private String pendingIngredientName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);
        repository = new IngredientRepository(this);
        userId = new SessionManager(this).getCurrentUserId();
        ingredientId = getIntent().getLongExtra(EXTRA_INGREDIENT_ID, -1);
        bindViews();
        setupSelectors();
        setupDefaults();
        if (ingredientId > 0) {
            loadIngredient();
        }
        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveIngredient();
            }
        });
    }

    private void bindViews() {
        nameView = findViewById(R.id.sp_name);
        quantityEdit = findViewById(R.id.et_quantity);
        unitEdit = findViewById(R.id.et_unit);
        buyDateEdit = findViewById(R.id.et_buy_date);
        noteEdit = findViewById(R.id.et_note);
        bestEatDateText = findViewById(R.id.tv_best_eat_date);
        expireDateText = findViewById(R.id.tv_expire_date);
        shelfLifeRuleText = findViewById(R.id.tv_shelf_life_rule);
        categoryView = findViewById(R.id.sp_category);
        storageView = findViewById(R.id.sp_storage);
    }

    private void setupSelectors() {
        categoryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetSelectUtil.show(AddIngredientActivity.this, "选择食材分类",
                        Arrays.asList(FoodCatalog.CATEGORIES), selectedCategory,
                        new BottomSheetSelectUtil.OnItemSelectedListener() {
                            @Override
                            public void onSelected(String value) {
                                selectedCategory = value;
                                categoryView.setText(value);
                                updateNameOptions(value);
                                unitEdit.setText(FoodCatalog.getDefaultUnit(value));
                                updateAutoDates();
                            }
                        });
            }
        });
        nameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetSelectUtil.show(AddIngredientActivity.this, "选择食材",
                        Arrays.asList(FoodCatalog.getNamesByCategory(selectedCategory)), selectedName,
                        new BottomSheetSelectUtil.OnItemSelectedListener() {
                            @Override
                            public void onSelected(String value) {
                                selectedName = value;
                                nameView.setText(value);
                                updateAutoDates();
                            }
                        });
            }
        });
        storageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetSelectUtil.show(AddIngredientActivity.this, "选择存放位置",
                        Arrays.asList(STORAGE_PLACES), selectedStorage,
                        new BottomSheetSelectUtil.OnItemSelectedListener() {
                            @Override
                            public void onSelected(String value) {
                                selectedStorage = value;
                                storageView.setText(value);
                                updateAutoDates();
                            }
                        });
            }
        });
        buyDateEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateAutoDates();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void updateNameOptions(String category) {
        String[] names = FoodCatalog.getNamesByCategory(category);
        if (!TextUtils.isEmpty(pendingIngredientName) && contains(names, pendingIngredientName)) {
            selectedName = pendingIngredientName;
        } else if (TextUtils.isEmpty(selectedName) || !contains(names, selectedName)) {
            selectedName = names.length == 0 ? "" : names[0];
        }
        nameView.setText(selectedName);
    }

    private boolean contains(String[] data, String value) {
        if (value == null) return false;
        for (String item : data) {
            if (value.equals(item)) return true;
        }
        return false;
    }

    private void setupDefaults() {
        TextView title = findViewById(R.id.tv_title);
        title.setText(ingredientId > 0 ? "编辑食材" : "添加食材");
        buyDateEdit.setText(DateUtil.getToday());
        selectedCategory = "叶菜类";
        selectedStorage = "冰箱";
        categoryView.setText(selectedCategory);
        storageView.setText(selectedStorage);
        updateNameOptions(selectedCategory);
        unitEdit.setText(FoodCatalog.getDefaultUnit(selectedCategory));
        quantityEdit.setText("1");
        updateAutoDates();
    }

    private void loadIngredient() {
        repository.findById(ingredientId, new DataCallback<IngredientEntity>() {
            @Override
            public void onResult(final IngredientEntity data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (data == null) {
                            SmartToast.show(AddIngredientActivity.this, "食材不存在");
                            finish();
                            return;
                        }
                        editingIngredient = data;
                        fillForm(data);
                    }
                });
            }
        });
    }

    private void fillForm(IngredientEntity data) {
        pendingIngredientName = data.name;
        selectedCategory = data.category;
        selectedStorage = data.storagePlace;
        categoryView.setText(selectedCategory);
        storageView.setText(selectedStorage);
        updateNameOptions(selectedCategory);
        selectedName = data.name;
        nameView.setText(selectedName);
        quantityEdit.setText(String.valueOf(data.quantity));
        buyDateEdit.setText(data.buyDate);
        noteEdit.setText(data.note);
        unitEdit.setText(data.unit);
        updateAutoDates();
    }

    private void saveIngredient() {
        String name = selectedName == null ? "" : selectedName;
        String unit = unitEdit.getText().toString().trim();
        String buyDate = buyDateEdit.getText().toString().trim();
        String note = noteEdit.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            SmartToast.show(this, "食材名称不能为空");
            return;
        }
        if (TextUtils.isEmpty(unit)) {
            SmartToast.show(this, "单位不能为空");
            return;
        }
        double quantity;
        try {
            quantity = Double.parseDouble(quantityEdit.getText().toString().trim());
        } catch (Exception e) {
            SmartToast.show(this, "数量格式不正确");
            return;
        }
        if (TextUtils.isEmpty(buyDate)) {
            SmartToast.show(this, "购买日期不能为空");
            return;
        }
        String bestEatDate = ShelfLifeUtil.calculateBestEatDate(buyDate, name, selectedCategory, selectedStorage);
        String expireDate = ShelfLifeUtil.calculateExpireDate(buyDate, name, selectedCategory, selectedStorage);
        if (editingIngredient == null) {
            IngredientEntity ingredient = new IngredientEntity(userId, name, selectedCategory, quantity, unit,
                    buyDate, bestEatDate, expireDate, selectedStorage, note, DateUtil.getToday());
            repository.insert(ingredient, new DataCallback<Long>() {
                @Override
                public void onResult(Long data) {
                    showSavedAndFinish("添加成功");
                }
            });
        } else {
            editingIngredient.name = name;
            editingIngredient.category = selectedCategory;
            editingIngredient.quantity = quantity;
            editingIngredient.unit = unit;
            editingIngredient.buyDate = buyDate;
            editingIngredient.bestEatDate = bestEatDate;
            editingIngredient.expireDate = expireDate;
            editingIngredient.storagePlace = selectedStorage;
            editingIngredient.note = note;
            repository.update(editingIngredient, new DataCallback<Boolean>() {
                @Override
                public void onResult(Boolean data) {
                    showSavedAndFinish("保存成功");
                }
            });
        }
    }

    private void showSavedAndFinish(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SmartToast.show(AddIngredientActivity.this, message);
                finish();
            }
        });
    }

    private void updateAutoDates() {
        if (bestEatDateText == null || expireDateText == null || shelfLifeRuleText == null) {
            return;
        }
        String buyDate = buyDateEdit == null ? "" : buyDateEdit.getText().toString().trim();
        String name = TextUtils.isEmpty(selectedName) ? FoodCatalog.getNamesByCategory(selectedCategory)[0] : selectedName;
        if (TextUtils.isEmpty(buyDate)) {
            bestEatDateText.setText("最佳食用日期：待购买日期填写后自动计算");
            expireDateText.setText("过期日期：待购买日期填写后自动计算");
            shelfLifeRuleText.setText("");
            return;
        }
        String bestEatDate = ShelfLifeUtil.calculateBestEatDate(buyDate, name, selectedCategory, selectedStorage);
        String expireDate = ShelfLifeUtil.calculateExpireDate(buyDate, name, selectedCategory, selectedStorage);
        bestEatDateText.setText("最佳食用日期：" + bestEatDate);
        expireDateText.setText("预计过期日期：" + expireDate);
        shelfLifeRuleText.setText(ShelfLifeUtil.getRuleDescription(name, selectedCategory, selectedStorage));
    }
}
