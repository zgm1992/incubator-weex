package com.meiju.weex.componentView;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by zhudp on 2018-7-19.
 */

public class CircleRotateLoadingView extends View {
    //小球在不同位置的半径状况
    private static final int MAX_BALL_RADIUS = 75;
    private static final int MID_BALL_RADIUS = 50;
    private static final int MIN_BALL_RADIUS = 25;

    //小球移动的距离(与属性动画相对应)
    private static final int MOVE_DISTANCE = 300;
    //默认的动画执行时间
    private static final int DEFAULT_ANIM_DO_TIME = 5000;

    // 默认第一个小球颜色
    private final static int DEFAULT_LEFT_BALL_COLOR = Color.parseColor("#C8E3FC");
    // 默认第二个小球颜色
    private final static int DEFAULT_RIGHT_BALL_COLOR = Color.parseColor("#46A0F5");

    //控件的中心点
    private float centerX;
    private float centerY;

    //左右两侧小球的画笔
    private Paint mLeftPaint;
    private Paint mRightPaint;

    //左右两个小球的数据类
    private Ball mLeftBall;
    private Ball mRightBall;

    private AnimatorSet mAnimatorSet;

    private int mAnimDoTime = DEFAULT_ANIM_DO_TIME;

    private int mMoveDistance = MOVE_DISTANCE;

    private int mLeftBallColor = DEFAULT_LEFT_BALL_COLOR;
    private int mRightBallColor = DEFAULT_RIGHT_BALL_COLOR;

    public CircleRotateLoadingView(Context context) {
        this(context, null);
    }

    public CircleRotateLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleRotateLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mLeftPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLeftPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mLeftPaint.setStrokeWidth(5);

        mRightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRightPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mRightPaint.setStrokeWidth(5);

        mLeftBall = new Ball();
        mRightBall = new Ball();
        mLeftBall.setColor(mLeftBallColor);
        mRightBall.setColor(mRightBallColor);

        mLeftPaint.setColor(mLeftBall.color);
        mRightPaint.setColor(mRightBall.color);
        initAnim();
    }

    //设置小球的展示动画
    private void initAnim() {
        mAnimatorSet = new AnimatorSet();
        //左侧小球的大小变化分别为: mid --> max --> mid -->min -->mid
        ValueAnimator mOneScale = ObjectAnimator.ofInt(
                mLeftBall,
                "radius",
                MID_BALL_RADIUS, MAX_BALL_RADIUS, MID_BALL_RADIUS, MIN_BALL_RADIUS, MID_BALL_RADIUS);
        mOneScale.setRepeatCount(ValueAnimator.INFINITE);
        //左侧小球的位移变化通过改变圆心
        ValueAnimator mOneMove = ValueAnimator.ofFloat(-1, 0, 1, 0, -1);
        mOneMove.setRepeatCount(ValueAnimator.INFINITE);
        mOneMove.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                mLeftBall.setCenterX(centerX + value * mMoveDistance);
                invalidate();
            }
        });


        //右侧小球动画
        ValueAnimator mTwoScale = ObjectAnimator.ofInt(
                mRightBall,
                "radius",
                MID_BALL_RADIUS, MIN_BALL_RADIUS, MID_BALL_RADIUS, MAX_BALL_RADIUS, MID_BALL_RADIUS);
        mTwoScale.setRepeatCount(ValueAnimator.INFINITE);
        //右侧小球动画
        ValueAnimator mTwoMove = ValueAnimator.ofFloat(1, 0, -1, 0, 1);
        mTwoMove.setRepeatCount(ValueAnimator.INFINITE);
        mTwoMove.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                mRightBall.setCenterX(centerX + value * mMoveDistance);
                invalidate();
            }
        });

        mAnimatorSet.setDuration(mAnimDoTime);
        mAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimatorSet.playTogether(mOneScale, mOneMove, mTwoScale, mTwoMove);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //先画大的球,防止重叠
        if (mLeftBall.radius >= mRightBall.radius) {
            canvas.drawCircle(mRightBall.centerX, centerY, mRightBall.radius, mRightPaint);
            canvas.drawCircle(mLeftBall.centerX, centerY, mLeftBall.radius, mLeftPaint);
        } else {
            canvas.drawCircle(mLeftBall.centerX, centerY, mLeftBall.radius, mLeftPaint);
            canvas.drawCircle(mRightBall.centerX, centerY, mRightBall.radius, mRightPaint);
        }
    }

    public void startAnim() {
        if (mAnimatorSet.isRunning()) {
            mAnimatorSet.cancel();
        }
        mAnimatorSet.start();
    }

    public void setMoveDistance(int moveDistance) {
        if(moveDistance > 0){
            mMoveDistance = moveDistance - MID_BALL_RADIUS -10;
        }
    }

    public void setLeftBallColor(String ballColor) {
        if(!TextUtils.isEmpty(ballColor)){
            mLeftBallColor = Color.parseColor(ballColor);
        }
    }

    public void setRightBallColor(String ballColor) {
        if(!TextUtils.isEmpty(ballColor)){
            mRightBallColor = Color.parseColor(ballColor);
        }
    }

    public void setDurationTime(int duration) {
        mAnimDoTime = duration;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAnimatorSet.isRunning()) {
            mAnimatorSet.cancel();
        }
    }

    /**
     * 正在旋转的小球
     */
    class Ball {
        //小球的半径
        private int radius;
        //小球的圆心点
        private float centerX;
        //小球的颜色
        private int color;

        public Ball() {
        }

        public Ball(int radius, float centerX, int color) {
            this.radius = radius;
            this.centerX = centerX;
            this.color = color;
        }

        public int getRadius() {
            return radius;
        }

        public void setRadius(int radius) {
            this.radius = radius;
        }

        public float getCenterX() {
            return centerX;
        }

        public void setCenterX(float centerX) {
            this.centerX = centerX;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }
    }
}
