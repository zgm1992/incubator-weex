package com.meiju.weex.componentView.ProgressCycleView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.view.View;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;


public class ProgressCycleView extends View {
    private final String TAG = getClass().getSimpleName();
    BuilderParams p = null;
    Paint mBackGroundPaint = null;
    Paint mBackGroundPaint2 = null;
    Paint mCompletePaint = null;
    Paint mInCompletePaint = null;
    Paint mStockPaint = null;
    Paint mTextPaint = null;
    //    Paint mLinePaint = null;
    RectF oval = null;
    Timer timer; //环形进度条，自动进度的时间调度器
    int cornerRadius = 0;
    int mBackgroundRadius = 0;
    int centerX = 0;
    int centerY = 0;
    int startingSlice = 0;

    public ProgressCycleView(Context context) {
        super(context);
        init(context);

    }

    private void init(Context context) {
        p = new BuilderParams(context);
        mCompletePaint = new Paint();
        mInCompletePaint = new Paint();
        mBackGroundPaint = new Paint();


        mCompletePaint.setStyle(Paint.Style.STROKE);
        mCompletePaint.setAntiAlias(true);

        mBackGroundPaint.setStyle(Paint.Style.FILL);
        mBackGroundPaint.setAntiAlias(true);
        mBackGroundPaint.setStrokeWidth(0);

        mBackGroundPaint2 = new Paint();
        mBackGroundPaint2.setStyle(Paint.Style.STROKE);
        mBackGroundPaint2.setAntiAlias(true);
        mBackGroundPaint2.setColor(Color.BLUE);

        mInCompletePaint.setStyle(Paint.Style.STROKE);
        mInCompletePaint.setAntiAlias(true);

        mTextPaint = new Paint();
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setFakeBoldText(true);

//        mLinePaint = new Paint();
//        mLinePaint.setColor(Color.RED);

        mStockPaint = new Paint();
//        if (p.mTrackRoundedCorners) {
        mStockPaint.setStrokeCap(Paint.Cap.ROUND);
//        }
        mStockPaint.setAntiAlias(true);
        mStockPaint.setColor(p.pointColor);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    private void createTimer() {
        if (timer == null && p.autoProgress) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!p.autoProgress) { //如果中途取消了自动进度，就把定时器关闭了
                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                        }
                    }
                    p.mProgress++;
                    postInvalidate();
                    if (p.mProgress == p.mMax) {
                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                        }
                    }
                }
            }, (10000 / 36), (10000 / 36));
        }
    }

    /**
     * 确定自适应系数
     *
     * @param radius
     * @param width
     * @param high
     * @return
     */
    private float getScale(float radius, float width, float high) {
        radius = radius * 2;
        if (width > radius && high > radius) {

            return 1f;
        }

        if (width > high) {
            return high / radius;
        } else {
            return width / radius;
        }
    }

    private void resetDrawParams() {
        createTimer();

        mCompletePaint.setColor(p.mCompletedColor);
        mInCompletePaint.setColor(p.mIncompletedColor);

        mBackGroundPaint.setColor(p.mBackgroundColor);
        mStockPaint.setColor(p.pointColor);

        mTextPaint.setColor(p.textColor);
        mTextPaint.setTextSize(p.textSize);

        int halfThickness = p.mThickness / 2;
        float scale = 1f; //自适应大小变化的刻度数据

        int ovalStartX = 0;
        int ovalStartY = 0;
        int ovalEndX = 0;
        int ovalEndY = 0;
        float offset = halfThickness > p.pointRadius ? halfThickness : p.pointRadius;
        float pointOffset = p.pointRadius - halfThickness;
        float backgroundOver = p.mBackgroundRadius - (p.cornerRadius + pointOffset);

        Log.i(TAG, " backgroundOver ->" + backgroundOver + "  p.cornerRadius ->" + p.cornerRadius + "  p.mBackgroundRadius ->" + p.mBackgroundRadius + "   halfThickness ->" + halfThickness + " width ->" + getWidth() + " height ->" + getHeight());
        if (backgroundOver > 0) {
            //如果背景区域比较大，那么就以背景边距作为取值使用
            scale = getScale(p.mBackgroundRadius, getWidth(), getHeight());
            mBackgroundRadius = (int) (p.mBackgroundRadius * scale);
            cornerRadius = (int) (p.cornerRadius * scale);

            ovalStartX = (int) ((backgroundOver + offset) * scale); //为了能正常绘制边距 或者 背景色，矩形需要预留一定的空间
            ovalStartY = ovalStartX;
            ovalEndX = mBackgroundRadius * 2 - ovalStartX;
            ovalEndY = ovalEndX;
            Log.i(TAG, "使用 背景色 处理  scale->" + scale + "  ovalStartX ->" + ovalStartX + " ovalEndX ->" + ovalEndX + "width ->" + getWidth() + "  mBackgroundRadius * 2 ->" + (mBackgroundRadius * 2));
        } else {
            //如果直径区域比较大，那么就使用直径区域作为取值使用
            float virtualCornerRadius = pointOffset > 0 ? p.cornerRadius + pointOffset : p.cornerRadius;
            scale = getScale(
//                    p.cornerRadius
                    virtualCornerRadius // 如果端点有冗余数据，则给端点预留处理
                    , getWidth(), getHeight());

            mBackgroundRadius = (int) (p.mBackgroundRadius * scale);
//            if (pointOffset > 0) {
//                cornerRadius = (int) ((p.cornerRadius - pointOffset) * scale);
//            } else {
                cornerRadius = (int) (p.cornerRadius * scale);
//            }

            ovalStartX = (int) (offset * scale); //为了能正常绘制边距 或者 背景色，矩形需要预留一定的空间
            ovalStartY = ovalStartX;

            ovalEndX = (int) ((virtualCornerRadius * scale) * 2) - ovalStartX;
            ovalEndY = ovalEndX;
            Log.i(TAG, "使用 环形半径处理  pointOffset ->"+pointOffset +"   scale->" + scale + "  ovalStartX ->" + ovalStartX + " ovalEndX ->" + ovalEndX + " widht ->" + getWidth() + "  halfThickness ->" + halfThickness);
        }

        int thickness = (int) (p.mThickness * scale);
        mCompletePaint.setStrokeWidth(thickness);
        mInCompletePaint.setStrokeWidth(thickness);

        if (p.pointRadius > 0) {
            mStockPaint.setStrokeWidth(p.pointRadius * 2 * scale);
        } else {
            mStockPaint.setStrokeWidth(thickness);
        }


        if (oval == null) {
            oval = new RectF(ovalStartX, ovalStartY, ovalEndX, ovalEndY);
        } else {
            oval.set(ovalStartX, ovalStartY, ovalEndX, ovalEndY);
        }

        centerX = (int) ((oval.right - oval.left) / 2 + oval.left);
        centerY = (int) ((oval.bottom - oval.top) / 2 + oval.top);

        if (p.mClockwise) {
            startingSlice = p.startingSlice;
        } else {
            startingSlice = 360 - p.startingSlice;
        }
    }

