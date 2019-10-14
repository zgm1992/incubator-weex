package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.highlight.Range;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.model.GradientColor;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import android.util.Log;

import java.util.List;

public class BarChartRenderer extends BarLineScatterCandleBubbleRenderer {

    protected BarDataProvider mChart;

    /**
     * the rect object that is used for drawing the bars
     */
    protected RectF mBarRect = new RectF();

    protected BarBuffer[] mBarBuffers;

    protected Paint mShadowPaint;
    protected Paint mBarBorderPaint;
    boolean isSet = false;
    int lastGroupIndex = 0;
    int lastBarIndex = 0;
    private RectF mBarShadowRectBuffer = new RectF();

    public BarChartRenderer(BarDataProvider chart, ChartAnimator animator,
                            ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        this.mChart = chart;

        mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightPaint.setStyle(Paint.Style.FILL);
        mHighlightPaint.setColor(Color.rgb(0, 0, 0));
        // set alpha after color
        mHighlightPaint.setAlpha(120);

        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadowPaint.setStyle(Paint.Style.FILL);

        mBarBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarBorderPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void initBuffers() {

        BarData barData = mChart.getBarData();
        mBarBuffers = new BarBuffer[barData.getDataSetCount()];

        for (int i = 0; i < mBarBuffers.length; i++) {
            IBarDataSet set = barData.getDataSetByIndex(i);
            mBarBuffers[i] = new BarBuffer(set.getEntryCount() * 4 * (set.isStacked() ? set.getStackSize() : 1),
                    barData.getDataSetCount(), set.isStacked());
        }
    }

    @Override
    public void drawData(Canvas c) {

        BarData barData = mChart.getBarData();

        for (int i = 0; i < barData.getDataSetCount(); i++) {

            IBarDataSet set = barData.getDataSetByIndex(i);

            if (set.isVisible()) {
                drawDataSet(c, set, i);
            }
        }
    }

    protected void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        mBarBorderPaint.setColor(dataSet.getBarBorderColor());
        mBarBorderPaint.setStrokeWidth(Utils.convertDpToPixel(dataSet.getBarBorderWidth()));

        final boolean drawBorder = dataSet.getBarBorderWidth() > 0.f;

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        // draw the bar shadow before the values
        if (mChart.isDrawBarShadowEnabled()) {
            mShadowPaint.setColor(dataSet.getBarShadowColor());

            BarData barData = mChart.getBarData();

            final float barWidth = barData.getBarWidth();
            final float barWidthHalf = barWidth / 2.0f;
            float x;

            for (int i = 0, count = Math.min((int) (Math.ceil((float) (dataSet.getEntryCount()) * phaseX)), dataSet.getEntryCount());
                 i < count;
                 i++) {

                BarEntry e = dataSet.getEntryForIndex(i);

                x = e.getX();

                mBarShadowRectBuffer.left = x - barWidthHalf;
                mBarShadowRectBuffer.right = x + barWidthHalf;

                trans.rectValueToPixel(mBarShadowRectBuffer);

                if (!mViewPortHandler.isInBoundsLeft(mBarShadowRectBuffer.right))
                    continue;

                if (!mViewPortHandler.isInBoundsRight(mBarShadowRectBuffer.left))
                    break;

                mBarShadowRectBuffer.top = mViewPortHandler.contentTop();
                mBarShadowRectBuffer.bottom = mViewPortHandler.contentBottom();

                c.drawRect(mBarShadowRectBuffer, mShadowPaint);
            }
        }

        // initialize the buffer
        BarBuffer buffer = mBarBuffers[index];
        buffer.setPhases(phaseX, phaseY);
        buffer.setDataSet(index);
        buffer.setInverted(mChart.isInverted(dataSet.getAxisDependency()));
        buffer.setBarWidth(mChart.getBarData().getBarWidth());

        buffer.feed(dataSet);

        trans.pointValuesToPixel(buffer.buffer);


        final boolean isSingleColor = dataSet.getColors().size() == 1;

        if (isSingleColor) {
            mRenderPaint.setColor(dataSet.getColor());
            mMaxValuePaint.setColor(dataSet.getMaxColor());
        }

        int counter = 0;
        for (int j = 0; j < buffer.size(); j += 4) {

            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2]))
                continue;

            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                break;


            if (!isSingleColor) {
                // Set the color for the currently drawn value. If the index
                // is out of bounds, reuse colors.
                mRenderPaint.setColor(dataSet.getColor(j / 4));
            }


            if (dataSet.getGradientColor() != null) {
                GradientColor gradientColor = dataSet.getGradientColor();
                mRenderPaint.setShader(
                        new LinearGradient(
                                buffer.buffer[j],
                                buffer.buffer[j + 3],
                                buffer.buffer[j],
                                buffer.buffer[j + 1],
                                gradientColor.getStartColor(),
                                gradientColor.getEndColor(),
                                android.graphics.Shader.TileMode.MIRROR));
            }

            if (dataSet.getGradientColors() != null) {
                mRenderPaint.setShader(
                        new LinearGradient(
                                buffer.buffer[j],
                                buffer.buffer[j + 3],
                                buffer.buffer[j],
                                buffer.buffer[j + 1],
                                dataSet.getGradientColor(j / 4).getStartColor(),
                                dataSet.getGradientColor(j / 4).getEndColor(),
                                android.graphics.Shader.TileMode.MIRROR));
            }

            BarEntry e = null;
            float percent = 1;
            try {
                e = dataSet.getEntryForIndex(j / 4);
                percent = e.getPercent();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            if (buffer.buffer[j + 1] != buffer.buffer[j + 3]) { //如果是没有高度的，就不需要绘制


                if (percent == 1 || percent == 0) {
                    Paint paint = mRenderPaint;
                    if (percent == 0 && mViewPortHandler.getSignPostRender().isShow()) {
                        paint = mMaxValuePaint;
                    }
                    drawBar(c, paint,
//                            percent == 1  || mViewPortHandler.getSignPostRender().isShow()? mRenderPaint : mMaxValuePaint,
                            buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2], buffer.buffer[j + 3],
                            dataSet.getRadius(), dataSet.getBottomRadius(), dataSet.getBorderType(), false);
                } else {
                    float top = buffer.buffer[j + 1];
                    float bottom = buffer.buffer[j + 3];
                    float center = (bottom - top) * percent + top;
                    int topRadius = dataSet.getRadius();
                    int bottomRadius = dataSet.getBottomRadius();

                    //宽度小于圆角的情况
                    float halWidth = (buffer.buffer[j + 2] - buffer.buffer[j]) / 2;
                    if (topRadius > halWidth) {
                        topRadius = (int) halWidth;
                    }

                    if (bottomRadius > halWidth) {
                        bottomRadius = (int) halWidth;
                    }

                    int barHigh = (int) (bottom - top);
                    int upHigh = (int) (center - top);
                    int downHigh = (int) (bottom - center);


                    //针对各种高度小于圆角的情况
                    if (topRadius == 0 && bottomRadius > 0) {
                        bottomRadius = bottomRadius > barHigh ? barHigh : bottomRadius;
                    } else if (topRadius > 0 && bottomRadius == 0) {
                        topRadius = topRadius > barHigh ? barHigh : topRadius;
                    } else if (topRadius > 0 && bottomRadius > 0) {
                        if (topRadius + bottomRadius > barHigh) {
                            bottomRadius = barHigh / 2;
                            topRadius = bottomRadius;
                        }
                    }

                    float tempBottom = center;
                    if (bottomRadius > downHigh + 5) { //先绘制底部 圆角绘制不齐全的部分
                        tempBottom -= (bottomRadius - downHigh);
                        drawBottomFragmentCircle(c, mMaxValuePaint,
                                buffer.buffer[j], tempBottom, buffer.buffer[j + 2], center,
                                bottomRadius);
                    }

                    //绘制上半部分的柱子 主体
                    drawBar(c, mMaxValuePaint,
                            buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2], tempBottom,
                            topRadius, 0, dataSet.getBorderType(), false);


                    float tempTop = center;

                    if (topRadius > upHigh + 5) {  //对应的，如果有max 部分 存在圆角绘制不全的情况
                        tempTop += (topRadius - upHigh);
                        drawTopFragmentCircle(c, mRenderPaint,
                                buffer.buffer[j], center, buffer.buffer[j + 2], tempTop, topRadius);
                    }


                    //绘制下半部分的柱子 主体
                    drawBar(c, mRenderPaint,
                            buffer.buffer[j], tempTop, buffer.buffer[j + 2], buffer.buffer[j + 3],
                            0, bottomRadius, dataSet.getBorderType(), false);
                }


                if (drawBorder) { //绘制全边框
                    drawBar(c, null,
                            buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2], buffer.buffer[j + 3],
                            dataSet.getRadius(), dataSet.getBottomRadius(), dataSet.getBorderType(), true);
                }

            }
            if (mViewPortHandler.getBarSelectIndex() == counter) {
                drawPreSetHighlighted(c, buffer.buffer, j,percent, dataSet.getRadius(), dataSet.getBottomRadius(), dataSet.getBorderType());
            }

            counter++; //避免0 长度的数据导致错乱

        }
    }

    //没有用户手势点击触发的情况下，如果上层weex设置了高亮的柱子，则
    private void drawPreSetHighlighted(Canvas c, float[] buffer, int j, float percent, int topRadius, int bottomRadius, List<String> borderType) {
        if (mViewPortHandler.getBarSelectIndex() == -1 || mViewPortHandler.getSignPostRender().getEvent() != null) {
            return;
        }

        mHighlightPaint.setColor(mViewPortHandler.getHighLightColor());
        mMaxHighlightPaint.setColor(mViewPortHandler.getMaxHighLightColor());

        if (percent == 1 || percent == 0) {
            Paint paint = mHighlightPaint;
            if (percent == 0 && mViewPortHandler.getSignPostRender().isShow()) {
                paint = mMaxHighlightPaint;
            }
            drawBar(c, paint,
                    buffer[j], buffer[j+1], buffer[j+2], buffer[j+3],
                    topRadius, bottomRadius, borderType, false);
        } else {
            float top = buffer[j+1];
            float bottom = buffer[j+3];
            float center = (bottom - top) * percent + top;

            //宽度小于圆角的情况
            float halWidth = (buffer[j+2] - buffer[j]) / 2;
            if (topRadius > halWidth) {
                topRadius = (int) halWidth;
            }

            if (bottomRadius > halWidth) {
                bottomRadius = (int) halWidth;
            }

            int barHigh = (int) (bottom - top);
            int upHigh = (int) (center - top);
            int downHigh = (int) (bottom - center);


            //针对各种高度小于圆角的情况
            if (topRadius == 0 && bottomRadius > 0) {
                bottomRadius = bottomRadius > barHigh ? barHigh : bottomRadius;
            } else if (topRadius > 0 && bottomRadius == 0) {
                topRadius = topRadius > barHigh ? barHigh : topRadius;
            } else if (topRadius > 0 && bottomRadius > 0) {
                if (topRadius + bottomRadius > barHigh) {
                    bottomRadius = barHigh / 2;
                    topRadius = bottomRadius;
                }
            }

            float tempBottom = center;
            if (bottomRadius > downHigh + 5) { //先绘制底部 圆角绘制不齐全的部分
                tempBottom -= (bottomRadius - downHigh);
                drawBottomFragmentCircle(c, mMaxHighlightPaint,
                        buffer[j], tempBottom, buffer[j+2], center,
                        bottomRadius);
            }

            //绘制上半部分的柱子 主体
            drawBar(c, mMaxHighlightPaint,
                    buffer[j], buffer[j+1], buffer[j+2], tempBottom,
                    topRadius, 0, borderType, false);

            float tempTop = center;

            if (topRadius > upHigh + 5) {  //对应的，如果有max 部分 存在圆角绘制不全的情况
                tempTop += (topRadius - upHigh);
                drawTopFragmentCircle(c, mHighlightPaint,
                        buffer[j], center, buffer[j+2], tempTop, topRadius);
            }
            //绘制下半部分的柱子 主体
            drawBar(c, mHighlightPaint,
                    buffer[j], tempTop, buffer[j+2], buffer[j+3],
                    0, bottomRadius, borderType, false);
        }

        if (mViewPortHandler.getSignPostRender().isShow()) {
            float barWidth = (buffer[j+2] - buffer[j]);
            float cursorX = buffer[j] + barWidth / 2;
            mViewPortHandler.getSignPostRender().setCurtSorX(cursorX);
            mViewPortHandler.getSignPostRender().setDrawCursorPreBean(c, cursorX, barWidth, mCursorPaint);
        }
    }

    //绘制柱状和对应的百分比内容
    private void drawBar(Canvas c, Paint paint,
                         float left, float top, float right, float bottom,
                         int topRadius, int bottomRadius, List<String> borderType, boolean drawBorder) {
        Path path = new Path();
        path.reset();


        float leftx = left;
        float topx = top;
        float rightx = right;
        float bottomx = bottom;

        if (topRadius != 0 && bottomRadius != 0) {
            int hight = (int) (bottom - top);
            if (topRadius + bottomRadius > hight) {
                topRadius = hight >> 1;
                bottomRadius = topRadius;
            }
        }
        float halWidth = (right - left) / 2;
        if (topRadius > halWidth) {
            topRadius = (int) halWidth;
        }

        if (bottomRadius > halWidth) {
            bottomRadius = (int) halWidth;
        }

        float sweepBottomAngle = 90;
        if (bottom < top + bottomRadius) { //计算空间不足的位置
            float high = bottomRadius - (bottom - top);
            sweepBottomAngle = (float) (Math.acos(high / ((float) bottomRadius)));
            sweepBottomAngle = (float) Math.toDegrees(sweepBottomAngle);
            int offset = (int) Math.sqrt(bottomRadius * bottomRadius - high * high);
            leftx = left + offset;
            rightx = right - offset;
        }
        int bottomDiameter = bottomRadius << 1;

        float sweepTopAngle = 90;
        if (bottom < top + topRadius) { //计算空间不足的位置
            float high = topRadius - (bottom - top);
            sweepTopAngle = (float) (Math.acos(high / ((float) topRadius)));
            sweepTopAngle = (float) Math.toDegrees(sweepTopAngle);
            int offset = (int) Math.sqrt(topRadius * topRadius - high * high);
            leftx = left + offset;
            rightx = right - offset;
        }

        //起点
        if (bottomRadius == 0) {
            path.moveTo(leftx, bottom); //左下角的点
        } else {
            path.moveTo(left + bottomRadius, bottom); //左下角的点
            RectF rectLeft = new RectF(left, bottom - bottomDiameter, left + bottomDiameter, bottom);
            path.arcTo(rectLeft, 90, sweepBottomAngle);
        }

        if (topRadius == 0) {
            path.lineTo(leftx, top); //左上角的点
            path.lineTo(rightx, top); //右上角的点
        } else { //绘制上边的圆角弧线
            if (sweepTopAngle == 90) {
                path.lineTo(left, top + topRadius); //左上角的点
            }
            int diameter = topRadius << 1;
            RectF rectLeft = new RectF(left, top, left + diameter, top + diameter);
            path.arcTo(rectLeft, 180 + (90 - sweepTopAngle), sweepTopAngle);

            path.lineTo(right - topRadius, top); //右上角的点

            RectF rectRight = new RectF(right - diameter, top, right, top + diameter);
            path.arcTo(rectRight, 270, sweepTopAngle);
//                path.lineTo(buffer.buffer[j + 2], buffer.buffer[j + 1]); //右上角的点
        }

        if (bottomRadius == 0) {
            path.lineTo(rightx, bottom); //右下角的点
        } else {
            if (sweepBottomAngle == 90) {
                path.lineTo(right, bottom - bottomRadius); //右下角的点
            }
            RectF rectRight = new RectF(right - bottomDiameter, bottom - bottomDiameter, right, bottom);
            path.arcTo(rectRight, 90 - sweepBottomAngle, sweepBottomAngle);
        }


        path.close();

        if (paint != null) {
            c.drawPath(path, paint);
        }

        //画边框
        if (drawBorder) {
            if (borderType != null && !borderType.isEmpty()) {
                if (borderType.contains("left")) {
                    Path leftPath = new Path();
                    if (bottomRadius == 0) {
                        leftPath.moveTo(leftx, bottom); //左下角的点
                    } else {
                        leftPath.moveTo(left, bottom - bottomDiameter); //左下角的点
                    }
                    if (topRadius == 0) {
                        leftPath.lineTo(leftx, top);
                    } else {
                        int topDiameter = topRadius << 1;
                        leftPath.lineTo(left, top + topDiameter);
                    }
                    c.drawPath(leftPath, mBarBorderPaint);
                }

                if (borderType.contains("top")) {
                    Path topPath = new Path();
                    if (topRadius == 0) {
                        topPath.moveTo(leftx, top); //左上角的点
                        topPath.lineTo(rightx, top); //右上角的点
                    } else {
                        int topDiameter = topRadius << 1;
                        if (sweepTopAngle == 90) {
                            topPath.moveTo(left, top + topRadius); //左上角的点
                        } else {
                            topPath.moveTo(left, top + topDiameter);
                        }
                        RectF rectLeft = new RectF(left, top, left + topDiameter, top + topDiameter);
                        topPath.arcTo(rectLeft, 180 + (90 - sweepTopAngle), sweepTopAngle);
                        topPath.lineTo(right - topRadius, top); //右上角的点
                        RectF rectRight = new RectF(right - topDiameter, top, right, top + topDiameter);
                        topPath.arcTo(rectRight, 270, sweepTopAngle);
                    }
                    c.drawPath(topPath, mBarBorderPaint);
                }

                if (borderType.contains("right")) {
                    Path rightPath = new Path();
                    if (topRadius == 0) {
                        rightPath.moveTo(rightx, top);
                    } else {
                        int topDiameter = topRadius << 1;
                        rightPath.moveTo(right, top + topDiameter);
                    }
                    if (bottomRadius == 0) {
                        rightPath.lineTo(rightx, bottom);
                    } else {
                        rightPath.lineTo(right, bottom - bottomDiameter);
                    }
                    c.drawPath(rightPath, mBarBorderPaint);
                }

                if (borderType.contains("bottom")) {
                    Path bottomPath = new Path();
                    if (bottomRadius == 0) {
                        bottomPath.moveTo(rightx, bottom);
                        bottomPath.lineTo(leftx, bottom);
                    } else {
                        if (sweepBottomAngle == 90) {
                            bottomPath.moveTo(right, bottom - bottomRadius);
                        } else {
                            bottomPath.moveTo(right, bottom - bottomDiameter);
                        }
                        RectF rectRight = new RectF(right - bottomRadius, bottom - bottomDiameter, right, bottom);
                        bottomPath.arcTo(rectRight, 90 - sweepBottomAngle, sweepBottomAngle);
                        bottomPath.lineTo(left + bottomRadius, bottom);
                        RectF recfLeft = new RectF(left, bottom - bottomDiameter, left + bottomRadius, bottom);
                        bottomPath.arcTo(recfLeft, 90, sweepBottomAngle);
                    }
                    c.drawPath(bottomPath, mBarBorderPaint);
                }
            } else {
                //空默认全部
                c.drawPath(path, mBarBorderPaint);
            }
        }


    }


    private void drawTopFragmentCircle(Canvas c, Paint paint,
                                       float left, float top, float right, float bottom,
                                       int topRadius) {


        Path path = new Path();
        path.reset();


//        float halWidth = (right - left) / 2;
//        if (topRadius > halWidth) {
//            topRadius = (int) halWidth;
//        }

        float sweepBottomAngle = 90;

        float high = bottom - top;
        sweepBottomAngle = (float) (Math.asin(high / ((float) topRadius)));
        sweepBottomAngle = (float) Math.toDegrees(sweepBottomAngle);


        //起点
        path.moveTo(left, bottom); //左下角的点

        int diameter = topRadius << 1;
        RectF rectLeft = new RectF(left, bottom - topRadius, left + diameter, bottom + topRadius);
        path.arcTo(rectLeft, 180, sweepBottomAngle);


        RectF rectRight = new RectF(right - diameter, bottom - topRadius, right, bottom + topRadius);
        path.arcTo(rectRight, 270 + (90 - sweepBottomAngle), sweepBottomAngle);

        path.lineTo(right, top); //右上角的点
        path.lineTo(right, bottom);

        path.close();

        if (paint != null) {
            c.drawPath(path, paint);
        }
    }

    private void drawBottomFragmentCircle(Canvas c, Paint paint,
                                          float left, float top, float right, float bottom,
                                          int bottomRadius) {

        Path path = new Path();
        path.reset();
//
//        float halWidth = (right - left) / 2;
//        if (bottomRadius > halWidth) {
//            bottomRadius = (int) halWidth;
//        }

        float sweepTopAngle = 90;

        float high = bottom - top;
        sweepTopAngle = (float) (Math.asin(high / ((float) bottomRadius)));
        sweepTopAngle = (float) Math.toDegrees(sweepTopAngle);

        //起点
        path.moveTo(right, top); //右上角的点
        int diameter = bottomRadius << 1;

        RectF rectRight = new RectF(right - diameter, top - bottomRadius, right, top + bottomRadius);
        path.arcTo(rectRight, 0, sweepTopAngle);


        RectF rectLeft = new RectF(left, top - bottomRadius, left + diameter, top + bottomRadius);
        path.arcTo(rectLeft, 90 + (90 - sweepTopAngle), sweepTopAngle);


        path.close();

        if (paint != null) {
            c.drawPath(path, paint);
        }

    }

    protected void prepareBarHighlight(float x, float y1, float y2,
                                       float barWidthHalf, Transformer trans) {

        float left = x - barWidthHalf;
        float right = x + barWidthHalf;
        float top = y1;
        float bottom = y2;

        mBarRect.set(left, top, right, bottom);

        trans.rectToPixelPhase(mBarRect, mAnimator.getPhaseY());
    }

    @Override
    public void drawValues(Canvas c) {

        // if values are drawn
        if (isDrawingValuesAllowed(mChart)) {

            List<IBarDataSet> dataSets = mChart.getBarData().getDataSets();

            final float valueOffsetPlus = Utils.convertDpToPixel(4.5f);
            float posOffset = 0f;
            float negOffset = 0f;
            boolean drawValueAboveBar = mChart.isDrawValueAboveBarEnabled();

            for (int i = 0; i < mChart.getBarData().getDataSetCount(); i++) {

                IBarDataSet dataSet = dataSets.get(i);

                if (!shouldDrawValues(dataSet))
                    continue;

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet);

                boolean isInverted = mChart.isInverted(dataSet.getAxisDependency());

                // calculate the correct offset depending on the draw position of
                // the value
                float valueTextHeight = Utils.calcTextHeight(mValuePaint, "8");
                posOffset = (drawValueAboveBar ? -valueOffsetPlus : valueTextHeight + valueOffsetPlus);
                negOffset = (drawValueAboveBar ? valueTextHeight + valueOffsetPlus : -valueOffsetPlus);

                if (isInverted) {
                    posOffset = -posOffset - valueTextHeight;
                    negOffset = -negOffset - valueTextHeight;
                }

                // get the buffer
                BarBuffer buffer = mBarBuffers[i];

                final float phaseY = mAnimator.getPhaseY();

                MPPointF iconsOffset = MPPointF.getInstance(dataSet.getIconsOffset());
                iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x);
                iconsOffset.y = Utils.convertDpToPixel(iconsOffset.y);

                // if only single values are drawn (sum)
                if (!dataSet.isStacked()) {

                    for (int j = 0; j < buffer.buffer.length * mAnimator.getPhaseX(); j += 4) {

                        float x = (buffer.buffer[j] + buffer.buffer[j + 2]) / 2f;

                        if (!mViewPortHandler.isInBoundsRight(x))
                            break;

                        if (!mViewPortHandler.isInBoundsY(buffer.buffer[j + 1])
                                || !mViewPortHandler.isInBoundsLeft(x))
                            continue;

                        BarEntry entry = dataSet.getEntryForIndex(j / 4);
                        float val = entry.getY();

                        if (dataSet.isDrawValuesEnabled()) {
                            drawValue(c, dataSet.getValueFormatter(), val, entry, i, x,
                                    val >= 0 ?
                                            (buffer.buffer[j + 1] + posOffset) :
                                            (buffer.buffer[j + 3] + negOffset),
                                    dataSet.getValueTextColor(j / 4));
                        }

                        if (entry.getIcon() != null && dataSet.isDrawIconsEnabled()) {

                            Drawable icon = entry.getIcon();

                            float px = x;
                            float py = val >= 0 ?
                                    (buffer.buffer[j + 1] + posOffset) :
                                    (buffer.buffer[j + 3] + negOffset);

                            px += iconsOffset.x;
                            py += iconsOffset.y;

                            Utils.drawImage(
                                    c,
                                    icon,
                                    (int) px,
                                    (int) py,
                                    icon.getIntrinsicWidth(),
                                    icon.getIntrinsicHeight());
                        }
                    }

                    // if we have stacks
                } else {

                    Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

                    int bufferIndex = 0;
                    int index = 0;

                    while (index < dataSet.getEntryCount() * mAnimator.getPhaseX()) {

                        BarEntry entry = dataSet.getEntryForIndex(index);

                        float[] vals = entry.getYVals();
                        float x = (buffer.buffer[bufferIndex] + buffer.buffer[bufferIndex + 2]) / 2f;

                        int color = dataSet.getValueTextColor(index);

                        // we still draw stacked bars, but there is one
                        // non-stacked
                        // in between
                        if (vals == null) {

                            if (!mViewPortHandler.isInBoundsRight(x))
                                break;

                            if (!mViewPortHandler.isInBoundsY(buffer.buffer[bufferIndex + 1])
                                    || !mViewPortHandler.isInBoundsLeft(x))
                                continue;

                            if (dataSet.isDrawValuesEnabled()) {
                                drawValue(c, dataSet.getValueFormatter(), entry.getY(), entry, i, x,
                                        buffer.buffer[bufferIndex + 1] +
                                                (entry.getY() >= 0 ? posOffset : negOffset),
                                        color);
                            }

                            if (entry.getIcon() != null && dataSet.isDrawIconsEnabled()) {

                                Drawable icon = entry.getIcon();

                                float px = x;
                                float py = buffer.buffer[bufferIndex + 1] +
                                        (entry.getY() >= 0 ? posOffset : negOffset);

                                px += iconsOffset.x;
                                py += iconsOffset.y;

                                Utils.drawImage(
                                        c,
                                        icon,
                                        (int) px,
                                        (int) py,
                                        icon.getIntrinsicWidth(),
                                        icon.getIntrinsicHeight());
                            }

                            // draw stack values
                        } else {

                            float[] transformed = new float[vals.length * 2];

                            float posY = 0f;
                            float negY = -entry.getNegativeSum();

                            for (int k = 0, idx = 0; k < transformed.length; k += 2, idx++) {

                                float value = vals[idx];
                                float y;

                                if (value == 0.0f && (posY == 0.0f || negY == 0.0f)) {
                                    // Take care of the situation of a 0.0 value, which overlaps a non-zero bar
                                    y = value;
                                } else if (value >= 0.0f) {
                                    posY += value;
                                    y = posY;
                                } else {
                                    y = negY;
                                    negY -= value;
                                }

                                transformed[k + 1] = y * phaseY;
                            }

                            trans.pointValuesToPixel(transformed);

                            for (int k = 0; k < transformed.length; k += 2) {

                                final float val = vals[k / 2];
                                final boolean drawBelow =
                                        (val == 0.0f && negY == 0.0f && posY > 0.0f) ||
                                                val < 0.0f;
                                float y = transformed[k + 1]
                                        + (drawBelow ? negOffset : posOffset);

                                if (!mViewPortHandler.isInBoundsRight(x))
                                    break;

                                if (!mViewPortHandler.isInBoundsY(y)
                                        || !mViewPortHandler.isInBoundsLeft(x))
                                    continue;

                                if (dataSet.isDrawValuesEnabled()) {
                                    drawValue(c,
                                            dataSet.getValueFormatter(),
                                            vals[k / 2],
                                            entry,
                                            i,
                                            x,
                                            y,
                                            color);
                                }

                                if (entry.getIcon() != null && dataSet.isDrawIconsEnabled()) {

                                    Drawable icon = entry.getIcon();

                                    Utils.drawImage(
                                            c,
                                            icon,
                                            (int) (x + iconsOffset.x),
                                            (int) (y + iconsOffset.y),
                                            icon.getIntrinsicWidth(),
                                            icon.getIntrinsicHeight());
                                }
                            }
                        }

                        bufferIndex = vals == null ? bufferIndex + 4 : bufferIndex + 4 * vals.length;
                        index++;
                    }
                }

                MPPointF.recycleInstance(iconsOffset);
            }
        }
    }


    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        BarData barData = mChart.getBarData();

