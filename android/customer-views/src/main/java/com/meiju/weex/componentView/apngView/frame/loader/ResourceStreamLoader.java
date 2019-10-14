package com.meiju.weex.componentView.apngView.frame.loader;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Description: 从资源加载流
 * @CreateDate: 2019/3/28
 */
public class ResourceStreamLoader extends StreamLoader {
    private final Context mContext;
    private final int mResId;


    public ResourceStreamLoader(Context context, int resId) {
        mContext = context.getApplicationContext();
        mResId = resId;
    }

    @Override
    protected InputStream getInputStream() throws IOException {
        return mContext.getResources().openRawResource(mResId);
    }
}
