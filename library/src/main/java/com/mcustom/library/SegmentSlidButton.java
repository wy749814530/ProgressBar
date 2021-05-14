package com.mcustom.library;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @WYU-WIN
 * @date 2021/5/13 14:25.
 * @description
 */
public class SegmentSlidButton extends View {
    private String TAG = getClass().getSimpleName();
    /**
     * 颜色
     */
    // 选中颜色
    private int selectColor = Color.parseColor("#49a6f6");
    // 预设颜色
    private int percentColor = Color.parseColor("#666666");
    private int backgroundColor = Color.WHITE;

    /**
     * 大小
     */
    // 字体大小
    private float textSize = value2PxSize(15);
    // 内圆半径
    private float innerRadius = 9f;
    // 外部圆圈颜色
    private float outerRadius = 20f;
    // 内圆宽度
    private float innerWidth = 2;
    // 外部宽度
    private float outerWidth = 3;
    // 线宽
    private float lineWidth = 3;

    Paint linePaint;
    Paint innnerCPaint;
    Paint outerCPaint;
    Paint outerBGCPaint;
    Paint textPercentPaint;
    String[] sectionText;

    int padding = 5;
    float textMarginTop = 10;
    HashMap<Integer, Float> sectionPoint = new HashMap<>();

    public SegmentSlidButton(Context context) {
        this(context, null);
    }

