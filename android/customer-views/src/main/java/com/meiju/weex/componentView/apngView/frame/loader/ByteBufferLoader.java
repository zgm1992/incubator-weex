package com.meiju.weex.componentView.apngView.frame.loader;

import com.meiju.weex.componentView.apngView.frame.io.ByteBufferReader;
import com.meiju.weex.componentView.apngView.frame.io.Reader;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @Description: ByteBufferLoader
 * @CreateDate: 2019-05-15
 */
public abstract class ByteBufferLoader implements Loader {
    public abstract ByteBuffer getByteBuffer();

    @Override
    public Reader obtain() throws IOException {
        return new ByteBufferReader(getByteBuffer());
    }
}
