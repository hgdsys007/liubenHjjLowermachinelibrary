package com.higer.lowermachinelibrary.functionBlock;

import java.io.InputStream;

// 信号 读取器接口
public interface IReader {
    void setInputStream(InputStream inputStream);
    int  read() throws Exception;//读取数据
    void setMatcher(IMatcher matcher);//设置匹配器
    IMatcher getMatcher();
}
