package com.example.smartfood.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter01.R;
import com.example.smartfood.entity.ShoppingItemEntity;
import com.example.smartfood.model.MissingIngredient;
import com.example.smartfood.model.RecipeRecommendResult;
import com.example.smartfood.repository.DataCallback;
import com.example.smartfood.repository.RecipeRepository;
import com.example.smartfood.repository.ShoppingRepository;
import com.example.smartfood.util.DateUtil;
import com.example.smartfood.util.RecipeCategoryUtil;
import com.example.smartfood.util.RecipeImageUtil;
import com.example.smartfood.util.SessionManager;
import com.example.smartfood.util.SmartToast;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {
    public static final String EXTRA_RECIPE_ID = "recipe_id";

    private TextView titleText;
    private TextView categoryText;
    private TextView summaryText;
    private TextView ingredientsText;
    private TextView stepsText;
    private ImageView recipeImage;
    private Button addMissingButton;
    private RecipeRecommendResult currentResult;
    private long userId;
    private long recipeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        userId = new SessionManager(this).getCurrentUserId();
        recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);
        bindViews();
        addMissingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMissingToShoppingList();
            }
        });
        loadRecipeDetail();
    }

    private void bindViews() {
        titleText = findViewById(R.id.tv_recipe_title);
        categoryText = findViewById(R.id.tv_recipe_category);
        summaryText = findViewById(R.id.tv_recipe_summary);
        ingredientsText = findViewById(R.id.tv_recipe_ingredients);
        stepsText = findViewById(R.id.tv_recipe_steps);
        recipeImage = findViewById(R.id.iv_recipe_detail_image);
        addMissingButton = findViewById(R.id.btn_add_missing);
    }

    private void loadRecipeDetail() {
        new RecipeRepository(this).getRecommendations(userId, 200, new DataCallback<List<RecipeRecommendResult>>() {
            @Override
            public void onResult(final List<RecipeRecommendResult> data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        currentResult = findResult(data);
                        if (currentResult == null) {
                            SmartToast.show(RecipeDetailActivity.this, "菜谱不存在");
                            finish();
                            return;
                        }
                        renderRecipe();
                    }
                });
            }
        });
    }

    private RecipeRecommendResult findResult(List<RecipeRecommendResult> data) {
        for (RecipeRecommendResult result : data) {
            if (result.recipe.id == recipeId) {
                return result;
            }
        }
        return null;
    }

    private void renderRecipe() {
        titleText.setText(currentResult.recipe.name);
        categoryText.setText(RecipeCategoryUtil.getDisplayCategories(currentResult.recipe));
        recipeImage.setImageResource(RecipeImageUtil.getImageResId(this, currentResult.recipe));
        summaryText.setText("简介\n"
                + currentResult.recipe.description + "\n\n"
                + "匹配度：" + Math.round(currentResult.matchRate * 100) + "%\n"
                + "推荐分数：" + Math.round(currentResult.score * 100) + "\n"
                + "推荐理由：" + currentResult.reason);
        ingredientsText.setText(buildIngredientText());
        stepsText.setText("制作步骤\n" + currentResult.recipe.steps);
        addMissingButton.setEnabled(!currentResult.missingIngredients.isEmpty());
        addMissingButton.setText(currentResult.missingIngredients.isEmpty() ? "所需食材已齐全" : "将缺少食材加入购物清单");
    }

    private String buildIngredientText() {
        StringBuilder builder = new StringBuilder();
        builder.append("已有食材\n");
        if (currentResult.ownedIngredients.isEmpty()) {
            builder.append("暂无\n");
        } else {
            for (String name : currentResult.ownedIngredients) {
                builder.append("• ").append(name).append("\n");
            }
        }
        builder.append("\n缺少食材\n");
        if (currentResult.missingIngredients.isEmpty()) {
            builder.append("无，当前库存可以制作这道菜\n");
        } else {
            for (MissingIngredient item : currentResult.missingIngredients) {
                builder.append("• ").append(item.name)
                        .append(" ").append(formatAmount(item.amount))
                        .append(item.unit).append("\n");
            }
        }
        return builder.toString();
    }

    private String formatAmount(double amount) {
        if (amount == Math.floor(amount)) {
            return String.valueOf((int) amount);
        }
        return String.valueOf(amount);
    }

    private void addMissingToShoppingList() {
        if (currentResult == null || currentResult.missingIngredients.isEmpty()) {
            SmartToast.show(this, "没有缺少食材需要添加");
            return;
        }
        List<ShoppingItemEntity> items = new ArrayList<>();
        for (MissingIngredient missing : currentResult.missingIngredients) {
            items.add(new ShoppingItemEntity(userId, missing.name, missing.amount, missing.unit,
                    0, currentResult.recipe.id, DateUtil.getToday()));
        }
        new ShoppingRepository(this).addItems(items, new DataCallback<Boolean>() {
            @Override
            public void onResult(Boolean data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SmartToast.show(RecipeDetailActivity.this, "已加入购物清单");
                    }
                });
            }
        });
    }
}
