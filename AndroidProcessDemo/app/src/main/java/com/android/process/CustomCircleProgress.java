package com.android.process;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;


/**
 * 自定义View，圆形进度条
 */
public class CustomCircleProgress extends ProgressBar {

    //定义一些属性常量
    private static final int PROGRESS_DEFAULT_COLOR = 0xededee;//默认圆(边框)的颜色
    private static final int PROGRESS_REACHED_COLOR = 0Xfe3a3d;//进度条的颜色
    private static final int PROGRESS_REACHED_HEIGHT = 2;//进度条的高度
    private static final int PROGRESS_DEFAULT_HEIGHT = 2;//默认圆的高度
    private static final int PROGRESS_RADIUS = 12;//圆的半径

    //View的当前状态，默认为未开始
    private Status mStatus = Status.STARTING;
    //画笔
    private Paint mPaint;
    //进度条的颜色
    private int mReachedColor = PROGRESS_REACHED_COLOR;
    //进度条的高度
    private int mReachedHeight = dp2px(PROGRESS_REACHED_HEIGHT);
    //默认圆(边框)的颜色
    private int mDefaultColor = PROGRESS_DEFAULT_COLOR;
    //默认圆(边框)的高度
    private int mDefaultHeight = dp2px(PROGRESS_DEFAULT_HEIGHT);
    //圆的半径
    private int mRadius = dp2px(PROGRESS_RADIUS);

    public CustomCircleProgress(Context context) {
        this(context, null);
    }

