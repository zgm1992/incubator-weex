package com.meiju.weex.componentView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.renderer.SignPostRender;
import com.github.mikephil.charting.utils.Utils;
import com.meiju.weex.componentView.barchart.MChartBean;
import com.meiju.weex.componentView.barchart.MChartUnit;
import com.meiju.weex.componentView.barchart.MChartValueFormatter;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.WXSDKManager;
import com.taobao.weex.annotation.Component;
import com.taobao.weex.common.Constants;
import com.taobao.weex.layout.ContentBoxMeasurement;
import com.taobao.weex.ui.action.BasicComponentData;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXComponentProp;
import com.taobao.weex.ui.component.WXVContainer;
import com.taobao.weex.ui.view.WXSwitchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by XIONGQI on 2018-3-19.
 */
@Component(lazyload = false)
public class MSmartWXLineChart extends WXComponent<MLineChart> {

    private static final ContentBoxMeasurement SWITCH_MEASURE_FUNCTION = new ContentBoxMeasurement() {
        @Override
        public void measureInternal(float width, float height, int widthMeasureMode, int heightMeasureMode) {
            Log.i("weex0.20.0.2", "measureInternal " + width + "," + height + "," + widthMeasureMode + "," + heightMeasureMode);
            try {
                Context context = mComponent.getContext();
                WXSwitchView wxSwitchView = new WXSwitchView(context);
                int widthSpec, heightSpec;
                heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                if (Float.isNaN(width)) {
                    widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                } else {
                    widthSpec = View.MeasureSpec.makeMeasureSpec((int) width, View.MeasureSpec.AT_MOST);
                }
                wxSwitchView.measure(widthSpec, heightSpec);
                mMeasureWidth = wxSwitchView.getMeasuredWidth();
                mMeasureHeight = wxSwitchView.getMeasuredHeight();
            } catch (RuntimeException e) {
            }
        }

        @Override
        public void layoutBefore() {
            Log.i("weex0.20.0.2", "layoutBefore ");

        }

        @Override
        public void layoutAfter(float computedWidth, float computedHeight) {
            Log.i("weex0.20.0.2", "layoutAfter " + computedWidth + "," + computedHeight);

        }
    };

    public MSmartWXLineChart(WXSDKInstance instance, WXVContainer parent, BasicComponentData basicComponentData) {
        super(instance, parent, basicComponentData);
        init();

    }

