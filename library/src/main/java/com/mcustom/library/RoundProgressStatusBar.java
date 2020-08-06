package com.mcustom.library;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;

/**
 * @WYU-WIN
 * @date 2020/6/24 2031.
 * description：圆形进度圈，带完成状态
 */
public class RoundProgressStatusBar extends View {
    private String TAG = RoundProgressStatusBar.class.getSimpleName();

    private enum StatusEnum {
        Loading,
        LoadSuccess,
        LoadFailure,
        LoadHeartbeat
    }

    private StatusEnum mStatus = StatusEnum.Loading;     //状态
    /**
     * 画笔对象的引用
     */
    private Paint roundPaint;
    private Paint roundProgressPaint;
    private Paint progressTextPaint;
    private Paint percentPaint;
    private Paint heartbeatPaint;
    /**
     * 圆环的颜色
     */
    private int roundColor = 0xffD1D1D1;

    /**
     * 圆环进度的颜色
     */
    private int roundProgressColor = Color.GREEN;

    /**
     * 中间进度百分比的字符串的颜色
     */
    private int textColor = 0x333333;
    private int percentColor = 0xff696969;
    private int heartbeatColor = 0xff25d1d3;
    private int successColor = Color.WHITE;
    private int failedColor = Color.RED;
    /**
     * 中间进度百分比的字符串的字体
     */
    private float textSize = value2PxSize(15);
    private float percentSize = value2PxSize(9);
    private float statusStrokeWidth = value2PxSize(6);
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
    private int progress = 0;
    /**
     * 是否显示中间的进度
     */
    private boolean textIsDisplayable;

    /**
     * 进度的风格，实心或者空心
     */
    private int style;

    public static final int STROKE = 0;
    public static final int FILL = 1;

    private Paint statusStrokePaint;
    private ValueAnimator circleAnimator;
    private float circleValue;
    private float successValue;
    private float failValueRight;
    private float failValueLeft;
    //追踪Path的坐标
    private PathMeasure mPathMeasure;
    //画圆的Path
    private Path mPathCircle;
    //截取PathMeasure中的path
    private Path mPathCircleDst;
    private Path successPath;
    private Path failurePathLeft;
    private Path failurePathRight;

    public RoundProgressStatusBar(Context context) {
        this(context, null);
    }

    public RoundProgressStatusBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressStatusBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (attrs != null) {
            TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar);