//        for (Highlight high : indices) {
        for (int i = 0; i < indices.length; i++) {

            Highlight high = indices[i];

            IBarDataSet set = barData.getDataSetByIndex(high.getDataSetIndex());


            if (set == null || !(set.isHighlightEnabled() || mViewPortHandler.getSignPostRender().isShow()))
                continue;

            BarEntry e = set.getEntryForXValue(high.getX(), high.getY());

            if (!isInBoundsX(e, set))
                continue;


            Transformer trans = mChart.getTransformer(set.getAxisDependency());

            mHighlightPaint.setColor(set.getHighLightColor());
            mMaxHighlightPaint.setColor(set.getMaxHighLightColor());


            boolean isStack = (high.getStackIndex() >= 0 && e.isStacked()) ? true : false;

            final float y1;
            final float y2;

            if (isStack) {

                if (mChart.isHighlightFullBarEnabled()) {

                    y1 = e.getPositiveSum();
                    y2 = -e.getNegativeSum();

                } else {

                    Range range = e.getRanges()[high.getStackIndex()];

                    y1 = range.from;
                    y2 = range.to;
                }

            } else {
                y1 = e.getY();
                y2 = 0.f;
            }

            prepareBarHighlight(e.getX(), y1, y2, barData.getBarWidth() / 2f, trans);

            setHighlightDrawPos(high, mBarRect);


            if (set.isHighlightEnabled()) {
                set.getRadius();
//                c.drawRect(mBarRect, mHighlightPaint);
//                float percent = e.getPercent();
//
//                RectF barRect = new RectF(mBarRect.left, mBarRect.top * percent, mBarRect.right, mBarRect.bottom);
//                c.drawRect(barRect, mMaxHighlightPaint);

                float percent = e.getPercent();


                if (percent == 1 || percent == 0) {

                    Paint paint = mHighlightPaint;
                    if (percent == 0 && mViewPortHandler.getSignPostRender().isShow()) {
                        paint = mMaxHighlightPaint;
                    }

                    drawBar(c, paint,
                            mBarRect.left, mBarRect.top, mBarRect.right, mBarRect.bottom,
                            set.getRadius(), set.getBottomRadius(), set.getBorderType(), false);
                } else {
                    float center = (mBarRect.bottom - mBarRect.top) * percent + mBarRect.top;
                    int topRadius = set.getRadius();
                    int bottomRadius = set.getBottomRadius();
                    int barHigh = (int) (mBarRect.bottom - mBarRect.top);
                    int upHigh = (int) (center - mBarRect.top);
                    int downHigh = (int) (mBarRect.bottom - center);

                    //宽度小于圆角的情况
                    float halWidth = (mBarRect.right - mBarRect.left) / 2;
                    if (topRadius > halWidth) {
                        topRadius = (int) halWidth;
                    }

                    if (bottomRadius > halWidth) {
                        bottomRadius = (int) halWidth;
                    }

                    //针对各种高度小于圆角的情况
                    if (topRadius == 0 && bottomRadius > 0) {
                        bottomRadius = bottomRadius > barHigh ? barHigh : bottomRadius;
                    } else if (topRadius > 0 && bottomRadius == 0) {
                        topRadius = topRadius > barHigh ? barHigh : topRadius;
                    } else if (topRadius > 0 && bottomRadius > 0) {
                        if (topRadius + bottomRadius > barHigh) {
                            bottomRadius = barHigh / 2;
                            topRadius = bottomRadius;
                        }
                    }

                    float tempBottom = center;
                    if (bottomRadius > downHigh + 5) { //先绘制底部 圆角绘制不齐全的部分
                        tempBottom -= (bottomRadius - downHigh);

                        drawBottomFragmentCircle(c, mMaxHighlightPaint,
                                mBarRect.left, tempBottom, mBarRect.right, center,
                                bottomRadius);
                        tempBottom -= 2; //不晓得为啥会有一个高亮的缝隙，所以这里补一下囧
                    }


                    drawBar(c, mMaxHighlightPaint,
                            mBarRect.left, mBarRect.top, mBarRect.right, tempBottom,
                            topRadius, 0, set.getBorderType(), false);


                    float tempTop = center;
                    if (topRadius > upHigh + 5) {  //对应的，如果有max 部分 存在圆角绘制不全的情况
                        tempTop += (topRadius - upHigh);
                        drawTopFragmentCircle(c, mHighlightPaint,
                                mBarRect.left, center, mBarRect.right, tempTop, topRadius);
                        tempTop -= 2;  //不晓得为啥会有一个高亮的缝隙，所以这里补一下囧
                    }

                    drawBar(c, mHighlightPaint,
                            mBarRect.left, tempTop, mBarRect.right, mBarRect.bottom,
                            0, bottomRadius, set.getBorderType(), false);
                }


            }


//            Log.i("jarvanTrace5"," value x is ->"+high.getValX()+" cursorX ");
            Log.i("jarvanTrace6", "high.getValX() ->" + high.getValX()
                    + " mViewPortHandler.getSignPostRender().setCurtSorX(event.getX()); x ->" + mViewPortHandler.getSignPostRender().getCurtSorX());
            MotionEvent event = mViewPortHandler.getSignPostRender().getEvent();

            float barWidth = mBarRect.right - mBarRect.left;
            if (event != null && event.getAction() == event.ACTION_MOVE) { //是否正在滑动
                mViewPortHandler.getSignPostRender().setCurtSorX(event.getX());
                mViewPortHandler.getSignPostRender().drawCursor(c, event.getX(), barWidth, mCursorPaint );
            } else { //是否已经松手了
                float cursorX = mBarRect.left + barWidth / 2;
                mViewPortHandler.getSignPostRender().setCurtSorX(cursorX);
                mViewPortHandler.getSignPostRender().drawCursor(c, cursorX, barWidth, mCursorPaint);
            }


//            if (event != null  ) { //是否正在滑动
//                if (event.getAction() == event.ACTION_MOVE){
//                    mViewPortHandler.getSignPostRender().drawCursor(c, event.getX(), mCursorPaint,false);
//                } else if (event.getAction() == event.ACTION_UP){
//                    mViewPortHandler.getSignPostRender().setEvent(null);
//                    mViewPortHandler.
//                }
//            } else { //是否已经松手了
//                float cursorX = mBarRect.left + (mBarRect.right - mBarRect.left) / 2;
//                mViewPortHandler.getSignPostRender().setCurtSorX(cursorX);
//                mViewPortHandler.getSignPostRender().drawCursor(c, cursorX, mCursorPaint,true);
//            }

            mViewPortHandler.getSignPostRender().checkIsNeedToShake(mBarRect.left, mBarRect.right);

            SignPostRender.OnBarClickListener onBarClickListener = mViewPortHandler.getSignPostRender().getOnBarClickListener(mViewPortHandler);
            int barIndex = (int) high.getX();
            mViewPortHandler.getSignPostRender().setCurrentClickBar(barIndex);
            if (onBarClickListener != null && (lastGroupIndex != high.getDataSetIndex() || lastBarIndex != barIndex)) {
                onBarClickListener.onBarClick(high.getDataSetIndex(), barIndex);
                lastGroupIndex = high.getDataSetIndex();
                lastBarIndex = barIndex;
            }

        }
    }


    /**
     * Sets the drawing position of the highlight object based on the riven bar-rect.
     *
     * @param high
     */
    protected void setHighlightDrawPos(Highlight high, RectF bar) {
        high.setDraw(bar.centerX(), bar.top);
    }

    @Override
    public void drawExtras(Canvas c) {
    }
}