    public MSmartWXLineChart(WXSDKInstance instance, WXVContainer parent, int type, BasicComponentData basicComponentData) {
        super(instance, parent, type, basicComponentData);
        init();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void init() {
        Log.i("weex0.20.0.2", "setContentBoxMeasurement ");
        setContentBoxMeasurement(SWITCH_MEASURE_FUNCTION);
    }

    @Override
    protected MLineChart initComponentHostView(@NonNull Context context) {
        MLineChart lineChart = new MLineChart(context);
        BarChart.LayoutParams layoutParams = new BarChart.LayoutParams(BarChart.LayoutParams.MATCH_PARENT, BarChart.LayoutParams.MATCH_PARENT);
        lineChart.setLayoutParams(layoutParams);
        lineChart.setBackgroundColor(0xff00ffff);
        return lineChart;
    }

    @Override
    protected boolean setProperty(String key, Object param) {
        switch (key) {
            case Constants.Name.VALUE:
                return true;
            case "data":
                setValue(param);
                return true;
            default:
                return false;
        }
    }

    @WXComponentProp(name = Constants.Name.SRC)
    private void setValue(Object param) {
//        String tempData = "{\n" +
//                "    \"x\": {\n" +
//                "        \"value\": [1, 2, 3, 4, 5, 6, 7],\n" +
//                "        \"label\": [\"11.6\", \"11.7\", \"11.8\", \"11.9\", \"11.10\", \"11.11\", \"11.12\"]\n" +
//                "    },\n" +
//                "    \"y\": [{\n" +
//                "            \"value\": [1, 6, 2, 1, 2, 3, 7],\n" +
//                "            \"label\": [\"1次\", \"6次\", \"2次\", \"四次\", \"五次\", \"六次\", \"七次\"],\n" +
//                "            \"title\": \"冷藏室\",\n" +
//                "            \"color\": \"#2AD2FC\",\n" +
//                "            \"starcolor\": \"#ffffff\",\n" +
//                "            \"endcolor\": \"#2AD2FC\",\n" +
//                "            \"smooth\": 1\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"value\": [10, 5, 3, 4, 1, 2, 6],\n" +
//                "            \"title\": \"下段冷冻室\",\n" +
//                "            \"color\": \"#1B81FB\",\n" +
//                "            \"starcolor\": \"#ffffff\",\n" +
//                "            \"endcolor\": \"#1B81FB\",\n" +
//                "            \"smooth\": 0\n" +
//                "        }\n" +
//                "    ],\n" +
//                "    \"description\": \"\",\n" +
//                "    \"legend\": {\n" +
//                "        \"position\": \"TOP_LEFT\",\n" +
//                "        \"orientation\": \"HORIZONTAL\"\n" +
//                "    },\n" +
//                "    \"unit\": {\n" +
//                "        \"x\": \"日期\",\n" +
//                "        \"y\": \"次数\"\n" +
//                "    }\n" +
//                "}";
//        param = tempData;
//        MChartBean twoMChartBean = JSON.parseObject((String) param,MChartBean.class);
//        setChart(getHostView(), twoMChartBean);
        MChartBean twoMChartBean = null;
        Log.i("LineChartValue","value is ->"+param);
        try {
            if(param instanceof String)
                twoMChartBean = JSON.parseObject((String)param, MChartBean.class);
            if(param instanceof  JSONObject)
//                twoMChartBean = ((JSONObject) param).toJavaObject(MChartBean.class);
                twoMChartBean = JSON.toJavaObject((JSONObject)param, MChartBean.class);
            if(twoMChartBean!=null)
                setChart(getHostView(), twoMChartBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setChart(MLineChart hostView, MChartBean mChartBean) {
        hostView.setNoDataText("");
        hostView.setPinchZoom(true);//设置按比例放缩柱状图
        hostView.animateX(200);//数据显示动画，从左往右依次显示
        hostView.setScaleEnabled(false);
        hostView.getDescription().setEnabled(false);//设置描述
        hostView.setDrawGridBackground(false);

        try {
            if (mChartBean.getBackground() != null) {
                int color = Color.parseColor(mChartBean.getBackground());
                if (color != 0) {
                    getHostView().setBackgroundColor(color);
                }
            }
        } catch (Throwable e) {

        }

        //图例设置
        initLegend(hostView, mChartBean);

        //初始化X轴 Y轴的样式
        initAxis(hostView, mChartBean);


        XAxis xAxis = hostView.getXAxis();
        //设置单位
        if (mChartBean.getUnit() == null) {
            hostView.setUnit(null);
        } else {
            MChartUnit unit = new MChartUnit(mChartBean.getUnit().getX(), mChartBean.getUnit().getY());
            unit.setXPosition(MChartUnit.RIGHT);
            unit.setYPosition(MChartUnit.TOP);

            if (TextUtils.isEmpty(mChartBean.getxAxisLabelColor())) {
                unit.setxLabelColor(Color.GRAY);
            } else {
                unit.setxLabelColor(Color.parseColor(mChartBean.getxAxisLabelColor()));
            }

            if (TextUtils.isEmpty(mChartBean.getyAxisLabelColor())) {
                unit.setyLabelColor(Color.GRAY);
            } else {
                unit.setyLabelColor(Color.parseColor(mChartBean.getyAxisLabelColor()));
            }
//            unit.setColor(ContextCompat.getColor(getContext(), R.color.text_color_gray));
            MChartBean.Unit bartUnit = mChartBean.getUnit();

            int unitDefaltDpSize = 13;

            int xtextSize = dp2px(getContext(), bartUnit.getxTextSize());
//            int xTop  = dp2px(getContext(),bartUnit.getxPaddingTop());
//            int xBottom  = dp2px(getContext(),bartUnit.getxPaddingBottom());
            int ytextSize = dp2px(getContext(), bartUnit.getyTextSize());
//            int yTop  = dp2px(getContext(),bartUnit.getyPaddingTop());
//            int yBottom  = dp2px(getContext(),bartUnit.getyPaddingBottom());


            unit.setxTextSize(dp2px(getContext(), (unitDefaltDpSize - 1)));
            unit.setyTextSize(dp2px(getContext(), (unitDefaltDpSize - 1)));

            unit.setXOffSet((int) (unit.getxTextSize()), 0, 0, (int) (xAxis.getTextSize()));
            unit.setYOffSet(0, (int) unit.getyTextSize(), 5, 0);

            hostView.setUnit(unit);

            if (unit != null) {
                xAxis.setYOffset(dp2px(getContext(), unitDefaltDpSize));
            }
        }


        initSignPost(hostView, mChartBean);

        MChartBean.axis[] yAxisValueArray = new MChartBean.axis[mChartBean.getY().size()];
        yAxisValueArray = mChartBean.getY().toArray(yAxisValueArray);
        setChartData(hostView, mChartBean, yAxisValueArray);
        hostView.invalidate();
    }

    private void initLegend(MLineChart hostView, MChartBean mChartBean) {
        //图例设置
        Legend legend = hostView.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        legend.setXOffset(10);
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setTextSize(10f);
        hostView.getLegend().setEnabled(!mChartBean.getLegendHide());
    }

    private void initAxis(MLineChart hostView, final MChartBean mChartBean) {
        //x坐标轴设置
        XAxis xAxis = hostView.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(mChartBean.getXAxisGridLine() != 0); //默认显示的
        xAxis.setDrawLabels(true);
        int labelCount = mChartBean.getX().getLabel().size();
        xAxis.setLabelCount(labelCount);
        xAxis.setTextSize(9f);
        IAxisValueFormatter ix = new MChartValueFormatter.MyAxisValueFormatter(mChartBean.getX().getLabel());
        xAxis.setValueFormatter(ix);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(labelCount - 1);
        xAxis.setGranularity(mChartBean.getGranularity()); //设置最下间隔？
        xAxis.setValue(mChartBean.getX().getValue());
        xAxis.setIsBoldText(mChartBean.isxAxisLabelHighLightThicke());
        if (mChartBean.getxAxisLabelTextSize()!=-1){
            xAxis.setTextSize(mChartBean.getxAxisLabelTextSize());
        }

        if (mChartBean.getxAxisLabelHighLightColor() != null) {
            try {
                int color = Color.parseColor(mChartBean.getxAxisLabelHighLightColor());
                if (color != 0) {
                    xAxis.setBoldTextColor(color);
                }
            } catch (Exception e) {

            }
        }


        if (!TextUtils.isEmpty(mChartBean.getxAxisGridColor())) {
            xAxis.setGridColor(Color.parseColor(mChartBean.getxAxisGridColor()));
        }
        try {
            if (!TextUtils.isEmpty(mChartBean.getxAxisGridAlpha())) {
                xAxis.setGridAlpha(Float.valueOf(mChartBean.getxAxisGridAlpha()));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (mChartBean.getxAxisLabelColor() != null) {
            int color = Color.parseColor(mChartBean.getxAxisLabelColor());
            if (color != 0) {
                xAxis.setTextColor(color);
            }
        }

        if (mChartBean.getxAxisColor() != null) {
            int color = Color.parseColor(mChartBean.getxAxisColor());
            if (color != 0) {
                xAxis.setAxisLineColor(color);
            }
        }


        //y轴左边栏设置
        final YAxis leftAxis = hostView.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setDrawGridLines(mChartBean.getYAxisGridLine() == 1); //默认不显示的
        leftAxis.setDrawAxisLine(true);
        leftAxis.setSpaceBottom(0);
        leftAxis.setDrawLabels(mChartBean.isyAxisLabelShow());
        if (mChartBean.getyAxisLabelCount() == -1) {
            leftAxis.setLabelCount(6, false); //恢复默认
        } else {
            leftAxis.setLabelCount(mChartBean.getyAxisLabelCount(), true);
        }


        if (mChartBean.getyAxisLabelTextSize()!=-1){
            leftAxis.setTextSize(mChartBean.getyAxisLabelTextSize());
        }

        //y坐标轴的label 颜色
        if (mChartBean.getyAxisLabelColor() != null) {
            try {
                int color = Color.parseColor(mChartBean.getyAxisLabelColor());
                leftAxis.setTextColor(color);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //设置y轴的轴线的颜色
        if (mChartBean.getyAxisColor() != null) {
            try {
                int color = Color.parseColor(mChartBean.getyAxisColor());
                leftAxis.setAxisLineColor(color);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //y轴 label 上加单位
        leftAxis.setValueFormatter(new DefaultAxisValueFormatter(mChartBean.getyAxisLabelDecimal()) {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (mChartBean.getyAxisLabelIgnoreCount() != -1 && leftAxis.getCurrentLabelIndex() >= 0) {
                    //如果这是要忽略的数据
                    if ((leftAxis.getCurrentLabelIndex() + 1) % mChartBean.getyAxisLabelIgnoreCount() == 0) {
                        return "";
                    }
                }
                Log.i("jarvanTrance11", "  value   ->" + value + "     i is ->" + leftAxis.getCurrentLabelIndex() + "  ");
                return super.getFormattedValue(value, axis) + mChartBean.getyGraduationLabel();
            }

            @Override
            public int getDecimalDigits() {
                return leftAxis.mDecimals;
            }
        });


//        //设置坐标轴最大最小值
        float yMax = mChartBean.getYMax();
        float yMin = mChartBean.getYMin();
        leftAxis.setAxisMinimum(yMin * 0.8f);
        if (mChartBean.getyAxisMaximum() != -1f) {
            leftAxis.setAxisMaximum(mChartBean.getyAxisMaximum());
            leftAxis.setSpaceTop(0);
//            leftAxis.setDrawTopYLabelEntry(false);
        } else {
            leftAxis.setAxisMaximum(yMax);
            leftAxis.setSpaceTop(10f);
        }


        //设置  折线图右侧的 y轴
        final YAxis rightAxis = hostView.getAxisRight();
        if (!mChartBean.isyRightAxisEnable()) {
            //隐藏右边Y轴
            hostView.getAxisRight().setEnabled(false);
            return; //后面的不需要再做设置了
        }
        hostView.getAxisRight().setEnabled(true);

        if (mChartBean.getyRightAxisLabelTextSize()!=-1){
            rightAxis.setTextSize(mChartBean.getyRightAxisLabelTextSize());
        }

        rightAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        rightAxis.setSpaceBottom(0);
        rightAxis.setDrawLabels(true);
        rightAxis.setDrawGridLines(false);
        //设置强制的显示数量
        if (mChartBean.getyRightAxisLabelCount() == -1) {
            rightAxis.setLabelCount(6, false); //恢复默认
        } else {
            rightAxis.setLabelCount(mChartBean.getyRightAxisLabelCount(), true);
        }

        if (mChartBean.getyRightAxisMaximum() != -1f) {
            rightAxis.setAxisMaximum(mChartBean.getyRightAxisMaximum());
            rightAxis.setSpaceTop(0);
//            rightAxis.setDrawTopYLabelEntry(false);
        } else {
            rightAxis.setAxisMaximum(leftAxis.getAxisMaximum());
            rightAxis.setSpaceTop(10);
        }

        rightAxis.setAxisMinimum(yMin * 0.8f);


        //设置y右边 轴的轴线的颜色
        if (mChartBean.getyRightAxisColor() != null) {
            int color = Color.parseColor(mChartBean.getyRightAxisColor());
            if (color != 0) {
                rightAxis.setAxisLineColor(color);
            }
        }

        //y右边坐标轴的label 颜色
        if (mChartBean.getyRightAxisLabelColor() != null) {
            try {
                int color = Color.parseColor(mChartBean.getyRightAxisLabelColor());
                rightAxis.setTextColor(color);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //y右边轴 label 上加单位
        rightAxis.setValueFormatter(new DefaultAxisValueFormatter(mChartBean.getyRightAxisLabelDecimal()) {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (mChartBean.getyRightAxisLabelIgnoreCount() != -1 && rightAxis.getCurrentLabelIndex() >= 0) {
                    //如果这是要忽略的数据
                    if ((rightAxis.getCurrentLabelIndex() + 1) % mChartBean.getyRightAxisLabelIgnoreCount() == 0) {
                        return "";
                    }
                }
                Log.i("jarvanTrance12", "  value   ->" + value + "     i is ->" + leftAxis.getCurrentLabelIndex() + "  ");

                return super.getFormattedValue(value, axis) + mChartBean.getyRightGraduationLabel();
            }

            @Override
            public int getDecimalDigits() {
                return rightAxis.mDecimals;
            }
        });

    }


    //初始化滑杆的设置
    private void initSignPost(MLineChart hostView, MChartBean mChartBean) {
        XAxis xAxis = hostView.getXAxis();

        if (mChartBean.getSignPost() == null || !mChartBean.getSignPost().isShow()) {
            //不需要显示滑杆的设置和数据
            xAxis.setShowSignPost(false);
            hostView.setExtraBottomOffset(0);
            hostView.getViewPortHandler().getSignPostRender().setSignPostBean(null);
            return;
        }
        //设置滑杆线和标签线
        MChartBean.SignPost signPost = mChartBean.getSignPost();
        //计算滑动标杆需要的内容
        hostView.setExtraBottomOffset( //额外空间，是算上
//                        Utils.convertPixelsToDp(xAxis.getYOffset())+
                        (mChartBean.getxAxisLabelTextSize() == -1 ? Utils.convertPixelsToDp(xAxis.getTextSize()) : mChartBean.getxAxisLabelTextSize()) +
                        signPost.getLineMarginTop() +
                        signPost.getLinePointRadius() * 2 +
                        signPost.getLineMarginBottom() +
                        signPost.getCursorMarginTop() +
                        signPost.getCursorHigh() +
                        signPost.getCursorMarginBottom()
        );
        Log.i("cursorHighDraw", " the extrabootom is ->" + hostView.getExtraBottomOffset());


        //如果需要显示标杆,先做刻度转换
        signPost.setCursorMarginTop(dp2px(getContext(), signPost.getCursorMarginTop()));
        signPost.setCursorMarginBottom(dp2px(getContext(), signPost.getCursorMarginBottom()));
        signPost.setLineMarginTop(dp2px(getContext(), signPost.getLineMarginTop()));
        signPost.setLineMarginBottom(dp2px(getContext(), signPost.getLineMarginBottom()));
        signPost.setLinePointRadius(dp2px(getContext(), signPost.getLinePointRadius()));
        signPost.setCursorHigh(dp2px(getContext(), signPost.getCursorHigh()));

        if (signPost.getLineColor() != null) {
            try {
                int color = Color.parseColor(signPost.getLineColor());
                signPost.setLineColorParse(color);
            } catch (IllegalArgumentException e) {
                signPost.setLineColorParse(Color.BLACK);
            }
        }

        if (signPost.getCursorColor() != null) {
            try {
                int color = Color.parseColor(signPost.getCursorColor());
                signPost.setCursorColorParse(color);
            } catch (IllegalArgumentException e) {
                signPost.setCursorColorParse(Color.BLACK);
            }
        }

        xAxis.setShowSignPost(true);
        xAxis.setSignPostLineColor(signPost.getLineColorParse());
        xAxis.setSignPostLineMarginTop(signPost.getLineMarginTop());
        xAxis.setSignPostLineMarginBottom(signPost.getLineMarginBottom());
        xAxis.setSignPostLinePointRadius(signPost.getLinePointRadius());

//        if (signPost.isSelectedShake()) {
//            Context context = getHostView().getContext();
//            hostView.getViewPortHandler().getSignPostRender().setVibrator((Vibrator) context.getSystemService(context.VIBRATOR_SERVICE));
//        } else {
//            hostView.getViewPortHandler().getSignPostRender().setVibrator(null);
//        }

        hostView.highlightValue(null); //清空高亮数据，以免setCurrentClickBar 数据无法生效
        hostView.getViewPortHandler().getSignPostRender().setSignPostBean(signPost);
        hostView.getViewPortHandler().getSignPostRender().setCurrentClickBar(mChartBean.getBarSelectIndex());
        hostView.getViewPortHandler().setBarSelectIndex(mChartBean.getBarSelectIndex());

        hostView.getViewPortHandler().getSignPostRender().setOnBarClickListener(hostView.getViewPortHandler(), new SignPostRender.OnBarClickListener() {
            @Override
            public void onBarClick(int groupIndex, int barIndex) {
                MSmartWXLineChart.this.notifyToWeex("barClicked", String.valueOf(groupIndex), String.valueOf(barIndex));
            }
        });

    }


    private void notifyToWeex(String event, String groupIndex, String barIndex) {
        Map<String, Object> params = new HashMap<>(3);
        params.put("barIndex", barIndex);
        params.put("groupIndex", groupIndex);
        params.put("timeStamp", System.currentTimeMillis());

        Map<String, Object> domChanges = new HashMap<>();
        Map<String, Object> attrsChanges = new HashMap<>();
        attrsChanges.put("barIndex", barIndex);
        attrsChanges.put("groupIndex", groupIndex);
        domChanges.put("attrs", attrsChanges);
        WXSDKManager.getInstance().fireEvent(getInstanceId(), getRef(), event, params, domChanges);
    }



    private LineData setChartData(MLineChart mLineChart, MChartBean mChartBean, MChartBean.axis... yAxisValue) {
        //根据weex端传袭来的x->value数据来构建，构建BarEntry List，以做到非均匀变化
        List<List<Entry>> entriesList = new ArrayList<>();
        for (Integer i = 0, n = yAxisValue.length; i < n; ++i) {
            MChartBean.axis yAxisValueItem = yAxisValue[i];
            if (yAxisValueItem == null) continue;
            List<Entry> entries = new ArrayList<>();
            List<Float> xValueList = mChartBean.getX().getValue();
            float offsetVal = xValueList.get(0);  //因为架构设置中是以0作为起步的,所以要减去偏移量
            float max = xValueList.get(xValueList.size() - 1) - offsetVal;
            float scale = xValueList.size() - 1;
//            for (Integer j = 0, m = yAxisValueItem.getValue().size(); j < m; ++j) {
//                float x = j;
//                if (j != 0 && xValueList != null && xValueList.size() > j) { //印射 坐标轴数值
//                    float temp = ((xValueList.get(j) - offsetVal) / max) * (yAxisValueItem.getValue().size() - 1);
//                    if (temp > 0) {
//                        x = temp;
//                    }
//                }
//                entries.add(new Entry(x, yAxisValueItem.getValue().get(j)));
//            }
            //换算不同的
            for (Integer j = 0, m = yAxisValueItem.getValue().size(); j < m; ++j) {
                float x = j;
                if (j != 0 && xValueList != null && xValueList.size() > j) { //印射 坐标轴数值
                    float temp = ((xValueList.get(j) - offsetVal) / max) * scale;
                    if (temp > 0) {
                        x = temp;
                    }
                }
                entries.add(new Entry(x, yAxisValueItem.getValue().get(j)));
            }
            if (entries.size() > 0)
                entriesList.add(entries);
        }

        LineData lineData = mLineChart.getData();

        //设置BarData每个BarDataSet的数据、样式等
        if (lineData != null && mLineChart.getData().getDataSetCount() > 0) {
            for (int i = 0, n = lineData.getDataSetCount(); i < n; i++) {
                LineDataSet dataSet = (LineDataSet) lineData.getDataSetByIndex(i);
                List<Entry> entries = entriesList.get(i);
                if (entries != null) {
                    dataSet.setValues(entries);
                }
                MChartBean.axis yAxisValueItem;
                if (i < yAxisValue.length) {
                    yAxisValueItem = yAxisValue[i];
                    setDataSetStyle(dataSet, yAxisValueItem);
                }
            }
            mLineChart.getData().notifyDataChanged();
            mLineChart.notifyDataSetChanged();
        } else {
            List<ILineDataSet> dataSetList = new ArrayList<>();
            for (int i = 0, n = (entriesList.size() - 1); i <= n; i++) {
                List<Entry> entries = entriesList.get(i);
                if (entries == null) continue;
                MChartBean.axis yAxisValueItem = null;
                if (i < yAxisValue.length)
                    yAxisValueItem = yAxisValue[i];
                if (yAxisValueItem == null) continue;
                LineDataSet dataSet = new LineDataSet(entries, null);

                setDataSetStyle(dataSet, yAxisValueItem);
                if (yAxisValueItem.isLineSidesPointAble()) {
                    //如果需要设置首尾端的端点
                    dataSet.setLineSidesPointAble(yAxisValueItem.isLineSidesPointAble());
                    if (!TextUtils.isEmpty(yAxisValueItem.getLineSidesPointColor())) {
                        try {
                            int cicleColor = Color.parseColor(yAxisValueItem.getLineSidesPointColor());
                            dataSet.setCircleColor(cicleColor);
                            dataSet.setCircleHoleColor(cicleColor);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }

                    if (yAxisValueItem.getLineSidesPointRadius() != 0) {
//                        int size = dp2px(getContext(), yAxisValueItem.getLineSidesPointRadius());
                        int size = yAxisValueItem.getLineSidesPointRadius();
                        dataSet.setCircleHoleRadius(size);
                        dataSet.setCircleRadius(size);
                    }
                } else { //描绘点和描绘圆孔

                    dataSet.setDrawCircles(yAxisValueItem.isDrawCircles());
                    try {
                        if (yAxisValueItem.getCircleColor()!=null) {
                            int cicleColor = Color.parseColor(yAxisValueItem.getCircleColor());
                            dataSet.setCircleColor(cicleColor);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (yAxisValueItem.getCircleRadius() != -1) {
                        dataSet.setCircleRadius(yAxisValueItem.getCircleRadius());
                    }


                    dataSet.setDrawCircleHole(yAxisValueItem.isDrawCircleHole());
                    try {
                        int cicleHoleColor = Color.parseColor(yAxisValueItem.getCircleHoleColor());
                        dataSet.setCircleHoleColor(cicleHoleColor);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (yAxisValueItem.getCircleHoleRadius() != -1) {
                        dataSet.setCircleHoleRadius(yAxisValueItem.getCircleHoleRadius());
                    }


                    dataSet.setDrawHighlightCircles(yAxisValueItem.isDrawHighLightCircles());

                    if (yAxisValueItem.getHighLightCircleColor() != null) {
                        try {
                            int hightLightCircleColor = Color.parseColor(yAxisValueItem.getHighLightCircleColor());
                            dataSet.setHighLightCircleColor(hightLightCircleColor);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    dataSet.setHighLightCircleRadius(yAxisValueItem.getHighLightCircleRadius());

                    if (yAxisValueItem.getHighLightCircleHoleColor() != null) {
                        try {
                            int hightCicleHoleColor = Color.parseColor(yAxisValueItem.getHighLightCircleHoleColor());
                            dataSet.setHighLightCircleHoleColor(hightCicleHoleColor);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    dataSet.setHighLightCircleHoleRadius(yAxisValueItem.getHighLightCircleHoleRadius());

                    if (yAxisValueItem.getHighLightCircleOutsideColor() != null) {
                        try {
                            int hightCicleHoleWidthColor = Color.parseColor(yAxisValueItem.getHighLightCircleOutsideColor());
                            dataSet.setHighLightCircleOutsideColor(hightCicleHoleWidthColor);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    dataSet.setHighLightCircleOutsideWidth(yAxisValueItem.getHighLightCircleOutsideWidth());

                }

                dataSetList.add(dataSet);
            }//for

            LineData data = new LineData(dataSetList);
            data.setValueTextSize(10f);
            mLineChart.setData(data);

        }//else

        return mLineChart.getLineData();
    }

    private void setDataSetStyle(LineDataSet dataSet, MChartBean.axis yAxisValueItem) {
        if (yAxisValueItem == null) return;
        //设置标题
        String title = yAxisValueItem.getTitle();

        if (yAxisValueItem.getLineWidth() != -1) {
            dataSet.setLineWidth(yAxisValueItem.getLineWidth());
        }

        dataSet.setLabel(title == null ? "" : title);
        //触发高亮函数
        dataSet.setHighlightEnabled(yAxisValueItem.isHighLightEnable());
        //不显示十字线
        dataSet.setDrawVerticalHighlightIndicator(false);
        dataSet.setDrawHorizontalHighlightIndicator(false);


        if (!TextUtils.isEmpty(yAxisValueItem.getAxisDependency())) {
            if (getHostView().getAxisRight().isEnabled() && "right".equalsIgnoreCase(yAxisValueItem.getAxisDependency().trim())) {
                dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
            } else {
                dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            }
        }

        //如果全都是 0 的数值，就不显示折线图 线条出来
        List<Float> values = yAxisValueItem.getValue();
        if (values != null && values.size() > 0) {
            boolean isAllZero = true;
            for (Float value : values) {
                if (value > 0) {
                    isAllZero = false;
                    break;
                }
            }
            if (isAllZero) {
                dataSet.setVisible(false);
            }
        }

        //不显示点
        dataSet.setDrawCircles(false);


        if (yAxisValueItem.getColor() != null) {
            try {
                dataSet.setColor(Color.parseColor(yAxisValueItem.getColor()));
                dataSet.setCircleColor(Color.parseColor(yAxisValueItem.getColor()));
//            } catch (IllegalArgumentException e) {
//                dataSet.setColor(Color.parseColor("#FF00FF"));
//                dataSet.setCircleColor(Color.parseColor("#FF00FF"));
            } catch (Throwable e) {
                dataSet.setColor(Color.TRANSPARENT);
                dataSet.setCircleColor(Color.TRANSPARENT);
            }
        }

        //如果没有设置渐变色，就做透明化处理
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && yAxisValueItem.getStarcolor() != null && yAxisValueItem.getEndcolor() != null) {
            //设置曲线填充
            dataSet.setDrawFilled(true);
            int startColor, endColor;
            try {
                startColor = Color.parseColor(yAxisValueItem.getStarcolor());
            } catch (Throwable e) {
                startColor = Color.YELLOW;
            }
            try {
                endColor = Color.parseColor(yAxisValueItem.getEndcolor());
            } catch (Throwable e) {
                endColor = Color.WHITE;
            }
            //设置渐变
            GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{startColor, endColor});
            drawable.setAlpha(200);
            dataSet.setFillDrawable(drawable);
        } else {
            dataSet.setFillColor(Color.TRANSPARENT);
            dataSet.setDrawFilled(false);
        }

        //是否设置为虚线样式
        if (yAxisValueItem.isLineDashAble()) {
            dataSet.enableDashedLine(10f, 5f, 0f);
            dataSet.enableDashedHighlightLine(10f, 5f, 0f);
        }
//        dataSet.enable(true);

        //设置线段是否平滑
        if (yAxisValueItem.isSmooth())
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        else
            dataSet.setMode(LineDataSet.Mode.LINEAR);

        List<Integer> entries = new ArrayList<>();
        for (Entry entry : dataSet.getValues()) {
            entries.add(entry.hashCode());
        }
        IValueFormatter ivf = new MChartValueFormatter.MyValueFormatter(entries, yAxisValueItem.getLabel());
        dataSet.setValueFormatter(ivf);
        dataSet.setDrawValues(true);
        dataSet.setHighlightEnabled(true);

    }
}
