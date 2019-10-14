package com.meiju.weex.componentView.barchart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.meiju.weex.componentView.indicatorseekbar.IndicatorUtils;
import android.util.Log;

/**
 * Created by XIONGQI on 2018-4-16.
 * 继承主要为了实现绘制X\Y轴的单位
 */
public class MBarChart extends BarChart {
    private final String TAG = getClass().getSimpleName();
    int barNumber = 0;
    int barTimes = 0;
    int measuredWidth = 0;
    float groupSpace = 0;
    float barSpace = 0;
    float needBarWidthPx = 0;
    float needGoupSpacePx = 0;
    float miniGoupSpacePx;
    OnBarSpaceAndGroupSpaceListener onBarSpaceAndGroupSpaceListener = null;
    private MChartUnit unit;

    public MBarChart(Context context) {
        super(context);
        init(context);
    }


    public MBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MBarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.miniGoupSpacePx = IndicatorUtils.dp2px(getContext(), 2);
    }

    public void setBarNumberAndTimes(int barNumber, int times) {
//        if (this.barNumber != barNumber) {
        this.barNumber = barNumber;
        this.barTimes = times;
        Log.i(TAG, "setBarNumber is ->" + barNumber);
//            caculateBarMatrix(measuredWidth, barNumber);
//        }
    }

    public void setBarWidthAndBarInterval(int barWidth, int barInterval) {
        this.needBarWidthPx = IndicatorUtils.dp2px(getContext(), barWidth);
        this.needGoupSpacePx = IndicatorUtils.dp2px(getContext(), barInterval);
    }

    public int getBarNumber() {
        return this.barNumber;
    }

    @Override
    protected void onSetMeasureDimension(int measuredWidth, int measuredHeight) {
        super.onSetMeasureDimension(measuredWidth, measuredHeight);
        Log.i(TAG, "measuredWidth is ->" + measuredWidth + "  measuredHeight  is ->" + measuredHeight);
        this.measuredWidth = measuredWidth;
        caculateBarMatrix(measuredWidth, barNumber, barTimes);
    }

    @Override
    public void groupBars(float fromX, float groupSpace, float barSpace) {
        super.groupBars(fromX, groupSpace, barSpace);
//        if (this.groupSpace != groupSpace) {
        this.groupSpace = groupSpace;
        this.barSpace = barSpace;
//        }
        Log.i(TAG, "set the groupSpace is ->" + groupSpace);
    }

    public void caculateBarMatrix() {
        Log.i(TAG, " data layout invoke caculateBarMatrix");
        caculateBarMatrix(this.measuredWidth, this.barNumber, barTimes);
    }

    public void caculateBarMatrix(int measuredWidth, int barNumber, int barTimes) {
        float needGoupSpacePx = this.needGoupSpacePx;
        //柱子之间的间距会有最小值 2dp
        Log.i(TAG, " weex set curBarcharSpace ->" + needGoupSpacePx);

        if (needGoupSpacePx == 0 && needBarWidthPx == 0) {
            Log.e(TAG, " 不需要设置 间距刻度 和 柱子的刻度");
            return;
        }

        if (measuredWidth == 0) {
            Log.e(TAG, " measure width is -> 0");
            return;
        }

        if (barNumber == 0) {
            Log.i(TAG, "cal cu late the , the bar number is 0");
            return;
        } else {
            Log.i(TAG, "cal cu late the , the bar number is ->" + barNumber + "  weex set bar space is ->" + groupSpace
                    + "  getXscale -> " + getViewPortHandler().getXscale() + " get scale x is ->" + getViewPortHandler().getScaleX());
        }

        //如果两段数据都存在
        //0.9是系数是因为  坐标轴占用了 接近 0.1 的空间
        float noScaleInterVal = (int) (measuredWidth * 0.9 * groupSpace / (this.barNumber + 1));


        if (needBarWidthPx == 0) { //不需要考虑柱子的宽度的情况，仅仅计算最小间距问题
            if (noScaleInterVal == 0) {
                Log.e(TAG, "仅仅需要考虑间距  ， 但是计算出来的内容 是 - >" + noScaleInterVal);
                return;
            }
            setScaleValue(needGoupSpacePx, noScaleInterVal);
            return;
        }

        if (needGoupSpacePx == 0) {
            needGoupSpacePx = miniGoupSpacePx;
        }

        float chartArea = barTimes * barNumber * needBarWidthPx;
        Log.i(TAG,"barTimes is ->"+barNumber);
        float allArea = needGoupSpacePx * (barNumber + 1) + chartArea;
        float barWidthRatio = 0.65f;
        if (allArea > measuredWidth * 0.9){ //需要对整体进行拉伸处理
            //纯数学推算方式，先数据内容设置到一个相对合理的比例控制值
            barWidthRatio = chartArea / (chartArea + (barNumber + 1) * needGoupSpacePx); //计算出新的比例数值
        } else { //一个屏幕可以装下数据，仅仅需要调整占据比例
            barWidthRatio = (float) (chartArea / (measuredWidth * 0.9));
        }


        //新的比例数值回调给上层，再次设置比例数据
        if (onBarSpaceAndGroupSpaceListener != null) {
            onBarSpaceAndGroupSpaceListener.onChange(barWidthRatio, 1 - barWidthRatio);
        }
        float noScaleBarwidth = (int) (measuredWidth * 0.9 * barWidthRatio / (this.barNumber*barTimes)); //使用新的比例数值来计算
        setScaleValue(needBarWidthPx, noScaleBarwidth);
    }

    public void setOnBarSpaceAndGroupSpaceListener(OnBarSpaceAndGroupSpaceListener onBarSpaceAndGroupSpaceListener) {
        this.onBarSpaceAndGroupSpaceListener = onBarSpaceAndGroupSpaceListener;
    }

    /**
     * 根据当前值和预设值来处理是否要将页面进行拉伸滑动的状态
     */
    private void setScaleValue(float expectPx, float currentPx) {

        //计算需要转变的刻度值
        if (expectPx != currentPx) { //当前柱状刻度系数和当前
            //去掉尾数
            float scale = expectPx / currentPx;
            int scaleInt = (int) (scale * 10);
            if (scaleInt % 10 > 5) {
                scaleInt++;
            }
            scale = scaleInt;
            scale = scale / 10;
            if (scale == getViewPortHandler().getXscale()) {
                Log.d(TAG, "old and new scale is same , not change  scale ->" + scale);
                return;
            } else if (scale <= 1f && getViewPortHandler().getXscale() > 1f) {
                Log.d(TAG, "the new scale is ->" + scale + " set to 1");
                scale = 1f;
            }
            // x轴放大n倍，y不变
            Matrix matrix = new Matrix();
            matrix.postScale(scale, 1.0f);
            Log.d(TAG, " calculate new  scale is different with old scale ,  new scale is " + scale + " old scale  is ->" + mViewPortHandler.getXscale());
            // 设置缩放
            mViewPortHandler.refreshXScale(matrix, scale, null, false);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (unit != null) {
            drawUnit(canvas);
        }
    }

    private void drawUnit(Canvas canvas) {
        int viewWidth = getMeasuredWidth();
//        int viewHeight = getMeasuredHeight();
        int viewHeight = getHeight();
        int x = 0;
        int y = 0;
        float textWidth = 0;
        TextPaint textPaint = new TextPaint();
        //绘制x轴label
        if (!TextUtils.isEmpty(unit.getxLable())) {
            textPaint.setTextSize(unit.getxTextSize());
            textPaint.setColor(unit.getxLabelColor());
            textWidth = textPaint.measureText(unit.getxLable());
            x = (int) (viewWidth - textWidth);
            y = viewHeight;
            x += unit.getxRightOffset();
            x -= unit.getxLeftOffset();
            y -= unit.getxBottomOffset();
//            y -= unit.getxTextSize();
            canvas.drawText(unit.getxLable(), x, y, textPaint);
        }

        //绘制y轴label
        if (!TextUtils.isEmpty(unit.getyLable())) {
            textPaint.setTextSize(unit.getyTextSize());
            textPaint.setColor(unit.getyLabelColor());
            x = 0;
            y = 0;
            x += unit.getyRightOffset();
//            x -= unit.getyLeftOffset();
//            y -= unit.getyBottomOffset();
            y += unit.getyTextSize();
            canvas.drawText(unit.getyLable(), x, y, textPaint);
        }
    }

    public void setUnit(MChartUnit unit) {
        this.unit = unit;
    }

    public interface OnBarSpaceAndGroupSpaceListener {
        void onChange(float barWidth, float groupSpace);
    }
}
