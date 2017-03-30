package com.yss.securityscananimator;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/29.
 */

public class SecurityScanView extends View {
    private Paint mArcPaint;//最外层圆的画笔
    private Paint mSectorPaint;//四分之一扫描扇形的画笔
    private Paint mSpotPaint;//点的画笔
    private Paint mWideArcPaint;//扫描结束后内环圆的画笔
    private Paint mResultArcPaint;//扫描结束后显示结果的百分比圆
    private Paint mTextPaint;//数字的画笔
    private Paint mUnitPaint;//单位的画笔

    private float mWidth;//view的宽
    private float mHeight;
    private float radius;//半径
    private float minArcRadius = 0f;//小圆半径
    private float mSectorRadius = 0f;//四分之一扫描扇形半径
    private float mSectorAngle=-90f;//四分之一扫描扇形半径的开始的角度
    private float mSpotRadius=0;//点的半径
    private int mSpotRadiusAlpha=150;//点的透明度
    private int   mSpotNum;//当前点的个数

    private List<Spot> mSpotLists=new ArrayList<>();
    private boolean mIsScanEnd =false;//是否扫描结束
    private float mArcSpac =40f ;//扫描后 两个圆的间距
    private float mWidthRadius;//扫描结束后内环圆的角度
    private float mPercentageRadius;//扫描结束后显示的结果百分比角度
    private boolean mIsWidthRadiusAminEnd = false;//扫描结束后内环圆是否画完
    private boolean mIsAminEnd = false ;//所有的动画是否结束

    private int mPercentageAngle = 120;//扫描结果的分数所占度角
    private int mPercentage = 100 ;//扫描结果的分数

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
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mWidth = getWidth();
        mHeight = getHeight();
        radius = (mWidth - getPaddingLeft() - getPaddingRight()) / 2;//半径

        canvas.translate(mWidth / 2, mHeight / 2);
        //外层圆
        canvas.drawCircle(0, 0, radius, mArcPaint);
        //
        if(!mIsScanEnd){
            //画扫描之前的内容
            drawScanStar(canvas);
        }else {
            //画扫描之后的内容
            drawScanEnd(canvas);
        }
        //中间的文本
        drawText(canvas);

