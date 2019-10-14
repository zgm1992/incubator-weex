package com.meiju.weex.componentView.apngView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.meiju.weex.componentView.apngView.apng.APNGDrawable;
import com.meiju.weex.componentView.apngView.frame.loader.FileLoader;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.ui.action.BasicComponentData;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXComponentProp;
import com.taobao.weex.ui.component.WXVContainer;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MSmartWXApngVIew extends WXComponent<ImageView> {

    private WXSDKInstance instance;
    private APNGDrawable apngDrawable;
    private String src;
    private boolean isLoop = false;
    private boolean isAuto = false;

    public MSmartWXApngVIew(WXSDKInstance instance, WXVContainer parent, BasicComponentData basicComponentData) {
        super(instance, parent, basicComponentData);
    }

    public MSmartWXApngVIew(WXSDKInstance instance, WXVContainer parent, int type, BasicComponentData basicComponentData) {
        super(instance, parent, type, basicComponentData);
    }

    @Override
    protected ImageView initComponentHostView(@NonNull Context context) {
        ImageView iv = new ImageView(context);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        iv.setLayoutParams(lp);

        return iv;
    }

    @Override
    protected boolean setProperty(String key, Object param) {
        if (key != null && param != null) {
            switch (key) {
                case "src":
                    return setSrc(param.toString());
                case "loop":
                    return setLoop(Boolean.parseBoolean(param.toString()));
                case "auto":
                    return setAuto(Boolean.parseBoolean(param.toString()));
                default:
                    return false;
            }
        }
        return false;
    }

    @WXComponentProp(name = "src")
    public boolean setSrc(String src) {
        this.src = src;
        return true;
    }

    @WXComponentProp(name = "loop")
    public boolean setLoop(boolean isLoop) {
        this.isLoop = isLoop;
        return true;
    }

    @WXComponentProp(name = "src")
    public boolean setAuto(boolean isAuto) {
        this.isAuto = isAuto;
        return true;
    }

    @SuppressLint("CheckResult")
    @Override
    public void readyToRender() {
        super.readyToRender();
        if (!TextUtils.isEmpty(src)) {
            if (src.startsWith("http")) {
                //从网络拉取图片
                Observable.just(src)
                        .subscribeOn(Schedulers.io())
                        .map(s -> Glide.with(getContext()).load(s).downloadOnly(1, 1).get())
                        .map(f -> new FileLoader(f.getAbsolutePath()))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::loadDrawable, throwable -> Toast.makeText(getContext(), "获取图片资源失败", Toast.LENGTH_SHORT).show());
            } else {
                //从本地获取图片
                if (!TextUtils.isEmpty(getInstance().getBundleUrl())) {
                    String localUrl;
                    StringBuilder localUrlBuilder = new StringBuilder();

                    String[] urlSplit = getInstance().getBundleUrl().split("/");
                    if (src.startsWith("./")) {
                        for (int i = 0; i < urlSplit.length - 1; i++) {
                            localUrlBuilder.append(urlSplit[i]).append("/");
                        }
                        localUrlBuilder.append(src);
                        localUrl = localUrlBuilder.toString().replace("./", "");
                    } else if (src.startsWith("../")) {
                        for (int i = 0; i < urlSplit.length - 2; i++) {
                            localUrlBuilder.append(urlSplit[i]).append("/");
                        }
                        localUrlBuilder.append(src);
                        localUrl = localUrlBuilder.toString().replace("../", "");
                    } else {
                        localUrlBuilder.append(getInstance().getBundleUrl()).append(src);
                        localUrl = localUrlBuilder.toString();
                    }
                    FileLoader loader = new FileLoader(localUrl);
                    loadDrawable(loader);
                }
            }
        }
    }

    private void loadDrawable(FileLoader loader) {
        apngDrawable = new APNGDrawable(loader);
        apngDrawable.setLoopLimit(isLoop ? 0 : 1);
        if (isAuto) {
            getHostView().setImageDrawable(apngDrawable);
        }
    }

    private boolean isStop = false;

    @JSMethod
    public void play(String param, JSCallback callback, JSCallback callbackFail) {
        if (apngDrawable != null) {
            if (apngDrawable.isRunning() && (apngDrawable.isPaused() || isStop)) {
                //重新启动
                apngDrawable.resume();
                apngDrawable.reset();
                isStop = false;
            } else {
                //手动启动
                getHostView().setImageDrawable(apngDrawable);
            }
            callbackToWeex(callback, 0);
            return;
        }
        callbackToWeex(callbackFail, "{\"errorMsg\": \"图片加载失败\"}");
    }

    @JSMethod
    public void stop(String param, JSCallback callback, JSCallback callbackFail) {
        if (apngDrawable != null) {
            if (apngDrawable.isRunning()) {
                isStop = true;
                apngDrawable.pause();
            }
            callbackToWeex(callback, 0);
            return;
        }
        callbackToWeex(callbackFail, "{\"errorMsg\": \"图片加载失败\"}");
    }

    @JSMethod
    public void resume(String param, JSCallback callback, JSCallback callbackFail) {
        if (apngDrawable != null) {
            if (apngDrawable.isRunning() && apngDrawable.isPaused() && !isStop) {
                apngDrawable.resume();
            }
            callbackToWeex(callback, 0);
            return;
        }
        callbackToWeex(callbackFail, "{\"errorMsg\": \"图片加载失败\"}");
    }

    @JSMethod
    public void pause(String param, JSCallback callback, JSCallback callbackFail) {
        if (apngDrawable != null) {
            if (apngDrawable.isRunning() && !apngDrawable.isPaused()) {
                apngDrawable.pause();
            }
            callbackToWeex(callback, 0);
            return;
        }
        callbackToWeex(callbackFail, "{\"errorMsg\": \"图片加载失败\"}");
    }

    @JSMethod
    public void goTo(String param, JSCallback callback, JSCallback callbackFail) {
        FrameCmd cmd = new Gson().fromJson(param, FrameCmd.class);
        if (cmd == null || cmd.getFrame() < 0) {
            callbackToWeex(callbackFail, "{\"errorMsg\": \"参数有误\"}");
        } else {
            if (apngDrawable != null) {
                if (TextUtils.isEmpty(cmd.getNext())) {
                    cmd.setNext("pause");
                }
                apngDrawable.getFrame(cmd.getFrame(), cmd.getNext());
                callbackToWeex(callback, 0);
                return;
            }
            callbackToWeex(callbackFail, "{\"errorMsg\": \"图片加载失败\"}");
        }
    }

    @JSMethod
    public void run(String param, JSCallback callback, JSCallback callbackFail) {
        FrameCmd cmd = new Gson().fromJson(param, FrameCmd.class);
        if (cmd == null || cmd.getFrame() < 0) {
            callbackToWeex(callbackFail, "{\"errorMsg\": \"参数有误\"}");
        } else {
            if (apngDrawable != null) {
                apngDrawable.runFrame(cmd.getFrame());
                callbackToWeex(callback, 0);
                return;
            }
            callbackToWeex(callbackFail, "{\"errorMsg\": \"图片加载失败\"}");
        }
    }

    private void callbackToWeex(JSCallback callback, Object param) {
        if (callback != null) callback.invoke(param);
    }

    class FrameCmd {
        private int frame;
        private String next;

        public int getFrame() {
            return frame;
        }

        public void setFrame(int frame) {
            this.frame = frame;
        }

        public String getNext() {
            return next;
        }

        public void setNext(String next) {
            this.next = next;
        }
    }
}