//    RectF  rectF = null;


    /**
     * 绘制环形进度的端点
     *
     * @param angle 环形进度的角度，以此来绘制端点
     */
    private void drawPoint(Canvas canvas, int angle) {
        if (angle > 360) {
            angle = angle % 360;
        }
        if (p.pointShow) {
            int radius = (int) ((oval.right - oval.left) / 2);
            int centerX = (int) (radius + oval.left);
            int centerY = (int) (radius + oval.top);

            if (angle == 0 || angle == 360) {
                centerY -= radius;
            } else if (angle == 90) {
                centerX += radius;
            } else if (angle == 180) {
                centerY += radius;
            } else if (angle == 270) {
                centerX -= radius;
            } else if (angle < 90) {
                double radians = Math.toRadians(angle);
                centerX += Math.sin(radians) * radius;
                centerY -= Math.cos(radians) * radius;
            } else if (angle < 180) {
                angle = 180 - angle;
                double radians = Math.toRadians(angle);
                centerX += Math.sin(radians) * radius;
                centerY += Math.cos(radians) * radius;
            } else if (angle < 270) {
                angle = angle - 180;
                double radians = Math.toRadians(angle);
                centerX -= Math.sin(radians) * radius;
                centerY += Math.cos(radians) * radius;
            } else {
                angle = 360 - angle;
                double radians = Math.toRadians(angle);
                centerX -= Math.sin(radians) * radius;
                centerY -= Math.cos(radians) * radius;
            }


            if (p.mPointBitmap != null) {
                canvas.drawBitmap(p.mPointBitmap, centerX - (p.mPointBitmap.getWidth() / 2), centerY - (p.mPointBitmap.getHeight() / 2), mStockPaint);
            } else {
                canvas.drawPoint(centerX, centerY, mStockPaint);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        RectF rectF = null;
//        rectF = new RectF(0, 0, getWidth(), getHeight());
//        Paint paint = new Paint();
//        paint.setStyle(Paint.Style.FILL);
//        paint.setColor(Color.YELLOW);
//        canvas.drawRect(rectF, paint);


        int progressAngle = 0;
        progressAngle = (int) (360 * (p.mProgress / p.mMax));
        resetDrawParams();

//        canvas.drawRect(oval, mLinePaint);
        if (p.mBackgroundColor != Color.TRANSPARENT) {
            //先描绘环形背景色
            canvas.drawCircle(centerX, centerY,
                    mBackgroundRadius <= 0 ? cornerRadius : mBackgroundRadius,
                    mBackGroundPaint);
        }

        if (progressAngle > 360) {
            //如果已经进度完成了，显示圆环
            canvas.drawArc(oval, 0, 360, false, mCompletePaint);
            return;
        }

        if (p.mIncompletedColor != 0) { //只有上层设置了未完成部分的颜色数值，才会进行对应的描述
            //直接绘制背景的条形颜色
            canvas.drawArc(oval, 0, 360, false, mInCompletePaint);
        }

        if (p.mProgress > p.mMin) {
            //绘制已经完成部分的颜色值
            int start = p.mClockwise ? getStartAngle(startingSlice) : (getStartAngle(360 - progressAngle + startingSlice)); //是否走顺时针的绘制方式
            canvas.drawArc(oval, start, progressAngle, false, mCompletePaint);
        }

        //绘制进度端的小点
        drawPoint(canvas, p.mClockwise ? (progressAngle + startingSlice) : (360 - progressAngle + startingSlice));


        if (!TextUtils.isEmpty(p.text)) {
            //绘制环形中央的字体
            //计算文字水平居中处理
            float textMeature = mTextPaint.measureText(p.text);
            float startX = centerX - textMeature / 2;
            //计算文字垂直居中处理
            Paint.FontMetricsInt fontMetricsInt = mTextPaint.getFontMetricsInt();
            float startY = centerY - fontMetricsInt.descent + (fontMetricsInt.bottom - fontMetricsInt.top) / 2;
            canvas.drawText(p.text, startX, startY, mTextPaint);
        }

//        canvas.drawLine(oval.left, oval.bottom / 2, oval.right, oval.bottom / 2, mLinePaint);
//
//        canvas.drawLine(0, oval.bottom / 2 + 10, p.cornerRadius * 2, oval.bottom / 2 + 10, mLinePaint);
//
//        canvas.drawLine(centerX, centerY - p.mBackgroundRadius, centerX, centerY + p.mBackgroundRadius, mLinePaint);
//
//        canvas.drawLine(centerX+10, centerY - p.cornerRadius, centerX+10, centerY + p.cornerRadius, mLinePaint);


        //自测使用的辅助线
//        RectF rectF = new RectF(0, 0, getWidth(), getHeight());
//        mInCompletePaint.setStrokeWidth(1);
//        canvas.drawRect(rectF, mInCompletePaint);
//        canvas.drawLine(oval.right - p.mThickness / 2, centerY, oval.right + p.mThickness / 2, centerY, mCompletePaint);
//        mCompletePaint.setStrokeWidth(1);
//        canvas.drawRect(oval, mCompletePaint);
    }


    //因为原生的绘制计算是 0 or 360 角度对应 对应 3：00 起始点  ，  270 角度 对应12：00 的起始点
    //和我们需要的业务绘制相差较大，所以做一个转义处理方式
    private int getStartAngle(int angle) {
        if (angle > 360) {
            angle = angle % 360;
        }

        angle = angle - 90;

        if (angle < 0) {
            angle = angle + 360;
        }
        return angle;
    }


    public void setCompletedColor(String color) {
        try {
            p.mCompletedColor = Color.parseColor(color);
            invalidate();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void setInCompletedColor(String color) {
        try {
            p.mIncompletedColor = Color.parseColor(color);
            invalidate();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void setTextSize(int textSize) {
        p.textSize = ProgressCycleUtils.dp2px(getContext(), textSize);
        invalidate();
    }


    public void setText(String text) {
        p.text = text;
        invalidate();
    }

    public void setTextColor(String color) {
        try {
            p.textColor = Color.parseColor(color);
            invalidate();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void setThickness(int thickness) {
        p.mThickness = (int) (ProgressCycleUtils.dp2px(getContext(), thickness));
    }

    public void setCornerRadius(int cornerRadius) {
        p.cornerRadius = (int) (ProgressCycleUtils.dp2px(getContext(), cornerRadius / 2)); //和ios保持一致，都使用一半的刻度
    }

    public void setProgressCount(int progressCount) {
        p.mProgress = progressCount;
        invalidate();
    }

    public void setTotalCounter(int totalCounter) {
        p.mMax = totalCounter;
        invalidate();
    }

    public void setBackgroundRadius(int backgroundRadius) {

        p.mBackgroundRadius = (int) (ProgressCycleUtils.dp2px(getContext(), backgroundRadius / 2)); //和ios保持一致，都使用一半的刻度
        invalidate();
    }


    public void setBackgroundColor(String color) {
        try {
            p.mBackgroundColor = Color.parseColor(color);
            invalidate();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }


//    public void setBackgroundRadius(int backgroundRadius) {
//        p.mBackgroundRadius = backgroundRadius;
//        invalidate();
//    }

    public void setAutoProgress(boolean b) {
        p.autoProgress = b;
        invalidate();
    }

    //环形进度开始的起始位置， 逆时针： 0: 0点钟位置起点，90:3点钟位置起点 180:6点钟位置起点 ， 正时针 镜像相反
    public void setStartingSlice(int startingSlice) {
        p.startingSlice = startingSlice;
    }

    public void setClockwise(boolean b) {
        p.mClockwise = b;
    }

    public void setPointShow(boolean show) {
        p.pointShow = show;
    }

    public void setPointColor(String color) {
        try {
            p.pointColor = Color.parseColor(color);
            invalidate();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

    }

    public void setPointRadius(int radius) {
        p.pointRadius = ProgressCycleUtils.dp2px(getContext(), radius);
    }

    public void setPointBitmap(Bitmap bitmap) {
        p.mPointBitmap = bitmap;
    }

}
