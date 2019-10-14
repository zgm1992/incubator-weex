package com.meiju.weex.componentView.ProgressCycleView;

import android.content.Context;
import android.util.TypedValue;

public class ProgressCycleUtils {
    static int dp2px(Context context, float dpValue)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    static int sp2px(Context context, float spValue)
    {
        return (int) (spValue * context.getResources().getDisplayMetrics().scaledDensity + 0.5f);
    }

    static int px2sp(Context context, float pxValue)
    {
        return (int) (pxValue / context.getResources().getDisplayMetrics().scaledDensity + 0.5f);
    }
}
