package com.meiju.weex.componentView;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.LineChart;
import com.meiju.weex.componentView.barchart.MChartUnit;

/**
 * Created by XIONGQI on 2018-4-16.
 * 继承主要为了实现绘制X\Y轴的单位
 */
public class MLineChart extends LineChart {
    private MChartUnit unit;
    public MLineChart(Context context) {
        super(context);
    }

    public MLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(unit!=null){
            drawUnit(canvas);
        }
    }

    private void drawUnit(Canvas canvas) {
        int viewWidth = getMeasuredWidth();
        int viewHeight = getMeasuredHeight();
        int x = 0;
        int y = 0;
        float textWidth = 0;
        TextPaint textPaint = new TextPaint();
        if(!TextUtils.isEmpty(unit.getxLable())) {
            textPaint.setTextSize(unit.getxTextSize());
            textPaint.setColor(unit.getxLabelColor());
            textWidth = textPaint.measureText(unit.getxLable());
            x = (int) (viewWidth - textWidth);
            y = viewHeight;
            x += unit.getxRightOffset();
            x -= unit.getxLeftOffset();
            y -= unit.getxBottomOffset();
            y += unit.getxTopOffset();
            canvas.drawText(unit.getxLable(), x, y, textPaint);
        }

        if(!TextUtils.isEmpty(unit.getyLable())){
            textPaint.setTextSize(unit.getyTextSize());
            textPaint.setColor(unit.getyLabelColor());
            x = 0;
            y = 0;
            x += unit.getyRightOffset();
            x -= unit.getyLeftOffset();
            y -= unit.getyBottomOffset();
            y += unit.getyTopOffset();
            canvas.drawText(unit.getyLable(), x, y, textPaint);
        }
    }

    public void setUnit(MChartUnit unit) {
        this.unit = unit;
    }
}
