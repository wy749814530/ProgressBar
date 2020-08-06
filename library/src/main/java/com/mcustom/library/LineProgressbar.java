package com.mcustom.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;

/**
 * @WYU-WIN
 * @date 2020/6/27 2114.
 * description：  线性进度条，带数据变化跟踪，允许设置最大值和最小值，
 */
public class LineProgressbar extends View implements View.OnTouchListener {
    private String TAG = "ProgressBar";
    boolean ENABLE_CTION_MOVE = false;
    /**
     * 进度相对进度条的位置
     * 0：左边
     * 1：上边
     * 2：右边
     * 3：上边随进度走动
     */

    private SITE relativeSite = SITE.TOP;

    enum SITE {
        TOP,
        TOP_MOVE,
        GONE
    }

    /**
     * 文字画笔属性
     */
    private Rect textRect;
    private Paint textPoint;
    private int textPointColor = 0xff25d1d3;
    private float textPointSize = 15;
    private String unit = "";
    /**
     * 进度条画笔属性
     */
    private Paint progressPoint;
    private Paint progressSpendPoint;
    private Paint circleHollowPoint;
    private int progressbgColor = 0xff999999;
    private int progressSpendColor = 0xff25d1d3;
    private int progressHeight = 6; // 进度条高度
    private int progress = 50;       // 进度
    /**
     * 当前进度坐标
     */
    private float CurrentX;
    private float CurrentY;

    /**
     * progress取值范围
     */
    private int minProgress = 0;
    private int maxProgress = 100;
    /**
     * 圆圈半径
     */
    private float innerRadius, outerRadius;
    private int pointImageResId = 0;
    Bitmap bitmap;

    /**
     * 间距
     */
    private int marginTopAndBottom = 5;
    private float marginLeft;
    private float marginRight;
    private float progressBarWidth = 1080;

    private OnProgressbarChangeListener mListener;

    public LineProgressbar(Context context) {
        super(context);
    }

    public LineProgressbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public LineProgressbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public void setOnProgressbarChangeListener(OnProgressbarChangeListener listener) {
        mListener = listener;
    }

    private void initView(Context context, AttributeSet attrs) {

        if (attrs != null) {
            TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.CustomProgressbar);
            textPointColor = attributes.getColor(R.styleable.CustomProgressbar_textPointColor, textPointColor);
            textPointSize = attributes.getDimensionPixelSize(R.styleable.CustomProgressbar_textPointSize, 16);
            int site = attributes.getInt(R.styleable.CustomProgressbar_relativesite, relativeSite.ordinal());
            progressbgColor = attributes.getColor(R.styleable.CustomProgressbar_progressbgColor, progressbgColor);
            progressSpendColor = attributes.getColor(R.styleable.CustomProgressbar_progressSpendColor, progressSpendColor);
            progressHeight = (int) attributes.getDimension(R.styleable.CustomProgressbar_progressHeight, progressHeight);
            progress = attributes.getInt(R.styleable.CustomProgressbar_defaultProgress, progress);
            maxProgress = attributes.getInt(R.styleable.CustomProgressbar_maxProgress, 100);
            minProgress = attributes.getInt(R.styleable.CustomProgressbar_minProgress, 0);
            innerRadius = attributes.getInt(R.styleable.CustomProgressbar_innerPointRadius, 0);
            outerRadius = attributes.getInt(R.styleable.CustomProgressbar_outerPointRadius, 0);
            pointImageResId = attributes.getResourceId(R.styleable.CustomProgressbar_pointImage, 0);
            unit = attributes.getString(R.styleable.CustomProgressbar_textUnit);

            if (site == 0) {
                relativeSite = SITE.TOP;
            } else if (site == 1) {
                relativeSite = SITE.TOP_MOVE;
            } else if (site == 2) {
                relativeSite = SITE.GONE;
            }
        }
        if (pointImageResId == 0) {
            if (outerRadius < innerRadius) {
                float minOutRadius = outerRadius;
                outerRadius = innerRadius;
                innerRadius = minOutRadius;
            }

            if (innerRadius == 0) {
                innerRadius = progressHeight * 3 / 4;
            }
            if (outerRadius == 0) {
                outerRadius = progressHeight * 1.4f;
            }
        }
        Log.i(TAG, "textPointSize : " + textPointSize);
        textRect = new Rect();

        textPoint = new Paint();
        textPoint.setColor(textPointColor);
        textPoint.setTextSize(textPointSize);
        textPoint.setStrokeWidth(1);
        textPoint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPoint.setStrokeJoin(Paint.Join.ROUND);
        textPoint.setStrokeCap(Paint.Cap.ROUND);
        textPoint.setAntiAlias(true);

