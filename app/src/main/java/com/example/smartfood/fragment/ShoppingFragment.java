package com.example.smartfood.fragment;

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
import com.example.smartfood.adapter.ShoppingAdapter;
import com.example.smartfood.entity.IngredientEntity;
import com.example.smartfood.entity.ShoppingItemEntity;
import com.example.smartfood.repository.DataCallback;
import com.example.smartfood.repository.IngredientRepository;
import com.example.smartfood.repository.ShoppingRepository;
import com.example.smartfood.util.DateUtil;
import com.example.smartfood.util.FoodCatalog;
import com.example.smartfood.util.SessionManager;
import com.example.smartfood.util.ShelfLifeUtil;
import com.example.smartfood.util.SmartToast;

import java.util.List;

public class ShoppingFragment extends Fragment {
    private ShoppingAdapter adapter;
    private TextView emptyText;
    private RecyclerView recyclerView;
    private ShoppingRepository shoppingRepository;
    private IngredientRepository ingredientRepository;
    private long userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping, container, false);
        userId = new SessionManager(requireContext()).getCurrentUserId();
        shoppingRepository = new ShoppingRepository(requireContext());
        ingredientRepository = new IngredientRepository(requireContext());
        emptyText = view.findViewById(R.id.tv_shopping_empty);
        recyclerView = view.findViewById(R.id.rv_shopping);
        adapter = new ShoppingAdapter();
        adapter.setOnShoppingActionListener(new ShoppingAdapter.OnShoppingActionListener() {
            @Override
            public void onTogglePurchased(ShoppingItemEntity item) {
                togglePurchased(item);
            }

            @Override
            public void onAddToStock(ShoppingItemEntity item) {
                addToStock(item);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadShoppingItems();
    }

    private void loadShoppingItems() {
        shoppingRepository.getShoppingItems(userId, new DataCallback<List<ShoppingItemEntity>>() {
            @Override
            public void onResult(final List<ShoppingItemEntity> data) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.submitList(data);
                        emptyText.setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);
                        recyclerView.setVisibility(data.isEmpty() ? View.GONE : View.VISIBLE);
                    }
                });
            }
        });
    }

    private void togglePurchased(final ShoppingItemEntity item) {
        item.status = item.status == 1 ? 0 : 1;
        shoppingRepository.update(item, new DataCallback<Boolean>() {
            @Override
            public void onResult(Boolean data) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SmartToast.show(requireContext(), item.status == 1 ? "已标记为已买" : "已取消已买");
                        loadShoppingItems();
                    }
                });
            }
        });
    }

    private void addToStock(final ShoppingItemEntity item) {
        String category = FoodCatalog.getCategoryByName(item.name);
        String storagePlace = FoodCatalog.getDefaultStoragePlace(category);
        String buyDate = DateUtil.getToday();
        String bestEatDate = ShelfLifeUtil.calculateBestEatDate(buyDate, item.name, category, storagePlace);
        String expireDate = ShelfLifeUtil.calculateExpireDate(buyDate, item.name, category, storagePlace);
        IngredientEntity ingredient = new IngredientEntity(userId, item.name, category, item.quantity, item.unit,
                buyDate, bestEatDate, expireDate, storagePlace, "由购物清单加入", DateUtil.getToday());
        ingredientRepository.insert(ingredient, new DataCallback<Long>() {
            @Override
            public void onResult(Long data) {
                item.status = 1;
                shoppingRepository.update(item, new DataCallback<Boolean>() {
                    @Override
                    public void onResult(Boolean data) {
                        if (getActivity() == null) return;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SmartToast.show(requireContext(), "已加入食材库存");
                                loadShoppingItems();
                            }
                        });
                    }
                });
            }
        });
    }
}
