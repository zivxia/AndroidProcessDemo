package com.android.process;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class SportProgressView extends View {

    private Paint mPaint;
    private Paint mTextPaint;

    private int mStartColor;
    private int mEndColor;
    private int mEmptyColor;

    // 控件的宽高
    private int mWidth;
    private int mHeight;

    private int mProgressWidth;
    private int mProgressR;
    private int mStepTextSize;

    private int mCurProgress;
    private Context mContext;

    private float mProgress;
    private int mCurValue;

    public SportProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SportProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray a = mContext.getTheme().obtainStyledAttributes(attrs, R.styleable.SportProgressView, defStyleAttr, 0);
        mStartColor = a.getColor(R.styleable.SportProgressView_spv_startColor, Color.parseColor("#ff5a34"));
        mEndColor = a.getColor(R.styleable.SportProgressView_spv_endColor, Color.parseColor("#ee3747"));
        mEmptyColor = a.getColor(R.styleable.SportProgressView_spv_emptyColor, Color.parseColor("#ffebe6"));
        mProgressWidth = a.getDimensionPixelSize(R.styleable.SportProgressView_spv_progressWidth, 14);
        mStepTextSize = a.getDimensionPixelSize(R.styleable.SportProgressView_spv_stepTextSize, 10);
        mWidth = a.getDimensionPixelSize(R.styleable.SportProgressView_spv_width, DensityUtil.dip2px(context, 228));
        mHeight = a.getDimensionPixelSize(R.styleable.SportProgressView_spv_width, DensityUtil.dip2px(context, 114));
        a.recycle();

        init();
    }

    /**
     * 初始化
     */
    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(mProgressWidth);
        mPaint.setStyle(Paint.Style.STROKE);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mEmptyColor);
        mTextPaint.setTextSize(mStepTextSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode != MeasureSpec.EXACTLY) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(mWidth, MeasureSpec.EXACTLY);
        }

        if (heightMode != MeasureSpec.EXACTLY) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY);
        }
        /**
         * 以给定的高度为限制条件，计算半径
         */
        mProgressR = mHeight - mProgressWidth / 2;
        mWidth = mProgressR * 2 + mProgressWidth + getPaddingLeft() + getPaddingRight();

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        drawProgress(canvas);

        drawProgressTitle(canvas);

        drawProgressValue(canvas);

    }

    private void drawProgress(Canvas canvas) {
        int left = mProgressWidth / 2 + getPaddingLeft();
        int right = left + 2 * mProgressR;
        int top = mHeight - mProgressR;
        int bottom = mHeight + mProgressR;
        RectF rect = new RectF(left, top, right, bottom);

        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(mEmptyColor);
        mPaint.setShader(null);
        canvas.drawArc(rect, 180, 180, false, mPaint);

        /**
         * 设置渐变颜色
         */
        int[] colors = new int[]{mStartColor, mEndColor};
        LinearGradient linearGradient = new LinearGradient(0, 0, mWidth, 0, colors,
                null, LinearGradient.TileMode.CLAMP);
        mPaint.setShader(linearGradient);
        canvas.drawArc(rect, 180, mProgress, false, mPaint);
    }

    /**
     * 绘制中间进度的文字 上方的文字
     *
     * @param canvas
     */
    private void drawProgressTitle(Canvas canvas) {
        mTextPaint.setColor(Color.parseColor("#070d0f"));
        mTextPaint.setTextSize(DensityUtil.sp2px(mContext, 14));
        String text = "总额度";
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        float baseline = mHeight - DensityUtil.dip2px(mContext, 36) - fm.descent;
        float textWith = mTextPaint.measureText(text);
        canvas.drawText(text, mWidth / 2 - textWith / 2, baseline, mTextPaint);
    }

    /**
     * 绘制进度代表的值
     *
     * @param canvas
     */
    private void drawProgressValue(Canvas canvas) {
        //画人名币符号
        mTextPaint.setColor(Color.parseColor("#070d0f"));
        mTextPaint.setTextSize(DensityUtil.sp2px(mContext, 16));

        String unit = "¥";
        Paint.FontMetrics unitFm = mTextPaint.getFontMetrics();
        float unitBaseline = mHeight;
        float unitTextWith = mTextPaint.measureText(unit);

        mTextPaint.setTextSize(DensityUtil.sp2px(mContext, 32));
        String text = String.valueOf(mCurValue);
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        float baseline = mHeight;
        float textWith = mTextPaint.measureText(text);

        mTextPaint.setTextSize(DensityUtil.sp2px(mContext, 16));
        canvas.drawText(unit, mWidth / 2 - (textWith + unitTextWith) / 2, baseline - DensityUtil.dip2px(mContext, 3), mTextPaint);

        mTextPaint.setTextSize(DensityUtil.sp2px(mContext, 32));
        canvas.drawText(text, mWidth / 2 + unitTextWith / 2 - textWith / 2, baseline + DensityUtil.dip2px(mContext, 6), mTextPaint);

    }

    public void setProgress(int endValue) {
        startAnimation(endValue);
    }

    /**
     * 动画效果的取值
     */
    public void startAnimation(int endValue) {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 180);
        animator.setDuration(1000).start();
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mProgress = (float) animation.getAnimatedValue();
                mCurValue = (int) (animation.getAnimatedFraction() * endValue);
                invalidate();
            }
        });
    }
}