    public SegmentSlidButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SegmentSlidButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.SegmentSlidButton);

            //获取自定义属性和默认值
            textSize = mTypedArray.getDimension(R.styleable.SegmentSlidButton_android_textSize, textSize);
            percentColor = mTypedArray.getColor(R.styleable.SegmentSlidButton_percentColor, percentColor);
            selectColor = mTypedArray.getColor(R.styleable.SegmentSlidButton_selectColor, selectColor);
            backgroundColor = mTypedArray.getColor(R.styleable.SegmentSlidButton_backgroundColor, backgroundColor);

            innerRadius = mTypedArray.getDimension(R.styleable.SegmentSlidButton_innerRadius, innerRadius);
            outerRadius = mTypedArray.getDimension(R.styleable.SegmentSlidButton_outerRadius, outerRadius);
            innerWidth = mTypedArray.getDimension(R.styleable.SegmentSlidButton_innerWidth, innerWidth);
            outerWidth = mTypedArray.getDimension(R.styleable.SegmentSlidButton_outerWidth, outerWidth);
            lineWidth = mTypedArray.getDimension(R.styleable.SegmentSlidButton_lineWidth, lineWidth);
            textMarginTop = mTypedArray.getDimension(R.styleable.SegmentSlidButton_text_marginTop, textMarginTop);

            String textSection = mTypedArray.getString(R.styleable.SegmentSlidButton_textSection);
            if (!TextUtils.isEmpty(textSection)) {
                sectionText = textSection.split(",");
            }
            mTypedArray.recycle();
        }

        linePaint = new Paint();
        linePaint.setColor(percentColor);
        linePaint.setStrokeWidth(lineWidth);
        linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setAntiAlias(true);


        innnerCPaint = new Paint();
        innnerCPaint.setColor(percentColor);
        innnerCPaint.setStrokeWidth(innerWidth);
        innnerCPaint.setStyle(Paint.Style.FILL);
        innnerCPaint.setStrokeJoin(Paint.Join.ROUND);
        innnerCPaint.setStrokeCap(Paint.Cap.ROUND);
        innnerCPaint.setAntiAlias(true);

        outerCPaint = new Paint();
        outerCPaint.setColor(percentColor);
        outerCPaint.setStrokeWidth(outerWidth);
        outerCPaint.setStyle(Paint.Style.STROKE);
        outerCPaint.setStrokeJoin(Paint.Join.ROUND);
        outerCPaint.setStrokeCap(Paint.Cap.ROUND);
        outerCPaint.setAntiAlias(true);

        outerBGCPaint = new Paint();
        outerBGCPaint.setColor(backgroundColor);
        outerBGCPaint.setStrokeWidth(outerWidth);
        outerBGCPaint.setStyle(Paint.Style.FILL);
        outerBGCPaint.setStrokeJoin(Paint.Join.ROUND);
        outerBGCPaint.setStrokeCap(Paint.Cap.ROUND);
        outerBGCPaint.setAntiAlias(true);

        textPercentPaint = new Paint();
        textPercentPaint.setColor(percentColor);
        textPercentPaint.setTextSize(textSize);
        textPercentPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPercentPaint.setStrokeJoin(Paint.Join.ROUND);
        textPercentPaint.setStrokeCap(Paint.Cap.ROUND);
        textPercentPaint.setAntiAlias(true);


    }

    private float value2PxSize(float dpValue) {
        float sp2 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dpValue, Resources.getSystem().getDisplayMetrics());
        return sp2;
    }

    float MIN_ZOOM_PX;
    float MAX_ZOOM_PX;
    float CURRENT_ZOOM;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.i(TAG, "onLayout : " + getWidth() + " , " + getHeight());
        setBackgroundColor(backgroundColor);

        if (getWidth() > 0 && getHeight() > 0) {

            Rect rect = new Rect();
            String zoom = "55s";
            textPercentPaint.getTextBounds(zoom, 0, zoom.length(), rect);

            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = (int) (outerRadius * 2 + padding * 2 + +textMarginTop) + rect.height() / 2 - rect.top;
            setLayoutParams(layoutParams);

            MIN_ZOOM_PX = outerRadius + padding;
            MAX_ZOOM_PX = getWidth() - outerRadius - padding;
            CURRENT_ZOOM = MIN_ZOOM_PX;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i(TAG, "onMeasure : " + getWidth() + " , " + getHeight());
    }

    private float getMaxValue() {
        return getWidth() - (outerRadius + padding) * 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float startX = outerRadius + padding;
        float startY = outerRadius + padding;

        float endX = getWidth() - outerRadius - padding;
        float endY = startY;

        Log.i(TAG, " getWidth() :" + getWidth() + " , getHeight(): " + getHeight());
        // 画线条
        canvas.drawLine(startX, startY, endX, endY, linePaint);

        innnerCPaint.setColor(percentColor);
        outerCPaint.setColor(percentColor);
        if (sectionText != null && sectionText.length > 0) {
            float value = getMaxValue() / (sectionText.length - 1);
            for (int i = 0; i < sectionText.length; i++) {
                // 画圆圈
                float startPox = i * value + startX;
                if (!sectionPoint.containsKey(i)) {
                    sectionPoint.put(i, startPox);
                }
                canvas.drawCircle(startPox, startY, outerRadius, outerBGCPaint);
                canvas.drawCircle(startPox, startY, innerRadius, innnerCPaint);
                canvas.drawCircle(startPox, startY, outerRadius, outerCPaint);

                // 画文字
                float textY = (outerRadius * 2 + padding * 2) + textMarginTop;
                Rect rect = new Rect();
                textPercentPaint.getTextBounds(sectionText[i], 0, sectionText[i].length(), rect);

                Log.i(TAG, " rect.top : " + rect.top + ", rect.height() : " + rect.height() + " , marginTop : " + textMarginTop);

                if (i == 0) {
                    canvas.drawText(sectionText[i], startPox + (rect.left - rect.width() / 2f), textY - rect.top - rect.height() / 2f + textMarginTop, textPercentPaint);
                } else if (i == sectionText.length - 1) {
                    canvas.drawText(sectionText[i], startPox - rect.left - rect.width() / 2f - 20, textY - rect.top - rect.height() / 2f + textMarginTop, textPercentPaint);
                } else {
                    canvas.drawText(sectionText[i], startPox - rect.left - rect.width() / 2f, textY - rect.top - rect.height() / 2f + textMarginTop, textPercentPaint);
                }
            }
        }

        innnerCPaint.setColor(selectColor);
        outerCPaint.setColor(selectColor);
        canvas.drawCircle(CURRENT_ZOOM, startY, outerRadius, outerBGCPaint);
        canvas.drawCircle(CURRENT_ZOOM, startY, innerRadius, innnerCPaint);
        canvas.drawCircle(CURRENT_ZOOM, startY, outerRadius, outerCPaint);
    }

    public void actionUpChanged() {
        float minDistance = Float.MAX_VALUE;
        float zoom = MIN_ZOOM_PX;
        String section = sectionText[0];
        for (Integer key : sectionPoint.keySet()) {
            if (sectionPoint.containsKey(key)) {
                float pointX = sectionPoint.get(key);
                if (minDistance == Float.MAX_VALUE) {
                    minDistance = Math.abs(pointX - CURRENT_ZOOM);
                    zoom = pointX;
                    section = sectionText[key];
                } else {
                    float abs = Math.abs(pointX - CURRENT_ZOOM);
                    if (abs < minDistance) {
                        minDistance = abs;
                        zoom = pointX;
                        section = sectionText[key];
                    }
                }
            }
        }
/*      // 方式1 ，立即变化
        CURRENT_ZOOM = zoom;
        invalidate();*/

        // 方式2， 渐渐变化
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(CURRENT_ZOOM, zoom);
        valueAnimator.setDuration(100);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                CURRENT_ZOOM = (Float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        valueAnimator.start();
        if (slidButtonListener != null) {
            slidButtonListener.onSlidSectionValue(section);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MIN_ZOOM_PX <= event.getX() && event.getX() <= MAX_ZOOM_PX) {
            CURRENT_ZOOM = event.getX();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            actionUpChanged();
        }
        invalidate();
        return true;
    }

    SlidButtonListener slidButtonListener;

    public void setSlidButtonListener(SlidButtonListener listener) {
        slidButtonListener = listener;
    }

    public interface SlidButtonListener {
        void onSlidSectionValue(String sectionText);
    }
}
