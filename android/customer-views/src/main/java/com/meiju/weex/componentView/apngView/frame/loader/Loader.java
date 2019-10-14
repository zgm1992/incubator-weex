package com.meiju.weex.componentView.apngView.frame.loader;

import com.meiju.weex.componentView.apngView.frame.io.Reader;

import java.io.IOException;

/**
 * @Description: Loader
 * @CreateDate: 2019-05-14
 */
public interface Loader {
    Reader obtain() throws IOException;
}
