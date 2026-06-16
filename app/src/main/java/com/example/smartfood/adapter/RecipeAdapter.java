package com.example.smartfood.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chapter01.R;
import com.example.smartfood.model.RecipeRecommendResult;
import com.example.smartfood.util.RecipeCategoryUtil;
import com.example.smartfood.util.RecipeImageUtil;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
    private final List<RecipeRecommendResult> data = new ArrayList<>();
    private OnRecipeClickListener listener;

    public interface OnRecipeClickListener {
        void onRecipeClick(RecipeRecommendResult result);
    }

    public void setOnRecipeClickListener(OnRecipeClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<RecipeRecommendResult> list) {
        data.clear();
        if (list != null) data.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final RecipeRecommendResult item = data.get(position);
        holder.thumbImage.setImageResource(RecipeImageUtil.getImageResId(holder.itemView.getContext(), item.recipe));
        holder.nameText.setText(item.recipe.name);
        holder.scoreText.setText("推荐 " + Math.round(item.score * 100));
        holder.metaText.setText(RecipeCategoryUtil.getDisplayCategories(item.recipe)
                + " | 匹配度 " + Math.round(item.matchRate * 100) + "%"
                + " | 已有 " + item.ownedCount + " 项"
                + " | 缺少 " + item.missingCount + " 项");
        holder.reasonText.setText(item.reason);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onRecipeClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        ImageView thumbImage;
        TextView scoreText;
        TextView metaText;
        TextView reasonText;

        ViewHolder(View itemView) {
            super(itemView);
            thumbImage = itemView.findViewById(R.id.iv_recipe_thumb);
            nameText = itemView.findViewById(R.id.tv_recipe_name);
            scoreText = itemView.findViewById(R.id.tv_recipe_score);
            metaText = itemView.findViewById(R.id.tv_recipe_meta);
            reasonText = itemView.findViewById(R.id.tv_recipe_reason);
        }
    }
}
