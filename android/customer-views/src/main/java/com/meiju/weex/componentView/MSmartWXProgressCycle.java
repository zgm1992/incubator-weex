package com.meiju.weex.componentView;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.meiju.weex.componentView.ProgressCycleView.ProgressCycleView;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.WXSDKManager;
import com.taobao.weex.annotation.Component;
import com.taobao.weex.ui.action.BasicComponentData;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXComponentProp;
import com.taobao.weex.ui.component.WXVContainer;
import com.taobao.weex.utils.WXUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


@Component(lazyload = false)
public class MSmartWXProgressCycle extends WXComponent<ProgressCycleView> {
    private final String TAG = getClass().getSimpleName();

    @Deprecated
    public MSmartWXProgressCycle(WXSDKInstance instance, WXVContainer parent, String instanceId, boolean isLazy, BasicComponentData basicComponentData) {
        this(instance,  parent, isLazy,basicComponentData);
    }

    public MSmartWXProgressCycle(WXSDKInstance instance, WXVContainer parent, boolean isLazy, BasicComponentData basicComponentData) {
        super(instance, parent, isLazy,basicComponentData);
    }



    @Override
    protected ProgressCycleView initComponentHostView(@NonNull Context context) {
        ProgressCycleView progressCycle = new ProgressCycleView(context);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        progressCycle.setLayoutParams(layoutParams);
        progressCycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MSmartWXProgressCycle.this.notify("progresscycleTap","click");
            }
        });
        return progressCycle;
    }


    @Override
    protected boolean setProperty(String key, Object param) {
        Log.i(TAG, " key is ->" + key + "  param is ->" + param);
        if ("data".equals(key)) {
            String jsonStr = WXUtils.getString(param, "");
            try {
                JSONObject jsonObject = new JSONObject(jsonStr);
                for (Method method : getClass().getMethods()) {
                    //通过注解判断要调用哪个方法，做特例处理
                    WXComponentProp wxComponentProp = method.getAnnotation(WXComponentProp.class);
                    if (wxComponentProp != null) {
                        if (jsonObject.has(wxComponentProp.name())) {
                            try {
//                                Log.e(TAG," key param is ->"+wxComponentProp.name()+" value param is ->"+wxComponentProp.name());
                                // method.invoke(MSmartWXProgressCycle.this, jsonObject.get(wxComponentProp.name()));
                                Class[] params = method.getParameterTypes();
                                if (params != null && params.length >= 1) {
                                    Object p = jsonObject.get(wxComponentProp.name());
                                    switch (params[0].getName()) {
                                        case "java.lang.String":
                                            if (!(p instanceof String)) {
                                                p = String.valueOf(p);
                                            }
                                            break;
                                        case "boolean":
                                            p = WXUtils.getBoolean(p, false);
                                            break;
                                        case "int":
                                            if (p instanceof String) {
                                                p = Double.valueOf((String) p).intValue();
                                            }else if(p instanceof Double ||p instanceof Float || p instanceof Short ||p instanceof Byte){
                                                p= Double.valueOf( p.toString()).intValue();
                                            }
                                            break;
                                    }
                                    method.invoke(MSmartWXProgressCycle.this, p);
                                }
//                                for (int j = 0; j < params.length; j++) {
//                                    paramTypes[j] = Class.forName(params[j].getName());
//                                }


                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return super.setProperty(key, param);
    }

    @WXComponentProp(name = "completedColor")
    public void setCompletedColor(String completedColor) {
        getHostView().setCompletedColor(completedColor);
    }

    @WXComponentProp(name = "incompletedColor")
    public void setInCompletedColor(String incompletedColor) {
        getHostView().setInCompletedColor(incompletedColor);
    }


    @WXComponentProp(name = "textSize")
    public void setTextSize(int textSize) {
        getHostView().setTextSize(textSize);
    }

    @WXComponentProp(name = "text")
    public void setText(String text) {
        getHostView().setText(text);
    }

    @WXComponentProp(name = "textColor")
    public void setTextColor(String color) {
        getHostView().setTextColor(color);
    }

//    @WXComponentProp(name = "isTextBold")
//    public void setText(boolean isTextBold) {
//        getHostView().setText(text);
//    }


    @WXComponentProp(name = "thickness")
    public void setThickness(int thickness) {
        getHostView().setThickness(thickness);
    }

    public void setThickness(String thickness) {
        try {
            int thick = Integer.valueOf(thickness);
            getHostView().setThickness(thick);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @WXComponentProp(name = "cornerRadius")
    public void setCornerRadius(int cornerRadius) {
        getHostView().setCornerRadius(cornerRadius);
    }

    public void setCornerRadius(String cornerRadius) {
//        int corner = Integer.valueOf(cornerRadius);
        try {
            int corner= Double.valueOf(cornerRadius).intValue();
            getHostView().setCornerRadius(corner);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }


    private void notify(String event, String newStatus) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("progresscycleTap", newStatus);
        params.put("timeStamp", System.currentTimeMillis());

        Map<String, Object> domChanges = new HashMap<>();
        Map<String, Object> attrsChanges = new HashMap<>();
        attrsChanges.put("progresscycleTap", newStatus);
        domChanges.put("attrs", attrsChanges);
        WXSDKManager.getInstance().fireEvent(getInstanceId(), getRef(), event, params, domChanges);
    }

    @WXComponentProp(name = "progressCounter")
    public void setProgressCounter(int progressCounter) {
        getHostView().setProgressCount(progressCounter);
    }

    public void setProgressCounter(String progressCounter) {
        try {
            int count = Double.valueOf(progressCounter).intValue();
            getHostView().setProgressCount(count);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @WXComponentProp(name = "totalCounter")
    public void setTotalCounter(int totalCounter) {
        getHostView().setTotalCounter(totalCounter);
    }

    @WXComponentProp(name = "backgroundColor")
    public void setBackgroundColor(String backgroundColor) {
        getHostView().setBackgroundColor(backgroundColor);
    }

    @WXComponentProp(name = "backgroundRadius")
    public void setBackgroundRadius(int backgroundRadius) {
        getHostView().setBackgroundRadius(backgroundRadius);
    }





    public void setTotalCounter(String totalCounter) {
        try {
            int count = Double.valueOf(totalCounter).intValue();
            getHostView().setTotalCounter(count);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }


    @WXComponentProp(name = "autoProgress")
    public void setAutoProgress(boolean autoProgress) {
        getHostView().setAutoProgress(autoProgress);
    }

    @WXComponentProp(name = "clockwise")
    public void setClockwise(boolean clockwise) {
        getHostView().setClockwise(clockwise);
    }

    @WXComponentProp(name = "startingSlice")
    public void setStartingSlice(int startingSlice) {
        getHostView().setStartingSlice(startingSlice);
    }


    public void setStartingSlice(String startingSlice) {
        try {
            int slice = Double.valueOf(startingSlice).intValue();
            getHostView().setStartingSlice(slice);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }


    @WXComponentProp(name = "pointShow")
    public void setPointShow(boolean show) {
        getHostView().setPointShow(show);
    }

    @WXComponentProp(name = "pointRadius")
    public void setpointRadius(int radius) {
        getHostView().setPointRadius(radius);
    }

    @WXComponentProp(name = "pointColor")
    public void setPointColor(String pointColor) {
        getHostView().setPointColor(pointColor);
    }


    @WXComponentProp(name = "pointImageBase64")
    public void setPointBase64(String base64) {
        getHostView().setPointBitmap(base64ToBitmap(base64));
    }

    private  Bitmap base64ToBitmap(String string) {
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



}

