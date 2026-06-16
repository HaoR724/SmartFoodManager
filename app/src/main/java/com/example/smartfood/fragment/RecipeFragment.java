package com.example.smartfood.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chapter01.R;
import com.example.smartfood.activity.RecipeDetailActivity;
import com.example.smartfood.adapter.RecipeAdapter;
import com.example.smartfood.model.RecipeRecommendResult;
import com.example.smartfood.repository.DataCallback;
import com.example.smartfood.repository.RecipeRepository;
import com.example.smartfood.util.BottomSheetSelectUtil;
import com.example.smartfood.util.RecipeCategoryUtil;
import com.example.smartfood.util.SessionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RecipeFragment extends Fragment {
    private static final String ALL_CATEGORY = "全部分类";
    private static final String ALL_RECIPE = "全部菜谱";
    private static final String SORT_RECOMMEND = "推荐优先";
    private static final String SORT_MATCH = "匹配度优先";
    private static final String SORT_MISSING = "缺少食材少";
    private static final String[] CATEGORIES = {
            ALL_CATEGORY, "早餐", "午餐", "晚餐", "家常菜", "汤类", "减脂餐", "儿童餐", "快手菜", "主食", "甜品/加餐"
    };
    private static final String[] SORTS = {SORT_RECOMMEND, SORT_MATCH, SORT_MISSING};

    private TextView nameView;
    private TextView categoryView;
    private TextView sortView;
    private TextView emptyText;
    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private RecipeRepository repository;
    private final List<RecipeRecommendResult> allResults = new ArrayList<>();
    private final List<String> currentRecipeNameOptions = new ArrayList<>();
    private long userId;
    private String selectedCategory = ALL_CATEGORY;
    private String selectedRecipeName = ALL_RECIPE;
    private String selectedSort = SORT_RECOMMEND;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);
        repository = new RecipeRepository(requireContext());
        userId = new SessionManager(requireContext()).getCurrentUserId();
        nameView = view.findViewById(R.id.sp_recipe_name);
        categoryView = view.findViewById(R.id.sp_recipe_category);
        sortView = view.findViewById(R.id.sp_recipe_sort);
        emptyText = view.findViewById(R.id.tv_recipe_empty);
        recyclerView = view.findViewById(R.id.rv_recipes);
        setupList();
        setupFilters();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRecipes();
    }

    private void setupList() {
        adapter = new RecipeAdapter();
        adapter.setOnRecipeClickListener(new RecipeAdapter.OnRecipeClickListener() {
            @Override
            public void onRecipeClick(RecipeRecommendResult result) {
                Intent intent = new Intent(requireContext(), RecipeDetailActivity.class);
                intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, result.recipe.id);
                startActivity(intent);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupFilters() {
        categoryView.setText(selectedCategory);
        sortView.setText(selectedSort);
        nameView.setText(selectedRecipeName);
        categoryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetSelectUtil.show(requireContext(), "选择菜谱分类",
                        Arrays.asList(CATEGORIES), selectedCategory,
                        new BottomSheetSelectUtil.OnItemSelectedListener() {
                            @Override
                            public void onSelected(String value) {
                                selectedCategory = value;
                                categoryView.setText(value);
                                updateRecipeNameOptions();
                            }
                        });
            }
        });
        sortView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetSelectUtil.show(requireContext(), "选择排序方式",
                        Arrays.asList(SORTS), selectedSort,
                        new BottomSheetSelectUtil.OnItemSelectedListener() {
                            @Override
                            public void onSelected(String value) {
                                selectedSort = value;
                                sortView.setText(value);
                                applyFilters();
                            }
                        });
            }
        });
        nameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetSelectUtil.show(requireContext(), "选择菜谱",
                        currentRecipeNameOptions, selectedRecipeName,
                        new BottomSheetSelectUtil.OnItemSelectedListener() {
                            @Override
                            public void onSelected(String value) {
                                selectedRecipeName = value;
                                nameView.setText(value);
                                applyFilters();
                            }
                        });
            }
        });
    }

    private void loadRecipes() {
        repository.getRecommendations(userId, 200, new DataCallback<List<RecipeRecommendResult>>() {
            @Override
            public void onResult(final List<RecipeRecommendResult> data) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        allResults.clear();
                        allResults.addAll(data);
                        updateRecipeNameOptions();
                    }
                });
            }
        });
    }

    private void applyFilters() {
        if (adapter == null) return;
        List<RecipeRecommendResult> filtered = new ArrayList<>();
        for (RecipeRecommendResult item : allResults) {
            if (!RecipeCategoryUtil.matchesCategory(item.recipe, selectedCategory)) {
                continue;
            }
            if (!ALL_RECIPE.equals(selectedRecipeName) && !selectedRecipeName.equals(item.recipe.name)) {
                continue;
            }
            filtered.add(item);
        }
        sortResults(filtered, selectedSort);
        adapter.submitList(filtered);
        emptyText.setText(allResults.isEmpty()
                ? "暂无匹配菜谱，请先添加与菜谱食材相同的库存食材"
                : "当前筛选条件下暂无菜谱");
        emptyText.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(filtered.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void updateRecipeNameOptions() {
        currentRecipeNameOptions.clear();
        currentRecipeNameOptions.add(ALL_RECIPE);
        for (RecipeRecommendResult result : allResults) {
            if (!RecipeCategoryUtil.matchesCategory(result.recipe, selectedCategory)) {
                continue;
            }
            if (!currentRecipeNameOptions.contains(result.recipe.name)) {
                currentRecipeNameOptions.add(result.recipe.name);
            }
        }
        if (!currentRecipeNameOptions.contains(selectedRecipeName)) {
            selectedRecipeName = ALL_RECIPE;
        }
        nameView.setText(selectedRecipeName);
        applyFilters();
    }

    private void sortResults(List<RecipeRecommendResult> data, String sort) {
        if (SORT_MATCH.equals(sort)) {
            Collections.sort(data, new Comparator<RecipeRecommendResult>() {
                @Override
                public int compare(RecipeRecommendResult o1, RecipeRecommendResult o2) {
                    return Double.compare(o2.matchRate, o1.matchRate);
                }
            });
            return;
        }
        if (SORT_MISSING.equals(sort)) {
            Collections.sort(data, new Comparator<RecipeRecommendResult>() {
                @Override
                public int compare(RecipeRecommendResult o1, RecipeRecommendResult o2) {
                    return Integer.compare(o1.missingCount, o2.missingCount);
                }
            });
            return;
        }
        Collections.sort(data, new Comparator<RecipeRecommendResult>() {
            @Override
            public int compare(RecipeRecommendResult o1, RecipeRecommendResult o2) {
                return Double.compare(o2.score, o1.score);
            }
        });
    }
}
