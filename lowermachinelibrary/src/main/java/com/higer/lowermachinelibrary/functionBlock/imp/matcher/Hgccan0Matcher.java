package com.higer.lowermachinelibrary.functionBlock.imp.matcher;

import com.higer.lowermachinelibrary.entity.Frame;
import com.higer.lowermachinelibrary.functionBlock.IMatcher;
import com.higer.lowermachinelibrary.functionBlock.IParser;

import com.higer.lowermachinelibrary.log.Logger;
import com.higer.lowermachinelibrary.statics.Config;
import com.higer.lowermachinelibrary.utils.StringHexUtil;
import com.higer.lowermachinelibrary.virtualIo.VirtualCan0Io;


final public class Hgccan0Matcher  implements IMatcher {


    public Hgccan0Matcher() {
    }
    @Override
    public void parseBuffer(byte[] buffer, int iCount) {//传递进来的就是一条完整的指令
        if(iCount<=0)
        {
            return;
        }

     // Logger.writeLog("LEVEL2   Hgccan0Matcher::parseBuffer  "+StringHexUtil.ArraytoAsciiString(buffer, 0, 8)+ArraytoHexString(buffer, 8, iCount-8));

        if (Config.logLevel<3)//2级
        {
            Logger.writeLog("LEVEL2   Hgccan0Matcher::parseBuffer  "+StringHexUtil.ArraytoAsciiString(buffer, 0, 8)+StringHexUtil.ArraytoHexString(buffer, 8, iCount-8));
        }




        int cmdLen=((buffer[8]&0xff)<<8)+(buffer[9]&0xff);
        if(cmdLen%13==0)
        {
            int iFrameCount=cmdLen/13;
            byte[] data=new byte[13];
            for (int i = 0; i < iFrameCount; i++) {

                System.arraycopy(buffer,10+i*13,data,0,13);

                Frame frame=new Frame();
                if (frame.parse(data))
                {
                    System.out.println("LEVEL2 --- "+frame.toString());
                    VirtualCan0Io.getInstance().output(frame);
                }else {
                    Logger.writeLog("CAN0 Hgccan0Matcher::parseBuffer  帧数据解析失败");
                }
            }
        }else {
            Logger.writeLog("CAN0 Hgccan0Matcher::parseBuffer  接收到的 数据长度不是10的倍数  len="+cmdLen+"   cmd="+StringHexUtil.ArraytoAsciiString(buffer, 0, 8)+StringHexUtil.ArraytoHexString(buffer, 8, iCount-8));
        }
    }



}
