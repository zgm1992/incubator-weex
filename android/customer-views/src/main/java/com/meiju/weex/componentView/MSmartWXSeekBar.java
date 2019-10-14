package com.meiju.weex.componentView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;

import com.meiju.weex.customer.R;
import com.meiju.weex.componentView.indicatorseekbar.IndicatorSeekBar;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.annotation.Component;
import com.taobao.weex.common.Constants;
import com.taobao.weex.ui.action.BasicComponentData;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXComponentProp;
import com.taobao.weex.ui.component.WXVContainer;
import com.taobao.weex.utils.WXUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * Author: jiejiang.wu@midea.com
 * Date: 2017/10/19
 * Desc:
 */

@Component(lazyload = false)
public class MSmartWXSeekBar extends WXComponent<IndicatorSeekBar> {
    WXVContainer mParent = null;
    private IndicatorSeekBar.OnSeekBarChangeListener mListener;
    private String filePath = null;

    @Deprecated
    public MSmartWXSeekBar(WXSDKInstance instance, WXVContainer parent, String instanceId, boolean isLazy, BasicComponentData basicComponentData) {
        this(instance, parent, isLazy, basicComponentData);
        mParent = parent;
    }

    public MSmartWXSeekBar(WXSDKInstance instance, WXVContainer parent, boolean isLazy, BasicComponentData basicComponentData) {
        super(instance, parent, isLazy, basicComponentData);
        mParent = parent;
    }

