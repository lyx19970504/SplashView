package com.example.splashview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

public class SplashView2 extends View {

    private static final String TAG = "SplashView";
    private float mCenterX;
    private float mCenterY;

    private float msCircleRadius = 18;  //小圆半径
    private float mlCircleRadius = 90;  //大圆半径

    private ValueAnimator mValueAnimator; //动画

    private float mCurrentRadius = mlCircleRadius;
    private Paint mHolePaint;


    private int[] mCircleColors;
    private int mBackgroundColor = Color.WHITE;
    private long mAnimate_DURATION = 1200;  //动画持续时间
    private float angle;   //旋转角度
    private float mCurrentHoleRadius = 0;  //扩展圆半径
    private float mDistance; //扩展圆半径最大距离

    private Paint mPaint;

    public SplashView2(Context context) {
        super(context);
        init();
    }

    public SplashView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SplashView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCircleColors = getResources().getIntArray(R.array.splash_circle_colors);
        mHolePaint.setStyle(Paint.Style.STROKE);
        mHolePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHolePaint.setColor(Color.WHITE);
        startRotateAnimation();   //开启小球旋转动画
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mCenterX = w / 2f;
        mCenterY = h / 2f;
        mDistance = (float) (Math.hypot(w,h) / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        rotate(canvas);
    }

    public void rotate(Canvas canvas){
        //画背景
        drawBackground(canvas);
        //画球
        float angle = (float) (Math.PI * 2 / mCircleColors.length);
        for (int i = 0; i < mCircleColors.length; i++) {
            float circleAngle = angle * i + this.angle;
            float cx = (float) (mCenterX + mCurrentRadius * Math.cos(circleAngle));
            float cy = (float) (mCenterY + mCurrentRadius * Math.sin(circleAngle));
            mPaint.setColor(mCircleColors[i]);

            canvas.drawCircle(cx,cy,msCircleRadius,mPaint);
        }
    }

    public void drawBackground(Canvas canvas){
        if(mCurrentHoleRadius > 0){
            float radius = mDistance / 2 + mCurrentHoleRadius;
            mHolePaint.setStrokeWidth(mDistance);
            canvas.drawCircle(mCenterX,mCenterY,radius,mHolePaint);
        }else {
            canvas.drawColor(mBackgroundColor);
        }
    }

    public void startRotateAnimation(){
        mValueAnimator = ValueAnimator.ofFloat(0, (float) (Math.PI * 2));
        mValueAnimator.setRepeatCount(2);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.setDuration(mAnimate_DURATION);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                angle = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        mValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //开启扩散动画
                startMergingAnimation();
            }
        });
        mValueAnimator.start();
    }

    public void startMergingAnimation(){
        mValueAnimator = ValueAnimator.ofFloat(msCircleRadius,mlCircleRadius);
        mValueAnimator.setDuration(mAnimate_DURATION);
        mValueAnimator.setInterpolator(new OvershootInterpolator(10f));
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mCurrentRadius = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        mValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //开启水波纹动画
                startExpandAnimation();
            }
        });
        mValueAnimator.reverse();
    }

    public void startExpandAnimation(){
        mValueAnimator = ValueAnimator.ofFloat(msCircleRadius, mDistance);
        mValueAnimator.setDuration(mAnimate_DURATION);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mCurrentHoleRadius = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        mValueAnimator.start();
    }
}
