package com.example.smartfood.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.chapter01.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.smartfood.activity.AddIngredientActivity;
import com.example.smartfood.activity.AddMealRecordActivity;
import com.example.smartfood.activity.LoginActivity;
import com.example.smartfood.activity.RecipeDetailActivity;
import com.example.smartfood.database.AppDatabase;
import com.example.smartfood.database.DefaultDataSeeder;
import com.example.smartfood.entity.IngredientEntity;
import com.example.smartfood.entity.MealRecordEntity;
import com.example.smartfood.entity.NutritionTargetEntity;
import com.example.smartfood.entity.UserEntity;
import com.example.smartfood.model.NutritionAnalyzeResult;
import com.example.smartfood.model.RecipeRecommendResult;
import com.example.smartfood.util.DateUtil;
import com.example.smartfood.util.ExpireUtil;
import com.example.smartfood.util.IngredientImageUtil;
import com.example.smartfood.util.NutritionUtil;
import com.example.smartfood.util.RecipeImageUtil;
import com.example.smartfood.util.RecipeRecommendUtil;
import com.example.smartfood.util.SessionManager;
import com.example.smartfood.view.CalorieRingView;
import com.example.smartfood.view.NutrientBarView;

import java.util.List;

public class HomeFragment extends Fragment {
    private static final int DEFAULT_CALORIE_TARGET = 2000;