        //动画只开启一次
        if (!isFirstArcAmin) {
            isFirstArcAmin = true;
            starAnim();
            handler.sendEmptyMessageDelayed(0, 10);
        }

    }

   //启动handler，实现4秒定时循环执行
    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {

            if(!mIsAminEnd){
                //逻辑处理
                postInvalidate();
                handler.sendEmptyMessageDelayed(0,10);//15毫秒后再次执行
            }
        }
    };

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

        mWideArcPaint = new Paint();
        mWideArcPaint.setColor(Color.WHITE);
        mWideArcPaint.setStrokeWidth(20f);
        mWideArcPaint.setStyle(Paint.Style.STROKE);
        mWideArcPaint.setAntiAlias(true);
        mWideArcPaint.setAlpha(50);

        mResultArcPaint = new Paint();
        mResultArcPaint.setColor(Color.WHITE);
        mResultArcPaint.setStrokeWidth(20f);
        mResultArcPaint.setStyle(Paint.Style.STROKE);
        mResultArcPaint.setAntiAlias(true);
        mResultArcPaint.setAlpha(150);

        mSpotPaint = new Paint();
        mSpotPaint.setColor(Color.WHITE);
        mSpotPaint.setStrokeWidth(0.5f);
        mSpotPaint.setAntiAlias(true);
        mSpotPaint.setAlpha(mSpotRadiusAlpha);

        mSectorPaint = new Paint();
        mSectorPaint.setStrokeWidth(0.5f);
        mSectorPaint.setAntiAlias(true);
        mSectorPaint.setAlpha(155);

        mTextPaint = new Paint();
        mTextPaint.setStrokeWidth(20f);
        mTextPaint.setTextSize(200);
        mTextPaint.setColor(0xCCffffff);
        mResultArcPaint.setAlpha(150);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mUnitPaint = new Paint();
        mUnitPaint.setStrokeWidth(20f);
        mUnitPaint.setTextSize(40);
        mUnitPaint.setColor(0xCCffffff);
        mResultArcPaint.setAlpha(150);
        mUnitPaint.setTextAlign(Paint.Align.RIGHT);

    }


    private void drawScanStar(Canvas canvas){
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
        for (int i=0;i<mSpotLists.size();i++){
            mSpotPaint.setAlpha(mSpotLists.get(i).getSpotAlpha(mSpotRadiusAlpha));
            canvas.drawCircle(mSpotLists.get(i).getX(),mSpotLists.get(i).getY(),mSpotLists.get(i).getSpotRadius(mSpotRadius),mSpotPaint);
        }
    }
    private void drawScanEnd(Canvas canvas){
        RectF oval2 = new RectF(-radius+mArcSpac, -radius+mArcSpac, radius-mArcSpac,radius-mArcSpac);
        // 设置个新的长方形，扫描测量
        if(!mIsWidthRadiusAminEnd){
            canvas.drawArc(oval2, -90, mWidthRadius, false, mWideArcPaint);

        }else {
            canvas.drawCircle(0, 0, radius-mArcSpac, mWideArcPaint);
//
////                int colorSweep[] = {0x80ffffff, 0x66ffffff };
//                int colorSweep[] = {Color.WHITE, 0x66ffffff };
////                int colorSweep[] = {0x66ffffff,0x66ffffff,Color.WHITE };
//                float position[]={0.3f,0.9f};
//                mResultArcPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
//                SweepGradient sweepGradient=new SweepGradient(0,0, colorSweep, null);
//                mResultArcPaint.setShader(sweepGradient);

            canvas.drawArc(oval2, -90, mPercentageRadius, false, mResultArcPaint);

        }
        drawShortScaleLine(canvas);
    }


    private void drawText(Canvas canvas){
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(mPercentage+"", 0, (mPercentage+"").length(), bounds);
        canvas.drawText(mPercentage+"",0,bounds.height()/2,mTextPaint);
        Rect unitBounds = new Rect();
        mUnitPaint.getTextBounds("分", 0, ("分").length(), unitBounds);
        canvas.drawText(mPercentage+"",0,bounds.height()/2,mTextPaint);
        canvas.drawText("分",bounds.width()/2 +unitBounds.width()*2,-bounds.height()/2 +unitBounds.height(),mUnitPaint);
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

    private void starAnim() {
        //中间圆的从小变大的动画 四分之一扇形由小变大的动画 两个动画同时进行
        starSectorAndArcAnim();

    }
    public void setmPercentage(int percentage){
        this.mPercentageAngle = percentage;
    }
    //中间圆的从小变大的动画 四分之一扇形由小变大的动画 两个动画同时进行
    private  void starSectorAndArcAnim(){
        float endRadius = radius / 3 * 2;
        ValueAnimator mMinArcAnim = ValueAnimator.ofFloat(minArcRadius, endRadius);
        mMinArcAnim.setInterpolator(new LinearInterpolator());
        mMinArcAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                minArcRadius = (float) valueAnimator.getAnimatedValue();
            }
        });

        ValueAnimator mSectorAnim = ValueAnimator.ofFloat(mSectorRadius, radius);
        mSectorAnim.setInterpolator(new LinearInterpolator());
        mSectorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                mSectorRadius = (float) valueAnimator.getAnimatedValue();
            }
        });
        mSectorAnim.addListener(new ValueAnimator.AnimatorListener(){

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // 小扇形的旋转 小点的变大 颜色渐变动画 字的变化
                randomSpotCoodrnate();
                starSectorTurnAnim();
                starSpotAnim();
                starTextAmin();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                mIsWidthRadiusAminEnd =true;
            }
        });

        AnimatorSet  animatorSet =new AnimatorSet();
        animatorSet.play(mSectorAnim).with(mMinArcAnim);
        animatorSet.setDuration(400);
        animatorSet.start();
    }
    //小点的变大 颜色渐变动画
    private void starSpotAnim(){
        final ValueAnimator mSpotAnim = ValueAnimator.ofFloat(mSpotRadius, 20);
        mSpotAnim.setInterpolator(new LinearInterpolator());
        mSpotAnim.setDuration(2000);
        mSpotAnim.setRepeatCount(1);
        mSpotAnim.addListener(new ValueAnimator.AnimatorListener(){

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                mSpotLists.clear();
                randomSpotCoodrnate();

            }
        });
        mSpotAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                mSpotRadius = (float) valueAnimator.getAnimatedValue();

            }
        });
        final ValueAnimator mSpotColorAnim = ValueAnimator.ofInt(mSpotRadiusAlpha, 0);
        mSpotColorAnim.setInterpolator(new LinearInterpolator());
        mSpotColorAnim.setDuration(2000);
        mSpotColorAnim.setRepeatCount(1);
        mSpotColorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                mSpotRadiusAlpha = (Integer) valueAnimator.getAnimatedValue();

            }
        });
        AnimatorSet animatorSet =new AnimatorSet();
        animatorSet.setDuration(2000);
        animatorSet.play(mSpotAnim).with(mSpotColorAnim);
        animatorSet.start();
    }
    //四分之一扇形旋转的动画
    private void starSectorTurnAnim(){
        final ValueAnimator mSectorTurnAnim = ValueAnimator.ofFloat(mSectorAngle, 270);
        mSectorTurnAnim.setInterpolator(new LinearInterpolator());
        mSectorTurnAnim.setDuration(1000);
        mSectorTurnAnim.setRepeatCount(3);
        mSectorTurnAnim.addListener(new ValueAnimator.AnimatorListener(){

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //扫描结束显示结果 开始结果的动画
                mIsScanEnd = true;
                starResultAnim();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mSectorTurnAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                mSectorAngle = (float) valueAnimator.getAnimatedValue();

            }
        });
        mSectorTurnAnim.start();

    }

    //数字变化的动画
    private void starTextAmin(){
        final int num =100-mPercentageAngle*100/360;
        Log.e("num:",num+"");
        ValueAnimator mMinArcAnim = ValueAnimator.ofInt(mPercentage, num);
        mMinArcAnim.setInterpolator(new LinearInterpolator());
        mMinArcAnim.setDuration(5000);
        mMinArcAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                int percentage =(Integer) valueAnimator.getAnimatedValue();
                if(percentage == num || percentage%4 == 0){
                    mPercentage= percentage;

                }

            }
        });
        mMinArcAnim.start();

    }
