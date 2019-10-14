package com.meiju.weex.componentView.apngView.frame.loader;

import com.meiju.weex.componentView.apngView.frame.io.FileReader;
import com.meiju.weex.componentView.apngView.frame.io.Reader;

import java.io.File;
import java.io.IOException;

/**
 * @Description: 从文件加载流
 * @CreateDate: 2019/3/28
 */
public class FileLoader implements Loader {

    private final File mFile;
    private Reader mReader;

    public FileLoader(String path) {
        mFile = new File(path);
    }

    @Override
    public synchronized Reader obtain() throws IOException {
        return new FileReader(mFile);
    }
}