            //获取自定义属性和默认值
            roundColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor, roundColor);
            roundProgressColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor, roundProgressColor);
            textColor = mTypedArray.getColor(R.styleable.RoundProgressBar_android_textColor, textColor);
            textSize = mTypedArray.getDimension(R.styleable.RoundProgressBar_android_textSize, textSize);
            percentColor = mTypedArray.getColor(R.styleable.RoundProgressBar_percentColor, percentColor);
            percentSize = mTypedArray.getDimension(R.styleable.RoundProgressBar_percentSize, percentSize);
            heartbeatColor = mTypedArray.getColor(R.styleable.RoundProgressBar_heartbeatColor, heartbeatColor);

            successColor = mTypedArray.getColor(R.styleable.RoundProgressBar_successColor, successColor);
            failedColor = mTypedArray.getColor(R.styleable.RoundProgressBar_failedColor, failedColor);
            statusStrokeWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_statusStrokeWidth, 6);

            percentSize = mTypedArray.getDimension(R.styleable.RoundProgressBar_percentSize, percentSize);
            roundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, 5);
            max = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 100);
            textIsDisplayable = mTypedArray.getBoolean(R.styleable.RoundProgressBar_textIsDisplayable, true);
            style = mTypedArray.getInt(R.styleable.RoundProgressBar_style, 0);
            mTypedArray.recycle();
        }

        Log.e("RoundProgressBar", " , textSize : " + textSize + " , percentSize : " + percentSize);

        // 圆环背景色
        roundPaint = new Paint();
        roundPaint.setColor(roundColor); //设置圆环的颜色
        roundPaint.setStyle(Paint.Style.STROKE); //设置空心
        roundPaint.setStrokeWidth(roundWidth); //设置圆环的宽度
        roundPaint.setAntiAlias(true);  //消除锯齿

        // 圆环前景色
        roundProgressPaint = new Paint();
        roundProgressPaint.setColor(roundProgressColor); //设置圆环的颜色
        roundProgressPaint.setStyle(Paint.Style.STROKE); //设置空心
        roundProgressPaint.setStrokeWidth(roundWidth); //设置圆环的宽度
        roundProgressPaint.setAntiAlias(true);  //消除锯齿

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


        heartbeatPaint = new Paint();
        heartbeatPaint.setColor(heartbeatColor);
        heartbeatPaint.setStyle(Paint.Style.FILL);
        heartbeatPaint.setStrokeWidth(1);
        heartbeatPaint.setStrokeJoin(Paint.Join.ROUND);
        heartbeatPaint.setStrokeCap(Paint.Cap.ROUND);
        heartbeatPaint.setAntiAlias(true);

        statusStrokePaint = new Paint();
        statusStrokePaint.setStyle(Paint.Style.STROKE);
        statusStrokePaint.setDither(true);
        statusStrokePaint.setAntiAlias(true);
        statusStrokePaint.setStrokeWidth(statusStrokeWidth);
        statusStrokePaint.setStrokeCap(Paint.Cap.ROUND);    //设置画笔为圆角笔触
        percentPaint.setAntiAlias(true);

        initPath();
        initAnim();
    }

    private void initPath() {
        mPathCircle = new Path();
        mPathMeasure = new PathMeasure();
        mPathCircleDst = new Path();
        successPath = new Path();
        failurePathLeft = new Path();
        failurePathRight = new Path();
    }

    private void initAnim() {
        circleAnimator = ValueAnimator.ofFloat(0, 1);
        circleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                circleValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
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
        int centre = getWidth() / 2; //获取圆心的x坐标
        int radius = (int) (centre - roundWidth / 2); //圆环的半径

        if (mStatus == StatusEnum.Loading) {
            /**
             * 画进度外圈圆背景色
             */
            canvas.drawCircle(centre, centre, radius, roundPaint); //画出圆环

            /**
             * 画进度外圈圆前景色，区域与进度等同
             */
            RectF oval = new RectF(centre - radius, centre - radius, centre + radius, centre + radius);  //用于定义的圆弧的形状和大小的界限
            switch (style) {
                case STROKE: {
                    // 空心进度
                    roundProgressPaint.setStyle(Paint.Style.STROKE);
                    canvas.drawArc(oval, 0, 360 * progress / max, false, roundProgressPaint);  //根据进度画圆弧
                    break;
                }
                case FILL: {
                    // 实心进度
                    roundProgressPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                    if (progress != 0)
                        canvas.drawArc(oval, 0, 360 * progress / max, true, roundProgressPaint);  //根据进度画圆弧
                    break;
                }
            }


            int percent = (int) (((float) progress / (float) max) * 100);  //中间的进度百分比，先转换成float在进行除法运算，不然都为0
            Rect bounds = new Rect();
            String percentText = percent + "";
            progressTextPaint.getTextBounds(percentText, 0, percentText.length(), bounds);

            if (textIsDisplayable && percent != 0 && style == STROKE) {
                canvas.drawText(percent + "", centre - bounds.width() / 2 - bounds.left, centre + bounds.height() / 2 - bounds.bottom, progressTextPaint); //画出进度百分比
            }
            canvas.drawText("%", centre + percentBounds.width() / 2, centre + percentBounds.height(), percentPaint);

        } else if (mStatus == StatusEnum.LoadHeartbeat) {
            canvas.drawCircle(centre, centre, (float) (radius * 0.8), heartbeatPaint); //最终底色
        } else if (mStatus == StatusEnum.LoadSuccess) {
            canvas.drawCircle(centre, centre, (float) (radius * 0.8), heartbeatPaint); //最终底色
            statusStrokePaint.setColor(successColor);
            successPath.moveTo(getWidth() / 8 * 3, getWidth() / 2);
            successPath.lineTo(getWidth() / 2, getWidth() / 5 * 3);
            successPath.lineTo(getWidth() / 3 * 2, getWidth() / 5 * 2);
            mPathMeasure.nextContour();
            mPathMeasure.setPath(successPath, false);
            mPathMeasure.getSegment(0, successValue * mPathMeasure.getLength(), mPathCircleDst, true);
            canvas.drawPath(mPathCircleDst, statusStrokePaint);
        } else if (mStatus == StatusEnum.LoadFailure) {
            canvas.drawCircle(centre, centre, (float) (radius * 0.8), heartbeatPaint); //最终底色
            statusStrokePaint.setColor(failedColor);
            failurePathRight.moveTo(getWidth() / 3 * 2, getWidth() / 3);
            failurePathRight.lineTo(getWidth() / 3, getWidth() / 3 * 2);
            mPathMeasure.nextContour();
            mPathMeasure.setPath(failurePathRight, false);
            mPathMeasure.getSegment(0, failValueRight * mPathMeasure.getLength(), mPathCircleDst, true);
            canvas.drawPath(mPathCircleDst, statusStrokePaint);
            if (failValueRight == 1) {    //表示叉叉的右边部分画完了,可以画叉叉的左边部分
                failurePathLeft.moveTo(getWidth() / 3, getWidth() / 3);
                failurePathLeft.lineTo(getWidth() / 3 * 2, getWidth() / 3 * 2);
                mPathMeasure.nextContour();
                mPathMeasure.setPath(failurePathLeft, false);
                mPathMeasure.getSegment(0, failValueLeft * mPathMeasure.getLength(), mPathCircleDst, true);
                canvas.drawPath(mPathCircleDst, statusStrokePaint);
            }
        } else {

        }
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
            loadState = 0;
            this.progress = progress;
            mStatus = StatusEnum.Loading;
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

    //重制路径
    private void resetPath() {
        successValue = 0;
        circleValue = 0;
        failValueLeft = 0;
        failValueRight = 0;
        mPathCircle.reset();
        mPathCircleDst.reset();
        failurePathLeft.reset();
        failurePathRight.reset();
        successPath.reset();
    }

    private void startSuccessAnim() {
        resetPath();
        ValueAnimator success = ValueAnimator.ofFloat(0f, 1.0f);
        success.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                successValue = (float) animation.getAnimatedValue();

                Log.i(TAG, "successValue : " + successValue + " mStatus : " + mStatus);
                mStatus = StatusEnum.LoadSuccess;
                invalidate();
            }
        });

        //组合动画,一先一后执行
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(success).after(circleAnimator);
        animatorSet.setDuration(400);
        animatorSet.start();
    }

    private void startFailAnim() {
        resetPath();
        ValueAnimator failLeft = ValueAnimator.ofFloat(0f, 1.0f);
        failLeft.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                failValueRight = (float) animation.getAnimatedValue();
                mStatus = StatusEnum.LoadFailure;
                invalidate();
            }
        });
        ValueAnimator failRight = ValueAnimator.ofFloat(0f, 1.0f);
        failRight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                failValueLeft = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        //组合动画,一先一后执行
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(failLeft).after(circleAnimator).before(failRight);
        animatorSet.setDuration(400);
        animatorSet.start();
    }


    // 按钮模拟心脏跳动
    int times = 0;

    private void playHeartbeatAnimation() {
        times += 1;
        mStatus = StatusEnum.LoadHeartbeat;
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(new ScaleAnimation(1.0f, 0.9f, 1.0f, 0.9f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f));
        animationSet.addAnimation(new AlphaAnimation(1.0f, 0.4f));
        animationSet.setDuration(50);
        animationSet.setInterpolator(new AccelerateInterpolator());
        animationSet.setFillAfter(true);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                AnimationSet animationSet = new AnimationSet(true);
                animationSet.addAnimation(new ScaleAnimation(0.9f, 1.0f, 0.9f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f));
                animationSet.addAnimation(new AlphaAnimation(0.4f, 1.0f));

                animationSet.setDuration(150);
                animationSet.setAnimationListener(new ShouSuo());
                animationSet.setInterpolator(new DecelerateInterpolator());
                animationSet.setFillAfter(false);

                // 实现心跳的View
                startAnimation(animationSet);
            }
        });

        // 实现心跳的View
        startAnimation(animationSet);
    }

    public class ShouSuo implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Log.i("ShouSuo", "ShouSuo onAnimationEnd");
            if (times < 2) {
                playHeartbeatAnimation();
                if (loadState == 1) {
                    startSuccessAnim();
                } else {
                    startFailAnim();
                }
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    int loadState = 0;
    private int localProgress = 0;
    private float ratioBase = 0;
    private float sProgress = 0;

    public void loadSuccess() {
        loadState = 1;
        times = 0;
        if (progress <= 95) {
            localProgress = progress;
            ratioBase = (100f - localProgress) / 100f;
            sProgress = 0;
            ValueAnimator progressAnimator = ValueAnimator.ofFloat(0, 1);
            progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    sProgress = (float) animation.getAnimatedValue();
                    progress = (int) (localProgress + ratioBase * sProgress * 100);
                    if (sProgress == 1) {
                        progress = 100;
                        playHeartbeatAnimation();
                    }
                    Log.i(TAG, "loadSuccess progress: " + progress + ", sProgress : " + sProgress);
                    invalidate();
                }
            });

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(progressAnimator);
            animatorSet.setDuration(500);
            animatorSet.start();
        } else {
            playHeartbeatAnimation();
        }
    }

    public void loadFailure() {
        loadState = 2;
        times = 0;
        playHeartbeatAnimation();
    }
}
