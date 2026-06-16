package com.example.smartfood.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chapter01.R;
import com.example.smartfood.entity.ShoppingItemEntity;

import java.util.ArrayList;
import java.util.List;

public class ShoppingAdapter extends RecyclerView.Adapter<ShoppingAdapter.ViewHolder> {
    private final List<ShoppingItemEntity> data = new ArrayList<>();
    private OnShoppingActionListener listener;

    public interface OnShoppingActionListener {
        void onTogglePurchased(ShoppingItemEntity item);

        void onAddToStock(ShoppingItemEntity item);
    }

    public void setOnShoppingActionListener(OnShoppingActionListener listener) {
        this.listener = listener;
    }

    public void submitList(List<ShoppingItemEntity> list) {
        data.clear();
        if (list != null) data.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shopping, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ShoppingItemEntity item = data.get(position);
        holder.checkBox.setText(item.status == 1 ? "✓" : "");
        holder.title.setText((item.status == 1 ? "已购买 " : "待购买 ") + item.name);
        holder.subtitle.setText(item.quantity + item.unit + " | " + (item.sourceRecipeId == null ? "手动添加" : "来自菜谱推荐"));
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onTogglePurchased(item);
            }
        });
        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onAddToStock(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView subtitle;
        TextView checkBox;
        Button addButton;
        ViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.tv_check_box);
            title = itemView.findViewById(R.id.tv_shopping_name);
            subtitle = itemView.findViewById(R.id.tv_shopping_detail);
            addButton = itemView.findViewById(R.id.btn_add_to_stock);
        }
    }
}
