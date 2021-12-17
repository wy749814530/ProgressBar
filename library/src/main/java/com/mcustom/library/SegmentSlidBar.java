package com.mcustom.library;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.mcustom.library.utils.ColorPickerUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @WYU-WIN
 * @date 2021/12/13
 * @Description: 低功耗设备红外侦测灵敏度控件
 */
public class SegmentSlidBar extends View {
    private String TAG = getClass().getSimpleName();
    private int[] gradientColors = {0xff49A6F6, 0xff92D556, 0xffFDBC84, 0xffFC5531, 0xffFF0080};
    private int textColor = Color.GRAY;
    private int selectColor = 0;

    // 文字距离渐变线的间距
    private float textMarginTop = 6;
    // 渐变线宽度
    private float linearGradientWidth = 10;
    // 外部圆圈颜色
    private float outerRadius = 15;
    private float outerWidth = 8;
    // 上下左右留白
    private float padding = 4;


    private float M_CURRENT_RADIOS = 0f;
    // 文字
    private ArrayList<String> sectionTexts = new ArrayList<>(); // 作用是保证顺序
    private ConcurrentHashMap<String, Float> textPointsHashMap = new ConcurrentHashMap(); // 存放测量结果

    float MIN_ZOOM_PX = 0f;
    float MAX_ZOOM_PX = 0f;
    float MAX_WIDTH = 0f;

    float startLine_X = 0f;
    float endLine_X = 0f;
    float line_Y = 0f;

    float startPoint_X = 0f;
    float endPoint_X = 0f;
    float textY = 0f;

    private int backgroundColor = Color.WHITE;
    private LinearGradient backGradient = null;
    private Paint linePaint = new Paint();
    private Paint innerPaint = new Paint();
    private Paint outerPaint = new Paint();
    private Paint textPaint = new Paint();
    private float textSize = value2PxSize(10);

    // 在OnDraw
    private boolean localOnMeasureEnable = true;
    private String selectSection = null;

    public SegmentSlidBar(Context context) {
        this(context, null);
    }

