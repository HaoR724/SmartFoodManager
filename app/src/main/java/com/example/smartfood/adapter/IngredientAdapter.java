package com.example.smartfood.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chapter01.R;
import com.example.smartfood.entity.IngredientEntity;
import com.example.smartfood.util.ExpireUtil;
import com.example.smartfood.util.IngredientImageUtil;
import com.example.smartfood.util.ShelfLifeUtil;

import java.util.ArrayList;
import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {
    private final List<IngredientEntity> data = new ArrayList<>();
    private OnIngredientActionListener listener;

    public interface OnIngredientActionListener {
        void onEdit(IngredientEntity ingredient);

        void onDelete(IngredientEntity ingredient);
    }

    public void setOnIngredientActionListener(OnIngredientActionListener listener) {
        this.listener = listener;
    }

    public void submitList(List<IngredientEntity> list) {
        data.clear();
        if (list != null) data.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final IngredientEntity item = data.get(position);
        int status = ExpireUtil.getExpireStatus(item.expireDate);
        long daysLeft = ExpireUtil.getDaysLeft(item.expireDate);
        holder.thumbText.setText(item.name == null || item.name.length() == 0 ? "食" : item.name.substring(0, 1));
        int imageResId = IngredientImageUtil.getImageResId(holder.itemView.getContext(), item.name, item.category);
        if (imageResId != 0) {
            holder.thumbImage.setImageResource(imageResId);
            holder.thumbImage.setVisibility(View.VISIBLE);
            holder.thumbText.setVisibility(View.GONE);
        } else {
            holder.thumbImage.setVisibility(View.GONE);
            holder.thumbText.setVisibility(View.VISIBLE);
        }
        holder.nameText.setText(item.name);
        holder.statusText.setText(ExpireUtil.getExpireStatusText(item.expireDate));
        holder.statusText.setTextColor(ExpireUtil.getExpireColor(item.expireDate));
        if (status == ExpireUtil.STATUS_EXPIRED) {
            holder.statusText.setBackgroundResource(R.drawable.bg_status_expired);
        } else if (status == ExpireUtil.STATUS_EXPIRING) {
            holder.statusText.setBackgroundResource(R.drawable.bg_status_expiring);
        } else {
            holder.statusText.setBackgroundResource(R.drawable.bg_status_normal);
        }
        holder.detailText.setText(item.category + "  " + item.quantity + item.unit + "  " + item.storagePlace);
        String bestEatDate = item.bestEatDate == null || item.bestEatDate.length() == 0
                ? ShelfLifeUtil.calculateBestEatDate(item.buyDate, item.name, item.category, item.storagePlace)
                : item.bestEatDate;
        String expireText = daysLeft < 0
                ? "过期日 " + item.expireDate + "  已超 " + Math.abs(daysLeft) + " 天"
                : "最佳 " + bestEatDate + "  过期 " + item.expireDate;
        holder.expireText.setText(expireText);
        holder.expireText.setTextColor(ExpireUtil.getExpireColor(item.expireDate));
        holder.daysLeftText.setText(daysLeft < 0 ? "超 " + Math.abs(daysLeft) + " 天" : "还剩 " + daysLeft + " 天");
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView thumbText;
        ImageView thumbImage;
        TextView statusText;
        TextView detailText;
        TextView expireText;
        TextView daysLeftText;
        Button editButton;
        Button deleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            thumbText = itemView.findViewById(R.id.tv_thumb);
            thumbImage = itemView.findViewById(R.id.iv_thumb);
            nameText = itemView.findViewById(R.id.tv_name);
            statusText = itemView.findViewById(R.id.tv_status);
            detailText = itemView.findViewById(R.id.tv_detail);
            expireText = itemView.findViewById(R.id.tv_expire);
            daysLeftText = itemView.findViewById(R.id.tv_days_left);
            editButton = itemView.findViewById(R.id.btn_edit);
            deleteButton = itemView.findViewById(R.id.btn_delete);
        }
    }
}
