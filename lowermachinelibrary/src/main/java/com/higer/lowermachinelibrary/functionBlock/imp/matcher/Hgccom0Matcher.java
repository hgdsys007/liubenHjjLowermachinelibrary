package com.higer.lowermachinelibrary.functionBlock.imp.matcher;

import com.higer.lowermachinelibrary.functionBlock.IMatcher;
import com.higer.lowermachinelibrary.functionBlock.IParser;
import com.higer.lowermachinelibrary.functionBlock.imp.parser.GpggaPaser;
import com.higer.lowermachinelibrary.functionBlock.imp.parser.GphpdPaser;
import com.higer.lowermachinelibrary.functionBlock.imp.parser.GpioPaser;
import com.higer.lowermachinelibrary.functionBlock.imp.parser.Heading3aPaser;
import com.higer.lowermachinelibrary.functionBlock.imp.parser.PtnlPaser;

import com.higer.lowermachinelibrary.log.Logger;
import com.higer.lowermachinelibrary.statics.Config;
import com.higer.lowermachinelibrary.statics.HeadGpsContext;
import com.higer.lowermachinelibrary.utils.StringHexUtil;

final public class Hgccom0Matcher  implements IMatcher {//GPS1
    private final int HEADER_LEN=10;//通道协议头长度 HGCCOM0:+两个字节长度  总共10个字节


    private GpsBanTeamDataMatcher gpsBanMatcher=null;

    public Hgccom0Matcher() {
        gpsBanMatcher=new GpsBanTeamDataMatcher();
        gpsBanMatcher.setGpsContext(HeadGpsContext.getInstance(),true);
    }
    @Override
    public void parseBuffer(byte[] buffer, int iCount) {
        if (Config.logLevel<3)//2级
        {
          Logger.writeLog("LEVEL2   Hgccom0Matcher::parseBuffer  "+StringHexUtil.ArraytoAsciiString(buffer, 0, iCount));
        }

        if(iCount>HEADER_LEN)
        {
            gpsBanMatcher.parse(buffer, HEADER_LEN,(iCount-HEADER_LEN));
        }
    }





//    @Override
//    public void setParser(IParser parser) {
//
//    }
//
//    @Override
//    public IParser getParser() {
//        return null;
//    }
}
