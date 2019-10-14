package com.github.mikephil.charting.renderer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.MotionEvent;

import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.meiju.weex.componentView.barchart.MChartBean;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignPostRender {
    private static SignPostRender instance;
    MotionEvent event;
    MChartBean.SignPost signPostBean;
    float curtSorX = -1;
    float labelBottom;
    int signPostLineY;
    Bitmap cursorBitmap;
    int labelMaxCountLimit = Integer.MAX_VALUE; // 标杆滑动线和点的限制内容
    //    OnBarClickListener onBarClickListener = null;
    Map<ViewPortHandler, OnBarClickListener> barClickListenerMap = new HashMap<>();
    //    private SignPostRender() {
//
//    }
//
//    public static SignPostRender getInstance() {
//        if (instance == null) {
//            synchronized (SignPostRender.class) {
//                if (instance == null) {
//                    instance = new SignPostRender();
//                }
//            }
//        }
//        return instance;
//    }
    List<Float> lastLabelXCoors = null;
    List<Float> currentLabelXCoors = null;
    int currentClickBar = -1;
    PreDrawCursorBean preDrawCursorBean = null;
    Vibrator vibrator = null;
    private float barWidth = 0;

    public void clear(){
        event = null;
        currentClickBar = -1;
        curtSorX = -1;
    }


    public MotionEvent getEvent() {
        return event;
    }

    public void setEvent(MotionEvent event) {
        this.event = event;
    }

    public float getCurtSorX() {
        return curtSorX;
    }

    public void setCurtSorX(float curtSorX) {
        this.curtSorX = curtSorX;
    }

    public int getLabelMaxCountLimit() {
        return labelMaxCountLimit;
    }

    public void setLabelMaxCountLimit(int labelMaxCountLimit) {
        this.labelMaxCountLimit = labelMaxCountLimit;
    }

    public boolean isShow() {
        if (signPostBean == null) {
            return false;
        }
        return signPostBean.isShow();
    }

    public float getLabelBottom() {
        return labelBottom;
    }

    public void setLabelBottom(float labelBottom) {
        this.labelBottom = labelBottom;
    }

    public MChartBean.SignPost getSignPostBean() {
        return signPostBean;
    }

    public void setSignPostBean(MChartBean.SignPost signPostBean) {
        this.signPostBean = signPostBean;
        if (signPostBean != null && !TextUtils.isEmpty(signPostBean.getCursorImageBase64())) {
            cursorBitmap = Utils.base64ToBitmap(signPostBean.getCursorImageBase64());
        }
    }

    /**
     * 绘制标杆的坐标线
     *
     * @param c
     * @param lastX
     * @param x
     * @param signPostLinePaint
     */
    public void drawLineAndPoint(Canvas c, float lastX, float x, Paint signPostLinePaint) {
        if (signPostBean == null) {
            return;
        }
        if (signPostBean.isShow()) {
            signPostLineY = (int) (labelBottom + signPostBean.getLineMarginTop() + signPostBean.getLinePointRadius());
            if (lastX > 0) {
                //绘制标杆的位置线
                c.drawLine(lastX, signPostLineY, x, signPostLineY, signPostLinePaint);
            }

            if (signPostBean.isSelectedDisappear() && curtSorX > 0) {
                float width = signPostBean.getLinePointRadius() + signPostBean.getCursorHigh() / 2;
                if ((curtSorX + width > x && curtSorX - width < x) || (signPostBean.isNeedMarkerLine() && curtSorX + barWidth > x && curtSorX - barWidth < x)) {
                    //选中的圆点，则不绘制
                    return;
                }
            }

            //绘制标杆的圆点
            c.drawCircle(x, signPostLineY, signPostBean.getLinePointRadius(), signPostLinePaint);
        }
    }

    /**
     * 绘制标杆的坐标线
     *
     * @param c
     * @param labelXCoors
     * @param signPostLinePaint
     */
    public void drawLineAndPoint(Canvas c, List<Float> labelXCoors, Paint signPostLinePaint) {
        if (signPostBean == null || labelXCoors == null || labelXCoors.size() <= 1) {
            return;
        }
        currentLabelXCoors = labelXCoors;
        // TODO: 2019/3/18 如果只有 1，2 柱状图数据 填充数据补齐,否则会有不对称的情况出现 ，这里要进行相关的去掉
        if (labelMaxCountLimit == 1 || labelMaxCountLimit == 2) {
            labelXCoors.remove(0); //去掉头数据
            if (labelXCoors.size() < 2) {
                return;
            }
            labelXCoors.remove(labelXCoors.size() - 1); //去掉尾数据
        }
        int size = labelMaxCountLimit < labelXCoors.size() ? labelMaxCountLimit : labelXCoors.size();
        Log.i("jarvanPoint", "  labelXCoors  size is ->" + labelXCoors.size());
        if ("none".equalsIgnoreCase(signPostBean.getShowType())) { //不描绘端点
            signPostLineY = (int) (labelBottom + signPostBean.getLineMarginTop() + signPostBean.getLinePointRadius());
            float start = labelXCoors.get(0);
            float end = labelXCoors.get(size - 1);
            c.drawLine(start, signPostLineY, end, signPostLineY, signPostLinePaint);
        } else if ("ends".equalsIgnoreCase(signPostBean.getShowType())) {
            float start = labelXCoors.get(0);
            float end = labelXCoors.get(size - 1);
            drawLineAndPoint(c, -1, start, signPostLinePaint);
            drawLineAndPoint(c, start, end, signPostLinePaint);
        } else {
            float lastX = -1;
            for (int i = 0; i < size; i++) {
                Float f = labelXCoors.get(i);
                drawLineAndPoint(c, lastX, f, signPostLinePaint);
                lastX = f;
            }
        }

        //补充开头和末尾的线段
        if (signPostBean.isNeedMarkerLine()) {
            float xSpace = labelXCoors.get(1) - labelXCoors.get(0);

            float leftXStart = labelXCoors.get(0) - xSpace / 2;
            float leftXEnd = labelXCoors.get(0);
            c.drawLine(leftXStart, signPostLineY, leftXEnd, signPostLineY, signPostLinePaint);

            float rightXStart = labelXCoors.get(labelXCoors.size() - 1);
            float rightXEnd = labelXCoors.get(labelXCoors.size() - 1) + xSpace / 2;
            c.drawLine(rightXStart, signPostLineY, rightXEnd, signPostLineY, signPostLinePaint);

            if (curtSorX > 0) {
                Paint markerLinePaint = new Paint();
                markerLinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
                markerLinePaint.setStrokeWidth(6f);
                markerLinePaint.setColor(signPostBean.getCursorColorParse());
                c.drawLine(curtSorX - barWidth / 2, signPostLineY, curtSorX + barWidth / 2, signPostLineY, markerLinePaint);
            }
        }

        lastLabelXCoors = labelXCoors;
    }

    public void setDrawCursorPreBean(Canvas c, float cursorX, float barWidth, Paint cursorPaint) {
        preDrawCursorBean = new PreDrawCursorBean();
        preDrawCursorBean.c = c;
        preDrawCursorBean.cursorX = cursorX;
        preDrawCursorBean.barWidth = barWidth;
        preDrawCursorBean.cursorPaint = cursorPaint;
    }

    public void drawCursorByPreBean() {
        //防止意外惊喜
        PreDrawCursorBean preDrawCursorBean = this.preDrawCursorBean;
        this.preDrawCursorBean = null;

        if (preDrawCursorBean != null) {
            drawCursor(preDrawCursorBean.c, preDrawCursorBean.cursorX, preDrawCursorBean.barWidth, preDrawCursorBean.cursorPaint);
        }
    }

    /**
     * 绘制 标杆的 图形 和 三角形
     *
     * @param c
     * @param cursorX
     * @param cursorPaint
     */
    public void drawCursor(Canvas c, float cursorX, float barWidth, Paint cursorPaint) {
        if (signPostBean == null) {
            return;
        }
        List<Float> labelXCoors = currentLabelXCoors;
        if (labelXCoors != null && labelXCoors.size() > 0) {
            float first = labelXCoors.get(0);
            if (cursorX < first) {
                cursorX = first;
            }

            int size = labelMaxCountLimit < labelXCoors.size() ? labelMaxCountLimit : labelXCoors.size();
            if (size >= 1) {
                float last = labelXCoors.get(size - 1);
                if (cursorX > last) {
                    cursorX = last;
                }
            }
//            Log.i("jarvanPoint", "first cursorX is ->" + first + "  last cursorX is ->" + last);
        }
        Log.i("jarvanPoint", "drawCursor  cursorX is ->" + cursorX);

        float pos = signPostLineY + signPostBean.getLinePointRadius() + signPostBean.getLineMarginBottom() + signPostBean.getCursorMarginTop();
        //绘制光标三角形
        cursorPaint.setColor(signPostBean.getCursorColorParse());
//        float length = 40 / 2;
//        float cursorHigh = (float) (length * Math.sqrt(3));
        float length = (float) (signPostBean.getCursorHigh() / Math.sqrt(3)); //根据等边三角形的高得出边长的一半
        float cursorBottom = pos + signPostBean.getCursorHigh();
        if (cursorBitmap == null) {
            Path path = new Path();
            path.moveTo(cursorX, pos);
            path.lineTo(cursorX + length, cursorBottom);
            path.lineTo(cursorX - length, cursorBottom);
            path.close();
            c.drawPath(path, cursorPaint);
        } else {
            c.drawBitmap(cursorBitmap, cursorX - cursorBitmap.getWidth() / 2, pos + cursorBitmap.getHeight() / 2, cursorPaint);
        }

        if (signPostBean.isNeedMarkerLine()) {
            this.barWidth = barWidth;
        }

//        if (isRefreshLineAndPoint) {
//            drawLineAndPoint(c, lastLabelXCoors, cursorPaint);
//        }
    }


//    boolean lastIsInBar = false;
    float lastBarLeft = -1;
    float lastBarRight = -1;
    public void checkIsNeedToShake(float barLeft, float barRight) {
        if (vibrator == null) {
            return;
        }
        boolean isShake = false;
        if (lastBarLeft!=barLeft && lastBarRight!=barRight){
            isShake  =true;
        }
        lastBarLeft = barLeft;
        lastBarRight = barRight;
//        if (curtSorX > 0) {
//            float width = signPostBean.getLinePointRadius() + signPostBean.getCursorHigh() / 2;
//            if (curtSorX + width > barLeft && curtSorX - width < barRight) {
//                //滑动进入
//                if (!lastIsInBar) { //初次或者新的一次滑动进入才会需要显示震动
//                    isShake = true;
//                    lastIsInBar = true;
//                }
//            } else if (curtSorX + width * 1.2 < barLeft || curtSorX - width * 1.2 > barRight) {
//                //滑动离开某个点
//                lastIsInBar = false;
//            }
//        }



        if (isShake) {
            vibrator.vibrate(18);
//            vibrator.vibrate(pattern,-1);
        }
    }

    long[] pattern = new long[]
            {5,1,10,1,
            15,1,};

    public void setVibrator(Vibrator vibrator) {
        this.vibrator = vibrator;
    }

    OnBarClickListener getOnBarClickListener(ViewPortHandler viewPortHandler) {
        return barClickListenerMap.get(viewPortHandler);
//        return this.onBarClickListener;
    }

    public int getCurrentClickBar() {
        return this.currentClickBar;
    }

    public void setCurrentClickBar(int currentClickBar) {
        this.currentClickBar = currentClickBar;
    }

    public void setOnBarClickListener(ViewPortHandler viewPortHandler, OnBarClickListener onBarClickListener) {
//        this.onBarClickListener = onBarClickListener;
        barClickListenerMap.put(viewPortHandler, onBarClickListener);
    }

    public interface OnBarClickListener {
        void onBarClick(int groupIndex, int barIndex);
    }

    private static class PreDrawCursorBean {
        Canvas c;
        float cursorX;
        float barWidth;
        Paint cursorPaint;
        boolean isRefreshLineAndPoint;
    }

}
