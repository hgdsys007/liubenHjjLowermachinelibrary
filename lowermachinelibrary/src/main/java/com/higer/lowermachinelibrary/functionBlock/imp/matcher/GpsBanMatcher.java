package com.higer.lowermachinelibrary.functionBlock.imp.matcher;

import android.util.Log;

import com.higer.lowermachinelibrary.functionBlock.IGpsContext;
import com.higer.lowermachinelibrary.functionBlock.IMatcher;
import com.higer.lowermachinelibrary.functionBlock.IParser;
import com.higer.lowermachinelibrary.functionBlock.imp.parser.GpggaPaser;
import com.higer.lowermachinelibrary.functionBlock.imp.parser.GphpdPaser;
import com.higer.lowermachinelibrary.functionBlock.imp.parser.Heading3aPaser;
import com.higer.lowermachinelibrary.functionBlock.imp.parser.PtnlPaser;
import com.higer.lowermachinelibrary.log.Logger;
import com.higer.lowermachinelibrary.statics.Config;
import com.higer.lowermachinelibrary.utils.StringHexUtil;

final public class GpsBanMatcher  implements IMatcher {

    private byte[] cmd = null;
    private int cmdPos = 0;
    private int iEnd=0;
    private boolean isHead = false;
    private IParser gpggaParser = null;
    private IParser heading3aParser=null;
    private IParser gphpdParser=null;
    private IParser ptnlParser=null;
    IGpsContext gpsContext=null;
    public void setGpsContext(IGpsContext gpsCon)
    {
        gpsContext=gpsCon;
    }
    public GpsBanMatcher() {
        isHead = false;
        cmdPos = 0;
        this.cmd = new byte[Config.UartCmdLen];
    }
    @Override
    public void parseBuffer(byte[] buffer, int iCount) {
        parse(buffer,0,iCount);
    }




    public void parse(byte[] buffer,int iStart, int iCount) {
        try{
            if ((cmdPos + iCount) > Config.UartCmdLen) {
                Logger.writeLog("LEVEL1  Recv1111  ASCII  GpsBanMatcher:parseBuffer  iCount=" + iCount + "   cmdPos="+cmdPos+"   Config.UartCmdLen="+Config.UartCmdLen+"  " + StringHexUtil.ArraytoAsciiString(buffer, iStart, iCount));
                Logger.writeLog("LEVEL1  Recv  二进制  GpsBanMatcher:parseBuffer  iCount=" + iCount + "   cmdPos="+cmdPos+"   Config.UartCmdLen="+Config.UartCmdLen+"  "+ StringHexUtil.ArraytoHexString(buffer, iStart,iCount));
                cmdPos = 0;
                isHead = false;
            }
   // System.out.println("================="+StringHexUtil.ArraytoHexString(buffer, iStart,iCount));
  //  System.out.println("-----------"+StringHexUtil.ArraytoAsciiString(buffer,iStart,iCount));

            iEnd=iStart+iCount;
            for (int i = iStart; i < iEnd; i++) {
                cmd[cmdPos++] = buffer[i];
                if (isHead)//找到协议头
                {
                    if (cmd[cmdPos-1] == 0X0D) // 0X0D 找到协议尾
                    {
          System.out.println("-----------"+StringHexUtil.ArraytoAsciiString(cmd,0,cmdPos));
                        if ((cmd[1]==71)&&(cmd[3]==71)&&(cmd[4]=='G')&&(cmd[5]=='A'))// $GNGGA  $GPGGA
                        {
              //        System.out.println("-----------"+StringHexUtil.ArraytoAsciiString(cmd,0,cmdPos));
                            if (gpggaParser != null) {
                                gpggaParser.parse(cmd, cmdPos);
                            } else {
                                gpggaParser = new GpggaPaser();
                                if(gpsContext!=null)
                                {
                                    ((GpggaPaser) gpggaParser).setGpsContext(gpsContext);
                                }
                                gpggaParser.parse(cmd, cmdPos);
                            }
                        }else if ((cmd[1]==71)&&(cmd[3]==72))// $GPHPD
                        //else if ((cmd[0]==36)&&(cmd[1]==71)&&(cmd[2]==80)&&(cmd[3]==72)&&(cmd[4]==80)&&(cmd[5]==68))// $GPHPD
                        {
                            if (gphpdParser != null) {
                                gphpdParser.parse(cmd, cmdPos);
                            } else {
                                gphpdParser = new GphpdPaser();
                                if(gpsContext!=null)
                                {
                                    ((GphpdPaser) gphpdParser).setGpsContext(gpsContext);
                                }
                                gphpdParser.parse(cmd, cmdPos);
                            }
                        }
                        // else  if ((cmd[0]==35)&&(cmd[1]==72)&&(cmd[2]==69)&&(cmd[3]==65)&&(cmd[4]==68)&&(cmd[5]==73)&&(cmd[5]==78)&&(cmd[6]==71))// #HEADING3A
                        else  if ((cmd[1]==72)&&(cmd[3]==65))//#HEADING3A
                        {
                            if (heading3aParser != null) {
                                heading3aParser.parse(cmd, cmdPos);
                            } else {
                                heading3aParser = new Heading3aPaser();
                                if(gpsContext!=null)
                                {
                                    ((Heading3aPaser) heading3aParser).setGpsContext(gpsContext);
                                }
                                heading3aParser.parse(cmd, cmdPos);
                            }
                        }
                        else if ((cmd[1]==80)&&(cmd[3]==78))// $PTNL
                        //  else if ((cmd[0]==36)&&(cmd[1]==80)&&(cmd[2]==84)&&(cmd[3]==78)&&(cmd[4]==76))// $PTNL
                        {//$PTNL
                            if (ptnlParser != null) {
                                ptnlParser.parse(cmd, cmdPos);
                            } else {
                                ptnlParser = new PtnlPaser();
                                if(gpsContext!=null)
                                {
                                    ((PtnlPaser) ptnlParser).setGpsContext(gpsContext);
                                }
                                ptnlParser.parse(cmd, cmdPos);
                            }
                        }
                        isHead = false;
                        cmdPos = 0;
                        continue;
                    }
                } else {//没有找到协议头
                    if ((cmd[cmdPos - 1] == '#') || (cmd[cmdPos - 1] == '$'))//协议头  #   35   $ 36
                    {
                        cmdPos = 0;
                        cmd[cmdPos++] = buffer[i];
                        isHead = true;
                        continue;
                    }
                }
            }
        }catch (Exception e)
        {
            Logger.writeLog("GpsBanMatcher:parseBuffer  Exception ---start");
            Logger.writeLog(e.getMessage()+"   "+e.toString());
            Logger.writeLog("GpsBanMatcher:parseBuffer  cmdPos="+cmdPos+"  isHead="+isHead);
            Logger.writeLog("cmd--ASCII="+StringHexUtil.ArraytoAsciiString(cmd,0,cmdPos));
            Logger.writeLog("cmd--HEX="+StringHexUtil.ArraytoHexString(cmd,0,cmdPos));

            Logger.writeLog("buffer--ASCII="+StringHexUtil.ArraytoAsciiString(buffer,0,iCount));
            Logger.writeLog("buffer--HEX="+StringHexUtil.ArraytoHexString(buffer,0,iCount));
            Logger.writeLog("GpsBanMatcher:parseBuffer  Exception ---end");
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
