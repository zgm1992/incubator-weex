package com.meiju.weex.componentView;

import android.util.Log;
import android.webkit.WebView;

import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.annotation.Component;
import com.taobao.weex.ui.action.BasicComponentData;
import com.taobao.weex.ui.component.WXVContainer;
import com.taobao.weex.ui.component.WXWeb;

import java.util.HashMap;
import java.util.Map;

/**
 * fix bug weex 的 web 需要重写，否则无法加载社区的一些url 会报 net 错误
 */
@Component(lazyload = false)
public class MSmartWXWeb extends WXWeb {

    private String[] interceptUrls;

    @Deprecated
    public MSmartWXWeb(WXSDKInstance instance, WXVContainer parent, String instanceId, boolean isLazy, BasicComponentData basicComponentData) {
        this(instance, parent, isLazy, basicComponentData);
    }

    public MSmartWXWeb(WXSDKInstance instance, WXVContainer parent, boolean isLazy, BasicComponentData basicComponentData) {
        super(instance, parent, isLazy, basicComponentData);
    }

    @Override
    protected boolean setProperty(String key, Object param) {
        if ("interceptUrls".equals(key) && param instanceof String) {
            interceptUrls = ((String) param).split(",");
            return true;
        }
        return super.setProperty(key, param);
    }

    @Override
    protected void createWebView() {
        mWebView = new MSmartWXWebView(getContext(), "");

        ((MSmartWXWebView) mWebView).setInterceptCallBack(new MSmartWXWebView.InterceptCallBack() {
            @Override
            public boolean shouldInterceptRequest(WebView view, String url) {
                Log.d("MSmartWXWeb", url);
                if (isContain(url)) {
                    Map<String, Object> map = new HashMap<>();
                    Map<String, Object> messageBody = new HashMap<>();
                    messageBody.put("url", url);
                    map.put("messageType", "webIntercept");
                    map.put("messageBody", messageBody);
                    getInstance().fireGlobalEventCallback("receiveMessageFromApp", map);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private boolean isContain(String url) {
        if (interceptUrls == null || url == null) return false;
        for (String intercept : interceptUrls) {
            if (url.startsWith(intercept)) {
                return true;
            }
        }
        return false;
    }

}
