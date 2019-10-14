package com.meiju.weex.componentView.apngView.frame.decode;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.meiju.weex.componentView.apngView.frame.io.Reader;
import com.meiju.weex.componentView.apngView.frame.io.Writer;


/**
 * @Description: One frame in an animation
 * @CreateDate: 2019/7/18
 */
public abstract class Frame<R extends Reader, W extends Writer> {
    protected final R reader;
    public int frameWidth;
    public int frameHeight;
    public int frameX;
    public int frameY;
    public int frameDuration;

    public Frame(R reader) {
        this.reader = reader;
    }

    public abstract Bitmap draw(Canvas canvas, Paint paint, int sampleSize, Bitmap reusedBitmap, W writer);
}