    public CustomCircleProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomCircleProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取自定义属性的值
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.CustomCircleProgress);
        //默认圆的颜色
        mDefaultColor = array.getColor(R.styleable.CustomCircleProgress_progress_default_color, PROGRESS_DEFAULT_COLOR);
        //进度条的颜色
        mReachedColor = array.getColor(R.styleable.CustomCircleProgress_progress_reached_color, PROGRESS_REACHED_COLOR);
        //默认圆的高度
        mDefaultHeight = (int) array.getDimension(R.styleable.CustomCircleProgress_progress_default_height, mDefaultHeight);
        //进度条的高度
        mReachedHeight = (int) array.getDimension(R.styleable.CustomCircleProgress_progress_reached_height, mReachedHeight);
        //圆的半径
        mRadius = (int) array.getDimension(R.styleable.CustomCircleProgress_circle_radius, mRadius);

        //最后不要忘了回收 TypedArray
        array.recycle();

        //设置画笔（new画笔的操作一般不要放在onDraw方法中，因为在绘制的过程中onDraw方法会被多次调用）
        setPaint();
    }

    private void setPaint() {
        mPaint = new Paint();
        //下面是设置画笔的一些属性
        mPaint.setAntiAlias(true);//抗锯齿
        mPaint.setDither(true);//防抖动，绘制出来的图要更加柔和清晰
        mPaint.setStyle(Paint.Style.STROKE);//设置填充样式
        /**
         *  Paint.Style.FILL    :填充内部
         *  Paint.Style.FILL_AND_STROKE  ：填充内部和描边
         *  Paint.Style.STROKE  ：仅描边
         */
        mPaint.setStrokeCap(Paint.Cap.ROUND);//设置画笔笔刷类型


    }

    /**
     * 使用onMeasure方法是因为我们的自定义圆形View的一些属性(如：进度条宽度等)都交给用户自己去自定义了，所以我们需要去测量下
     * 看是否符合要求
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int paintHeight = Math.max(mReachedHeight, mDefaultHeight);//比较两数，取最大值

        if (heightMode != MeasureSpec.EXACTLY) {
            //如果用户没有精确指出宽高时，我们就要测量整个View所需要分配的高度了，测量自定义圆形View设置的上下内边距+圆形view的直径+圆形描边边框的高度
            int exceptHeight = getPaddingTop() + getPaddingBottom() + mRadius * 2 + paintHeight;
            //然后再将测量后的值作为精确值传给父类，告诉他我需要这么大的空间，你给我分配吧
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(exceptHeight, MeasureSpec.EXACTLY);
        }
        if (widthMode != MeasureSpec.EXACTLY) {
            //这里在自定义属性中没有设置圆形边框的宽度，所以这里直接用高度代替
            int exceptWidth = getPaddingLeft() + getPaddingRight() + mRadius * 2 + paintHeight;
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(exceptWidth, MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /**
         * 这里canvas.save();和canvas.restore();是两个相互匹配出现的，作用是用来保存画布的状态和取出保存的状态的
         * 当我们对画布进行旋转，缩放，平移等操作的时候其实我们是想对特定的元素进行操作,但是当你用canvas的方法来进行这些操作的时候，其实是对整个画布进行了操作，
         * 那么之后在画布上的元素都会受到影响，所以我们在操作之前调用canvas.save()来保存画布当前的状态，当操作之后取出之前保存过的状态，
         * (比如：前面元素设置了平移或旋转的操作后，下一个元素在进行绘制之前执行了canvas.save();和canvas.restore()操作)这样后面的元素就不会受到(平移或旋转的)影响
         */
        canvas.save();
        //为了保证最外层的圆弧全部显示，我们通常会设置自定义view的padding属性，这样就有了内边距，所以画笔应该平移到内边距的位置，这样画笔才会刚好在最外层的圆弧上
        //画笔平移到指定paddingLeft， getPaddingTop()位置
        canvas.translate(getPaddingLeft(), getPaddingTop());
        mPaint.setStyle(Paint.Style.STROKE);
        //画默认圆(边框)的一些设置
        mPaint.setColor(mDefaultColor);
        mPaint.setStrokeWidth(mDefaultHeight);
        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);

        //画进度条的一些设置
        mPaint.setColor(mReachedColor);
        mPaint.setStrokeWidth(mReachedHeight);
        //根据进度绘制圆弧
        float sweepAngle = getProgress() * 1.0f / getMax() * 360;
        canvas.drawArc(new RectF(0, 0, mRadius * 2, mRadius * 2), -90, sweepAngle, false, mPaint);//drawArc：绘制圆弧

        //有了path之后就可以在onDraw中绘制三角形的End和Starting状态了
        //暂停
        if (mStatus == Status.PAUSING) {
            //未开始状态，画笔填充三角形
            mPaint.setStyle(Paint.Style.FILL);
            //设置颜色
            mPaint.setColor(Color.parseColor("#fe3a3d"));
            //画三角形
            canvas.drawRoundRect(new RectF(mRadius * 2 / 3, mRadius * 2 / 3, 2 * mRadius - (mRadius * 2 / 3), 2 * mRadius - (mRadius * 2 / 3)), dp2px(2), dp2px(2), mPaint);
        } else {
            //正在进行状态,画两条竖线
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(dp2px(2));
            mPaint.setColor(Color.parseColor("#fe3a3d"));
            canvas.drawLine(mRadius * 5 / 6, mRadius * 2 / 3, mRadius * 5 / 6, 2 * mRadius * 2 / 3, mPaint);
            canvas.drawLine(2 * mRadius - (mRadius * 5 / 6), mRadius * 2 / 3, 2 * mRadius - (mRadius * 5 / 6), 2 * mRadius * 2 / 3, mPaint);
        }

        canvas.restore();

    }

    /**
     * dp 2 px
     *
     * @param dpVal
     */
    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }

    /**
     * sp 2 px
     *
     * @param spVal
     * @return
     */
    protected int sp2px(int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, getResources().getDisplayMetrics());

    }


    //当前view显示的状态
    public enum Status {
        /**
         * 开始
         */
        STARTING,
        /**
         * 暂停中
         */
        PAUSING,
    }

    //设置Status的set/get方法
    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status status) {
        this.mStatus = status;
        invalidate();
    }

}
