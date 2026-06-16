package com.example.smartfood.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chapter01.R;
import com.example.smartfood.entity.MealRecordEntity;

import java.util.ArrayList;
import java.util.List;

public class MealRecordAdapter extends RecyclerView.Adapter<MealRecordAdapter.ViewHolder> {
    private final List<MealRecordEntity> data = new ArrayList<>();
    private OnMealRecordActionListener listener;

    public interface OnMealRecordActionListener {
        void onEdit(MealRecordEntity record);

        void onDelete(MealRecordEntity record);
    }

    public void setOnMealRecordActionListener(OnMealRecordActionListener listener) {
        this.listener = listener;
    }

    public void submitList(List<MealRecordEntity> list) {
        data.clear();
        if (list != null) data.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meal_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MealRecordEntity item = data.get(position);
        holder.title.setText(item.mealType + "：" + item.foodName);
        holder.subtitle.setText(item.mealDate + " | " + formatAmount(item.amountGram) + "g");
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onEdit(item);
            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onDelete(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private String formatAmount(double amount) {
        if (amount == Math.floor(amount)) {
            return String.valueOf((int) amount);
        }
        return String.valueOf(amount);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView subtitle;
        TextView editButton;
        TextView deleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_meal_title);
            subtitle = itemView.findViewById(R.id.tv_meal_detail);
            editButton = itemView.findViewById(R.id.btn_meal_edit);
            deleteButton = itemView.findViewById(R.id.btn_meal_delete);
        }
    }
}