//扫描结束后的动画
    private void starResultAnim(){
        final ValueAnimator percentRadiusAnim = ValueAnimator.ofFloat(mPercentageRadius, mPercentageAngle);
        percentRadiusAnim.setInterpolator(new LinearInterpolator());
        percentRadiusAnim.setDuration(1000);
        percentRadiusAnim.addListener(new ValueAnimator.AnimatorListener(){

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAminEnd = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        percentRadiusAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                mPercentageRadius = (float) valueAnimator.getAnimatedValue();

            }
        });


        ValueAnimator widthRadiusAnim = ValueAnimator.ofFloat(mWidthRadius, 360);
        widthRadiusAnim.setInterpolator(new LinearInterpolator());
        widthRadiusAnim.setDuration(1000);

        widthRadiusAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                mWidthRadius = (float) valueAnimator.getAnimatedValue();

            }
        });
        widthRadiusAnim.addListener(new ValueAnimator.AnimatorListener(){

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsWidthRadiusAminEnd = true;
                percentRadiusAnim.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        widthRadiusAnim.start();



    }
    private void randomSpotCoodrnate(){
         mSpotNum =(int)(Math.random()*10f)+5;
        for (int i=0;i<mSpotNum;i++){
            Spot spot=new Spot(radius);
            mSpotLists.add(spot);
        }
    }


    //画短刻度
    private void drawShortScaleLine(Canvas canvas){
        float length = 20f;
        canvas.drawLine(radius - 80 ,0,radius-60,0,mArcPaint);

        if(mWidthRadius >= 270){
            canvas.drawLine(0,-radius + 80 ,0,-radius+60,mArcPaint);
            canvas.drawLine(radius - 80 ,0,radius-60,0,mArcPaint);
            canvas.drawLine(0,radius - 80 ,0,radius-60,mArcPaint);
            canvas.drawLine(-radius + 80 ,0,-radius+60,0,mArcPaint);

        }else if(mWidthRadius >=180){
            canvas.drawLine(0,-radius + 80 ,0,-radius+60,mArcPaint);
            canvas.drawLine(radius - 80 ,0,radius-60,0,mArcPaint);
            canvas.drawLine(0,radius - 80 ,0,radius-60,mArcPaint);

        }else if(mWidthRadius >=90){
            canvas.drawLine(0,-radius + 80 ,0,-radius+60,mArcPaint);
            canvas.drawLine(radius - 80 ,0,radius-60,0,mArcPaint);

        }else {
            canvas.drawLine(0,-radius + 80 ,0,-radius+60,mArcPaint);

        }

    }

    class Spot{
        //点的位置 圆内的点
        // 半径  最大半径 20 15 10
         //透明度 起始透明度 200 150 100
        int x;
        int y;
        private int[] mMaxRadius={20 ,15 ,10 };
        private int[] mStartAlpha={200 ,150 ,100};
        private int mRadius;

        private float pi = 3.1415926f;
        private int mAlpha;
        public Spot(float radius){

            randomXY(radius);
            setSpotPaintAlpha();
            setSpotRadius();

        }
        private void randomXY(float radius){
            //随机半径50 - 100范围内
            int r= (int)(Math.random()*(radius - 20)+10);
            int angle = (int)(Math.random()*360);
            this.x =(int)( Math.sin(  angle * pi / 180  ) * r);
            this.y = (int)(Math.cos(  angle * pi / 180) * r);
        }

        public int getY() {
            return y;
        }
        public int getX() {
            return x;
        }




        private void  setSpotPaintAlpha() {
            this.mAlpha = mStartAlpha[(int)(Math.random()*3)];
        }



        private void  setSpotRadius() {

            this.mRadius = mMaxRadius[(int)(Math.random()*3)];
        }
        public float getSpotRadius(float spotRadius){
            if(spotRadius<mRadius){
                return spotRadius;
            }else {
                return mRadius;
            }

        }
        public int getSpotAlpha(int spotAlpha){
            if(spotAlpha<mAlpha){
                return spotAlpha;
            }else {
                return mAlpha;
            }

        }
    }
}
