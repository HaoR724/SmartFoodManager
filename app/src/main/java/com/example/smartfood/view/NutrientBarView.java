package com.example.smartfood.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

public class NutrientBarView extends View {
    private final Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float progress = 0.45f;
    private int trackColor = Color.parseColor("#331400");
    private int progressColor = Color.parseColor("#FF6000");
    private int glowColor = Color.parseColor("#80FF6000");
    private ValueAnimator animator;

    public NutrientBarView(Context context) {
        super(context);
        init();
    }

    public NutrientBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NutrientBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        trackPaint.setStyle(Paint.Style.STROKE);
        trackPaint.setStrokeCap(Paint.Cap.ROUND);
        trackPaint.setStrokeWidth(dp(6));
        trackPaint.setColor(trackColor);

        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setStrokeWidth(dp(12));
        progressPaint.setColor(progressColor);
        progressPaint.setShadowLayer(dp(18), 0f, 0f, glowColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float centerX = getWidth() / 2f;
        float top = dp(12);
        float bottom = getHeight() - dp(12);
        canvas.drawLine(centerX, top, centerX, bottom, trackPaint);

        float progressTop = bottom - (bottom - top) * progress;
        canvas.drawLine(centerX, progressTop, centerX, bottom, progressPaint);
    }

    public void setBarColor(int color, int shadowColor) {
        progressColor = color;
        glowColor = shadowColor;
        progressPaint.setColor(progressColor);
        progressPaint.setShadowLayer(dp(18), 0f, 0f, glowColor);
        invalidate();
    }

    public void setTrackColor(int color) {
        trackColor = color;
        trackPaint.setColor(trackColor);
        invalidate();
    }

    public void setProgress(float value, boolean animate) {
        float endProgress = Math.max(0f, Math.min(1f, value));
        if (!animate) {
            progress = endProgress;
            invalidate();
            return;
        }
        if (animator != null) {
            animator.cancel();
        }
        final float startProgress = progress;
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(650);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = (float) animation.getAnimatedValue();
                progress = startProgress + (endProgress - startProgress) * fraction;
                invalidate();
            }
        });
        animator.start();
    }

    private float dp(int value) {
        return value * getResources().getDisplayMetrics().density;
    }
}
