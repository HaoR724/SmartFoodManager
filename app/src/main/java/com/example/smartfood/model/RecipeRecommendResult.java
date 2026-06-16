package com.example.smartfood.model;

import com.example.smartfood.entity.RecipeEntity;

import java.util.ArrayList;
import java.util.List;

public class RecipeRecommendResult {
    public RecipeEntity recipe;
    public double matchRate;
    public double expireUseRate;
    public double score;
    public int ownedCount;
    public int missingCount;
    public boolean usesExpiringIngredient;
    public String reason;
    public final List<String> ownedIngredients = new ArrayList<>();
    public final List<MissingIngredient> missingIngredients = new ArrayList<>();
}
