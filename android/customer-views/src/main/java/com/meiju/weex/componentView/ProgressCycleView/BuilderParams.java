package com.meiju.weex.componentView.ProgressCycleView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;


public class BuilderParams {
    Context mContext;
    //seekBar
    int mSeekBarType = 0;
    float mMax = 360;
    float mMin = 0;
    float mProgress = 0;


    int mBackgroundTrackSize;
    int mProgressTrackSize;
    int mCompletedColor = Color.parseColor("#267AFF"); //环形进度条完成后的部分的颜色
    float mCompletedColorAlpha = 1f;

    int mIncompletedColor = 0; //环形进度条未完成部分的颜色

    int mBackgroundRadius = 0;
    int mBackgroundColor  = Color.TRANSPARENT;


    boolean isTextBold = false;
    String text = null;
    int textSize = 13;
    int textColor = 0;

    float mIncompletedColorAlpha = 1f;

    boolean mClockwise = true; //环形自动执行进度的方向，默认是true，即顺时针方向，false为逆时针方向
    int startingSlice = 0; //环形进度开始的起始位置，0: 0点钟位置起点，25:3点钟位置起点 50:6点钟位置起点
    int cornerRadius = 0;
    //tick
    int mThickness; //环形的宽度
    Bitmap mPointBitmap = null;
    boolean pointShow = false;
    int  pointRadius;
    int pointColor = Color.parseColor("#FFFFFF"); ;

    boolean autoProgress = false;


    BuilderParams(Context context) {
        this.mContext = context;
        this.mThickness = ProgressCycleUtils.dp2px(mContext, 4);
    }
}
