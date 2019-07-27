package com.example.splashview

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.animation.addListener
import java.util.*
import kotlin.collections.ArrayList

class SplashView3: View{

    private var mPaint:Paint
    private var mHolePaint:Paint
    private val mCircleRadius:Float = 18f
    private val mBigRadius:Float = 90f
    private var mCenterX: Float = 0.0f
    private var mCenterY: Float = 0.0f
    private var mColorList:IntArray
    private lateinit var mValueAnimator: ValueAnimator
    private var mCurrentAngle:Float = 0.0f
    private var mDefaultDuration:Long = 1200L
    private var mCurrentRadius:Float = mBigRadius
    private var mDistance:Float = 0f
    private var mHoleRadius = 0f

    constructor(context: Context):super(context)
    constructor(context: Context,attributeSet: AttributeSet):super(context,attributeSet)
    constructor(context: Context,attributeSet: AttributeSet,int: Int):super(context,attributeSet,int)

    init {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mHolePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mHolePaint.style = Paint.Style.STROKE
        mHolePaint.color = Color.WHITE
        mColorList = resources.getIntArray(R.array.splash_circle_colors)

        mValueAnimator = ValueAnimator.ofFloat(0f,(Math.PI * 2).toFloat())
        mValueAnimator.repeatCount = 2
        mValueAnimator.duration = mDefaultDuration
        mValueAnimator.interpolator = LinearInterpolator()
        mValueAnimator.addUpdateListener(ValueAnimator.AnimatorUpdateListener {
            mCurrentAngle = it.animatedValue as Float
            invalidate()
        })
        mValueAnimator.addListener(object : AnimatorListener{
            override fun onAnimationStart(p0: Animator?) {
            }
            override fun onAnimationEnd(p0: Animator?) {
                startMergingAnimation()
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationRepeat(p0: Animator?) {
            }
        })
        mValueAnimator.start()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        startDraw(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCenterX = w / 2f
        mCenterY = h / 2f
        mDistance = (Math.hypot(w.toDouble(),h.toDouble()) / 2).toFloat()
    }

    fun startDraw(canvas: Canvas?){
        if(mHoleRadius > 0){
            val radius = mDistance / 2 + mHoleRadius
            mHolePaint.strokeWidth = mDistance
            canvas?.drawCircle(mCenterX,mCenterY,radius,mHolePaint)
        }else {
            canvas?.drawColor(Color.WHITE)
            drawCircles(canvas)
        }
    }

    fun drawCircles(canvas: Canvas?){
        var angle = Math.PI * 2 / mColorList.size

        for ((index,it) in mColorList.withIndex()){
            val circleAngle = angle * index + mCurrentAngle
            val x = (mCenterX.plus(mCurrentRadius *  Math.cos(circleAngle).toFloat()))
            val y = (mCenterY.plus(mCurrentRadius * Math.sin(circleAngle).toFloat()))
            mPaint.color = it
            canvas?.drawCircle(x,y,mCircleRadius,mPaint)
        }
    }

    fun startMergingAnimation(){
        mValueAnimator = ValueAnimator.ofFloat(mCircleRadius,mBigRadius)
        mValueAnimator.duration = 1200
        mValueAnimator.interpolator = OvershootInterpolator(10f)
        mValueAnimator.addUpdateListener(ValueAnimator.AnimatorUpdateListener {
            mCurrentRadius = it.animatedValue as Float
            invalidate()
        })
        mValueAnimator.addListener(object : Animator.AnimatorListener{
            override fun onAnimationStart(p0: Animator?) {
            }
            override fun onAnimationEnd(p0: Animator?) {
                startExpandAnimation()
            }
            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationRepeat(p0: Animator?) {
            }
        })
        mValueAnimator.reverse()
    }

    fun startExpandAnimation(){
        mValueAnimator = ValueAnimator.ofFloat(mCircleRadius,mDistance)
        mValueAnimator.interpolator = LinearInterpolator()
        mValueAnimator.duration = mDefaultDuration
        mValueAnimator.addUpdateListener(ValueAnimator.AnimatorUpdateListener {
            mHoleRadius = it.animatedValue as Float
            invalidate()
        })
        mValueAnimator.start()
    }
}
