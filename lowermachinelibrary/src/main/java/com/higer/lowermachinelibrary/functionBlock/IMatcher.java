package com.higer.lowermachinelibrary.functionBlock;

//匹配器接口
public interface IMatcher {
    void parseBuffer(byte buffer[],int iCount);//解析成 一条指令
//    void setParser(IParser parser);//设置 解析器
//    IParser getParser();
}
