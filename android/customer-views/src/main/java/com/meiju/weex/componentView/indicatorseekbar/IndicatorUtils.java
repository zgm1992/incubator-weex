package com.meiju.weex.componentView.indicatorseekbar;

import android.content.Context;
import android.util.TypedValue;

/**
 * created by ZhuangGuangquan on  2017/9/9
 */

public class IndicatorUtils
{
    public static int dp2px(Context context, float dpValue)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    public static int sp2px(Context context, float spValue)
    {
        return (int) (spValue * context.getResources().getDisplayMetrics().scaledDensity + 0.5f);
    }

    public static int px2sp(Context context, float pxValue)
    {
        return (int) (pxValue / context.getResources().getDisplayMetrics().scaledDensity + 0.5f);
    }

}
