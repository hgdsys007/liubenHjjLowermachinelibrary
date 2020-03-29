package com.higer.lowermachinelibrary.functionBlock.imp.matcher;

import com.higer.lowermachinelibrary.functionBlock.IMatcher;
import com.higer.lowermachinelibrary.statics.Config;
import com.higer.lowermachinelibrary.threads.radar.shortr.ShortRadarParser;

final public class Hgccom3Matcher  implements IMatcher {//侧面雷达  ID  x-y
    private final int HEADER_LEN=10;//通道协议头长度 HGCCOM3:+两个字节长度  总共10个字节
    private ShortRadarParser shortRadarParser=null;

    public Hgccom3Matcher() {
        shortRadarParser=new ShortRadarParser();
        shortRadarParser.setIdRage(Config.com3MinId,Config.com3MaxId);
        shortRadarParser.startParser();
    }
    @Override
    public void parseBuffer(byte[] buffer, int iCount) {
        shortRadarParser.parseShortLeida(buffer,HEADER_LEN,iCount);
    }

}
