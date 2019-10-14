package com.meiju.weex.componentView.verticalseekbar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;

public class MsVerticalSeekBar extends View {

    private float sLeft, sTop, sRight, sBottom;
    private float sWidth, sHeight;

    private float x, y;
    private float mRadius;
    private float mProgress;

    private Bitmap mDragThumbBitmap;
    private int mProgressMax = 100;
    private int mProgressMin = 0;
    private int progressDefaultColor = Color.BLACK;
    private int tickMarkTextSize = dip2px(16);
    private boolean mIsEnable = true;
    private int mHeight = 0;

    private OnSlideChangeListener onSlideChangeListener;

    Paint thumbPaint = new Paint();
    Paint textPaint = new Paint();

    public MsVerticalSeekBar(Context context) {
        this(context, null);
    }

    public MsVerticalSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MsVerticalSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        int defaultWidth = dip2px(50);
        int defaultHeight = dip2px(345);

        if (widthSpecSize <= 0) {
            Log.i("MsVerticalSeekBar", "widthSpecSize 为0，重新赋默认值");
            widthSpecSize = defaultWidth;
        }
        if (heightSpecSize <= 0) {
            Log.i("MsVerticalSeekBar", "widthSpecSize 为0，重新赋默认值");
            heightSpecSize = defaultHeight;
        }

        Log.i("MsVerticalSeekBar", "onMeasure widthSpecSize : " + widthSpecSize + " heightSpecSize : " + heightSpecSize);

        setMeasuredDimension(widthSpecSize, heightSpecSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int h = getMeasuredHeight();
        int w = getMeasuredWidth();

        if (mDragThumbBitmap != null)
            mRadius = (float) mDragThumbBitmap.getHeight() / 2;

        sLeft = 0;
        sRight = w;

        sTop = getTop();
        sBottom = sTop + h;
        sWidth = sRight - sLeft;
        sHeight = h;

        x = (float) w / 2;
        y = (float) (1 - 0.01 * mProgress) * sHeight;

        Log.i("MsVerticalSeekBar", "mProgress : " + mProgress + " , y : " + y);

        drawThumb(canvas);
        drawText(canvas);
        thumbPaint.reset();
        textPaint.reset();
    }

    private void drawThumb(Canvas canvas) {
        if (mDragThumbBitmap == null) return;

        float thumbY = this.y;
        thumbY = thumbY < mRadius ? mRadius : thumbY;
        thumbY = thumbY > sHeight - mRadius ? sHeight - mRadius : thumbY;

        thumbPaint.setAntiAlias(true);
        thumbPaint.setStyle(Paint.Style.FILL);

        int right = getMeasuredWidth();
        int left = right - mDragThumbBitmap.getWidth();

        Rect rect = new Rect(left, (int) (thumbY - mDragThumbBitmap.getHeight() / 2), right, (int) (thumbY + mDragThumbBitmap.getHeight() / 2));

        canvas.drawBitmap(mDragThumbBitmap, null, rect, null);
    }

    private void drawText(Canvas canvas) {
        float textY = this.y;
        textY = textY < mRadius ? mRadius : textY;
        textY = textY > sHeight - mRadius ? sHeight - mRadius : textY;

        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(progressDefaultColor);
        textPaint.setTextSize(tickMarkTextSize);

        int realProgress = (int) exchange2RealProgress(mProgress);
        if (realProgress < mProgressMin) {
            realProgress = mProgressMin;
        }
        if (realProgress > mProgressMax) {
            realProgress = mProgressMax;
        }

        int right = getMeasuredWidth();
        int thumbLeft = 0;
        if (mDragThumbBitmap != null)
            thumbLeft = right - mDragThumbBitmap.getWidth();

        int x = thumbLeft - 120;
        if (x < 0)
            x = 10;

        String text2Draw = realProgress + "℃—";
        canvas.drawText(text2Draw, x, textY + 13, textPaint);//UI切图不居中有误差，大概13个像素,F***...
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mIsEnable) {
            return true;
        }

