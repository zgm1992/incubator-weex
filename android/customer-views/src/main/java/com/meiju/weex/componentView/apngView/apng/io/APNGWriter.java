package com.meiju.weex.componentView.apngView.apng.io;

import com.meiju.weex.componentView.apngView.frame.io.ByteBufferWriter;

import java.nio.ByteOrder;

/**
 * @Description: APNGWriter
 * @CreateDate: 2019/7/18
 */
public class APNGWriter extends ByteBufferWriter {
    public APNGWriter() {
        super();
    }

    public void writeFourCC(int val) {
        putByte((byte) (val & 0xff));
        putByte((byte) ((val >> 8) & 0xff));
        putByte((byte) ((val >> 16) & 0xff));
        putByte((byte) ((val >> 24) & 0xff));
    }

    public void writeInt(int val) {
        putByte((byte) ((val >> 24) & 0xff));
        putByte((byte) ((val >> 16) & 0xff));
        putByte((byte) ((val >> 8) & 0xff));
        putByte((byte) (val & 0xff));
    }

    @Override
    public void reset(int size) {
        super.reset(size);
        this.byteBuffer.order(ByteOrder.BIG_ENDIAN);
    }
}
