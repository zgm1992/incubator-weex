package com.meiju.weex.componentView;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.taobao.weex.ui.view.WXWebView;
import com.taobao.weex.utils.WXLogUtils;

public class MSmartWXWebView extends WXWebView {

    private OnErrorListener mOnErrorListener;
    private OnPageListener mOnPageListener;

    private InterceptCallBack interceptCallBack;

    private WebView web;

    public MSmartWXWebView(Context context, String origin) {
        super(context, origin);
    }

    public void setInterceptCallBack(InterceptCallBack c) {
        interceptCallBack = c;
    }

    public WebView getWeb() {
        return web;
    }

    @Override
    public View getView() {
        View view = super.getView();
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                View childView = viewGroup.getChildAt(i);
                if (childView instanceof WebView) {
                    initWebView((WebView) childView);
                    break;
                }
            }
        }
        return view;
    }

    @Override
    public void setOnErrorListener(OnErrorListener listener) {
        super.setOnErrorListener(listener);
        mOnErrorListener = listener;
    }

    @Override
    public void setOnPageListener(OnPageListener listener) {
        super.setOnPageListener(listener);
        mOnPageListener = listener;
    }

    private final String TAG = getClass().getSimpleName();


    /**
     * 取得本应用的版本名getVersionName
     *
     * @param ctx
     * @return
     */
    public  String getVersionName(Context ctx) {
        String versionName = "";
        PackageManager manager = ctx.getPackageManager();
        PackageInfo info;
        try {
            info = manager.getPackageInfo(ctx.getPackageName(), 0);
            versionName = info.versionName;
            Log.d(TAG, "versionCode =============" + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getVersionName exception:" + e.getMessage());
        }
        return versionName;
    }


    /**
     * 自定义样式的再处理一下wx的web容器
     */

    private void initWebView(WebView webview) {
        this.web = webview;
        String ua = webview.getSettings().getUserAgentString();
        webview.getSettings().setUserAgentString(ua + " meiju/" + getVersionName(webview.getContext()));
        webview.getSettings().setAllowFileAccess(true);
        webview.getSettings().setAllowContentAccess(true);
        webview.getSettings().setBlockNetworkImage(false);

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.setWebViewClient(new WebViewClient() {

// TODO: 2018/11/2 如果强制复写这个，会有 iosbridge 的报错，因为也没有拦截iosbridge 的内容
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
//                WXLogUtils.v("tag", "onPageOverride " + url);
//                return true;
//            }


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    url = request.getUrl().toString();
                } else {
                    url = request.toString();
                }
                if (interceptCallBack != null) {
                    boolean isIntercept = interceptCallBack.shouldInterceptRequest(view, url);
                    if (isIntercept) {
                        return true;
                    }
                }
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                return super.shouldInterceptRequest(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                WXLogUtils.v("tag", "onPageStarted " + url);
                if (mOnPageListener != null) {
                    mOnPageListener.onPageStart(url);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                WXLogUtils.v("tag", "onPageFinished " + url);
                if (mOnPageListener != null) {
                    mOnPageListener.onPageFinish(url, view.canGoBack(), view.canGoForward());
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (mOnErrorListener != null) {
                    //mOnErrorListener.onError("error", "page error code:" + error.getErrorCode() + ", desc:" + error.getDescription() + ", url:" + request.getUrl());
                    mOnErrorListener.onError("error", "page error");
                }
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                if (mOnErrorListener != null) {
                    mOnErrorListener.onError("error", "http error");
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                if (mOnErrorListener != null) {
                    mOnErrorListener.onError("error", "ssl error");
                }
            }

        });
    }

    public interface InterceptCallBack {
        boolean shouldInterceptRequest(WebView view, String url);
    }
}
