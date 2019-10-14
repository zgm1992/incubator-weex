package com.meiju.weex.componentView.apngView.apng;

import android.content.Context;

import com.meiju.weex.componentView.apngView.apng.decode.APNGDecoder;
import com.meiju.weex.componentView.apngView.frame.FrameAnimationDrawable;
import com.meiju.weex.componentView.apngView.frame.decode.FrameSeqDecoder;
import com.meiju.weex.componentView.apngView.frame.loader.AssetStreamLoader;
import com.meiju.weex.componentView.apngView.frame.loader.FileLoader;
import com.meiju.weex.componentView.apngView.frame.loader.Loader;
import com.meiju.weex.componentView.apngView.frame.loader.ResourceStreamLoader;

/**
 * @Description: APNGDrawable
 * @CreateDate: 2019/7/18
 */
public class APNGDrawable extends FrameAnimationDrawable {
    public APNGDrawable(Loader provider) {
        super(provider);
    }

    @Override
    protected FrameSeqDecoder createFrameSeqDecoder(Loader streamLoader, FrameSeqDecoder.RenderListener listener) {
        return new APNGDecoder(streamLoader, listener);
    }


    public static APNGDrawable fromAsset(Context context, String assetPath) {
        AssetStreamLoader assetStreamLoader = new AssetStreamLoader(context, assetPath);
        return new APNGDrawable(assetStreamLoader);
    }

    public static APNGDrawable fromFile(String filePath) {
        FileLoader fileLoader = new FileLoader(filePath);
        return new APNGDrawable(fileLoader);
    }

    public static APNGDrawable fromResource(Context context, int resId) {
        ResourceStreamLoader resourceStreamLoader = new ResourceStreamLoader(context, resId);
        return new APNGDrawable(resourceStreamLoader);
    }
}