        progressPoint = new Paint();
        progressPoint.setColor(progressbgColor);
        progressPoint.setStrokeWidth(1);
        progressPoint.setStyle(Paint.Style.FILL_AND_STROKE);
        progressPoint.setStrokeJoin(Paint.Join.ROUND);
        progressPoint.setStrokeCap(Paint.Cap.ROUND);
        progressPoint.setAntiAlias(true);

        progressSpendPoint = new Paint();
        progressSpendPoint.setColor(progressSpendColor);
        progressSpendPoint.setStrokeWidth(1);
        progressSpendPoint.setStyle(Paint.Style.FILL_AND_STROKE);
        progressSpendPoint.setStrokeJoin(Paint.Join.ROUND);
        progressSpendPoint.setStrokeCap(Paint.Cap.ROUND);
        progressSpendPoint.setAntiAlias(true);

        circleHollowPoint = new Paint();
        circleHollowPoint.setColor(progressSpendColor);
        circleHollowPoint.setStrokeWidth(1);
        circleHollowPoint.setStyle(Paint.Style.STROKE);
        circleHollowPoint.setStrokeJoin(Paint.Join.ROUND);
        circleHollowPoint.setStrokeCap(Paint.Cap.ROUND);
        circleHollowPoint.setAntiAlias(true);
        setOnTouchListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        invalidateLayout();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        invalidateLayout();
    }

    protected void invalidateLayout() {
        float progressH;
        String text = "159" + unit;
        textPoint.getTextBounds(text, 0, text.length(), textRect);
        if (pointImageResId != 0) {
            bitmap = BitmapFactory.decodeResource(getResources(), pointImageResId);
            if (bitmap != null) {
                outerRadius = bitmap.getHeight() / 2;
                innerRadius = outerRadius;
            }
        }

        marginLeft = marginRight = (int) (outerRadius + 4);
        progressBarWidth = getWidth() - marginLeft - marginRight;
        progressH = progressHeight * 1.4f;
        if (progressHeight < outerRadius) {
            progressH = outerRadius * 2;
        }

        int maxHeight;
        if (relativeSite == SITE.GONE) {
            maxHeight = (int) (marginTopAndBottom + progressH + marginTopAndBottom);
        } else {
            maxHeight = (int) (marginTopAndBottom + textRect.height() + marginTopAndBottom + progressH + marginTopAndBottom);
        }

        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.height = maxHeight;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        setLayoutParams(layoutParams);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float progressTop = getHeight() - outerRadius - marginTopAndBottom;
        float progressLeft = getProgress2dp(progress);
        //画进度条背景色
        canvas.drawRect(marginLeft, progressTop, getWidth() - marginRight, progressTop + progressHeight, progressPoint);
        // 画进度
        canvas.drawRect(marginLeft, progressTop, progressLeft, progressTop + progressHeight, progressSpendPoint);

        if (bitmap == null) {
            // 画圆点
            canvas.drawCircle(progressLeft, progressTop + progressHeight / 2, innerRadius, progressSpendPoint);
            canvas.drawCircle(progressLeft, progressTop + progressHeight / 2, outerRadius, circleHollowPoint);
        } else {
            canvas.drawBitmap(bitmap, progressLeft - bitmap.getWidth() / 2, progressTop + progressHeight / 2 - bitmap.getHeight() / 2, progressSpendPoint);
        }


        CurrentX = progressLeft;
        CurrentY = progressTop + progressHeight / 2;
        if (relativeSite != SITE.GONE) {
            String text = progress + unit;
            textPoint.getTextBounds(text, 0, text.length(), textRect);
            if (relativeSite == SITE.TOP_MOVE) {
                float textX = progressLeft - textRect.width() / 2;
                if ((progressLeft + textRect.width() - 10) > getWidth()) {
                    //在最右边
                    textX = getWidth() - textRect.width() - 10;
                } else if (textX < 10) {
                    // 在最左边
                    textX = 10;
                }
                canvas.drawText(text, textX - textRect.left, marginTopAndBottom - textRect.top, textPoint);
            } else {
                float textX = (getWidth() - textRect.width()) / 2;
                canvas.drawText(text, textX - textRect.left, marginTopAndBottom - textRect.top, textPoint);
            }
        }
    }

    private boolean isDragItem(float x, float y) {
        double dis = Math.sqrt(Math.pow(CurrentX - x, 2) + Math.pow(CurrentY - y, 2));
        if (dis < 60) {
            return true;
        }
        return false;
    }

    private float getProgress2dp(int progress) {
        return progress * (progressBarWidth) / maxProgress + marginLeft;
    }

    private int getDp2Progress(float dpx) {
        return (int) ((dpx - marginLeft) * maxProgress / progressBarWidth);
    }

    public interface OnProgressbarChangeListener {
        void onProgressChanged(LineProgressbar progressbar, int progress);

        void onDragging(LineProgressbar progressbar, int progress);
    }

    private float downX;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            boolean isDragButton = isDragItem(event.getX(), event.getY());
            if (isDragButton) {
                ENABLE_CTION_MOVE = true;
                downX = event.getX();
            } else {
                ENABLE_CTION_MOVE = false;
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (ENABLE_CTION_MOVE) {
                int localProgress = getDp2Progress(event.getX());
                if (event.getX() < downX) {
                    if (localProgress > minProgress) {
                        progress = localProgress;
                        CurrentX = event.getX();
                    } else {
                        progress = minProgress;
                        CurrentX = getProgress2dp(progress);
                    }
                } else if (event.getX() > downX) {
                    if (localProgress < maxProgress) {
                        progress = localProgress;
                        CurrentX = event.getX();
                    } else {
                        progress = maxProgress;
                        CurrentX = getProgress2dp(progress);
                    }
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mListener != null && ENABLE_CTION_MOVE) {
                mListener.onDragging(this, progress);
            }
            ENABLE_CTION_MOVE = false;
        }
        if (mListener != null) {
            mListener.onProgressChanged(this, progress);
        }
        invalidate();
        return true;
    }

    //======================================================
    // 对外方法

    /**
     * 设置内圈半径
     *
     * @param radius
     * @return
     */
    public LineProgressbar setInnerRadius(float radius) {
        if (outerRadius >= radius) {
            innerRadius = radius;
        }
        invalidate();
        return this;
    }

    /**
     * 设置外圈半径
     *
     * @param radius
     * @return
     */
    public LineProgressbar setOuterRadius(float radius) {
        if (innerRadius <= radius) {
            outerRadius = radius;
            invalidateLayout();
        }
        return this;
    }

    public LineProgressbar setPointImage(@IdRes int resId) {
        pointImageResId = resId;
        invalidateLayout();
        return this;
    }

    /**
     * 设置提示信息所在位置
     *
     * @param site
     */
    public LineProgressbar setRelativeSite(SITE site) {
        if (relativeSite != site) {
            relativeSite = site;
            invalidateLayout();
        }
        return this;
    }

    /**
     * 设置进度
     *
     * @param progress
     * @return
     */
    public LineProgressbar setProgress(int progress) {
        this.progress = progress;
        invalidate();
        return this;
    }

    /**
     * 设置进度条背景颜色
     *
     * @param color
     * @return
     */
    public LineProgressbar setProgressBgColor(int color) {
        this.progressbgColor = color;
        progressPoint.setColor(color);
        invalidate();
        return this;
    }

    /**
     * 设置进度区域前景色
     *
     * @param color
     * @return
     */
    public LineProgressbar setProgressSpendColor(int color) {
        this.progressSpendColor = color;
        progressSpendPoint.setColor(color);
        invalidate();
        return this;
    }

    /**
     * 设置提示文字的字体大小
     *
     * @param size
     * @return
     */
    public LineProgressbar setTextSize(float size) {
        this.textPointSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, size, getResources().getDisplayMetrics());
        textPoint.setTextSize(textPointSize);
        invalidateLayout();
        return this;
    }

    /**
     * 设置提示文字颜色
     *
     * @param color
     * @return
     */
    public LineProgressbar setTextColor(int color) {
        this.textPointColor = color;
        textPoint.setColor(color);
        invalidate();
        return this;
    }

    /**
     * 设置单位
     *
     * @param unit
     * @return
     */
    public LineProgressbar setUnit(String unit) {
        this.unit = unit;
        invalidate();
        return this;
    }

    /**
     * 设置最大值
     *
     * @param progress
     * @return
     */
    public LineProgressbar setMaxProgress(int progress) {
        this.maxProgress = progress;
        if (this.progress > progress) {
            this.progress = progress;
        }
        invalidate();
        return this;
    }

    /**
     * 设置最小值
     *
     * @param progress
     * @return
     */
    public LineProgressbar setMinProgress(int progress) {
        this.minProgress = progress;
        if (this.progress < progress) {
            this.progress = progress;
        }
        invalidate();
        return this;
    }
}