    public static Bitmap stringToBitmap(String string) {
        Bitmap bitmap = null;
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        try {
            byte[] bitmapArray = Base64.decode(string.split(",")[1], Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected IndicatorSeekBar initComponentHostView(@NonNull Context context) {
        IndicatorSeekBar seekBar = (IndicatorSeekBar) LayoutInflater.from(context)
                .inflate(R.layout.wx_seek_bar, null);
        if (mParent != null) {
            seekBar.setParentView(mParent.getHostView());
        }
        return seekBar;
    }

    int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void resetPadding() {
        IndicatorSeekBar indicatorSeekBar = getHostView();
        if (null != indicatorSeekBar) {
            int size = getHostView().getThubSize();
            if (size == 0) {
                size = dp2px(getContext(), 10);
            } else {
                size = size / 2; //只需要一半的距离
            }

            if (indicatorSeekBar.getThumbProgressStay()) {
                indicatorSeekBar.setPadding(size,
                        dp2px(getContext(), 15),
                        size, 0);
            } else {
                indicatorSeekBar.setPadding(size,
                        0,
                        size, 0);
            }
            indicatorSeekBar.invalidate();
        }
    }


    @Override
    public void addEvent(String type) {
        super.addEvent(type);

        if (type != null
                && (type.equals("slideChange") || type.equals("slideEnd"))
                && getHostView() != null) {
            if (mListener == null) {
                mListener = new IndicatorSeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(IndicatorSeekBar seekBar, float progress, float progressFloat, boolean fromUserTouch) {
                        Map<String, Object> params = new HashMap<>(2);
                        params.put("value", progress);
                        fireEvent("slideChange", params);
                    }

                    @Override
                    public void onSectionChanged(IndicatorSeekBar seekBar, int thumbPosOnTick, String tickBelowText, boolean fromUserTouch) {

                    }

                    @Override
                    public void onStartTrackingTouch(IndicatorSeekBar seekBar, int thumbPosOnTick) {

                    }

                    @Override
                    public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                        Map<String, Object> params = new HashMap<>(2);
                        params.put("value", seekBar.getProgress());
                        fireEvent("slideEnd", params);
                    }
                };
            }
        }
    }

    @Override
    protected boolean setProperty(String key, Object param) {
        switch (key) {
            case Constants.Name.MAX:
//                int max = WXUtils.getInt(param);
                float max = WXUtils.getFloat(param);
                setMaxValue(max);
                return true;
            case Constants.Name.MIN:
//                int min = WXUtils.getInt(param);
                float min = WXUtils.getFloat(param);
                setMinValue(min);
                return true;
            case "step":
//                int step = WXUtils.getInt(param);
                float step = WXUtils.getFloat(param);
                setStepValue(step);
                return true;
            case "unit":
                String unit = WXUtils.getString(param, "%");
                setUnitValue(unit);
                return true;
            case Constants.Name.VALUE:
//                int value = WXUtils.getInt(param);
                float value = WXUtils.getFloat(param);
                setValue(value);
                return true;
            case "showTip":
                //是否显示指示符
                boolean showTip = WXUtils.getBoolean(param, true);
                showTip(showTip);
                return true;
            case "pointColor":
                //圆球颜色(滑动条已滑动区域颜色同圆球一样）
                String pointColor = WXUtils.getString(param, "#00b9ef");
                setPointColor(pointColor);
                return true;
            case "axisColor":
                //未滑动条的颜色
                String axisColor = WXUtils.getString(param, "#FF4081");
                setAxisColor(axisColor);
                return true;
            case "axisStartColor":
                //未滑动条的渐变开始颜色
                String axisStartColor = WXUtils.getString(param, "");
                setAxisStartColor(axisStartColor);
                return true;
            case "axisEndColor":
                //未滑动条的渐变开始颜色
                String axisEndColor = WXUtils.getString(param, "");
                setAxisEndColor(axisEndColor);
                return true;

            case "axisH":
                //滑动条高度（像素）
                int axisH = WXUtils.getInt(param);
                setAxisH(axisH);
                return true;
            case "pointH":
                //圆球的高度
                int pointH = WXUtils.getInt(param);
                setPointH(pointH);
                resetPadding();
                return true;
            case "disable":
                boolean disable = WXUtils.getBoolean(param, false);
                setDisable(disable);
                return true;
            case "axisBgColor":
                String axisBgColor = WXUtils.getString(param, "#00b9ef");
                setAxisBgColor(axisBgColor);
                return true;
//            case "axisBgStartColor":
//                String axisBgStartColor = WXUtils.getString(param, "");
//                setAxisBgStartColor(axisBgStartColor);
//                return true;
//            case "axisBgEndColor":
//                String axisBgEndColor = WXUtils.getString(param, "");
//                setAxisBgEndColor(axisBgEndColor);
//                return true;
            case "axisAlpha":
                float axisAlpha = WXUtils.getFloat(param, 1f);
                setAxisAlpha(axisAlpha);
                return true;
            case "axisBgAlpha":
                float axisBgAlpha = WXUtils.getFloat(param, 1f);
                setAxisBgAlpha(axisBgAlpha);
                return true;
            case "sliderImage":
                String base64 = WXUtils.getString(param, "");
                setSliderImage(base64);
                return true;
        }
        return super.setProperty(key, param);
    }

    /**
     * 这个接口，weex会传入图片的base64数据
     *
     * @param sliderImage
     */
    @WXComponentProp(name = "sliderImage")
    public void setSliderImage(String sliderImage) {
        Bitmap bitmap = stringToBitmap(sliderImage);
        if (bitmap != null) {
            getHostView().setThumbDraw(bitmap);
            getHostView().invalidate();
        }

    }

//    @WXComponentProp(name = "pointImg")
//    public void setPointImg(String imgPath) {
//        getHostView().setOnSeekChangeListener(null);
//        Log.i("jarvan", " path is ->" + imgPath);
//        if (filePath == null) {
//            Context context = getInstance().getContext();
//            if (context != null && context instanceof IndexActivity) {
//                String path = ((IndexActivity) context).getPath();
////                File file = new File(path);
//                if (path == null) {
//                    return;
//                }
//                if (path.endsWith(".js")) {
//                    filePath = path.substring(0, path.lastIndexOf("/"));
//                }
//                Log.i("jarvan", " path  ->" + path + " file path -> " + filePath);
//            }
//        }
//        String realPath = getRelativePath(filePath, imgPath);
//        getHostView().setThumbImagePath(realPath);
//
//        getHostView().setOnSeekChangeListener(mListener);
//    }

    private String getRelativePath(String path, String chilPath) {
        if (TextUtils.isEmpty(chilPath) || TextUtils.isEmpty(path)) {
            return path;
        }
        if (chilPath.startsWith("./")) {
            chilPath = chilPath.substring(2); // 去掉./这样的包装
            return getRelativePath(path, chilPath);
        } else if (chilPath.startsWith("../")) { //上一层的路径
            File file = new File(path);
            path = file.getParent().toString();
            chilPath = chilPath.substring(3);   //去掉 ../这样的包装
            return getRelativePath(path, chilPath);
        } else {
            return combine(path, chilPath);
        }
    }

    private String combine(String path, String childPath) {
        if (path.endsWith("/") && childPath.startsWith("/")) {
            return path + childPath.substring(1);
        } else if (!path.endsWith("/") && !childPath.startsWith("/")) {
            return path + "/" + childPath;
        } else {
            return path + childPath;
        }
    }

    @WXComponentProp(name = "disable")
    public void setDisable(boolean disable) {
        getHostView().setOnSeekChangeListener(null);
        getHostView().setDisable(disable);
        getHostView().setOnSeekChangeListener(mListener);
    }

    @WXComponentProp(name = Constants.Name.MAX)
    public void setMaxValue(float max) {
        getHostView().setOnSeekChangeListener(null);
        getHostView().setMax(max);
        getHostView().setOnSeekChangeListener(mListener);
    }

    @WXComponentProp(name = Constants.Name.MIN)
    public void setMinValue(float min) {
        getHostView().setOnSeekChangeListener(null);
        getHostView().setMin(min);
        getHostView().setOnSeekChangeListener(mListener);
    }

    @WXComponentProp(name = "step")
    public void setStepValue(float step) {
        getHostView().setOnSeekChangeListener(null);
        getHostView().setStep(step);
        getHostView().setOnSeekChangeListener(mListener);
    }

    @WXComponentProp(name = "unit")
    public void setUnitValue(String unit) {
        getHostView().setOnSeekChangeListener(null);
        getHostView().setUnit(unit);
        getHostView().setOnSeekChangeListener(mListener);
    }

    @WXComponentProp(name = Constants.Name.VALUE)
    public void setValue(float value) {
        getHostView().setOnSeekChangeListener(null);
        getHostView().setProgress(value);
        getHostView().setOnSeekChangeListener(mListener);
    }

    @WXComponentProp(name = "showTip")
    public void showTip(boolean showTip) {
        getHostView().setOnSeekChangeListener(null);
        getHostView().setThumbProgressStay(showTip);
        resetPadding();
        getHostView().setOnSeekChangeListener(mListener);
    }

    @WXComponentProp(name = "pointColor")
    public void setPointColor(String pointColor) {
        getHostView().setOnSeekChangeListener(null);
        getHostView().setThumbColor(pointColor);
        getHostView().invalidate();
        getHostView().setOnSeekChangeListener(mListener);


    }

    @WXComponentProp(name = "axisBgColor")
    public void setAxisBgColor(String axisBgColor) {
        getHostView().setOnSeekChangeListener(null);
        getHostView().setAxisBgColor(axisBgColor);
        getHostView().invalidate();
        getHostView().setOnSeekChangeListener(mListener);
    }

//    @WXComponentProp(name = "axisBgStartColor")
//    public void setAxisBgStartColor(String axisBgStartColor) {
//        getHostView().setOnSeekChangeListener(null);
//        getHostView().setAxisBgStartColor(axisBgStartColor);
//        getHostView().invalidate();
//        getHostView().setOnSeekChangeListener(mListener);
//    }
//
//    @WXComponentProp(name = "axisBgEndColor")
//    public void setAxisBgEndColor(String axisBgEndColor) {
//        getHostView().setOnSeekChangeListener(null);
//        getHostView().setAxisBgEndColor(axisBgEndColor);
//        getHostView().invalidate();
//        getHostView().setOnSeekChangeListener(mListener);
//    }


    @WXComponentProp(name = "axisAlpha")
    public void setAxisAlpha(float alpha) {
        getHostView().setOnSeekChangeListener(null);
        getHostView().setBackgroundTrackAlpha(alpha);
        getHostView().invalidate();
        getHostView().setOnSeekChangeListener(mListener);
    }

    @WXComponentProp(name = "axisBgAlpha")
    public void setAxisBgAlpha(float alpha) {
        getHostView().setOnSeekChangeListener(null);
        getHostView().setAxisBgAlpha(alpha);
        getHostView().invalidate();
        getHostView().setOnSeekChangeListener(mListener);
    }


    @WXComponentProp(name = "axisColor")
    public void setAxisColor(String axisColor) {
        getHostView().setOnSeekChangeListener(null);
        getHostView().setBackgroundTrackColor(axisColor);
        getHostView().invalidate();
        getHostView().setOnSeekChangeListener(mListener);
    }

    @WXComponentProp(name = "axisStartColor")
    public void setAxisStartColor(String axisStartColor) {
        getHostView().setOnSeekChangeListener(null);
        getHostView().setBackgroundTrackStartColor(axisStartColor);
        getHostView().invalidate();
        getHostView().setOnSeekChangeListener(mListener);
    }

    @WXComponentProp(name = "axisColor")
    public void setAxisEndColor(String axisEndColor) {
        getHostView().setOnSeekChangeListener(null);
        getHostView().setBackgroundTrackEndColor(axisEndColor);
        getHostView().invalidate();
        getHostView().setOnSeekChangeListener(mListener);
    }

    @WXComponentProp(name = "axisH")
    public void setAxisH(int axisH) {
        getHostView().setOnSeekChangeListener(null);
        getHostView().setProgressTrackSize(axisH);
        getHostView().setOnSeekChangeListener(mListener);
    }

    @WXComponentProp(name = "pointH")
    public void setPointH(int axisH) {
        getHostView().setOnSeekChangeListener(null);
        getHostView().setThumbSize(axisH);
        getHostView().setOnSeekChangeListener(mListener);
    }

    @Override
    public void onActivityDestroy() {
        if (getHostView() != null) {
            Bitmap bitmap = getHostView().getThumbDraw();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        super.onActivityDestroy();

    }

    //在这里计算TickNum值，因为setProperty获取到step值、max值、min值没有固定次序
    @Override
    public void readyToRender() {
        super.readyToRender();

        float step = getHostView().getStep();
        float max = getHostView().getMax();
        float min = getHostView().getMin();

        int tickNum = (int) ((max - min) / step);
        getHostView().setTickNum(tickNum);
    }

}