    public SegmentSlidBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SegmentSlidBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.SegmentSlidBar);
            //获取自定义属性和默认值
            textSize = mTypedArray.getDimension(R.styleable.SegmentSlidBar_android_textSize, textSize);
            textColor = mTypedArray.getColor(R.styleable.SegmentSlidBar_android_textColor, textColor);
            selectColor = mTypedArray.getColor(R.styleable.SegmentSlidBar_selectColor, selectColor);
            String splitcolors = mTypedArray.getString(R.styleable.SegmentSlidBar_gradientColors);
            backgroundColor = mTypedArray.getColor(R.styleable.SegmentSlidBar_backgroundColor, backgroundColor);
            outerRadius = mTypedArray.getDimension(R.styleable.SegmentSlidBar_outerRadius, outerRadius);
            outerWidth = mTypedArray.getDimension(R.styleable.SegmentSlidBar_outerWidth, outerWidth);
            linearGradientWidth = mTypedArray.getDimension(R.styleable.SegmentSlidBar_lineWidth, linearGradientWidth);
            textMarginTop = mTypedArray.getDimension(R.styleable.SegmentSlidBar_text_marginTop, textMarginTop);
            String textSection = mTypedArray.getString(R.styleable.SegmentSlidBar_textSection);

            if (!TextUtils.isEmpty(splitcolors)) {
                String[] splitColors = splitcolors.replace(" ", "").split(",");
                if (splitColors != null && splitColors.length > 0) {
                    gradientColors = null;
                    gradientColors = new int[splitColors.length];
                    for (int i = 0; i < splitColors.length; i++) {
                        gradientColors[i] = Color.parseColor(splitColors[i]);
                    }
                }
            }

            if (!TextUtils.isEmpty(textSection)) {
                String[] splitTxt = textSection.replace(" ", "").split(",");
                if (splitTxt != null && splitTxt.length > 0) {
                    selectSection = splitTxt[0];
                    for (String text : splitTxt) {
                        sectionTexts.add(text);
                        textPointsHashMap.put(text, 0f);
                    }
                }
            }
            mTypedArray.recycle();
        }

        innerPaint.setColor(Color.WHITE);
        innerPaint.setStrokeWidth(2);
        innerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        innerPaint.setStrokeJoin(Paint.Join.ROUND);
        innerPaint.setStrokeCap(Paint.Cap.ROUND);
        innerPaint.setAntiAlias(true);

        outerPaint.setStrokeWidth(2);
        outerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        outerPaint.setStrokeJoin(Paint.Join.ROUND);
        outerPaint.setStrokeCap(Paint.Cap.ROUND);
        outerPaint.setAntiAlias(true);

        textPaint.setStrokeWidth(0.5f);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setStrokeJoin(Paint.Join.ROUND);
        textPaint.setStrokeCap(Paint.Cap.ROUND);
        textPaint.setAntiAlias(true);
    }

    private float value2PxSize(float dpValue) {
        float sp2 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dpValue, Resources.getSystem().getDisplayMetrics());
        return sp2;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        setBackgroundColor(backgroundColor);
        Log.i(TAG, "onLayout : " + getWidth() + " , " + getHeight());
        if (getWidth() > 0 && getHeight() > 0) {
            MIN_ZOOM_PX = padding;
            MAX_ZOOM_PX = getWidth() - padding;

            MAX_WIDTH = getWidth() - (padding) * 2;

            startLine_X = padding;
            endLine_X = getWidth() - padding;
            line_Y = outerRadius + padding;

            startPoint_X = outerRadius + padding;
            endPoint_X = getWidth() - padding - outerRadius;

            textY = padding + outerRadius * 2 + textMarginTop;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Rect rect = new Rect();
        String zoom = "测量";
        textPaint.getTextBounds(zoom, 0, zoom.length(), rect);

        int height = (int) (padding + outerRadius * 2 + textMarginTop + textMarginTop + rect.height() /*- rect.top + padding*/);
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = height;
        setLayoutParams(layoutParams);
    }

    /**
     * @ 测量分割阶段点的位置
     */
    private synchronized void onPointsMeasure() {
        float segmentpx = MAX_WIDTH / (textPointsHashMap.size() - 1);
        int position = 0;

        for (String key : sectionTexts) {
            if (position == 0) {
                textPointsHashMap.put(key, startPoint_X);
            } else if (position == textPointsHashMap.size()) {
                textPointsHashMap.put(key, MAX_ZOOM_PX);
            } else {
                float startPox = position * segmentpx + padding;
                textPointsHashMap.put(key, startPox);
            }
            position++;
        }
        localOnMeasureEnable = false;
    }

    /**
     * @ 测量当前分段的位置
     */
    private synchronized void onSectionMeasure() {
        for (Map.Entry<String, Float> entry : textPointsHashMap.entrySet()) {
            if (loaclSection.equals(entry.getKey())) {
                float currentPoints = entry.getValue();
                if (currentPoints <= MIN_ZOOM_PX) {
                    M_CURRENT_RADIOS = MIN_ZOOM_PX;
                } else if (currentPoints >= MAX_ZOOM_PX) {
                    M_CURRENT_RADIOS = MAX_ZOOM_PX - outerRadius;
                } else {
                    M_CURRENT_RADIOS = currentPoints;
                }
            }
        }
        loaclSection = null;
    }

    private String loaclSection = null;

    public synchronized void setCurrentSection(String section) {
        loaclSection = section;
        selectSection = section;
        postInvalidate();
    }

    public synchronized void updataGradientPoints(ArrayList<String> contexts) {
        textPointsHashMap.clear();
        sectionTexts.clear();
        for (String text : contexts) {
            sectionTexts.add(text);
            textPointsHashMap.put(text, 0f);
        }
        localOnMeasureEnable = true;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (localOnMeasureEnable) {
            // 外界主动设置了，但是还没有测量完成，重新测量
            onPointsMeasure();
        }

        if (!TextUtils.isEmpty(loaclSection)) {
            onSectionMeasure();
        }

        if (backGradient == null) {         //start_X
            backGradient = new LinearGradient(startLine_X, line_Y, endLine_X, line_Y, gradientColors, null, Shader.TileMode.CLAMP);
        }
        if (linePaint == null) {
            linePaint = new Paint();
        }
        linePaint.setShader(backGradient);
        linePaint.setStrokeWidth(linearGradientWidth);
        canvas.drawLine(startLine_X, line_Y, endLine_X, line_Y, linePaint);

        if (M_CURRENT_RADIOS <= MIN_ZOOM_PX) {
            int color = ColorPickerUtils.getColor(gradientColors, 0);
            outerPaint.setColor(color);
            canvas.drawCircle(startPoint_X, line_Y, outerRadius, outerPaint);
            canvas.drawCircle(startPoint_X, line_Y, outerRadius - outerWidth, innerPaint);
        } else if (M_CURRENT_RADIOS >= MAX_ZOOM_PX) {
            int color = ColorPickerUtils.getColor(gradientColors, 1);
            outerPaint.setColor(color);
            canvas.drawCircle(MAX_ZOOM_PX, line_Y, outerRadius, outerPaint);
            canvas.drawCircle(MAX_ZOOM_PX, line_Y, outerRadius - outerWidth, innerPaint);
        } else {
            float lineRadios = (M_CURRENT_RADIOS - MIN_ZOOM_PX) / MAX_WIDTH;
            int color = ColorPickerUtils.getColor(gradientColors, lineRadios);
            outerPaint.setColor(color);
            canvas.drawCircle(M_CURRENT_RADIOS, line_Y, outerRadius, outerPaint);
            canvas.drawCircle(M_CURRENT_RADIOS, line_Y, outerRadius - outerWidth, innerPaint);
        }

        // 文字
        int position = 0;
        float segmentpx = MAX_WIDTH / (textPointsHashMap.size() - 1);
        for (String text : sectionTexts) {
            float startPox = position * segmentpx + padding;

            if (!TextUtils.isEmpty(selectSection) && selectSection.equals(text)) {
                if (selectColor == 0) {
                    float lineRadios = (startPox - MIN_ZOOM_PX) / MAX_WIDTH;
                    int color = ColorPickerUtils.getColor(gradientColors, lineRadios);
                    textPaint.setColor(color);
                } else {
                    textPaint.setColor(selectColor);
                }
            } else {
                textPaint.setColor(textColor);
            }

            Rect rect = new Rect();
            textPaint.getTextBounds(text, 0, text.length(), rect);
            if (position == 0) {
                canvas.drawText(text, startPox - rect.left, textY - rect.top, textPaint);
            } else if (position == textPointsHashMap.size() - 1) {
                canvas.drawText(text, startPox - rect.left - rect.width(), textY - rect.top, textPaint);
            } else {
                canvas.drawText(text, startPox - rect.left - rect.width() / 2f, textY - rect.top, textPaint);
            }
            position++;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getX() <= MIN_ZOOM_PX) {
            M_CURRENT_RADIOS = MIN_ZOOM_PX;
        } else if (event.getX() >= MAX_ZOOM_PX) {
            M_CURRENT_RADIOS = MAX_ZOOM_PX - outerRadius;
        } else {
            M_CURRENT_RADIOS = event.getX();
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            actionUpChanged();
        }
        invalidate();
        return true;
    }

    private void actionUpChanged() {
        if (textPointsHashMap.size() < 2) {
            return;
        }

        boolean start = true;
        float absMinPoints = 0f;
        float currentPoints = 0f;
        String currentKey = "";
        for (Map.Entry<String, Float> entry : textPointsHashMap.entrySet()) {
            float pointx = Math.abs(entry.getValue() - M_CURRENT_RADIOS);
            if (start) {
                start = false;
                absMinPoints = pointx;
                currentPoints = entry.getValue();
                currentKey = entry.getKey();
            } else {
                if (pointx < absMinPoints) {
                    absMinPoints = pointx;
                    currentPoints = entry.getValue();
                    currentKey = entry.getKey();
                }
            }
        }

        if (currentPoints <= MIN_ZOOM_PX) {
            M_CURRENT_RADIOS = MIN_ZOOM_PX;
        } else if (currentPoints >= MAX_ZOOM_PX) {
            M_CURRENT_RADIOS = MAX_ZOOM_PX - outerRadius;
        } else {
            M_CURRENT_RADIOS = currentPoints;
        }
        selectSection = currentKey;

        if (mListener != null && !TextUtils.isEmpty(currentKey)) {
            mListener.onSectionValue(currentKey);
        }
        postInvalidate();
    }


    private SegmentSlidListener mListener;

    public void setSegmentSlidListener(SegmentSlidListener listener) {
        mListener = listener;
    }

    public interface SegmentSlidListener {
        void onSectionValue(String sectionText);
    }
}