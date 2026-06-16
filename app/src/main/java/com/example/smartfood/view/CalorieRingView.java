package com.example.smartfood.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

import com.example.chapter01.R;

public class CalorieRingView extends View {
    private final Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint numberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint targetPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF arcBounds = new RectF();
    private float progress;
    private int calories;
    private int target = 2000;
    private ValueAnimator animator;

    public CalorieRingView(Context context) {
        super(context);
        init();
    }

    public CalorieRingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CalorieRingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        trackPaint.setStyle(Paint.Style.STROKE);
        trackPaint.setStrokeWidth(dp(7));
        trackPaint.setStrokeCap(Paint.Cap.ROUND);
        trackPaint.setColor(Color.parseColor("#331400"));

        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(dp(14));
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setColor(Color.parseColor("#FF6000"));
        progressPaint.setShadowLayer(dp(24), 0f, 0f, Color.parseColor("#99FF6000"));

        labelPaint.setColor(getResources().getColor(R.color.smart_text_secondary));
        labelPaint.setTextAlign(Paint.Align.CENTER);
        labelPaint.setTextSize(sp(13));
        labelPaint.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));

        numberPaint.setColor(Color.WHITE);
        numberPaint.setTextAlign(Paint.Align.CENTER);
        numberPaint.setTextSize(sp(34));
        numberPaint.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        numberPaint.setShadowLayer(dp(12), 0f, 0f, Color.parseColor("#80FF6000"));

        targetPaint.setColor(getResources().getColor(R.color.smart_text_secondary));
        targetPaint.setTextAlign(Paint.Align.CENTER);
        targetPaint.setTextSize(sp(12));
        targetPaint.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float padding = dp(18);
        float size = Math.min(getWidth(), getHeight()) - padding * 2;
        float left = (getWidth() - size) / 2f;
        float top = (getHeight() - size) / 2f;
        arcBounds.set(left, top, left + size, top + size);

        canvas.drawArc(arcBounds, -90, 360, false, trackPaint);
        float sweep = 360f * progress;
        if (sweep > 0f) {
            canvas.drawArc(arcBounds, -90, sweep, false, progressPaint);
        }

        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        canvas.drawText("\u5df2\u6444\u5165", centerX, centerY - dp(23), labelPaint);
        canvas.drawText(String.valueOf(calories), centerX, centerY + dp(13), numberPaint);
        canvas.drawText("\u76ee\u6807 " + target + " \u5343\u5361", centerX, centerY + dp(36), targetPaint);
    }

    public void setCalories(int calories, int target, boolean animate) {
        this.target = Math.max(1, target);
        int endCalories = Math.max(0, calories);
        float endProgress = Math.min(1f, endCalories / (float) this.target);
        if (!animate) {
            this.calories = endCalories;
            this.progress = endProgress;
            invalidate();
            return;
        }
        animateTo(endCalories, endProgress);
    }

    private void animateTo(final int endCalories, final float endProgress) {
        if (animator != null) {
            animator.cancel();
        }
        final int startCalories = calories;
        final float startProgress = progress;
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(850);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = (float) animation.getAnimatedValue();
                calories = Math.round(startCalories + (endCalories - startCalories) * fraction);
                progress = startProgress + (endProgress - startProgress) * fraction;
                invalidate();
            }
        });
        animator.start();
    }

    private float dp(int value) {
        return value * getResources().getDisplayMetrics().density;
    }

    private float sp(int value) {
        return value * getResources().getDisplayMetrics().scaledDensity;
    }
}
