package com.higer.lowermachinelibrary.functionBlock.imp.matcher;

import com.higer.lowermachinelibrary.functionBlock.IMatcher;
import com.higer.lowermachinelibrary.functionBlock.IParser;
import com.higer.lowermachinelibrary.log.Logger;
import com.higer.lowermachinelibrary.statics.Config;
import com.higer.lowermachinelibrary.utils.StringHexUtil;

final public class Hgccom4Matcher  implements IMatcher {// 第一款雷达，作为备用方案
    private final int HEADER_LEN=10;//通道协议头长度 HGCCOM4:+两个字节长度  总共10个字节




    public Hgccom4Matcher() {


    }
    @Override
    public void parseBuffer(byte[] buffer, int iCount) {

        if (Config.logLevel<3)//2级
        {
          Logger.writeLog("LEVEL2   Hgccom4Matcher::parseBuffer  "+StringHexUtil.ArraytoAsciiString(buffer, 0, iCount));
        }
        parseOutsideLeida(buffer,iCount);
    }

    private void parseOutsideLeida(byte[] buffer,int iLen)
    {
        if((iLen-HEADER_LEN)<=0)
        {
            return;
        }
    }



}
