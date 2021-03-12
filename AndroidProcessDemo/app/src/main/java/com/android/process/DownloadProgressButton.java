package com.android.process;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.math.BigDecimal;

/**
 * 下载进度按钮
 *
 * @author zivxia
 */
public class DownloadProgressButton extends View {

    private final static int MAX_PROGRESS = 100;

    private int mWidth;
    private int mHeight;
    private int mRoundedRadius;
    private int mBorderWith;
    private int mBorderColor;
    private int mDisableBorderColor;
    private int mBackgroundColor;
    private int mProgressColor;
    private int mTextColor;
    private int mDisableTextColor;
    private int mTextSize;
    private int mTextOverlapColor;
    private int mMaxProgress;
    private String mProgressText;

    private Paint mRoundedRectanglePaint;
    private Paint mTextPaint;
    private Paint mProgressPaint;

    private @GameDownloadStatus
    int mDownloadStatus = GameDownloadStatus.UNLOAD;
    private int mProgress;

    public DownloadProgressButton(Context context) {
        this(context, null);
    }

    public DownloadProgressButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DownloadProgressButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DownloadProgressButton);
        mWidth = typedArray.getDimensionPixelSize(R.styleable.DownloadProgressButton_dpb_width, dip2px(context, 64));
        mHeight = typedArray.getDimensionPixelSize(R.styleable.DownloadProgressButton_dpb_height, dip2px(context, 28));
        mRoundedRadius = typedArray.getDimensionPixelSize(R.styleable.DownloadProgressButton_dpb_rounded_radius, dip2px(context, 14));
        mBorderWith = typedArray.getDimensionPixelSize(R.styleable.DownloadProgressButton_dpb_border_width, dip2px(context, 1));
        mBorderColor = typedArray.getColor(R.styleable.DownloadProgressButton_dpb_border_color, Color.parseColor("#80fe3a3d"));
        mDisableBorderColor = typedArray.getColor(R.styleable.DownloadProgressButton_dpb_border_color, Color.parseColor("#cfd0d1"));
        mBackgroundColor = typedArray.getColor(R.styleable.DownloadProgressButton_dpb_background_color, Color.parseColor("#ffffff"));
        mProgressColor = typedArray.getColor(R.styleable.DownloadProgressButton_dpb_progress_color, Color.parseColor("#ffebe6"));
        mTextColor = typedArray.getColor(R.styleable.DownloadProgressButton_dpb_text_color, Color.parseColor("#fe3a3d"));
        mDisableTextColor = typedArray.getColor(R.styleable.DownloadProgressButton_dpb_text_color, Color.parseColor("#cfd0d1"));
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.DownloadProgressButton_dpb_text_size, sp2px(context, 14));
        mTextOverlapColor = typedArray.getColor(R.styleable.DownloadProgressButton_dpb_text_overlap_color, Color.parseColor("#fe3a3d"));
        mMaxProgress = typedArray.getInt(R.styleable.DownloadProgressButton_dpb_max_progress, MAX_PROGRESS);
        typedArray.recycle();
        init(context);
    }

    private void init(Context context) {

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setStrokeWidth(dip2px(context, 0.33333f));
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);

        mRoundedRectanglePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mRoundedRectanglePaint.setColor(mBorderColor);
        mRoundedRectanglePaint.setStrokeWidth(mBorderWith);
        mRoundedRectanglePaint.setStyle(Paint.Style.STROKE);

        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mProgressPaint.setColor(mProgressColor);
        mProgressPaint.setStyle(Paint.Style.FILL);

        mProgressText = getProgressText();
    }


    public void setBorderColor(int borderColor) {
        this.mBorderColor = borderColor;
    }

    public void setProgressColor(int progressColor) {
        this.mProgressColor = progressColor;
    }

    public void setTextColor(int textColor) {
        this.mTextColor = textColor;
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
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBackground(canvas);

        drawProgress(canvas);

        drawProgressText(canvas);

    }

    public void setProgressAnDownloadStatus(int progress, @GameDownloadStatus int downloadStatus) {
        this.mDownloadStatus = downloadStatus;
        this.mProgress = progress;
        this.mProgressText = getProgressText();
        invalidate();
    }

    public void setDownloadStatus(@GameDownloadStatus int downloadStatus) {
        if (this.mDownloadStatus != downloadStatus) {
            this.mDownloadStatus = downloadStatus;
            this.mProgressText = getProgressText();
            invalidate();
        }
    }

    public @GameDownloadStatus
    int getDownloadStatus() {
        return mDownloadStatus;
    }

    public void setProgress(int progress) {
        if (this.mProgress != progress) {
            this.mProgress = progress;
            this.mProgressText = getProgressText();
            invalidate();
        }
    }

    public void setProgressText(String progressText) {
        if (TextUtils.isEmpty(progressText)) {
            return;
        }
        this.mProgressText = progressText;
        invalidate();
    }

    public void reset() {
        setProgressAnDownloadStatus(0, GameDownloadStatus.UNLOAD);
    }

    public int getMaxProgress() {
        return mMaxProgress;
    }

    private void drawBackground(Canvas canvas) {

        mRoundedRectanglePaint.setColor(isEnabled() ? mBorderColor : mDisableBorderColor);

        RectF rectF = new RectF(mBorderWith / 2, mBorderWith / 2, mWidth - mBorderWith / 2, mHeight - mBorderWith / 2);
        //画线框
        canvas.drawRoundRect(rectF, mRoundedRadius, mRoundedRadius, mRoundedRectanglePaint);
    }

    private void drawProgress(Canvas canvas) {
        canvas.save();
        mProgressPaint.setColor(mProgressColor);
        //画背景
        RectF progressRectF = new RectF(mBorderWith, mBorderWith, mWidth - mBorderWith, mHeight - mBorderWith);
        canvas.clipRect(mBorderWith, mBorderWith, mWidth * (isEnabled() ? getProgressPercent() : 0), mHeight - mBorderWith);
        canvas.drawRoundRect(progressRectF, mRoundedRadius, mRoundedRadius, mProgressPaint);
    }

    private void drawProgressText(Canvas canvas) {

        canvas.restore();

        mTextPaint.setColor(isEnabled() ? mTextColor : mDisableTextColor);

        RectF rectF = new RectF(mBorderWith / 2, mBorderWith / 2, mWidth - mBorderWith / 2, mHeight - mBorderWith / 2);
        //画文字
        //计算baseline
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        float baseline = rectF.centerY() + distance;
        canvas.drawText(mProgressText, rectF.centerX(), baseline, mTextPaint);
    }

    private String getProgressText() {
        switch (mDownloadStatus) {
            case GameDownloadStatus.DOWNLOADING:
                return String.format("%d%%", (int) (getProgressPercent() * 100));
            case GameDownloadStatus.PAUSEING:
                return "继续";
            case GameDownloadStatus.COMPLETED:
                return "安装";
            case GameDownloadStatus.INSTALL_COMPLETED:
                return "打开";
            case GameDownloadStatus.UPGRADE:
                return "更新";
            default:
                return "下载";
        }
    }

    private float getProgressPercent() {
        float percent = BigDecimal.valueOf(mProgress).divide(BigDecimal.valueOf(mMaxProgress)).floatValue();
        return percent > 1 ? 1.0f : percent;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

}
