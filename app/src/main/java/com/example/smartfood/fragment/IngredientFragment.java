package com.example.smartfood.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chapter01.R;
import com.example.smartfood.activity.AddIngredientActivity;
import com.example.smartfood.adapter.IngredientAdapter;
import com.example.smartfood.entity.IngredientEntity;
import com.example.smartfood.repository.DataCallback;
import com.example.smartfood.repository.IngredientRepository;
import com.example.smartfood.util.ExpireUtil;
import com.example.smartfood.util.SessionManager;
import com.example.smartfood.util.SmartToast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class IngredientFragment extends Fragment {
    private IngredientRepository repository;
    private IngredientAdapter adapter;
    private final List<IngredientEntity> allIngredients = new ArrayList<>();
    private final List<TextView> statusChips = new ArrayList<>();
    private final List<TextView> categoryChips = new ArrayList<>();
    private EditText searchEdit;
    private TextView emptyText;
    private RecyclerView recyclerView;
    private String selectedStatus = "全部";
    private String selectedCategory = "全部分类";
    private long userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ingredient, container, false);
        repository = new IngredientRepository(requireContext());
        userId = new SessionManager(requireContext()).getCurrentUserId();
        searchEdit = view.findViewById(R.id.et_search);
        emptyText = view.findViewById(R.id.tv_empty);
        recyclerView = view.findViewById(R.id.rv_ingredients);
        setupList();
        setupFilters(view);
        view.findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireContext(), AddIngredientActivity.class));
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadIngredients();
    }

    private void setupList() {
        adapter = new IngredientAdapter();
        adapter.setOnIngredientActionListener(new IngredientAdapter.OnIngredientActionListener() {
            @Override
            public void onEdit(IngredientEntity ingredient) {
                Intent intent = new Intent(requireContext(), AddIngredientActivity.class);
                intent.putExtra(AddIngredientActivity.EXTRA_INGREDIENT_ID, ingredient.id);
                startActivity(intent);
            }

            @Override
            public void onDelete(final IngredientEntity ingredient) {
                confirmDelete(ingredient);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupFilters(View view) {
        bindStatusChip(view.findViewById(R.id.chip_status_all), "全部");
        bindStatusChip(view.findViewById(R.id.chip_status_normal), "正常");
        bindStatusChip(view.findViewById(R.id.chip_status_expiring), "即将过期");
        bindStatusChip(view.findViewById(R.id.chip_status_expired), "已过期");

        bindCategoryChip(view.findViewById(R.id.chip_category_all), "全部分类");
        bindCategoryChip(view.findViewById(R.id.chip_category_leaf), "叶菜类");
        bindCategoryChip(view.findViewById(R.id.chip_category_root), "根茎类");
        bindCategoryChip(view.findViewById(R.id.chip_category_fruit), "水果类");
        bindCategoryChip(view.findViewById(R.id.chip_category_meat), "肉禽类");
        bindCategoryChip(view.findViewById(R.id.chip_category_seafood), "水产类");
        bindCategoryChip(view.findViewById(R.id.chip_category_egg), "蛋奶类");
        bindCategoryChip(view.findViewById(R.id.chip_category_staple), "主食谷物");
        updateChipStates();

        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void bindStatusChip(final TextView chip, final String status) {
        statusChips.add(chip);
        chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedStatus = status;
                updateChipStates();
                applyFilters();
            }
        });
    }

    private void bindCategoryChip(final TextView chip, final String category) {
        categoryChips.add(chip);
        chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedCategory = category;
                updateChipStates();
                applyFilters();
            }
        });
    }

    private void updateChipStates() {
        for (TextView chip : statusChips) {
            applyChipStyle(chip, chip.getText().toString().equals(selectedStatus));
        }
        for (TextView chip : categoryChips) {
            applyChipStyle(chip, isCategoryChipSelected(chip.getText().toString()));
        }
    }

    private boolean isCategoryChipSelected(String text) {
        if ("全部".equals(text)) return "全部分类".equals(selectedCategory);
        if ("叶菜".equals(text)) return "叶菜类".equals(selectedCategory);
        if ("根茎".equals(text)) return "根茎类".equals(selectedCategory);
        if ("水果".equals(text)) return "水果类".equals(selectedCategory);
        if ("肉禽".equals(text)) return "肉禽类".equals(selectedCategory);
        if ("水产".equals(text)) return "水产类".equals(selectedCategory);
        if ("蛋奶".equals(text)) return "蛋奶类".equals(selectedCategory);
        if ("主食".equals(text)) return "主食谷物".equals(selectedCategory);
        return false;
    }

    private void applyChipStyle(TextView chip, boolean selected) {
        chip.setBackgroundResource(selected ? R.drawable.bg_filter_chip_selected : R.drawable.bg_filter_chip);
        chip.setTextColor(getResources().getColor(selected ? R.color.smart_nav : R.color.smart_text_primary));
    }

    private void loadIngredients() {
        repository.getIngredients(userId, new DataCallback<List<IngredientEntity>>() {
            @Override
            public void onResult(final List<IngredientEntity> data) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        allIngredients.clear();
                        allIngredients.addAll(data);
                        applyFilters();
                    }
                });
            }
        });
    }

    private void applyFilters() {
        if (adapter == null) return;
        String keyword = searchEdit == null ? "" : searchEdit.getText().toString().trim();
        List<IngredientEntity> filtered = new ArrayList<>();
        for (IngredientEntity item : allIngredients) {
            if (keyword.length() > 0 && !item.name.contains(keyword)) {
                continue;
            }
            if (!"全部分类".equals(selectedCategory) && !selectedCategory.equals(item.category)) {
                continue;
            }
            if (!matchesStatus(item, selectedStatus)) {
                continue;
            }
            filtered.add(item);
        }
        Collections.sort(filtered, new Comparator<IngredientEntity>() {
            @Override
            public int compare(IngredientEntity o1, IngredientEntity o2) {
                return o1.expireDate.compareTo(o2.expireDate);
            }
        });
        adapter.submitList(filtered);
        emptyText.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(filtered.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private boolean matchesStatus(IngredientEntity item, String statusText) {
        if ("全部".equals(statusText)) return true;
        int status = ExpireUtil.getExpireStatus(item.expireDate);
        if ("正常".equals(statusText)) return status == ExpireUtil.STATUS_NORMAL;
        if ("即将过期".equals(statusText)) return status == ExpireUtil.STATUS_EXPIRING;
        if ("已过期".equals(statusText)) return status == ExpireUtil.STATUS_EXPIRED;
        return true;
    }

    private void confirmDelete(final IngredientEntity ingredient) {
        new AlertDialog.Builder(requireContext())
                .setTitle("删除食材")
                .setMessage("确定删除“" + ingredient.name + "”吗？")
                .setNegativeButton("取消", null)
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        repository.delete(ingredient, new DataCallback<Boolean>() {
                            @Override
                            public void onResult(Boolean data) {
                                if (getActivity() == null) return;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        SmartToast.show(requireContext(), "删除成功");
                                        loadIngredients();
                                    }
                                });
                            }
                        });
                    }
                })
                .show();
    }
}
