package com.meiju.weex.componentView.barchart;

import java.util.Collections;
import java.util.List;

public class MChartBean {
    private axis x;
    private List<axis> y;
    private String description;
    private Legend legend;
    private float YMax;
    private float YMin;
    private Unit unit;
    private SignPost signPost;
    private int borderRadius;
    private int bottomBorderRadius;
    private float borderWidth;
    private String borderColor;
    private List<String> borderType;
    private String background;
    private String xAxisColor; //x轴线的颜色
    private String yAxisColor; //y轴线的颜色
    private boolean yRightAxisEnable = false; //是否显示右边的坐标轴
    private String yRightAxisColor; //y右边轴线的颜色
    private String xAxisLabelColor; //x label的字体颜色
    private String yAxisLabelColor; //y label的字体颜色
    private String yRightAxisLabelColor; //y右边 label的字体颜色
    private String xAxisGridColor; //x轴上分割线颜色
    private String xAxisGridAlpha; //x轴分割线颜色
    private boolean drawYGridLine = false;
    private String yGridColor;
    private int barSpacing;
    private int barWidth; //柱子的粗细设置
    private boolean needScroll = true;
    private boolean yAxisLabelShow = true;
    private float granularity = 1f;
    private boolean legendHide = false;
    private boolean drawValues = true; //是否显示柱子上的数字
    private String valueTextColor;  //柱子上数字的字体颜色
    private float valueTextSize;    //柱子上数字的字体大小，默认17f
    private int yAxisGridLine = -1; //因为折线图和柱状图都使用这个属性，但是默认数据样式又不一样，所以这里改成数字形式
    private int xAxisGridLine = -1; //因为折线图和柱状图都使用这个属性，但是默认数据样式又不一样，所以这里改成数字形式;
    private String axisGridColor = null;
    private String yGraduationLabel = "";
    private String yRightGraduationLabel = "";
    private String xAxisLabelHighLightColor = null;
    private boolean xAxisLabelHighLightThicke = false;
    private int barSelectIndex = -1;
    private String barTouchTop;
    private boolean yAxisLabelEnable = true;
    private float yAxisMaximum = -1f;
    private float yRightAxisMaximum = -1f;

    private int yAxisLabelCount = -1; //组件源码里面，默认就是6个
    private int yRightAxisLabelCount = -1; //组件源码里面，默认就是6个

    private int yAxisLabelIgnoreCount = -1;

    private int yRightAxisLabelIgnoreCount = -1;

    private int yAxisLabelDecimal = 0;

    private int yRightAxisLabelDecimal = 0;

    private int xAxisLabelTextSize = -1;

    private int yAxisLabelTextSize = -1;

    private int yRightAxisLabelTextSize = -1;

    public int getxAxisLabelTextSize() {
        return xAxisLabelTextSize;
    }

    public void setxAxisLabelTextSize(int xAxisLabelTextSize) {
        this.xAxisLabelTextSize = xAxisLabelTextSize;
    }

    public int getyAxisLabelTextSize() {
        return yAxisLabelTextSize;
    }

    public void setyAxisLabelTextSize(int yAxisLabelTextSize) {
        this.yAxisLabelTextSize = yAxisLabelTextSize;
    }

    public int getyRightAxisLabelTextSize() {
        return yRightAxisLabelTextSize;
    }

    public void setyRightAxisLabelTextSize(int yRightAxisLabelTextSize) {
        this.yRightAxisLabelTextSize = yRightAxisLabelTextSize;
    }

    public int getyAxisLabelCount() {
        return yAxisLabelCount;
    }

    public void setyAxisLabelCount(int yAxisLabelCount) {
        this.yAxisLabelCount = yAxisLabelCount;
    }

    public int getyRightAxisLabelIgnoreCount() {
        return yRightAxisLabelIgnoreCount;
    }

