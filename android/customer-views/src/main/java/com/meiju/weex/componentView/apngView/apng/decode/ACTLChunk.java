package com.meiju.weex.componentView.apngView.apng.decode;

import com.meiju.weex.componentView.apngView.apng.io.APNGReader;

import java.io.IOException;

/**
 * @Description: https://developer.mozilla.org/en-US/docs/Mozilla/Tech/APNG#.27acTL.27:_The_Animation_Control_Chunk
 * @CreateDate: 2019/7/18
 */
class ACTLChunk extends Chunk {
    static final int ID = Chunk.fourCCToInt("acTL");
    int num_frames;
    int num_plays;

    @Override
    void innerParse(APNGReader apngReader) throws IOException {
        num_frames = apngReader.readInt();
        num_plays = apngReader.readInt();
    }
}
