package com.meiju.weex.componentView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.meiju.weex.customer.R;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.annotation.Component;
import com.taobao.weex.common.Constants;
import com.taobao.weex.ui.action.BasicComponentData;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXComponentProp;
import com.taobao.weex.ui.component.WXVContainer;
import com.taobao.weex.utils.WXUtils;
import com.meiju.weex.componentView.verticalseekbar.MsVerticalSeekBar;

import java.util.HashMap;
import java.util.Map;

@Component(lazyload = false)
public class MSmartWXVerticalSeekBar extends WXComponent<MsVerticalSeekBar> {
    private MsVerticalSeekBar.OnSlideChangeListener mListener;

    @Deprecated
    public MSmartWXVerticalSeekBar(WXSDKInstance instance, WXVContainer parent, String instanceId, boolean isLazy, BasicComponentData basicComponentData) {
        this(instance, parent, isLazy,basicComponentData);
    }

    public MSmartWXVerticalSeekBar(WXSDKInstance instance, WXVContainer parent, boolean isLazy, BasicComponentData basicComponentData) {
        super(instance, parent, isLazy,basicComponentData);
    }

    @Override
    protected MsVerticalSeekBar initComponentHostView(@NonNull Context context) {
        MsVerticalSeekBar seekBar = (MsVerticalSeekBar) LayoutInflater.from(context).inflate(R.layout.wx_msvertical_seek_bar, null);
        return seekBar;
    }

    @Override
    public void addEvent(String type) {
        super.addEvent(type);

        if (type != null
                && type.equals("slideEnd")
                && getHostView() != null) {
            if (mListener == null) {
                mListener = new MsVerticalSeekBar.OnSlideChangeListener() {
                    @Override
                    public void OnSlideChangeListener(View view, float progress) {
                        Map<String, Object> params = new HashMap<>(2);
                        params.put("value", progress);
                        fireEvent("slideChange", params);
                    }

                    @Override
                    public void onSlideStopTouch(View view, float progress) {
                        Map<String, Object> params = new HashMap<>(2);
                        params.put("value", progress);
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
                int max = WXUtils.getInt(param);
                setMaxValue(max);
                return true;
            case Constants.Name.MIN:
                int min = WXUtils.getInt(param);
                setMinValue(min);
                return true;
            case Constants.Name.VALUE:
                int value = WXUtils.getInt(param);
                setValue(value);
                return true;
            case "image":
                String image = WXUtils.getString(param, "");
                setImageValue(image);
                return true;
            case "enable":
                boolean enable = WXUtils.getBoolean(param, true);
                setEnableValue(enable);
                return true;
        }
        return super.setProperty(key, param);
    }

    @WXComponentProp(name = Constants.Name.MAX)
    public void setMaxValue(int max) {
        getHostView().setOnSlideChangeListener(null);
        getHostView().setMax(max);
        getHostView().setOnSlideChangeListener(mListener);
    }

    @WXComponentProp(name = Constants.Name.MIN)
    public void setMinValue(int min) {
        getHostView().setOnSlideChangeListener(null);
        getHostView().setMin(min);
        getHostView().setOnSlideChangeListener(mListener);
    }

    @WXComponentProp(name = Constants.Name.VALUE)
    public void setValue(int value) {
        Log.i("MsVerticalSeekBar", "setValue " + value);
        getHostView().setOnSlideChangeListener(null);
        getHostView().setProgress(value);
        getHostView().setOnSlideChangeListener(mListener);
    }

    @WXComponentProp(name = "image")
    public void setImageValue(String imagePath) {
        getHostView().setOnSlideChangeListener(null);
        getHostView().setImage(imagePath);
        getHostView().setOnSlideChangeListener(mListener);
    }

    @WXComponentProp(name = "enable")
    public void setEnableValue(boolean enable) {
        getHostView().setOnSlideChangeListener(null);
        getHostView().setIsEnable(enable);
        Log.i("MsVerticalSeekBar", "setEnabled " + enable);
        getHostView().setOnSlideChangeListener(mListener);
    }


}