    private TextView greetingText;
    private TextView dateText;
    private TextView avatarText;
    private CalorieRingView calorieRingView;
    private TextView proteinText;
    private TextView fatText;
    private TextView carbohydrateText;
    private TextView fiberText;
    private TextView ingredientCountText;
    private TextView expireCountText;
    private TextView mealCountText;
    private TextView calorieGapText;
    private TextView fiberStatusText;
    private LinearLayout recentMealsLayout;
    private TextView homeTipText;
    private NutrientBarView proteinBar;
    private NutrientBarView fatBar;
    private NutrientBarView carbohydrateBar;
    private NutrientBarView fiberBar;
    private LinearLayout recipeCardsLayout;
    private long userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_dashboard, container, false);
        userId = new SessionManager(requireContext()).getCurrentUserId();
        bindViews(view);
        setupAvatarLogout();
        setupQuickActions(view);
        configureNutrientBars();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDashboard();
    }

    private void bindViews(View view) {
        greetingText = view.findViewById(R.id.tv_home_greeting);
        dateText = view.findViewById(R.id.tv_home_date);
        avatarText = view.findViewById(R.id.tv_avatar);
        calorieRingView = view.findViewById(R.id.view_calorie_ring);
        proteinText = view.findViewById(R.id.tv_protein);
        fatText = view.findViewById(R.id.tv_fat);
        carbohydrateText = view.findViewById(R.id.tv_carbohydrate);
        fiberText = view.findViewById(R.id.tv_fiber);
        ingredientCountText = view.findViewById(R.id.tv_ingredient_count);
        expireCountText = view.findViewById(R.id.tv_expire_count);
        mealCountText = view.findViewById(R.id.tv_home_meal_count);
        calorieGapText = view.findViewById(R.id.tv_home_calorie_gap);
        fiberStatusText = view.findViewById(R.id.tv_home_fiber_status);
        recentMealsLayout = view.findViewById(R.id.layout_recent_meals);
        homeTipText = view.findViewById(R.id.tv_home_tip);
        proteinBar = view.findViewById(R.id.bar_protein);
        fatBar = view.findViewById(R.id.bar_fat);
        carbohydrateBar = view.findViewById(R.id.bar_carbohydrate);
        fiberBar = view.findViewById(R.id.bar_fiber);
        recipeCardsLayout = view.findViewById(R.id.layout_recipe_cards);
    }

    private void setupAvatarLogout() {
        avatarText.setContentDescription("退出登录");
        avatarText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("退出登录")
                .setMessage("确定要退出当前账号吗？")
                .setNegativeButton("取消", null)
                .setPositiveButton("退出", (dialog, which) -> {
                    new SessionManager(requireContext()).logout();
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .show();
    }

    private void setupQuickActions(View view) {
        view.findViewById(R.id.action_add_meal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireContext(), AddMealRecordActivity.class));
            }
        });
        view.findViewById(R.id.action_add_ingredient).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireContext(), AddIngredientActivity.class));
            }
        });
        view.findViewById(R.id.action_view_recipes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomNavigationView nav = requireActivity().findViewById(R.id.bottom_navigation);
                if (nav != null) {
                    nav.setSelectedItemId(R.id.nav_recipe);
                }
            }
        });
    }

    private void configureNutrientBars() {
        proteinBar.setTrackColor(Color.parseColor("#001A33"));
        proteinBar.setBarColor(Color.parseColor("#2F86FF"), Color.parseColor("#802F86FF"));

        fatBar.setTrackColor(Color.parseColor("#002B2B"));
        fatBar.setBarColor(Color.parseColor("#22D5D5"), Color.parseColor("#8022D5D5"));

        carbohydrateBar.setTrackColor(Color.parseColor("#001A33"));
        carbohydrateBar.setBarColor(Color.parseColor("#2F86FF"), Color.parseColor("#802F86FF"));

        fiberBar.setTrackColor(Color.parseColor("#002B16"));
        fiberBar.setBarColor(Color.parseColor("#34E27A"), Color.parseColor("#8034E27A"));
    }

    private void loadDashboard() {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = AppDatabase.getInstance(requireContext());
                DefaultDataSeeder.seed(db);
                final UserEntity user = db.userDao().findById(userId);
                final List<IngredientEntity> ingredients = db.ingredientDao().getByUser(userId);
                final List<MealRecordEntity> meals = db.mealRecordDao().getByDate(userId, DateUtil.getToday());
                final NutritionTargetEntity target = db.nutritionTargetDao().getByUser(userId);
                final NutritionAnalyzeResult nutrition = NutritionUtil.analyzeTodayNutrition(
                        meals,
                        db.foodNutritionDao().getAll(),
                        target
                );
                final List<RecipeRecommendResult> recipes = RecipeRecommendUtil.calculateRecommendations(
                        ingredients,
                        db.recipeDao().getAll(),
                        db.recipeIngredientDao().getAll(),
                        8
                );
                if (getActivity() == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        renderUser(user);
                        renderInventory(ingredients);
                        renderMeals(meals);
                        renderNutrition(nutrition, target);
                        renderRecipes(recipes);
                    }
                });
            }
        });
    }

    private void renderUser(UserEntity user) {
        String nickname = user == null || user.nickname == null || user.nickname.length() == 0 ? "haor" : user.nickname;
        String greeting = "\u4f60\u597d,\n" + nickname;
        SpannableString styledGreeting = new SpannableString(greeting);
        styledGreeting.setSpan(new StyleSpan(Typeface.BOLD), greeting.indexOf(nickname), greeting.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        greetingText.setText(styledGreeting);
        dateText.setText(DateUtil.getToday());
        avatarText.setText(nickname.substring(0, 1).toUpperCase());
    }

    private void renderInventory(List<IngredientEntity> ingredients) {
        int expiring = 0;
        int expired = 0;
        StringBuilder expiringNames = new StringBuilder();
        for (IngredientEntity item : ingredients) {
            int status = ExpireUtil.getExpireStatus(item.expireDate);
            if (status == ExpireUtil.STATUS_EXPIRING) {
                expiring++;
                if (expiringNames.length() < 30) {
                    expiringNames.append(item.name).append("\u3001");
                }
            } else if (status == ExpireUtil.STATUS_EXPIRED) {
                expired++;
            }
        }
        ingredientCountText.setText("\u98df\u6750\u5e93\u5b58\n" + ingredients.size() + " \u79cd");
        expireCountText.setText("\u4e34\u671f/\u8fc7\u671f\n" + expiring + " / " + expired + " \u79cd");
        if (expiringNames.length() > 0) {
            String names = expiringNames.substring(0, expiringNames.length() - 1);
            homeTipText.setText("\u4f18\u5148\u6d88\u8017\uff1a" + names);
        } else {
            homeTipText.setText("\u4eca\u65e5\u5e93\u5b58\u72b6\u6001\u7a33\u5b9a");
        }
    }

    private void renderMeals(List<MealRecordEntity> meals) {
        recentMealsLayout.removeAllViews();
        if (meals.isEmpty()) {
            recentMealsLayout.addView(createEmptyMealView());
            return;
        }
        int limit = Math.min(3, meals.size());
        for (int i = 0; i < limit; i++) {
            recentMealsLayout.addView(createMealRow(meals.get(i), i));
        }
    }

    private void renderNutrition(NutritionAnalyzeResult data, NutritionTargetEntity target) {
        int calorieTarget = target == null ? DEFAULT_CALORIE_TARGET : round0(target.caloriesTarget);
        calorieRingView.setCalories(round0(data.calories), calorieTarget, true);
        setNutrient(proteinText, proteinBar, "\u86cb\u767d", data.protein, 60);
        setNutrient(fatText, fatBar, "\u8102\u80aa", data.fat, 60);
        setNutrient(carbohydrateText, carbohydrateBar, "\u78b3\u6c34", data.carbohydrate, 250);
        setNutrient(fiberText, fiberBar, "\u7ea4\u7ef4", data.fiber, 25);
        renderDailySummary(data, calorieTarget);
        if (data.nextMealRecommend != null && data.nextMealRecommend.length() > 0 && "\u4eca\u65e5\u5e93\u5b58\u72b6\u6001\u7a33\u5b9a".contentEquals(homeTipText.getText())) {
            homeTipText.setText(cleanSuggestion(data.nextMealRecommend));
        }
    }

    private void renderDailySummary(NutritionAnalyzeResult data, int calorieTarget) {
        mealCountText.setText("餐食记录\n" + data.recordCount + " 条");
        int gap = calorieTarget - round0(data.calories);
        calorieGapText.setText(gap > 0 ? "热量缺口\n" + gap + " kcal" : "热量目标\n已达成");
        int fiberPercent = (int) Math.min(100, Math.round(data.fiber * 100 / 25));
        fiberStatusText.setText("膳食纤维\n" + fiberPercent + "%");
    }

    private void setNutrient(TextView label, NutrientBarView bar, String name, double value, double target) {
        int percent = (int) Math.min(100, Math.round(value * 100 / target));
        label.setText(name + "\n" + percent + "%");
        bar.setProgress(percent / 100f, true);
    }

    private void renderRecipes(List<RecipeRecommendResult> recipes) {
        recipeCardsLayout.removeAllViews();
        if (recipes.isEmpty()) {
            View empty = createRecipeCard("\u6682\u65e0\u63a8\u8350", "\u6dfb\u52a0\u5e93\u5b58\u98df\u6750\u540e\u751f\u6210\u63a8\u8350", null, null, 0);
            recipeCardsLayout.addView(empty);
            return;
        }
        int count = Math.min(6, recipes.size());
        for (int i = 0; i < count; i++) {
            final RecipeRecommendResult result = recipes.get(i);
            View card = createRecipeCard(
                    result.recipe.name,
                    "\u5339\u914d " + Math.round(result.matchRate * 100) + "%  \u7f3a " + result.missingCount + " \u79cd",
                    getRecipeThumbIngredient(result),
                    result.recipe.category,
                    i
            );
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(requireContext(), RecipeDetailActivity.class);
                    intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, result.recipe.id);
                    startActivity(intent);
                }
            });
            recipeCardsLayout.addView(card);
        }
    }

    private View createMealRow(MealRecordEntity item, int index) {
        LinearLayout row = new LinearLayout(requireContext());
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, index == 0 ? 0 : dp(7), 0, 0);

        LinearLayout content = new LinearLayout(requireContext());
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(0, 0, dp(10), 0);

        TextView title = new TextView(requireContext());
        title.setText(item.foodName);
        title.setTextColor(getResources().getColor(R.color.smart_text_primary));
        title.setTextSize(15);
        title.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        title.setIncludeFontPadding(false);

        TextView subtitle = new TextView(requireContext());
        subtitle.setText(item.mealType + "  " + round0(item.amountGram) + "g");
        subtitle.setTextColor(getResources().getColor(R.color.smart_text_secondary));
        subtitle.setTextSize(12);
        subtitle.setSingleLine(true);
        subtitle.setIncludeFontPadding(false);

        content.addView(title);
        content.addView(subtitle);
        row.addView(content, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        MaterialCardView imageCard = new MaterialCardView(requireContext());
        imageCard.setRadius(dp(12));
        imageCard.setCardElevation(dp(3));
        imageCard.setStrokeWidth(0);
        imageCard.setCardBackgroundColor(getResources().getColor(R.color.smart_surface));
        int mealImageResId = IngredientImageUtil.getImageResId(requireContext(), item.foodName);
        if (mealImageResId != 0) {
            ImageView mealImage = new ImageView(requireContext());
            mealImage.setImageResource(mealImageResId);
            mealImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageCard.addView(mealImage, new ViewGroup.LayoutParams(dp(48), dp(48)));
        } else {
            TextView fallback = new TextView(requireContext());
            fallback.setGravity(Gravity.CENTER);
            fallback.setText(getMealIconText(item.mealType));
            fallback.setTextColor(getResources().getColor(R.color.smart_accent));
            fallback.setTextSize(15);
            fallback.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
            imageCard.addView(fallback, new ViewGroup.LayoutParams(dp(48), dp(48)));
        }
        row.addView(imageCard, new LinearLayout.LayoutParams(dp(48), dp(48)));
        return row;
    }

    private View createEmptyMealView() {
        TextView empty = new TextView(requireContext());
        empty.setText("\u4eca\u65e5\u6682\u65e0\u996e\u98df\u8bb0\u5f55\n\u70b9\u51fb\u5e95\u90e8 + \u8bb0\u5f55\u65e9\u9910\u3001\u5348\u9910\u3001\u665a\u9910\u6216\u52a0\u9910");
        empty.setTextColor(getResources().getColor(R.color.smart_text_secondary));
        empty.setTextSize(14);
        empty.setLineSpacing(dp(4), 1);
        empty.setGravity(Gravity.CENTER);
        empty.setMinHeight(dp(52));
        return empty;
    }

    private View createRecipeCard(String title, String subtitle, String imageIngredientName, String category, int index) {
        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.bg_recipe_showcase);
        card.setElevation(dp(10));
        card.setPadding(0, 0, 0, dp(8));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp(128), dp(102));
        params.setMargins(0, 0, dp(10), 0);
        card.setLayoutParams(params);

        FrameLayout thumbFrame = new FrameLayout(requireContext());
        thumbFrame.setBackgroundResource(R.drawable.bg_recipe_thumb);
        ImageView thumbImage = new ImageView(requireContext());
        thumbImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        int imageResId = RecipeImageUtil.getImageResId(requireContext(), title, category);
        if (imageResId == 0) {
            imageResId = IngredientImageUtil.getImageResId(requireContext(), imageIngredientName);
        }
        if (imageResId != 0) {
            thumbImage.setImageResource(imageResId);
            thumbFrame.addView(thumbImage, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
        } else {
            TextView thumbText = new TextView(requireContext());
            thumbText.setGravity(Gravity.CENTER);
            thumbText.setText(getRecipeThumbText(title));
            thumbText.setTextColor(getResources().getColor(R.color.smart_nav));
            thumbText.setTextSize(22);
            thumbText.setTypeface(Typeface.create("sans-serif-black", Typeface.BOLD));
            thumbFrame.addView(thumbText, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
        }
        card.addView(thumbFrame, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(54)));

        TextView name = new TextView(requireContext());
        name.setText(title);
        name.setTextColor(getResources().getColor(R.color.smart_text_primary));
        name.setTextSize(13);
        name.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        name.setSingleLine(true);
        name.setIncludeFontPadding(false);
        name.setPadding(dp(9), dp(7), dp(9), 0);
        card.addView(name, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView meta = new TextView(requireContext());
        meta.setText(subtitle);
        meta.setTextColor(getResources().getColor(R.color.smart_text_secondary));
        meta.setTextSize(10);
        meta.setSingleLine(true);
        meta.setIncludeFontPadding(false);
        meta.setPadding(dp(9), dp(3), dp(9), 0);
        card.addView(meta, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return card;
    }

    private String getRecipeThumbText(String title) {
        if (title == null || title.length() == 0 || title.startsWith("\u6682\u65e0")) {
            return "+";
        }
        return title.substring(0, 1);
    }

    private String getRecipeThumbIngredient(RecipeRecommendResult result) {
        if (result == null) {
            return null;
        }
        for (String name : result.ownedIngredients) {
            if (IngredientImageUtil.getImageResId(requireContext(), name) != 0) {
                return name;
            }
        }
        if (!result.ownedIngredients.isEmpty()) {
            return result.ownedIngredients.get(0);
        }
        if (!result.missingIngredients.isEmpty()) {
            return result.missingIngredients.get(0).name;
        }
        return null;
    }

    private String getMealIconText(String mealType) {
        if (mealType == null || mealType.length() == 0) {
            return "+";
        }
        return mealType.substring(0, 1);
    }

    private String cleanSuggestion(String suggestion) {
        if (suggestion.contains("\u86cb\u767d") || suggestion.contains("\u7ea4\u7ef4") || suggestion.contains("\u7ef4\u751f\u7d20")) {
            return suggestion;
        }
        return "\u5efa\u8bae\u4e0b\u4e00\u9910\u4f18\u5148\u8865\u5145\u86cb\u767d\u8d28\u3001\u81b3\u98df\u7ea4\u7ef4\uff0c\u5e76\u6d88\u8017\u4e34\u671f\u98df\u6750";
    }

    private int round0(double value) {
        return (int) Math.round(value);
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
