package com.yss.securityscananimator;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by Administrator on 2017/3/29.
 */

public class SecurityScanView extends View {
    private Paint mArcPaint;//最外层圆的画笔
    private Paint mSectorPaint;//扇形的画笔
    private Paint mSpotPaint;//点的画笔

    private float mWidth;//view的宽
    private float mHeight;
    private float radius;//半径
    private float minArcRadius = 0f;//小圆半径
    private float mSectorRadius = 0f;//扫描扇形半径
    private float mSectorAngle=-90f;//半径的开始的角度
    private float mSpotRadius=5;//点的半径
    private int mSpotRadiusAlpha=155;
    private int[][] mSpotCoordinate =new int[15][2];


    //中间圆的动画只有一次
    private boolean isFirstArcAmin = false;


    public SecurityScanView(Context context) {
        this(context, null);
    }

    public SecurityScanView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SecurityScanView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //wrap_content 时view的宽高
        int wrap_Len = 800;
        int width = measureDimension(wrap_Len, widthMeasureSpec);
        int height = measureDimension(wrap_Len, heightMeasureSpec);
        int len = Math.min(width, height);
        //保证是一个正方形
        setMeasuredDimension(len, len);

    }

    private void init() {
        initPaint();
    }

    private void initPaint() {
        mArcPaint = new Paint();
        mArcPaint.setColor(Color.WHITE);
        mArcPaint.setStrokeWidth(0.5f);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setAntiAlias(true);
        mArcPaint.setAlpha(155);

        mSpotPaint = new Paint();
        mSpotPaint.setColor(Color.WHITE);
        mSpotPaint.setStrokeWidth(0.5f);
        mSpotPaint.setAntiAlias(true);
        mSpotPaint.setAlpha(mSpotRadiusAlpha);

        mSectorPaint = new Paint();
        mSectorPaint.setStrokeWidth(0.5f);
        mSectorPaint.setAntiAlias(true);
        mSectorPaint.setAlpha(155);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mWidth = getWidth();
        mHeight = getHeight();
        radius = (mWidth - getPaddingLeft() - getPaddingRight()) / 2;//半径

        canvas.translate(mWidth / 2, mHeight / 2);
        drawArcView(canvas);

        //中间圆的动画只执行一次
        if (!isFirstArcAmin) {
            isFirstArcAmin = true;
            starAnim();
        }

    }

    public int measureDimension(int defaultSize, int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = defaultSize;   //UNSPECIFIED
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    public void starAnim() {
        arcAnimator();
    }

    private void arcAnimator() {
        float endRadius = radius / 3 * 2;
        ValueAnimator mMinArcAnim = ValueAnimator.ofFloat(minArcRadius, endRadius);
        mMinArcAnim.setInterpolator(new LinearInterpolator());
        mMinArcAnim.setDuration(400);
        mMinArcAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                minArcRadius = (float) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        mMinArcAnim.start();


        ValueAnimator mSectorAnim = ValueAnimator.ofFloat(mSectorRadius, radius);
        mSectorAnim.setInterpolator(new LinearInterpolator());
        mSectorAnim.setDuration(400);

        mSectorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                mSectorRadius = (float) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });

        final ValueAnimator mSectorTurnAnim = ValueAnimator.ofFloat(mSectorAngle, 270);
        mSectorTurnAnim.setInterpolator(new LinearInterpolator());
        mSectorTurnAnim.setDuration(1000);
        mSectorTurnAnim.setRepeatCount(3);
        mSectorTurnAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                mSectorAngle = (float) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        final ValueAnimator mSpotAnim = ValueAnimator.ofFloat(mSpotRadius, 20);
        mSpotAnim.setInterpolator(new LinearInterpolator());
        mSpotAnim.setDuration(1000);
        mSpotAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                mSpotRadius = (float) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        final ValueAnimator mSpotColorAnim = ValueAnimator.ofInt(mSpotRadiusAlpha, 0);
        mSpotColorAnim.setInterpolator(new LinearInterpolator());
        mSpotColorAnim.setDuration(1000);
        mSpotColorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                mSpotRadiusAlpha = (Integer) valueAnimator.getAnimatedValue();
                Log.e("mSpotRadiusAlpha:",mSpotRadiusAlpha+"");
                postInvalidate();
            }
        });
        mSectorAnim.addListener(new ValueAnimator.AnimatorListener(){

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mSectorTurnAnim.start();
                mSpotAnim.start();
                mSpotColorAnim.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mSectorAnim.start();



    }


    //画最外层的圆 中间的线
    private void drawArcView(Canvas canvas) {
        canvas.drawCircle(0, 0, radius, mArcPaint);
        canvas.drawLine(-radius, 0f, radius, 0f, mArcPaint);
        canvas.drawLine(0f, -radius, 0f, radius, mArcPaint);
        if (minArcRadius != 0) {
            canvas.drawCircle(0, 0, minArcRadius, mArcPaint);
        }
        if (mSectorRadius != 0) {
            Shader mShader = new RadialGradient(0, 0, mSectorRadius,
                    new int[] {  Color.TRANSPARENT,Color.TRANSPARENT, 0x66ffffff}, null, Shader.TileMode.REPEAT); // 一个材质,打造出一个线性梯度沿著一条线。
            mSectorPaint.setShader(mShader);
            RectF oval2 = new RectF(-mSectorRadius, -mSectorRadius, mSectorRadius,mSectorRadius);// 设置个新的长方形，扫描测量
            canvas.drawArc(oval2, mSectorAngle, 90, true, mSectorPaint);

        }
        mSpotPaint.setAlpha(mSpotRadiusAlpha);
        canvas.drawCircle(30,40,mSpotRadius,mSpotPaint);



    }
}