    public void setyRightAxisLabelIgnoreCount(int yRightAxisLabelIgnoreCount) {
        this.yRightAxisLabelIgnoreCount = yRightAxisLabelIgnoreCount;
    }

    public int getyRightAxisLabelCount() {
        return yRightAxisLabelCount;
    }

    public int getyAxisLabelDecimal() {
        return yAxisLabelDecimal;
    }

    public void setyAxisLabelDecimal(int yAxisLabelDecimal) {
        this.yAxisLabelDecimal = yAxisLabelDecimal;
    }

    public int getyRightAxisLabelDecimal() {
        return yRightAxisLabelDecimal;
    }

    public void setyRightAxisLabelDecimal(int yRightAxisLabelDecimal) {
        this.yRightAxisLabelDecimal = yRightAxisLabelDecimal;
    }

    public void setyRightAxisLabelCount(int yRightAxisLabelCount) {
        this.yRightAxisLabelCount = yRightAxisLabelCount;
    }

    public int getyAxisLabelIgnoreCount() {
        return yAxisLabelIgnoreCount;
    }

    public void setyAxisLabelIgnoreCount(int yAxisLabelIgnoreCount) {
        this.yAxisLabelIgnoreCount = yAxisLabelIgnoreCount;
    }

    public boolean isyAxisLabelEnable() {
        return yAxisLabelEnable;
    }

    public void setyAxisLabelEnable(boolean yAxisLabelEnable) {
        this.yAxisLabelEnable = yAxisLabelEnable;
    }

    public String getBarTouchTop() {
        return barTouchTop;
    }

    public void setBarTouchTop(String barTouchTop) {
        this.barTouchTop = barTouchTop;
    }

    public int getBarSelectIndex() {
        return barSelectIndex;
    }

    public void setBarSelectIndex(int barSelectIndex) {
        this.barSelectIndex = barSelectIndex;
    }

    public boolean isxAxisLabelHighLightThicke() {
        return xAxisLabelHighLightThicke;
    }

    public void setxAxisLabelHighLightThicke(boolean xAxisLabelHighLightThicke) {
        this.xAxisLabelHighLightThicke = xAxisLabelHighLightThicke;
    }

    public String getxAxisLabelHighLightColor() {
        return xAxisLabelHighLightColor;
    }

    public void setxAxisLabelHighLightColor(String xAxisLabelHighLightColor) {
        this.xAxisLabelHighLightColor = xAxisLabelHighLightColor;
    }

    public String getyGraduationLabel() {
        return yGraduationLabel;
    }

    public void setyGraduationLabel(String yGraduationLabel) {
        this.yGraduationLabel = yGraduationLabel;
    }

    public String getyRightGraduationLabel() {
        return yRightGraduationLabel;
    }

    public void setyRightGraduationLabel(String yRightGraduationLabel) {
        this.yRightGraduationLabel = yRightGraduationLabel;
    }

    public int getYAxisGridLine() {
        return yAxisGridLine;
    }

    public void setyAxisGridLine(boolean yAxisGridLine) {
        this.yAxisGridLine = yAxisGridLine ? 1 : 0;
    }

    public int getXAxisGridLine() {
        return xAxisGridLine;
    }

    public void setxAxisGridLine(boolean xAxisGridLine) {
        this.xAxisGridLine = xAxisGridLine ? 1 : 0;
    }

    public String getAxisGridColor() {
        return axisGridColor;
    }

    public void setAxisGridColor(String axisGridColor) {
        this.axisGridColor = axisGridColor;
    }

    public int getBarSpacing() {
        return barSpacing;
    }

    public void setBarSpacing(int barSpacing) {
        this.barSpacing = barSpacing;
    }

    public int getBarWidth() {
        return barWidth;
    }

    public void setBarWidth(int barWidth) {
        this.barWidth = barWidth;
    }

    public boolean isNeedScroll() {
        return needScroll;
    }

