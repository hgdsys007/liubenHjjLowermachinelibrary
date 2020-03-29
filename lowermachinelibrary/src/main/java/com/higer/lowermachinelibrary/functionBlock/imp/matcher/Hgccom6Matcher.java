package com.higer.lowermachinelibrary.functionBlock.imp.matcher;

import com.higer.lowermachinelibrary.functionBlock.IMatcher;
import com.higer.lowermachinelibrary.log.Logger;
import com.higer.lowermachinelibrary.statics.Config;
import com.higer.lowermachinelibrary.utils.StringHexUtil;
import com.higer.lowermachinelibrary.virtualIo.VirtualCom5Io;
//360摄像头
public final class Hgccom6Matcher implements IMatcher {
    private final int HEADER_LEN=10;//通道协议头长度 HGCCOM5:+两个字节长度  总共10个字节

    public Hgccom6Matcher() {

    }
    @Override
    public void parseBuffer(byte[] buffer, int iCount) {
        parseSfz(buffer,iCount);//规定的 COM5 外接 身份证读卡器,如更换设备，可增加一个解析 方法  修改这里
    }



    //规定的 COM5 外接 360摄像头
    //传递过来的数据就是 一条完整的数据
    private void parseSfz(byte[] buffer,int iLen)
    {
        if(iLen<=0)
        {
            return;
        }


        if (Config.logLevel<3)//2级
        {
            Logger.writeLog("LEVEL2   Hgccom5Matcher::parseBuffer  "+ StringHexUtil.ArraytoAsciiString(buffer,0,8)+StringHexUtil.ArraytoHexString(buffer, 8, iLen-8));
        }

        byte[] data=new byte[iLen-HEADER_LEN];
        System.arraycopy(buffer,HEADER_LEN,data,0,iLen-HEADER_LEN);
        // Logger.writeLog(ArraytoHexString(data));
       // VirtualCom6Io.getInstance().output(data);//直接输送到  虚拟IO口中去
    }


}
