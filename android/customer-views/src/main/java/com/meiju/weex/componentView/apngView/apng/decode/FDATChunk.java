package com.meiju.weex.componentView.apngView.apng.decode;

import com.meiju.weex.componentView.apngView.apng.io.APNGReader;

import java.io.IOException;

/**
 * @CreateDate: 2019/7/18
 */
class FDATChunk extends Chunk {
    static final int ID = Chunk.fourCCToInt("fdAT");
    int sequence_number;

    @Override
    void innerParse(APNGReader reader) throws IOException {
        sequence_number = reader.readInt();
    }
}
