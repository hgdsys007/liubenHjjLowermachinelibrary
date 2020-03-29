package com.higer.lowermachinelibrary.functionBlock.imp.matcher;

import com.higer.lowermachinelibrary.functionBlock.IMatcher;
import com.higer.lowermachinelibrary.statics.Config;
import com.higer.lowermachinelibrary.threads.radar.shortr.ShortRadarParser;


public final class Hgccom2Matcher implements IMatcher {
    private final int HEADER_LEN=10;//通道协议头长度 HGCCOM4:+两个字节长度  总共10个字节
    private ShortRadarParser shortRadarParser=null;
    public Hgccom2Matcher() {
        shortRadarParser=new ShortRadarParser();
        shortRadarParser.setIdRage(Config.com2MinId,Config.com2MaxId);
        shortRadarParser.startParser();
    }


    @Override
    public void parseBuffer(byte[] buffer, int iCount) {

//        if (Config.logLevel<3)//2级
//        {
//            Logger.writeLog("LEVEL2   Hgccom4Matcher::parseBuffer  "+ StringHexUtil.ArraytoAsciiString(buffer, 0, iCount));
//        }
//        byte[] data=new byte[iCount-HEADER_LEN];
//        System.arraycopy(buffer,HEADER_LEN,data,0,iCount-HEADER_LEN);
//        // Logger.writeLog(ArraytoHexString(data));
//        VirtualCom2Io.getInstance().output(data);


        shortRadarParser.parseShortLeida(buffer,HEADER_LEN,iCount);
    }
}
