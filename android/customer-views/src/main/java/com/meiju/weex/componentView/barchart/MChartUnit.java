package com.meiju.weex.componentView.barchart;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class MChartUnit  {
    /**
     * 图表单位类
     * 需要最后绘制在图表上面
     */
    public static final int TOP = 0x000001;
    public static final int BOTTOM = 0x000002;
    public static final int LEFT = 0x000003;
    public static final int RIGHT = 0x000004;
    private int xLeftOffset;
    private int xTopOffset;
    private int xRightOffset;
    private int xBottomOffset;
    private int yLeftOffset;
    private int yTopOffset;
    private int yRightOffset;
    private int yBottomOffset;
    private float xTextSize;
    private float yTextSize;
    private int xLabelColor;
    private int yLabelColor;

    public int getxLeftOffset() {
        return xLeftOffset;
    }

    public void setxLeftOffset(int xLeftOffset) {
        this.xLeftOffset = xLeftOffset;
    }

    public int getxTopOffset() {
        return xTopOffset;
    }

    public void setxTopOffset(int xTopOffset) {
        this.xTopOffset = xTopOffset;
    }

    public int getxRightOffset() {
        return xRightOffset;
    }

    public void setxRightOffset(int xRightOffset) {
        this.xRightOffset = xRightOffset;
    }

    public int getxBottomOffset() {
        return xBottomOffset;
    }

    public void setxBottomOffset(int xBottomOffset) {
        this.xBottomOffset = xBottomOffset;
    }

    public int getyLeftOffset() {
        return yLeftOffset;
    }

    public void setyLeftOffset(int yLeftOffset) {
        this.yLeftOffset = yLeftOffset;
    }

    public int getyTopOffset() {
        return yTopOffset;
    }

    public void setyTopOffset(int yTopOffset) {
        this.yTopOffset = yTopOffset;
    }

    public int getyRightOffset() {
        return yRightOffset;
    }

    public void setyRightOffset(int yRightOffset) {
        this.yRightOffset = yRightOffset;
    }

    public int getyBottomOffset() {
        return yBottomOffset;
    }

    public void setyBottomOffset(int yBottomOffset) {
        this.yBottomOffset = yBottomOffset;
    }

    public String getxLable() {
        return xLable;
    }

    public String getyLable() {
        return yLable;
    }

    @IntDef({TOP, BOTTOM, LEFT, RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface POSITION {
    }

    private final String xLable;
    private final String yLable;

    private int xPosition;
    private int yPosition;

    public MChartUnit(String x, String y) {
        this.xLable = x;
        this.yLable = y;
    }

    public int getxLabelColor() {
        return xLabelColor;
    }

    public void setxLabelColor(int xLabelColor) {
        this.xLabelColor = xLabelColor;
    }

    public int getyLabelColor() {
        return yLabelColor;
    }

    public void setyLabelColor(int yLabelColor) {
        this.yLabelColor = yLabelColor;
    }

    public float getxTextSize() {
        return xTextSize;
    }

    public void setxTextSize(float xTextSize) {
        this.xTextSize = xTextSize;
    }

    public float getyTextSize() {
        return yTextSize;
    }

    public void setyTextSize(float yTextSize) {
        this.yTextSize = yTextSize;
    }

    public void setXPosition(@POSITION int position) {
        this.xPosition = position;
    }

    public void setXOffSet(int left, int top, int right, int bottom) {
        this.xLeftOffset = left;
        this.xTopOffset = top;
        this.xRightOffset = right;
        this.xBottomOffset = bottom;
    }

    public void setYOffSet(int left, int top, int right, int bottom) {
        this.yLeftOffset = left;
        this.yTopOffset = top;
        this.yRightOffset = right;
        this.yBottomOffset = bottom;
    }

    public void setYPosition(@POSITION int position) {
        this.yPosition = position;
    }


}

