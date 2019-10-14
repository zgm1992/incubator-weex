package com.meiju.weex.componentView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.CompoundButton;

import com.meiju.weex.customer.R;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.annotation.Component;
import com.taobao.weex.common.Constants;
import com.taobao.weex.ui.action.BasicComponentData;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXComponentProp;
import com.taobao.weex.ui.component.WXVContainer;
import com.taobao.weex.ui.view.WXSwitchView;
import com.taobao.weex.utils.WXUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: jiejiang.wu@midea.com
 * Date: 2017/10/27
 * Desc:
 */
@Component(lazyload = false)
public class MSmartWXSwitch extends WXComponent<WXSwitchView> {
    private CompoundButton.OnCheckedChangeListener mListener;

    public MSmartWXSwitch(WXSDKInstance instance, WXVContainer parent, BasicComponentData basicComponentData) {
        super(instance,  parent,basicComponentData);
    }

    @Override
    protected WXSwitchView initComponentHostView(@NonNull Context context) {
        WXSwitchView switchView = new WXSwitchView(context);

        switchView.setThumbDrawable(ContextCompat.getDrawable(context, R.drawable.weexcomm_ic_switch_selector));
        switchView.setTrackDrawable(ContextCompat.getDrawable(context, R.drawable.weexcomm_ic_switch_track_selector));

        return switchView;
    }


    @Override
    public void addEvent(String type) {
        super.addEvent(type);
        if (type != null && type.equals(Constants.Event.CHANGE) && getHostView() != null) {
            if (mListener == null) {
                mListener = new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Map<String, Object> params = new HashMap<>(2);
                        params.put("value", isChecked);

                        Map<String, Object> domChanges = new HashMap<>();
                        Map<String, Object> attrsChanges = new HashMap<>();
                        attrsChanges.put("checked", Boolean.toString(isChecked));
                        domChanges.put("attrs",attrsChanges);
                        fireEvent(Constants.Event.CHANGE, params,domChanges);
                    }
                };
            }
            getHostView().setOnCheckedChangeListener(mListener);
        }
    }

    @Override
    protected void removeEventFromView(String type) {
        super.removeEventFromView(type);
        if (getHostView() != null && Constants.Event.CHANGE.equals(type)) {
            getHostView().setOnCheckedChangeListener(null);
        }
    }

    @Override
    protected boolean setProperty(String key, Object param) {
        switch (key) {
            case Constants.Name.CHECKED:
                Boolean result = WXUtils.getBoolean(param, null);
                if (result != null) {
                    setChecked(result);
                }
                return true;
        }
        return super.setProperty(key, param);
    }

    @WXComponentProp(name = Constants.Name.CHECKED)
    public void setChecked(boolean checked) {
        getHostView().setOnCheckedChangeListener(null);
        getHostView().setChecked(checked);
        getHostView().setOnCheckedChangeListener(mListener);
    }

}