        this.y = (int) event.getY();
        mProgress = (sHeight - y) / sHeight * 100;
//        Log.i("MsVerticalSeekBar", "sHeight : " + sHeight + " , y : " + y + " , progress : " + mProgress);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onSlideProgress(MotionEvent.ACTION_DOWN, mProgress);
                break;
            case MotionEvent.ACTION_UP:
                onSlideProgress(MotionEvent.ACTION_UP, mProgress);
                break;
            case MotionEvent.ACTION_MOVE:
                onSlideProgress(MotionEvent.ACTION_MOVE, mProgress);
                break;

        }

        return true;
    }

    public void setIsEnable(boolean isEnable) {
        mIsEnable = isEnable;
    }

    public void setProgress(float progress) {
        Log.i("MsVerticalSeekBar", "setProgress progress : " + progress);
        this.mProgress = exchange2BarProgress(progress);
        invalidate();
    }

    public float getProgress() {
        return exchange2RealProgress(mProgress);
    }

    public int getMin() {
        return mProgressMin;
    }

    public int getMax() {
        return mProgressMax;
    }

    public void setMin(int min) {
        mProgressMin = min;
    }

    public void setMax(int max) {
        mProgressMax = max;
    }

    public void setImage(String imagePath) {
        updateThumbBitmap(imagePath);
        invalidate();
    }

    public interface OnSlideChangeListener {
        void OnSlideChangeListener(View view, float progress);

        void onSlideStopTouch(View view, float progress);
    }

    public void setOnSlideChangeListener(OnSlideChangeListener onStateChangeListener) {
        this.onSlideChangeListener = onStateChangeListener;
    }

    private void updateThumbBitmap(String imagePath) {
        if (TextUtils.isEmpty(imagePath)) {
            Log.i("MsVerticalSeekBar", "imagePath is NULL");
            return;
        }

        imagePath = imagePath.replaceFirst("file://", "");//过滤掉文件系统不识别的符号
        File file = new File(imagePath);
        if (file.exists()) {
            mDragThumbBitmap = BitmapFactory.decodeFile(imagePath);
        } else {
            Log.i("MsVerticalSeekBar", "image file NOT EXIST!!! " + imagePath);
        }
    }

    /**
     * 由于最大值和最小值不一定是100和0，所以需要转换
     *
     * @param barProgress
     * @return
     */
    private float exchange2RealProgress(float barProgress) {
        if (mProgressMax == 100 && mProgressMin == 0)
            return barProgress;

        float step = (mProgressMax - mProgressMin) / 100f;
        float realProgress = mProgressMin + step * barProgress;
        return realProgress;
    }

    /**
     * 由于最大值和最小值不一定是100和0，所以需要转换
     *
     * @param realProgress
     * @return
     */
    private float exchange2BarProgress(float realProgress) {
        if (mProgressMax == 100 && mProgressMin == 0)
            return realProgress;

        float step = (mProgressMax - mProgressMin) / 100f;
        float barProgress = (realProgress - mProgressMin) / step;
        return barProgress;
    }

    private void onSlideProgress(int event, float progress) {
        int realProgress = (int) exchange2RealProgress(progress);
        if (realProgress < mProgressMin) {
            realProgress = mProgressMin;
        }
        if (realProgress > mProgressMax) {
            realProgress = mProgressMax;
        }
        switch (event) {
            case MotionEvent.ACTION_UP:
                if (onSlideChangeListener != null) {
                    onSlideChangeListener.onSlideStopTouch(this, realProgress);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (onSlideChangeListener != null) {
                    onSlideChangeListener.OnSlideChangeListener(this, realProgress);
                }

                this.mProgress = progress;
                invalidate();

                this.invalidate();
                break;
            case MotionEvent.ACTION_DOWN:
                this.invalidate();
                break;
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle state = new Bundle();
        state.putFloat("progress", mProgress);
        state.putParcelable("super", super.onSaveInstanceState());
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mProgress = bundle.getFloat("progress");
            super.onRestoreInstanceState(bundle.getParcelable("super"));
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    public int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