    public void setNeedScroll(boolean needScroll) {
        this.needScroll = needScroll;
    }

    public SignPost getSignPost() {
        return signPost;
    }

    public void setSignPost(SignPost signPost) {
        this.signPost = signPost;
    }

    public int getBottomBorderRadius() {
        return bottomBorderRadius;
    }

    public void setBottomBorderRadius(int bottomBorderRadius) {
        this.bottomBorderRadius = bottomBorderRadius;
    }

    public float getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
    }

    public String getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    public List<String> getBorderType() {
        return borderType;
    }

    public void setBorderType(List<String> borderType) {
        this.borderType = borderType;
    }

    public boolean getLegendHide() {
        return this.legendHide;
    }

    public void setLegendHide(boolean legendHide) {
        this.legendHide = legendHide;
    }

    public float getGranularity() {
        return granularity;
    }

    public void setGranularity(float granularity) {
        this.granularity = granularity;
    }

    public boolean isyAxisLabelShow() {
        return yAxisLabelShow;
    }

    public void setyAxisLabelShow(boolean yAxisLabelShow) {
        this.yAxisLabelShow = yAxisLabelShow;
    }

    public String getxAxisGridAlpha() {
        return xAxisGridAlpha;
    }

    public void setxAxisGridAlpha(String xAxisGridAlpha) {
        this.xAxisGridAlpha = xAxisGridAlpha;
    }

    public boolean isDrawYGridLine() {
        return drawYGridLine;
    }

    public void setDrawYGridLine(boolean drawYGridLine) {
        this.drawYGridLine = drawYGridLine;
    }

    public String getyGridColor() {
        return yGridColor;
    }

    public void setyGridColor(String yGridColor) {
        this.yGridColor = yGridColor;
    }

    public String getxAxisGridColor() {
        return xAxisGridColor;
    }

    public void setxAxisGridColor(String xAxisGridColor) {
        this.xAxisGridColor = xAxisGridColor;
    }

    public String getxAxisColor() {
        return xAxisColor;
    }

    public void setxAxisColor(String xAxisColor) {
        this.xAxisColor = xAxisColor;
    }

    public String getyAxisColor() {
        return yAxisColor;
    }

    public void setyAxisColor(String yAxisColor) {
        this.yAxisColor = yAxisColor;
    }

    public boolean isyRightAxisEnable() {
        return yRightAxisEnable;
    }

    public void setyRightAxisEnable(boolean yRightAxisEnable) {
        this.yRightAxisEnable = yRightAxisEnable;
    }

    public String getyRightAxisColor() {
        return yRightAxisColor;
    }

    public void setyRightAxisColor(String yRightAxisColor) {
        this.yRightAxisColor = yRightAxisColor;
    }

    public String getxAxisLabelColor() {
        return xAxisLabelColor;
    }

    public void setxAxisLabelColor(String xAxisLabelColor) {
        this.xAxisLabelColor = xAxisLabelColor;
    }

    public String getyAxisLabelColor() {
        return yAxisLabelColor;
    }

    public void setyAxisLabelColor(String yAxisLabelColor) {
        this.yAxisLabelColor = yAxisLabelColor;
    }

    public String getyRightAxisLabelColor() {
        return yRightAxisLabelColor;
    }

    public void setyRightAxisLabelColor(String yRightAxisLabelColor) {
        this.yRightAxisLabelColor = yRightAxisLabelColor;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String backgroud) {
        this.background = backgroud;
    }

    public int getBorderRadius() {
        return borderRadius;
    }

    public void setBorderRadius(int borderRadius) {
        this.borderRadius = borderRadius;
    }

    public axis getX() {
        return x;
    }

    public void setX(axis x) {
        this.x = x;
    }

    public List<axis> getY() {
        return y;
    }

    public void setY(List<axis> y) {
        this.y = y;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Legend getLegend() {
        return legend;
    }

    public void setLegend(Legend legend) {
        this.legend = legend;
    }

    public float getXMax() {
        return Collections.max(getX().getValue());
    }

    public float getyAxisMaximum() {
        return yAxisMaximum;
    }

    public void setyAxisMaximum(float yAxisMaximum) {
        this.yAxisMaximum = yAxisMaximum;
    }

    public float getyRightAxisMaximum() {
        return yRightAxisMaximum;
    }

    public void setyRightAxisMaximum(float yRightAxisMaximum) {
        this.yRightAxisMaximum = yRightAxisMaximum;
    }

    public float getYMax() {
        Float yMax = 0f;
        Float yMaxTemp;
        for (axis yAxis : getY()) {
            yMaxTemp = Collections.max(yAxis.getValue());
            yMax = Double.valueOf((yMax > yMaxTemp ? yMax : yMaxTemp)).floatValue();
        }
        return yMax;
    }

    public float getYMin() {
        Float yMin = 0f;
        Float yMinTemp;
        for (axis xAxis : getY()) {
            yMinTemp = Collections.min(xAxis.getValue());
            yMin = Double.valueOf((yMin < yMinTemp ? yMin : yMinTemp)).floatValue();
        }
        return yMin;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public boolean isDrawValues() {
        return drawValues;
    }

    public void setDrawValues(boolean drawValues) {
        this.drawValues = drawValues;
    }

    public String getValueTextColor() {
        return valueTextColor;
    }

    public void setValueTextColor(String valueTextColor) {
        this.valueTextColor = valueTextColor;
    }

    public float getValueTextSize() {
        return valueTextSize;
    }

    public void setValueTextSize(float valueTextSize) {
        this.valueTextSize = valueTextSize;
    }

    public static class Legend {
        private String postion;
        private String orientation;
        private boolean show = true;

        public boolean isShow() {
            return this.show;
        }

        public void setShow(boolean show) {
            this.show = show;
        }

        public String getPostion() {
            return postion;
        }

        public void setPostion(String postion) {
            this.postion = postion;
        }

        public String getOrientation() {
            return orientation;
        }

        public void setOrientation(String orientation) {
            this.orientation = orientation;
        }
    }

    /**
     * 坐标轴x,y显示名称bean
     */
    public static class Unit {
        /**
         * x轴需要显示的名称
         */
        private String x;
        /**
         * y轴需要显示的名称
         */
        private String y;

        private float xTextSize = -1; //-1 是兼容默认值的标识

        private float xPaddingTop = 0;

        private float xPaddingBottom = -1;

        private boolean horizontalXLabel = false;

        private float yTextSize = -1; //-1 是兼容默认值的标识

        private float yPaddingTop = 0;

        private float yPaddingBottom = 0;


        public String getX() {
            return x;
        }

        public void setX(String x) {
            this.x = x;
        }

        public String getY() {
            return y;
        }

        public void setY(String y) {
            this.y = y;
        }

        public float getxTextSize() {
            return xTextSize;
        }

        public void setxTextSize(float xTextSize) {
            this.xTextSize = xTextSize;
        }

        public float getxPaddingTop() {
            return xPaddingTop;
        }

        public void setxPaddingTop(float xPaddingTop) {
            this.xPaddingTop = xPaddingTop;
        }

        public float getxPaddingBottom() {
            return xPaddingBottom;
        }

        public void setxPaddingBottom(float xPaddingBottom) {
            this.xPaddingBottom = xPaddingBottom;
        }

        public boolean isHorizontalXLabel() {
            return horizontalXLabel;
        }

        public void setHorizontalXLabel(boolean horizontalXLabel) {
            this.horizontalXLabel = horizontalXLabel;
        }

        public float getyTextSize() {
            return yTextSize;
        }

        public void setyTextSize(float yTextSize) {
            this.yTextSize = yTextSize;
        }

        public float getyPaddingTop() {
            return yPaddingTop;
        }

        public void setyPaddingTop(float yPaddingTop) {
            this.yPaddingTop = yPaddingTop;
        }

        public float getyPaddingBottom() {
            return yPaddingBottom;
        }

        public void setyPaddingBottom(float yPaddingBottom) {
            this.yPaddingBottom = yPaddingBottom;
        }
    }

    /**
     * 标杆插件
     */
    public static class SignPost {
        /**
         * x轴需要显示的名称
         */
        private String lineColor;

        private int lineColorParse;

        private int lineMarginTop;

        private int lineMarginBottom;

        private int linePointRadius;

        private String cursorColor;

        private int cursorColorParse;

        private int cursorMarginTop;

        private int cursorMarginBottom;

        private int cursorHigh = 10;

        private String cursorImageBase64;

        private boolean show = false;

        private String showType = "all";

        private boolean isSelectedDisappear = true;

        private boolean needMarkerLine = false;     //是否需要标志线

        private boolean isSelectedShake = false;

        public boolean isSelectedShake() {
            return isSelectedShake;
        }

        public void setSelectedShake(boolean selectedShake) {
            isSelectedShake = selectedShake;
        }

        public String getShowType() {
            return showType;
        }

        public void setShowType(String showType) {
            this.showType = showType;
        }

        public boolean isSelectedDisappear() {
            return isSelectedDisappear;
        }

        public void setSelectedDisappear(boolean selectedDisappear) {
            isSelectedDisappear = selectedDisappear;
        }

        public boolean isShow() {
            return show;
        }

        public void setShow(boolean show) {
            this.show = show;
        }

        public boolean isNeedMarkerLine() {
            return needMarkerLine;
        }

        public void setNeedMarkerLine(boolean needMarkerLine) {
            this.needMarkerLine = needMarkerLine;
        }

        public int getLineColorParse() {
            return lineColorParse;
        }

        public void setLineColorParse(int lineColorParse) {
            this.lineColorParse = lineColorParse;
        }

        public int getCursorHigh() {
            return cursorHigh;
        }

        public void setCursorHigh(int cursorHigh) {
            this.cursorHigh = cursorHigh;
        }

        public int getCursorColorParse() {
            return cursorColorParse;
        }

        public void setCursorColorParse(int cursorColorParse) {
            this.cursorColorParse = cursorColorParse;
        }

        public String getLineColor() {
            return lineColor;
        }

        public void setLineColor(String lineColor) {
            this.lineColor = lineColor;
        }

        public int getLineMarginTop() {
            return lineMarginTop;
        }

        public void setLineMarginTop(int lineMarginTop) {
            this.lineMarginTop = lineMarginTop;
        }

        public int getLinePointRadius() {
            return linePointRadius;
        }

        public void setLinePointRadius(int linePointRadius) {
            this.linePointRadius = linePointRadius;
        }

        public int getLineMarginBottom() {
            return lineMarginBottom;
        }

        public void setLineMarginBottom(int lineMarginBottom) {
            this.lineMarginBottom = lineMarginBottom;
        }

        public String getCursorColor() {
            return cursorColor;
        }

        public void setCursorColor(String cursorColor) {
            this.cursorColor = cursorColor;
        }

        public int getCursorMarginTop() {
            return cursorMarginTop;
        }

        public void setCursorMarginTop(int cursorMarginTop) {
            this.cursorMarginTop = cursorMarginTop;
        }

        public int getCursorMarginBottom() {
            return cursorMarginBottom;
        }

        public void setCursorMarginBottom(int cursorMarginBottom) {
            this.cursorMarginBottom = cursorMarginBottom;
        }

        public String getCursorImageBase64() {
            return cursorImageBase64;
        }

        public void setCursorImageBase64(String cursorImageBase64) {
            this.cursorImageBase64 = cursorImageBase64;
        }
    }

    /**
     * 坐标轴类数据，提供数值、标签、颜色等描述
     */
    public static class axis {
        private List<Float> value;
        private List<Float> maxValue;
        private List<String> label;
        private String title;
        private String color;
        private String maxColor;
        private String background;
        private String starcolor;
        private String endcolor;
        private String highLightColor = "#19FFBB73";
        private String maxHighLightColor = "#19FFBB73";
        //        private int highLightAlpha = 10;
        private boolean highLightEnable = true;

        private String circleColor = null;
        private int circleRadius = -1;
        private String circleHoleColor = null;
        private int circleHoleRadius = -1;


        private String highLightCircleColor = null;
        private int highLightCircleRadius = -1;

        private String highLightCircleHoleColor = null;
        private int highLightCircleHoleRadius = -1;

        private String highLightCircleOutsideColor = null;
        private int highLightCircleOutsideWidth = -1;


        private boolean isDrawCircles = false;
        private boolean isDrawCircleHole = false;

        private boolean isDrawHighLightCircles = false;
        private String highLightStype; //支持 "crossLine"和"circle" 两种实现方式

        private String axisDependency = null; //默认对齐left , 设置right 才会生效变化

        private int lineWidth = -1;

        /**
         * 是否平滑曲线
         * 0 不平滑
         * 1 平滑
         */
        private String smooth;

        private boolean lineDashAble = false; //曲线的类型可调: 是否使用虚线，默认false

        private boolean lineSidesPointAble = false; //首尾可以设置是否有端点,默认false

        private int lineSidesPointRadius; //端点大小，lineSidesPointAble为ture有效

        private String lineSidesPointColor; //端点颜色，lineSidesPointAble为ture有效

        public int getLineWidth() {
            return lineWidth;
        }

        public void setLineWidth(int lineWidth) {
            this.lineWidth = lineWidth;
        }

        public String getAxisDependency() {
            return axisDependency;
        }

        public void setAxisDependency(String axisDependency) {
            this.axisDependency = axisDependency;
        }

        public boolean isLineSidesPointAble() {
            return lineSidesPointAble;
        }

        public void setLineSidesPointAble(boolean lineSidesPointAble) {
            this.lineSidesPointAble = lineSidesPointAble;
        }

        public String getHighLightStype() {
            return highLightStype;
        }

        public void setHighLightStype(String highLightStype) {
            this.highLightStype = highLightStype;
        }

        public String getCircleColor() {
            return circleColor;
        }

        public void setCircleColor(String circleColor) {
            this.circleColor = circleColor;
        }

        public int getCircleRadius() {
            return circleRadius;
        }

        public void setCircleRadius(int circleRadius) {
            this.circleRadius = circleRadius;
        }

        public String getCircleHoleColor() {
            return circleHoleColor;
        }

        public void setCircleHoleColor(String circleHoleColor) {
            this.circleHoleColor = circleHoleColor;
        }

        public int getCircleHoleRadius() {
            return circleHoleRadius;
        }

        public void setCircleHoleRadius(int circleHoleRadius) {
            this.circleHoleRadius = circleHoleRadius;
        }

        public String getHighLightCircleColor() {
            return highLightCircleColor;
        }

        public void setHighLightCircleColor(String highLightCircleColor) {
            this.highLightCircleColor = highLightCircleColor;
        }

        public int getHighLightCircleRadius() {
            return highLightCircleRadius;
        }

        public void setHighLightCircleRadius(int highLightCircleRadius) {
            this.highLightCircleRadius = highLightCircleRadius;
        }

        public String getHighLightCircleHoleColor() {
            return highLightCircleHoleColor;
        }

        public void setHighLightCircleHoleColor(String highLightCircleHoleColor) {
            this.highLightCircleHoleColor = highLightCircleHoleColor;
        }

        public int getHighLightCircleHoleRadius() {
            return highLightCircleHoleRadius;
        }

        public void setHighLightCircleHoleRadius(int highLightCircleHoleRadius) {
            this.highLightCircleHoleRadius = highLightCircleHoleRadius;
        }

        public String getHighLightCircleOutsideColor() {
            return highLightCircleOutsideColor;
        }

        public void setHighLightCircleOutsideColor(String highLightCircleOutsideColor) {
            this.highLightCircleOutsideColor = highLightCircleOutsideColor;
        }

        public int getHighLightCircleOutsideWidth() {
            return highLightCircleOutsideWidth;
        }

        public void setHighLightCircleOutsideWidth(int highLightCircleOutsideWidth) {
            this.highLightCircleOutsideWidth = highLightCircleOutsideWidth;
        }

        public boolean isDrawCircles() {
            return isDrawCircles;
        }

        public void setDrawCircles(boolean drawCircles) {
            isDrawCircles = drawCircles;
        }

        public boolean isDrawCircleHole() {
            return isDrawCircleHole;
        }

        public void setDrawCircleHole(boolean drawCircleHole) {
            isDrawCircleHole = drawCircleHole;
        }

        public boolean isDrawHighLightCircles() {
            return isDrawHighLightCircles;
        }

        public void setDrawHighLightCircles(boolean drawHighLightCircles) {
            isDrawHighLightCircles = drawHighLightCircles;
        }


        public String getHighLightColor() {
            return highLightColor;
        }

        public void setHighLightColor(String highLightColor) {
            this.highLightColor = highLightColor;
        }

        public boolean isHighLightEnable() {
            return highLightEnable;
        }

        public void setHighLightEnable(boolean highLightEnable) {
            this.highLightEnable = highLightEnable;
        }

        public String getMaxHighLightColor() {
            return maxHighLightColor;
        }

        public void setMaxHighLightColor(String maxHighLightColor) {
            this.maxHighLightColor = maxHighLightColor;
        }


        //        public int getHighLightAlpha() {
//            return highLightAlpha;
//        }
//
//        public void setHighLightAlpha(int highLightAlpha) {
//            this.highLightAlpha = highLightAlpha;
//        }


        public String getMaxColor() {
            return maxColor;
        }

        public void setMaxColor(String maxColor) {
            this.maxColor = maxColor;
        }

        public int getLineSidesPointRadius() {
            return lineSidesPointRadius;
        }

        public void setLineSidesPointRadius(int lineSidesPointRadius) {
            this.lineSidesPointRadius = lineSidesPointRadius;
        }

        public String getLineSidesPointColor() {
            return lineSidesPointColor;
        }

        public void setLineSidesPointColor(String lineSidesPointColor) {
            this.lineSidesPointColor = lineSidesPointColor;
        }

        public boolean isLineDashAble() {
            return lineDashAble;
        }


        public void setLineDashAble(boolean lineDashAble) {
            this.lineDashAble = lineDashAble;
        }


        public List<Float> getMaxValue() {
            return maxValue;
        }

        public void setMaxValue(List<Float> maxValue) {
            this.maxValue = maxValue;
        }

        public List<Float> getValue() {
            return value;
        }

        public void setValue(List<Float> value) {
            this.value = value;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getBackground() {
            return background;
        }

        public void setBackground(String backgroud) {
            this.background = backgroud;
        }

        public List<String> getLabel() {
            return label;
        }

        public void setLabel(List<String> label) {
            this.label = label;
        }

        public String getStarcolor() {
            return starcolor;
        }

        public void setStarcolor(String starcolor) {
            this.starcolor = starcolor;
        }

        public String getEndcolor() {
            return endcolor;
        }

        public void setEndcolor(String endcolor) {
            this.endcolor = endcolor;
        }

        /**
         * 是否平滑曲线
         * 0 不平滑
         * 1 平滑
         */
        public boolean isSmooth() {
            return "1".equals(smooth);
        }

        public void setSmooth(String smooth) {
            this.smooth = smooth;
        }
    }
}

