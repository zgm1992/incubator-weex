package com.meiju.weex.componentView.apngView.frame.loader;

import com.meiju.weex.componentView.apngView.frame.io.Reader;
import com.meiju.weex.componentView.apngView.frame.io.StreamReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * @CreateDate: 2019/3/28
 */
public abstract class StreamLoader implements Loader {
    protected abstract InputStream getInputStream() throws IOException;


    public final synchronized Reader obtain() throws IOException {
        return new StreamReader(getInputStream());
    }
}
