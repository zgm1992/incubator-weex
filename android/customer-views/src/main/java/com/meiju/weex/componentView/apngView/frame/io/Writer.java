package com.meiju.weex.componentView.apngView.frame.io;

import java.io.IOException;

/**
 * @Description: APNG4Android
 * @CreateDate: 2019-05-12
 */
public interface Writer {
    void reset(int size);

    void putByte(byte b);

    void putBytes(byte[] b);

    int position();

    void skip(int length);

    byte[] toByteArray();

    void close() throws IOException;
}
