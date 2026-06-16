package com.example.smartfood.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chapter01.R;
import com.example.smartfood.activity.AddMealRecordActivity;
import com.example.smartfood.adapter.MealRecordAdapter;
import com.example.smartfood.entity.MealRecordEntity;
import com.example.smartfood.model.NutritionAnalyzeResult;
import com.example.smartfood.repository.DataCallback;
import com.example.smartfood.repository.HealthRepository;
import com.example.smartfood.util.SessionManager;
import com.example.smartfood.util.SmartToast;

import java.util.List;

public class HealthFragment extends Fragment {
    private TextView todayButton;
    private TextView monthButton;
    private TextView calorieValueText;
    private TextView calorieTargetText;
    private TextView calorieGapText;
    private TextView analysisCaptionText;
    private TextView proteinLineText;
    private TextView fatLineText;
    private TextView carbLineText;
    private TextView proteinStatusText;
    private TextView fatStatusText;
    private TextView carbStatusText;
    private ProgressBar proteinProgress;
    private ProgressBar fatProgress;
    private ProgressBar carbProgress;
    private TextView fiberRatioText;
    private TextView calciumRatioText;
    private TextView ironRatioText;
    private TextView vitaminRatioText;
    private TextView dayCountText;
    private TextView recordCountText;
    private TextView suggestionText;
    private TextView emptyText;
    private TextView recordTitleText;
    private RecyclerView recyclerView;
    private MealRecordAdapter adapter;
    private HealthRepository repository;
    private boolean monthMode;
    private long userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health, container, false);
        repository = new HealthRepository(requireContext());
        userId = new SessionManager(requireContext()).getCurrentUserId();
        bindViews(view);
        setupRecycler();
        setupActions(view);
        updateModeButtons();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadHealthData();
    }

    private void bindViews(View view) {
        todayButton = view.findViewById(R.id.btn_analyze_today);
        monthButton = view.findViewById(R.id.btn_analyze_month);
        calorieValueText = view.findViewById(R.id.tv_calorie_value);
        calorieTargetText = view.findViewById(R.id.tv_calorie_target);
        calorieGapText = view.findViewById(R.id.tv_calorie_gap);
        analysisCaptionText = view.findViewById(R.id.tv_analysis_caption);
        proteinLineText = view.findViewById(R.id.tv_protein_line);
        fatLineText = view.findViewById(R.id.tv_fat_line);
        carbLineText = view.findViewById(R.id.tv_carb_line);
        proteinStatusText = view.findViewById(R.id.tv_protein_status);
        fatStatusText = view.findViewById(R.id.tv_fat_status);
        carbStatusText = view.findViewById(R.id.tv_carb_status);
        proteinProgress = view.findViewById(R.id.progress_protein);
        fatProgress = view.findViewById(R.id.progress_fat);
        carbProgress = view.findViewById(R.id.progress_carb);
        fiberRatioText = view.findViewById(R.id.tv_fiber_ratio);
        calciumRatioText = view.findViewById(R.id.tv_calcium_ratio);
        ironRatioText = view.findViewById(R.id.tv_iron_ratio);
        vitaminRatioText = view.findViewById(R.id.tv_vitamin_ratio);
        dayCountText = view.findViewById(R.id.tv_day_count);
        recordCountText = view.findViewById(R.id.tv_record_count);
        suggestionText = view.findViewById(R.id.tv_health_suggestion);
        emptyText = view.findViewById(R.id.tv_meal_empty);
        recordTitleText = view.findViewById(R.id.tv_record_title);
        recyclerView = view.findViewById(R.id.rv_meal_records);
    }

    private void setupRecycler() {
        adapter = new MealRecordAdapter();
        adapter.setOnMealRecordActionListener(new MealRecordAdapter.OnMealRecordActionListener() {
            @Override
            public void onEdit(MealRecordEntity record) {
                editMealRecord(record);
            }

            @Override
            public void onDelete(MealRecordEntity record) {
                confirmDeleteMealRecord(record);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
    }

    private void setupActions(View view) {
        view.findViewById(R.id.btn_add_meal_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireContext(), AddMealRecordActivity.class));
            }
        });
        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthMode = false;
                updateModeButtons();
                loadHealthData();
            }
        });
        monthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthMode = true;
                updateModeButtons();
                loadHealthData();
            }
        });
        view.findViewById(R.id.btn_generate_month).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateMonthData();
            }
        });
    }

    private void updateModeButtons() {
        todayButton.setBackgroundResource(monthMode ? R.drawable.bg_health_segment_unselected : R.drawable.bg_health_segment_selected);
        monthButton.setBackgroundResource(monthMode ? R.drawable.bg_health_segment_selected : R.drawable.bg_health_segment_unselected);
        todayButton.setTextColor(getResources().getColor(monthMode ? R.color.smart_text_secondary : R.color.smart_primary));
        monthButton.setTextColor(getResources().getColor(monthMode ? R.color.smart_primary : R.color.smart_text_secondary));
        recordTitleText.setText(monthMode ? "近30天饮食记录" : "今日饮食记录");
        emptyText.setText(monthMode ? "近30天暂无饮食记录，可生成本月演示数据" : "暂无饮食记录，请点击右下角 + 添加");
    }

    private void loadHealthData() {
        if (monthMode) {
            loadMonthData();
            return;
        }
        repository.analyzeToday(userId, new DataCallback<NutritionAnalyzeResult>() {
            @Override
            public void onResult(final NutritionAnalyzeResult data) {
                renderNutritionOnUi(data, false);
            }
        });
        repository.getTodayRecords(userId, new DataCallback<List<MealRecordEntity>>() {
            @Override
            public void onResult(final List<MealRecordEntity> data) {
                renderRecordsOnUi(data);
            }
        });
    }

    private void loadMonthData() {
        repository.analyzeRecentMonth(userId, new DataCallback<NutritionAnalyzeResult>() {
            @Override
            public void onResult(final NutritionAnalyzeResult data) {
                renderNutritionOnUi(data, true);
            }
        });
        repository.getRecentMonthRecords(userId, new DataCallback<List<MealRecordEntity>>() {
            @Override
            public void onResult(final List<MealRecordEntity> data) {
                renderRecordsOnUi(data);
            }
        });
    }

    private void generateMonthData() {
        repository.addRecentMonthSampleRecords(userId, new DataCallback<Integer>() {
            @Override
            public void onResult(final Integer count) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SmartToast.show(requireContext(), count > 0 ? "已生成 " + count + " 条本月饮食记录" : "本月演示数据已存在");
                        monthMode = true;
                        updateModeButtons();
                        loadHealthData();
                    }
                });
            }
        });
    }

    private void editMealRecord(MealRecordEntity record) {
        Intent intent = new Intent(requireContext(), AddMealRecordActivity.class);
        intent.putExtra(AddMealRecordActivity.EXTRA_RECORD_ID, record.id);
        startActivity(intent);
    }

    private void confirmDeleteMealRecord(final MealRecordEntity record) {
        new AlertDialog.Builder(requireContext())
                .setTitle("删除饮食记录")
                .setMessage("确定删除“" + record.mealType + "：" + record.foodName + "”吗？")
                .setNegativeButton("取消", null)
                .setPositiveButton("删除", (dialog, which) -> deleteMealRecord(record))
                .show();
    }

    private void deleteMealRecord(MealRecordEntity record) {
        repository.deleteMealRecord(record, new DataCallback<Boolean>() {
            @Override
            public void onResult(Boolean data) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SmartToast.show(requireContext(), "饮食记录已删除");
                        loadHealthData();
                    }
                });
            }
        });
    }

    private void renderNutritionOnUi(final NutritionAnalyzeResult data, final boolean month) {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                renderNutrition(data, month);
            }
        });
    }

    private void renderRecordsOnUi(final List<MealRecordEntity> data) {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.submitList(data);
                boolean empty = data == null || data.isEmpty();
                emptyText.setVisibility(empty ? View.VISIBLE : View.GONE);
                recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
            }
        });
    }

    private void renderNutrition(NutritionAnalyzeResult data, boolean month) {
        int days = Math.max(1, data.dayCount);
        double calorieTarget = 2000 * days;
        double proteinTarget = 60 * days;
        double fatTarget = 60 * days;
        double carbTarget = 250 * days;
        double fiberTarget = 25 * days;
        double calciumTarget = 800 * days;
        double ironTarget = 12 * days;
        double vitaminTarget = 100 * days;

        analysisCaptionText.setText(month ? "近30天摄入估算" : "摄入估算");
        calorieValueText.setText(String.valueOf(round0(data.calories)));
        calorieTargetText.setText("/ " + round0(calorieTarget) + " kcal");
        calorieGapText.setText(buildGapText(calorieTarget - data.calories, "kcal"));

        setMacroRow(proteinLineText, proteinProgress, proteinStatusText, "蛋白质", data.protein, proteinTarget, "g", true);
        setMacroRow(fatLineText, fatProgress, fatStatusText, "脂肪", data.fat, fatTarget, "g", false);
        setMacroRow(carbLineText, carbProgress, carbStatusText, "碳水", data.carbohydrate, carbTarget, "g", false);

        fiberRatioText.setText(percentText(data.fiber, fiberTarget));
        calciumRatioText.setText(percentText(data.calcium, calciumTarget));
        ironRatioText.setText(percentText(data.iron, ironTarget));
        vitaminRatioText.setText(percentText(data.vitaminC, vitaminTarget));
        dayCountText.setText(data.mealDayCount + "天");
        recordCountText.setText(data.recordCount + "条");

        String suggestionTitle = month ? "阶段饮食建议" : "下一餐建议";
        suggestionText.setText("· " + data.suggestion + "\n\n"
                + "· " + suggestionTitle + "：" + data.nextMealRecommend);
    }

    private void setMacroRow(TextView label, ProgressBar progressBar, TextView status, String name,
                             double value, double target, String unit, boolean showReachedCheck) {
        int percent = percent(value, target);
        double gap = target - value;
        label.setText(name + " (" + round1(value) + "/" + round1(target) + unit + ")");
        progressBar.setProgress(percent);
        if (gap > 0) {
            status.setText("Gap: " + round1(gap) + unit);
            status.setTextColor(getResources().getColor(R.color.smart_text_secondary));
        } else if (showReachedCheck) {
            status.setText("✓");
            status.setTextColor(getResources().getColor(R.color.status_normal));
        } else {
            status.setText("已达成");
            status.setTextColor(getResources().getColor(R.color.status_normal));
        }
    }

    private String buildGapText(double gap, String unit) {
        if (gap > 0) {
            return "缺口 " + round0(gap) + " " + unit;
        }
        return "已达成";
    }

    private String percentText(double value, double target) {
        return percent(value, target) + "%";
    }

    private int percent(double value, double target) {
        if (target <= 0) {
            return 0;
        }
        return (int) Math.min(100, Math.round(value * 100 / target));
    }

    private int round0(double value) {
        return (int) Math.round(value);
    }

    private double round1(double value) {
        return Math.round(value * 10) / 10.0;
    }
}
