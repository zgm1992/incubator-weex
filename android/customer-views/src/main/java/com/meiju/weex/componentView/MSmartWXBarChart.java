package com.meiju.weex.componentView;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.meiju.weex.customer.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.SignPostRender;
import com.github.mikephil.charting.utils.Utils;
import com.meiju.weex.componentView.barchart.MBarChart;
import com.meiju.weex.componentView.barchart.MChartBean;
import com.meiju.weex.componentView.barchart.MChartUnit;
import com.meiju.weex.componentView.barchart.MChartValueFormatter;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.WXSDKManager;
import com.taobao.weex.annotation.Component;
import com.taobao.weex.common.Constants;
import com.taobao.weex.ui.action.BasicComponentData;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXComponentProp;
import com.taobao.weex.ui.component.WXVContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by XIONGQI on 2018-3-19.
 */

@Component(lazyload = false)
public class MSmartWXBarChart extends WXComponent<MBarChart> {

    boolean isFirst = true;

    public MSmartWXBarChart(WXSDKInstance instance, WXVContainer parent, BasicComponentData basicComponentData) {
        super(instance, parent, basicComponentData);
    }

    public MSmartWXBarChart(WXSDKInstance instance, WXVContainer parent, int type, BasicComponentData basicComponentData) {
        super(instance, parent, type, basicComponentData);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dp(Context context, float px) {
        try {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (px / scale + 0.5f);
        } catch (Exception e) {
            return (int) (px / 2 + 0.5f);
        }
    }

    @Override
    protected MBarChart initComponentHostView(@NonNull Context context) {
        MBarChart barChart = new MBarChart(context);
        BarChart.LayoutParams layoutParams = new BarChart.LayoutParams(BarChart.LayoutParams.MATCH_PARENT, BarChart.LayoutParams.MATCH_PARENT);
        barChart.setLayoutParams(layoutParams);
        return barChart;
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
        //测试数据，测图表用
//        String tempData = "{\n" +
//                "  \"x\": {\n" +
//                "    \"value\": [1, 2, 3, 4, 5, 6, 7],\n" +
//                "    \"label\": [\"3.22\", \"3.23\", \"3.24\", \"3.25\", \"3.26\", \"3.27\", \"3.28\"]\n" +
//                "  },\n" +
//                "  \"y\": [{\n" +
//                "    \"value\": [6, 6, 2, 1, 2, 3, 7],\n" +
//                "    \"label\": [\"1次\", \"6次\", \"2次\", \"四次\", \"五次\", \"六次\", \"七次\"],\n" +
//                "    \"title\": \"冷藏室\",\n" +
//                "    \"color\": \"#157EFB\",\n" +
//                "    \"backgroud\": \"#fff\"\n" +
//                "  }, {\n" +
//                "    \"value\": [10, 5, 3, 4, 1, 2, 6],\n" +
//                "    \"title\": \"冷冻室\",\n" +
//                "    \"color\": \"#00b9ef\",\n" +
//                "    \"backgroud\": \"#fff\"\n" +
//                "  }, {\n" +
//                "    \"value\": [11, 12, 14, 11,12,15, 12],\n" +
//                "    \"title\": \"查查查\",\n" +
//                "    \"color\": \"#01b24f\",\n" +
//                "    \"backgroud\": \"#fff\"\n" +
//                "  }],\n" +
//                "  \"description\": \"\",\n" +
//                "  \"legend\": {\n" +
//                "    \"postion\": \"TOP_LEFT\",\n" +
//                "    \"orientation\": \"HORIZONTAL\"\n" +
//                "  },\n" +
//                "  \"unit\": {\n" +
//                "    \"x\": \"日期\",\n" +
//                "    \"y\": \"次数\"\n" +
//                "  }\n" +
//                "}";
//        param = tempData;
//        MChartBean barChartBean = JSON.parseObject((String) param,MChartBean.class);
        MChartBean mChartBean = null;

        if (!isFirst) {
            getHostView().getViewPortHandler().getSignPostRender().clear(); //重置设置的手势，这样 setBarSelectIndex 才能生效
            getHostView().clear(); //切换页面数据，进行页面数据重置
        }

        try {
            if (param instanceof String)
                mChartBean = JSON.parseObject((String) param, MChartBean.class);
            if (param instanceof JSONObject)
//                mChartBean = ((JSONObject) param).toJavaObject(MChartBean.class);
                mChartBean = JSON.toJavaObject((JSONObject)param, MChartBean.class);
            if (mChartBean != null)
                setBarChart(getHostView(), mChartBean);

            if (isFirst) {
//                if (param instanceof String)
//                    mChartBean = JSON.parseObject((String) param, MChartBean.class);
//                if (param instanceof JSONObject)
//                    mChartBean = ((JSONObject) param).toJavaObject(MChartBean.class);
//                if (mChartBean != null)
//                    setBarChart(getHostView(), mChartBean);
                isFirst = false;
            }

//            getHostView().setPadding(20,100,20,100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setBarChart(MBarChart hostView, MChartBean barChartBean) {
        float barWidth;
        float groupSpace;
        float barSpace = 0.00f;

        if (barChartBean.getBarWidth() > 0) {
            barWidth = barChartBean.getBarWidth() / (barChartBean.getBarWidth() + barChartBean.getBarSpacing() / 2);
            groupSpace = 1 - barWidth;
        } else {
            barWidth = 0.65f;
            groupSpace = 0.35f;
        }

//        if (barChartBean.getBarSpacing() != 0) {
//            barChartBean.setBarSpacing((int) (IndicatorUtils.dp2px(getContext(), barChartBean.getBarSpacing()) * 1.1));
//        }

        int barTimes = 1;
        if (barChartBean.getY() != null) {
            List<MChartBean.axis> list = barChartBean.getY();
            for (MChartBean.axis axis : list) {
                //如果 存在maxValue 数据 则  配置Y 的 maxValue 和 value 做互换，以便保持已有的描绘机制
                if (axis.getMaxValue() != null && axis.getMaxValue().size() > 0) {
                    List<Float> temp = axis.getValue();
                    axis.setValue(axis.getMaxValue());
                    axis.setMaxValue(temp);
                }
            }
            barTimes = list.size();
        }

        hostView.setNoDataText("");
        hostView.setPinchZoom(true);//设置按比例放缩柱状图
        hostView.setDrawBarShadow(false);
        hostView.animateX(200);//数据显示动画，从左往右依次显示
        hostView.setScaleEnabled(false);
        hostView.getDescription().setEnabled(false);//设置描述


        float unitDefaltDpSize = 13;
        int barNumber = 0;
        // TODO: 2019/3/18 如果只有 1，2 柱状图数据 填充数据补齐,否则会有不对称的情况出现
        if (barChartBean.getX() != null && barChartBean.getX().getLabel() != null) {
            List<String> labelList = barChartBean.getX().getLabel();
            int originalSize = labelList.size();
            if (originalSize == 1 || originalSize == 2) { //如果只是一个柱状图，那么就前后填充一个透明的柱状图，以便自动对齐
                List<Float> vals = barChartBean.getX().getValue();
                List<Float> maxVals = barChartBean.getX().getMaxValue();
                if (vals != null && (vals.size() == labelList.size())) {
                    vals.add(0, vals.get(0) - 1);
                    vals.add(vals.get(vals.size() - 1) + 1);
                }
                if (maxVals != null && (maxVals.size() == labelList.size())) {
                    maxVals.add(0, vals.get(0) - 1);
                    maxVals.add(vals.get(vals.size() - 1) + 1);
                }
                labelList.add(0, "");
                labelList.add("");

                List<MChartBean.axis> yArrays = barChartBean.getY();
                if (yArrays != null) {
//                    int max = 0;
                    for (MChartBean.axis axis : yArrays) {
                        if (axis != null && axis.getValue() != null && axis.getValue().size() == originalSize) {
                            List<String> yLabels = axis.getLabel();
                            if (yLabels != null && yLabels.size() == originalSize) {
                                yLabels.add(0, "");
                                yLabels.add("");
                            }
                            List<Float> yVals = axis.getValue();
                            yVals.add(0, 0f);
                            yVals.add(0f);

                        }

                    }
                    barTimes = yArrays.size();
                }
            }
            getHostView().setBarNumberAndTimes(labelList.size(), barTimes);  //需要在拉取新的柱子数量
            getHostView().getViewPortHandler().getSignPostRender().setLabelMaxCountLimit(originalSize);

        }

        if (barChartBean.isNeedScroll()) {
            //设置barWidthRatio后，就让barSpacing和barWidth失效
            getHostView().setBarWidthAndBarInterval(barChartBean.getBarWidth(), barChartBean.getBarSpacing());
        }


        try {
            if (barChartBean.getBackground() != null) {
                int color = Color.parseColor(barChartBean.getBackground());
                if (color != 0) {
                    getHostView().setBackgroundColor(color);
                }
            }
        } catch (Throwable e) {

        }


        //图例设置
        Legend legend = hostView.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setTextSize(10f);
        legend.setShow(barChartBean.getLegend().isShow());
        //x坐标轴设置
        final XAxis xAxis = hostView.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(barChartBean.getXAxisGridLine()==1); //默认不显示
        xAxis.setDrawLabels(true);
        xAxis.setGranularity(0.5f);//设置最小间隔，防止当放大时，出现重复标签。
        xAxis.setLabelCount(barChartBean.getX().getLabel().size());
        xAxis.setCenterAxisLabels(true);//字体下面的标签 显示在每个直方图的中间
        xAxis.setTextSize(9f);
        IAxisValueFormatter ix = new MChartValueFormatter.MyAxisValueFormatter(barChartBean.getX().getLabel());
        xAxis.setValueFormatter(ix);
        xAxis.setIsBoldText(barChartBean.isxAxisLabelHighLightThicke());
        //设置x轴的label的字体颜色
        xAxis.setTextColor(Color.WHITE);
        if (barChartBean.getxAxisLabelColor() != null) {
            try {
                int color = Color.parseColor(barChartBean.getxAxisLabelColor());
                if (color != 0) {
                    xAxis.setTextColor(color);
                }
            } catch (IllegalArgumentException e) {

            }

        }




        if (barChartBean.getSignPost() == null || !barChartBean.getSignPost().isShow()) {
            xAxis.setShowSignPost(false);
            hostView.setExtraBottomOffset(0);
            hostView.getViewPortHandler().getSignPostRender().setSignPostBean(null);
        } else {
            MChartBean.SignPost signPost = barChartBean.getSignPost();
            //计算滑动标杆需要的内容
            hostView.setExtraBottomOffset(signPost.getLineMarginTop() +
                    signPost.getLinePointRadius() * 2 +
                    +1 +
                    signPost.getLineMarginBottom() +
                    signPost.getCursorMarginTop() +
                    signPost.getCursorHigh() +
                    signPost.getCursorMarginBottom()
            );


            //如果需要显示标杆,先做刻度转换
            signPost.setCursorMarginTop(dp2px(getContext(), signPost.getCursorMarginTop()));
            signPost.setCursorMarginBottom(dp2px(getContext(), signPost.getCursorMarginBottom()));
            signPost.setLineMarginTop(dp2px(getContext(), signPost.getLineMarginTop()));
            signPost.setLineMarginBottom(dp2px(getContext(), signPost.getLineMarginBottom()));
            signPost.setLinePointRadius(dp2px(getContext(), signPost.getLinePointRadius()));

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
            // TODO: 2019-08-20 因为 交互 认为安卓的震动太强，体验比ios差好多，所以去掉
//            if (signPost.isSelectedShake()) {
//                Context context = getHostView().getContext();
//                hostView.getViewPortHandler().getSignPostRender().setVibrator((Vibrator) context.getSystemService(context.VIBRATOR_SERVICE));
//            } else {
//                hostView.getViewPortHandler().getSignPostRender().setVibrator(null);
//            }

            hostView.getViewPortHandler().getSignPostRender().setSignPostBean(signPost);
        }

        hostView.getViewPortHandler().setBarSelectIndex(barChartBean.getBarSelectIndex());


        //设置x轴的轴线的颜色
        xAxis.setAxisLineColor(Color.WHITE);
        if (barChartBean.getxAxisColor() != null) {
            try {
                int color = Color.parseColor(barChartBean.getxAxisColor());
                if (color != 0) {
                    xAxis.setAxisLineColor(color);
                }
            } catch (Exception e) {

            }

        }

        if (barChartBean.getxAxisLabelHighLightColor() != null) {
            try {
                int color = Color.parseColor(barChartBean.getxAxisLabelHighLightColor());
                if (color != 0) {
                    xAxis.setBoldTextColor(color);
                }
            } catch (Exception e) {

            }

        }


        MChartUnit unit = null;
        //设置单位
        if (barChartBean.getUnit() == null) {
            hostView.setUnit(null);
        } else {
            MChartBean.Unit bartUnit = barChartBean.getUnit();
            unit = new MChartUnit(bartUnit.getX(), bartUnit.getY());
            unit.setXPosition(MChartUnit.RIGHT);
            unit.setYPosition(MChartUnit.TOP);
//            unit.setColor(ContextCompat.getColor(getContext(), R.color.text_color_gray));

            if (TextUtils.isEmpty(barChartBean.getxAxisLabelColor())) {
                unit.setxLabelColor(Color.WHITE);
            } else {
                try {
                    unit.setxLabelColor(Color.parseColor(barChartBean.getxAxisLabelColor()));
                } catch (Exception e) {

                }
            }

            if (TextUtils.isEmpty(barChartBean.getyAxisLabelColor())) {
                unit.setyLabelColor(Color.WHITE);
            } else {
                try {
                    unit.setyLabelColor(Color.parseColor(barChartBean.getyAxisLabelColor()));
                } catch (Exception e) {

                }
            }

            if (bartUnit.getxTextSize() == -1) {
                bartUnit.setxTextSize(unitDefaltDpSize - 1);
                if (bartUnit.getxPaddingBottom() == -1) {
                    if (bartUnit.isHorizontalXLabel()) {
                        bartUnit.setxPaddingBottom(0);
                    } else {
                        bartUnit.setxPaddingBottom(px2dp(getContext(), xAxis.getTextSize()));
                    }
                }
            } else if (bartUnit.getxPaddingBottom() == -1) {
                bartUnit.setxPaddingBottom(0);
            }

            int xtextSize = dp2px(getContext(), bartUnit.getxTextSize());
            int xTop = dp2px(getContext(), bartUnit.getxPaddingTop());
            int xBottom = dp2px(getContext(), bartUnit.getxPaddingBottom());

            if (bartUnit.getyTextSize() == -1) {
                bartUnit.setyTextSize(unitDefaltDpSize - 1);
            }

            int ytextSize = dp2px(getContext(), bartUnit.getyTextSize());
            int yTop = dp2px(getContext(), bartUnit.getyPaddingTop());
            int yBottom = dp2px(getContext(), bartUnit.getyPaddingBottom());

            Log.i("jarvanTest2", "the trace  text size is ->" + xtextSize + "  top is ->" + xTop + "  bottom is ->" + xBottom + " xAxis.getTextSize() is ->" + xAxis.getTextSize());
            if (bartUnit.isHorizontalXLabel()) {
                xAxis.setYOffset(xTop + xBottom + Math.max(xtextSize, xAxis.getTextSize()));
            } else {
                xAxis.setYOffset(xTop + xtextSize + xBottom - xAxis.getTextSize()); //设置间距, 需要设置为与x轴的字体重叠样式，对标于ios的情况
            }
            xBottom += getHostView().getExtraBottomOffset(); //剔去滑动标杆重复的可能

            unit.setxTextSize(xtextSize);
            unit.setyTextSize(ytextSize);

            unit.setXOffSet(bartUnit.isHorizontalXLabel() ? 0 : xtextSize, xTop, 0, xBottom);
            unit.setYOffSet(0, yTop, 0, yBottom);

            if (bartUnit.isHorizontalXLabel() && !TextUtils.isEmpty(unit.getxLable())) {
                hostView.setExtraRightOffset(Utils.convertPixelsToDp(unit.getxTextSize() * unit.getxLable().length()));
            }
            hostView.setUnit(unit);


            getHostView().getViewPortHandler().setInterceptTop(ytextSize + yTop + yBottom);//y轴距离顶部距离的高度设置
        }


//        if (barChartBean.getBarSpacing() > 10) {
////        // TODO: 2019/3/25 简单的调试处理x轴向的缩放
//            Matrix matrix = new Matrix();
//            // x轴放大4倍，y不变
//            float scale = ((float) barChartBean.getBarSpacing()) / 10;
//            matrix.postScale(scale, 1.0f);
////        // 设置缩放
//            hostView.getViewPortHandler().refreshXScale(matrix, scale, hostView, false);
//
//        }



        //y轴设置
        YAxis leftAxis = hostView.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
//        leftAxis.setDrawGridLines();
        leftAxis.setDrawGridLines(barChartBean.isDrawYGridLine() || barChartBean.getYAxisGridLine()==1); //默认不显示的格子线
        try {
            int yGridColor = Color.parseColor(barChartBean.getyGridColor());
            leftAxis.setGridColor(yGridColor);
        } catch (Exception e) {
            leftAxis.setGridColor(0x333333);
        }
        leftAxis.setDrawAxisLine(true);
        leftAxis.setSpaceBottom(0.2f);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setUnitLabel(barChartBean.getyGraduationLabel());
        leftAxis.setIsIgnoreFirstDrawGridLines(true);
        if (barChartBean.getyAxisLabelCount() != -1) {
            leftAxis.setLabelCount(barChartBean.getyAxisLabelCount(),true);
        }

        if (barChartBean.isyAxisLabelEnable()) {
            hostView.setMinOffset(15f);
            leftAxis.setDrawLabels(true);
        } else {
            hostView.setMinOffset(0);
            leftAxis.setDrawLabels(false);
        }

        if (barChartBean.getyAxisLabelColor() != null) {
            try {
                int color = Color.parseColor(barChartBean.getyAxisLabelColor());
                if (color != 0) {
                    leftAxis.setTextColor(color);
                }
            } catch (Exception e) {

            }
        }

        if (!TextUtils.isEmpty(barChartBean.getBarTouchTop())) {
            //因为要保证这个值在weex层设置了才生效,所以做String 存储，再转义
            if (Boolean.valueOf(barChartBean.getBarTouchTop())) {
                leftAxis.setTouchType(AxisBase.TouchType.notShowValue);
            } else {
                leftAxis.setTouchType(AxisBase.TouchType.ShowValue);
            }
        }

        //设置y轴的轴线的颜色
        leftAxis.setAxisLineColor(Color.WHITE);
        if (barChartBean.getyAxisColor() != null) {
            try {
                int color = Color.parseColor(barChartBean.getyAxisColor());
                if (color != 0) {
                    leftAxis.setAxisLineColor(color);
                }
            } catch (Exception e) {

            }
        }

        //设置格子线属性
        if (barChartBean.getAxisGridColor() != null) {

            try {
                int color = Color.parseColor(barChartBean.getAxisGridColor());
                if (color != 0) {
                    leftAxis.setGridColor(color);
                    xAxis.setGridColor(color);
                }
            } catch (Exception e) {

            }
        }


        //隐藏右边Y轴
        hostView.getAxisRight().setEnabled(false);
        //设置坐标轴最大最小值
        float yMax = barChartBean.getYMax();
        float yMin = barChartBean.getYMin();
        yMax = (float) (yMax * 1.1);
        yMax = yMax == 0f ? 1f : yMax;
        leftAxis.setAxisMaximum(yMax);
        leftAxis.setAxisMinimum(yMin * 0.8f);

        final MChartBean.axis yAxisValue1 = barChartBean.getY().get(0);
        MChartBean.axis[] yAxisValueArray = new MChartBean.axis[barChartBean.getY().size()];
        yAxisValueArray = barChartBean.getY().toArray(yAxisValueArray);
        setChartData(hostView, barChartBean, yAxisValueArray);


        setSpace(xAxis, yAxisValueArray, yAxisValue1, barSpace, groupSpace, barWidth);

        final MChartBean.axis[] finalYAxisValueArray = yAxisValueArray;

        hostView.setOnBarSpaceAndGroupSpaceListener(new MBarChart.OnBarSpaceAndGroupSpaceListener() {
            @Override
            public void onChange(float barWidth1, float groupSpace1) {
                setSpace(xAxis, finalYAxisValueArray, yAxisValue1, 1 - groupSpace1 - barWidth1, groupSpace1, barWidth1);
            }
        });


        hostView.invalidate();
        hostView.caculateBarMatrix();

        hostView.getViewPortHandler().getSignPostRender().setOnBarClickListener(hostView.getViewPortHandler(), new SignPostRender.OnBarClickListener() {
            @Override
            public void onBarClick(int groupIndex, int barIndex) {
                MSmartWXBarChart.this.notifyToWeex("barClicked", String.valueOf(groupIndex), String.valueOf(barIndex));
            }
        });

    }


    private void setSpace(XAxis xAxis, MChartBean.axis[] yAxisValueArray, MChartBean.axis yAxisValue1,
                          float barSpace, float groupSpace, float barWidth) {
        //设置两根柱子的距离
        BarData barData = getHostView().getBarData();
        if (yAxisValueArray.length > 0) {
            barData.setBarWidth(barWidth / yAxisValueArray.length);
        }
        float groupWidth = barData.getGroupWidth(groupSpace, barSpace);
        xAxis.setAxisMinimum(0);
//        xAxis.setAxisMaximum(groupWidth * yAxisValue1.getValue().size());
        xAxis.setAxisMaximum(groupWidth * yAxisValue1.getValue().size() + groupSpace);
        getHostView().groupBars(0, groupSpace, barSpace);

    }


    @Override
    public void onActivityDestroy() {
        super.onActivityDestroy();
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


    private BarData setChartData(MBarChart barChart, MChartBean barChartBean, MChartBean.axis... yAxisValue) {
        //根据数据构建BarEntry List
        List<List<BarEntry>> entriesList = new ArrayList<>();
        for (Integer i = 0, n = yAxisValue.length; i < n; ++i) {
            MChartBean.axis yAxisValueItem = yAxisValue[i];
            if (yAxisValueItem == null) continue;
            List<BarEntry> entries = new ArrayList<>();
            for (Integer j = 0, l = yAxisValueItem.getValue().size(); j < l; ++j) {
                if (j >= yAxisValueItem.getValue().size()) {
                    break;
                }
                float maxValue = yAxisValueItem.getValue().get(j); //在一开始做了数据互换的
                BarEntry barEntry = new BarEntry(i + 1, maxValue);
                if (yAxisValueItem.getMaxValue() != null && yAxisValueItem.getMaxValue().size() > j) {
                    //在一开始做了数据互换的,所以从 getValue() 取出的是 maxValue , getMaxValue()取出的是 value ，这是为了不破坏原有的描述逻辑
                    float tempValue = yAxisValueItem.getMaxValue().get(j);
                    if (tempValue >= maxValue) {
                        tempValue = 1;
                    } else if (tempValue > 0) {
                        tempValue = tempValue / maxValue;
                    }
                    barEntry.setPercent(tempValue); //设置成百分比，到描述层后再做取用

                }
                entries.add(barEntry);
            }
            if (entries.size() > 0)
                entriesList.add(entries);
        }

        BarData barData = barChart.getData();
        //设置BarData每个BarDataSet的数据、样式等
        if (barData != null && barChart.getData().getDataSetCount() > 0) {
            for (int i = 0, n = barData.getDataSetCount(); i < n; i++) {
                BarDataSet dataSet = (BarDataSet) barChart.getData().getDataSetByIndex(i);
                List<BarEntry> entries = entriesList.get(i);

                if (entries != null) {
                    dataSet.setValues(entries);
                }
                MChartBean.axis yAxisValueItem;
                if (i < yAxisValue.length) {
                    yAxisValueItem = yAxisValue[i];
                    setDataSetStyle(dataSet, yAxisValueItem, barChartBean);
                }
            }
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            ArrayList<IBarDataSet> dataSetList = new ArrayList<>();
            for (int i = 0, n = entriesList.size(); i < n; i++) {
                List<BarEntry> entries = entriesList.get(i);
                if (entries == null) continue;

                MChartBean.axis yAxisValueItem = null;
                if (i < yAxisValue.length)
                    yAxisValueItem = yAxisValue[i];
                if (yAxisValueItem == null) continue;
                BarDataSet dataSet = new BarDataSet(entries, null);
                dataSet.setRadius(dp2px(getContext(), barChartBean.getBorderRadius()));
                dataSet.setBottomRadius(dp2px(getContext(), barChartBean.getBottomBorderRadius()));
                dataSet.setBarBorderWidth(barChartBean.getBorderWidth());
                dataSet.setBorderType(barChartBean.getBorderType());
                if (barChartBean.getBorderColor() != null) {
                    try {
                        dataSet.setBarBorderColor(Color.parseColor(barChartBean.getBorderColor()));
                    } catch (IllegalArgumentException e) {
                        dataSet.setBarBorderColor(Color.TRANSPARENT);
                    }
                }
                setDataSetStyle(dataSet, yAxisValueItem, barChartBean);
                dataSetList.add(dataSet);
            }

            BarData data = new BarData(dataSetList);
            data.setValueTextSize(10f);
            barChart.setData(data);
            //这里是value 的数值显示，不是label的处理
//            data.setValueFormatter((value, entry, i, viewPortHandler) -> {
//
////                    if (value != 0.0f) {
////                        return String.valueOf(value);
////                    } else {
//                return "";
////                    }
//            });
        }

        return barChart.getBarData();
    }


    private void setDataSetStyle(BarDataSet dataSet, MChartBean.axis yAxisValueItem, MChartBean bean) {
        if (yAxisValueItem == null) return;
        //设置标题
        String title = yAxisValueItem.getTitle();
        dataSet.setLabel(title == null ? "" : title);


        //设置被选中的高亮颜色

        try {
            if (!TextUtils.isEmpty(yAxisValueItem.getHighLightColor())) {
                int color = Color.parseColor(yAxisValueItem.getHighLightColor());
                dataSet.setHighLightColor(color);
                getHostView().getViewPortHandler().setHighLightColor(color);
            }
        } catch (Throwable e) {
            int color = Color.parseColor("#3FFFBB73");
            dataSet.setHighLightColor(color);
            getHostView().getViewPortHandler().setHighLightColor(color);
        }

        try {
            if (!TextUtils.isEmpty(yAxisValueItem.getMaxHighLightColor())) {
                int color = Color.parseColor(yAxisValueItem.getMaxHighLightColor());
                dataSet.setMaxHighLightColor(color);
                getHostView().getViewPortHandler().setMaxHighLightColor(color);
            }
        } catch (Throwable e) {
            int color = Color.parseColor("#3FFFBB73");
            dataSet.setMaxHighLightColor(color);
            getHostView().getViewPortHandler().setMaxHighLightColor(color);
        }

        dataSet.setHighlightEnabled(yAxisValueItem.isHighLightEnable());
        if (getHostView().getViewPortHandler().getSignPostRender().getSignPostBean() != null) {
//            dataSet.isShowSignal = getHostView().getViewPortHandler().getSignPostRender().getSignPostBean().isShow();
            dataSet.setSignalEnabled(getHostView().getViewPortHandler().getSignPostRender().getSignPostBean().isShow());
        }

        try {
            if (yAxisValueItem.getColor().toLowerCase().matches("(\\[).*?(\\])")) {
                //数组，渐变色
                String[] colorArray = yAxisValueItem.getColor().substring(1, yAxisValueItem.getColor().length() - 1).split(",");
                if (colorArray.length > 1) {
                    int endColor = Color.parseColor(colorArray[0].replace("\"", ""));   //end是上方
                    int startColor = Color.parseColor(colorArray[1].replace("\"", "")); //start是下方
                    dataSet.setGradientColor(startColor, endColor);
                }
            } else {
                dataSet.setColor(Color.parseColor(yAxisValueItem.getColor()));
            }
        } catch (Throwable e) {
            dataSet.setColor(Color.TRANSPARENT);
        }

        try {
            dataSet.setMaxColor(Color.parseColor(yAxisValueItem.getMaxColor()));
        } catch (Throwable e) {
            dataSet.setMaxColor(Color.TRANSPARENT);
        }

        if (yAxisValueItem.getBackground() == null) {
            dataSet.setBarShadowColor(ContextCompat.getColor(getContext(),R.color.translucent));
        } else {
            try {
                dataSet.setBarShadowColor(Color.parseColor(yAxisValueItem.getBackground()));
            } catch (Throwable e) {
                dataSet.setBarShadowColor(Color.TRANSPARENT);
            }
        }

        //柱子上的数字的相关
        dataSet.setDrawValues(bean.isDrawValues());
        if (bean.isDrawValues()) {
//            if (yAxisValueItem.getLabel() != null) {
                List<Integer> entries = new ArrayList<>();
                for (Entry entry : dataSet.getValues()) {
                    entries.add(entry.hashCode());
                }
                IValueFormatter ivf = new MChartValueFormatter.MyValueFormatter(entries, yAxisValueItem.getLabel());
                dataSet.setValueFormatter(ivf);
//            }
            try {
                dataSet.setValueTextColor(Color.parseColor(bean.getValueTextColor()));
            } catch (Throwable e) {
                dataSet.setValueTextColor(Color.BLACK);
            }
            if (bean.getValueTextSize() > 0f) {
                dataSet.setValueTextSize(bean.getValueTextSize());
            }
        }
    }
}
