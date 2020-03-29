package com.higer.lowermachinelibrary.functionBlock.imp.matcher;

import com.higer.lowermachinelibrary.functionBlock.IMatcher;
import com.higer.lowermachinelibrary.functionBlock.IParser;
import com.higer.lowermachinelibrary.functionBlock.imp.parser.GpggaPaser;
import com.higer.lowermachinelibrary.functionBlock.imp.parser.GphpdPaser;
import com.higer.lowermachinelibrary.functionBlock.imp.parser.Heading3aPaser;
import com.higer.lowermachinelibrary.functionBlock.imp.parser.PtnlPaser;
import com.higer.lowermachinelibrary.log.Logger;
import com.higer.lowermachinelibrary.statics.BackGpsContext;
import com.higer.lowermachinelibrary.statics.Config;
import com.higer.lowermachinelibrary.utils.StringHexUtil;

final public class Hgccom1Matcher implements IMatcher {//GPS2
    private final int HEADER_LEN=10;//通道协议头长度 HGCCOM1:+两个字节长度  总共10个字节
    private GpsBanTeamDataMatcher gpsBanMatcher=null;


    public Hgccom1Matcher() {
        gpsBanMatcher=new GpsBanTeamDataMatcher();
        gpsBanMatcher.setGpsContext(BackGpsContext.getInstance(),false);
    }
    @Override
    public void parseBuffer(byte[] buffer, int iCount) {
        if (Config.logLevel<3)//2级
        {
            Logger.writeLog("LEVEL2   Hgccom1Matcher::parseBuffer  "+ StringHexUtil.ArraytoAsciiString(buffer, 0, iCount));
        }

        if(iCount>HEADER_LEN)
        {
            gpsBanMatcher.parse(buffer, HEADER_LEN,(iCount-HEADER_LEN));
        }
    }


    //规定的 COM1 外接 GPS2,如更换设备，可增加一个解析 方法
  /*  private void parseGps2(byte[] buffer,int iLen)
    {
        iLen-=HEADER_LEN;
        if(iLen<=0)
        {
            return;
        }


        if((bufferPos+iLen)>Config.BufferLen)
        {
            bufferPos=0;
            cmdPos=0;
        }
        System.arraycopy(buffer,HEADER_LEN,bufferArray,bufferPos,iLen);

        bufferPos+=iLen;

        int iHaveDone=0;
        for(int i=0;i<bufferPos;i++)
        {
            if(!isHead)
            {
                if (((bufferArray[i] & 0xff) == 35)||((bufferArray[i] & 0xff) == 36))//协议头  #   35   $ 36
                {
                    cmdPos = 0;
                    cmd[cmdPos++] = bufferArray[i];
                    isHead = true;
                    continue;
                }
            }

            if(isHead) {
                cmd[cmdPos++] = bufferArray[i];
                if ((cmd[cmdPos - 1] & 0xff) == 0X0D) // 0X0D 找到协议尾
                {
                    String sCmd=StringHexUtil.ArraytoAsciiString(cmd, 0, cmdPos);
                    iHaveDone = i + 1;
                    isHead = false;



                    if (sCmd.contains("GPGGA")||sCmd.contains("$GNGGA"))
                    {
                        if (gpggaParser!=null)
                        {
                            gpggaParser.parse(cmd,cmdPos);
                        }else {
                            gpggaParser=new GpggaPaser();
                            gpggaParser.parse(cmd,cmdPos);
                        }
                    }else if(sCmd.contains("#HEADING3A")){
                        if (heading3aParser!=null)
                        {
                            heading3aParser.parse(cmd,cmdPos);
                        }else {
                            heading3aParser=new Heading3aPaser();
                            heading3aParser.parse(cmd,cmdPos);
                        }
                    }else if(sCmd.contains("GPHPD")){
                        if (gphpdParser!=null)
                        {
                            gphpdParser.parse(cmd,cmdPos);
                        }else {
                            gphpdParser=new GphpdPaser();
                            gphpdParser.parse(cmd,cmdPos);
                        }
                    }else if(sCmd.contains("PTNL")){
                        if (ptnlParser!=null)
                        {
                            ptnlParser.parse(cmd,cmdPos);
                        }else {
                            ptnlParser=new PtnlPaser();
                            ptnlParser.parse(cmd,cmdPos);
                        }
                    }
                    cmdPos = 0;
                    continue;
                }
            }
        }


        isHead=false;
        cmdPos=0;
        if(iHaveDone>0)
        {
            if(iHaveDone==bufferPos)
            {
                bufferPos=0;
            }else {
                int tempArraySize=bufferPos-iHaveDone;
                byte[] tempArray=new byte[tempArraySize];
                int g=0;
                for(int m=iHaveDone;m<bufferPos;m++)
                {
                    tempArray[g++]=bufferArray[m];
                }
                System.arraycopy(tempArray,0,bufferArray,0,tempArraySize);
                bufferPos=tempArraySize;
            }
        }
    }*/

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
