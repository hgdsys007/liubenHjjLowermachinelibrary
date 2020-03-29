package com.higer.lowermachinelibrary.functionBlock.imp.matcher;

import com.higer.lowermachinelibrary.functionBlock.IMatcher;
import com.higer.lowermachinelibrary.functionBlock.IParser;
import com.higer.lowermachinelibrary.log.Logger;
import com.higer.lowermachinelibrary.virtualIo.VirtualMcuUpIo;


final public class HGCUpgradeMatcher implements IMatcher {
    private final int HEADER_LEN = 13;//通道协议头长度 HGCUpgrade:+两个字节长度  总共13个字节

    @Override
    public void parseBuffer(byte[] buffer, int iCount) {
        if (iCount <= 0) {
            return;
        }
//        if (Config.logLevel < 3)//2级
//        {
//        }

        if (buffer[0] == '$') {
            byte[] data = new byte[iCount];
            System.arraycopy(buffer, 0, data, 0, iCount);
            VirtualMcuUpIo.getInstance().out(data);
        } else if (iCount == 16) {
            int iRes = ((buffer[13] & 0xff) << 16) + ((buffer[14] & 0xff) << 8) + (buffer[15] & 0xff);
            VirtualMcuUpIo.getInstance().out(String.valueOf(iRes).getBytes());
        }else {
            Logger.writeLog("HGCUpgradeMatcher:parseBuffer  数据解析失败");
        }
    }


}
