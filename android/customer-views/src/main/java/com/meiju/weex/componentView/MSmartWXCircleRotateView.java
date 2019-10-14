package com.meiju.weex.componentView;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.alibaba.weex.commons.R;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.annotation.Component;
import com.taobao.weex.ui.action.BasicComponentData;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXComponentProp;
import com.taobao.weex.ui.component.WXVContainer;
import com.taobao.weex.utils.WXUtils;

import org.json.JSONException;
import org.json.JSONObject;

@Component(lazyload = false)
public class MSmartWXCircleRotateView extends WXComponent<CircleRotateLoadingView> {

    @Deprecated
    public MSmartWXCircleRotateView(WXSDKInstance instance, WXVContainer parent, String instanceId, boolean isLazy, BasicComponentData basicComponentData) {
        this(instance,  parent, isLazy,basicComponentData);
    }

    public MSmartWXCircleRotateView(WXSDKInstance instance, WXVContainer parent, boolean isLazy, BasicComponentData basicComponentData) {
        super(instance,  parent, isLazy,basicComponentData);
    }

    @Override
    protected CircleRotateLoadingView initComponentHostView(@NonNull Context context) {
        final CircleRotateLoadingView circleRotateView = (CircleRotateLoadingView) LayoutInflater.from(context)
                .inflate(R.layout.wx_circle_rotate_view, null);
        ViewTreeObserver vto = circleRotateView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    circleRotateView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    circleRotateView.startAnim();
                }
            }
        });
        return circleRotateView;
    }


    @Override
    public void addEvent(String type) {
        super.addEvent(type);
    }

    @Override
    protected boolean setProperty(String key, Object param) {
        switch (key) {
            case "option":
                //控件的宽度
                String option = WXUtils.getString(param, "{\"colorDark\":\"#46a0f5\",\"colorLight\":\"#cccccc\"," +
                        "\"duration\":4000,\"height\":300,\"width\":400}");
                setHeight(option);
                return true;
        }
        return super.setProperty(key, param);
    }

    @WXComponentProp(name = "option")
    public void setHeight(String option) {
        try {
            JSONObject optionJson = new JSONObject(option);
//            int width = optionJson.getInt("width");
            int height = optionJson.optInt("height");
            int duration = optionJson.optInt("duration");
            String colorDark = optionJson.optString("colorDark");
            String colorLight = optionJson.optString("colorLight");
            Log.d("WXCircleRotateView", "height = " + height);

            Context context = getHostView().getContext();
            DisplayMetrics outMetrics = context.getResources().getDisplayMetrics();
            int width = (int) (outMetrics.widthPixels / 2.5);

            if (height < 180) {
                height = 180;
            }

            ViewGroup.LayoutParams params = getHostView().getLayoutParams();
            params.width = width;
            params.height = height;
            getHostView().setMoveDistance(width / 2);
            getHostView().setLayoutParams(params);
            getHostView().setDurationTime(duration);
            getHostView().setLeftBallColor(colorLight);
            getHostView().setRightBallColor(colorDark);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }
}
