package com.mcustom.library;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

/**
 * @WYU-WIN
 * @date 2020/6/21 1247.
 * description：  仿iphone带进度的进度条，线程安全的View，可直接在线程中更新进度
 */

public class RoundProgressBar extends View {
    /**
     * 画笔对象的引用
     */
    private Paint paint;
    private Paint progressTextPaint;
    private Paint percentPaint;
    /**
     * 圆环的颜色
     */
    private int roundColor = Color.RED;

    /**
     * 圆环进度的颜色
     */
    private int roundProgressColor = Color.GREEN;

    /**
     * 中间进度百分比的字符串的颜色
     */
    private int textColor = Color.GREEN;
    private int percentColor = Color.GREEN;
    /**
     * 中间进度百分比的字符串的字体
     */
    private float textSize = value2PxSize(15);
    private float percentSize = value2PxSize(9);

    private Rect percentBounds = new Rect();
    /**
     * 圆环的宽度
     */
    private float roundWidth;

    /**
     * 最大进度
     */
    private int max;

    /**
     * 当前进度
     */
    private int progress;
    /**
     * 是否显示中间的进度
     */
    private boolean textIsDisplayable;

    public static final int STROKE = 0;
    public static final int FILL = 1;

    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (attrs != null) {
            TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar);

            //获取自定义属性和默认值
            roundColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor, Color.RED);
            roundProgressColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor, Color.GREEN);
            textColor = mTypedArray.getColor(R.styleable.RoundProgressBar_android_textColor, Color.GREEN);
            textSize = mTypedArray.getDimension(R.styleable.RoundProgressBar_android_textSize, textSize);
            percentColor = mTypedArray.getColor(R.styleable.RoundProgressBar_percentColor, Color.GREEN);
            percentSize = mTypedArray.getDimension(R.styleable.RoundProgressBar_percentSize, percentSize);
            roundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, 5);
            max = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 100);
            textIsDisplayable = mTypedArray.getBoolean(R.styleable.RoundProgressBar_textIsDisplayable, true);
            mTypedArray.recycle();
        }

        Log.e("RoundProgressBar", " , textSize : " + textSize + " , percentSize : " + percentSize);

        paint = new Paint();

        progressTextPaint = new Paint();
        progressTextPaint.setColor(textColor);
        progressTextPaint.setTextSize(textSize);
        progressTextPaint.setStyle(Paint.Style.FILL);
        progressTextPaint.setStrokeWidth(1);
        progressTextPaint.setStrokeJoin(Paint.Join.ROUND);
        progressTextPaint.setStrokeCap(Paint.Cap.ROUND);
        progressTextPaint.setAntiAlias(true);
        progressTextPaint.setTypeface(Typeface.DEFAULT_BOLD); //设置字体
        progressTextPaint.getTextBounds("100", 0, "100".length(), percentBounds);


        percentPaint = new Paint();
        percentPaint.setColor(percentColor);
        percentPaint.setTextSize(percentSize);
        percentPaint.setStyle(Paint.Style.FILL);
        percentPaint.setStrokeWidth(1);
        percentPaint.setStrokeJoin(Paint.Join.ROUND);
        percentPaint.setStrokeCap(Paint.Cap.ROUND);
        percentPaint.setAntiAlias(true);


    }

    private float value2PxSize(float dpValue) {
        float sp2 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dpValue, Resources.getSystem().getDisplayMetrics());
        return sp2;
    }

    private float dp2px(float dpValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return dpValue * scale + 0.5f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /**
         * 画最外层的大圆环
         */
        int centre = getWidth() / 2; //获取圆心的x坐标
        int radius = (int) (centre - roundWidth / 2); //圆环的半径
        paint.setColor(roundColor); //设置圆环的颜色
        paint.setStyle(Paint.Style.STROKE); //设置空心
        paint.setStrokeWidth(roundWidth); //设置圆环的宽度
        paint.setAntiAlias(true);  //消除锯齿
        canvas.drawCircle(centre, centre, radius, paint); //画出圆环


        /**
         * 画进度百分比
         */

        paint.setStrokeWidth(0);
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        paint.setTypeface(Typeface.DEFAULT_BOLD); //设置字体
        int percent = (int) (((float) progress / (float) max) * 100);  //中间的进度百分比，先转换成float在进行除法运算，不然都为0
        float textWidth = paint.measureText(percent + "");   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间

        Rect bounds = new Rect();
        String percentText = percent + "";
        paint.getTextBounds(percentText, 0, percentText.length(), bounds);

        if (textIsDisplayable && percent != 0) {
            canvas.drawText(percent + "", centre - bounds.width() / 2 - bounds.left, centre + bounds.height() / 2 - bounds.bottom, progressTextPaint); //画出进度百分比
        }
        Log.i("RoundProgressBar", "width 1 : " + bounds.width() + " , height 1 : " + bounds.height());
        Log.i("RoundProgressBar", "width : " + percentBounds.width() + " , height : " + percentBounds.height());
        canvas.drawText("%", centre + percentBounds.width() / 2, centre + percentBounds.height(), percentPaint);
        /**
         * 画圆弧 ，画圆环的进度
         */

        //设置进度是实心还是空心
        paint.setStrokeWidth(roundWidth); //设置圆环的宽度
        paint.setColor(roundProgressColor);  //设置进度的颜色
        RectF oval = new RectF(centre - radius, centre - radius, centre + radius, centre + radius);  //用于定义的圆弧的形状和大小的界限

        paint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(oval, 0, 360 * progress / max, false, paint);  //根据进度画圆弧
    }


    public synchronized int getMax() {
        return max;
    }

    /**
     * 设置进度的最大值
     *
     * @param max
     */
    public synchronized void setMax(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("max not less than 0");
        }
        this.max = max;
    }

    /**
     * 获取进度.需要同步
     *
     * @return
     */
    public synchronized int getProgress() {
        return progress;
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
     * 刷新界面调用postInvalidate()能在非UI线程刷新
     *
     * @param progress
     */
    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }
        if (progress > max) {
            progress = max;
        }
        if (progress <= max) {
            this.progress = progress;
            postInvalidate();
        }

    }


    public int getCricleColor() {
        return roundColor;
    }

    public void setCricleColor(int cricleColor) {
        this.roundColor = cricleColor;
    }

    public int getCricleProgressColor() {
        return roundProgressColor;
    }

    public void setCricleProgressColor(int cricleProgressColor) {
        this.roundProgressColor = cricleProgressColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public float getRoundWidth() {
        return roundWidth;
    }

    public void setRoundWidth(float roundWidth) {
        this.roundWidth = roundWidth;
    }
}
