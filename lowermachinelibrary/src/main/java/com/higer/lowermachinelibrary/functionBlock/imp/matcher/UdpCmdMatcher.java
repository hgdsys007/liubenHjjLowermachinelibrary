package com.higer.lowermachinelibrary.functionBlock.imp.matcher;

import com.higer.lowermachinelibrary.functionBlock.IMatcher;
import com.higer.lowermachinelibrary.functionBlock.IParser;
import com.higer.lowermachinelibrary.functionBlock.imp.parser.GpioPaser;
import com.higer.lowermachinelibrary.log.Logger;
import com.higer.lowermachinelibrary.statics.Config;
import com.higer.lowermachinelibrary.utils.StringHexUtil;

public class UdpCmdMatcher implements IMatcher {
    private IParser gpioParser=null;
    private IMatcher hgccan0Matcher=null;
    private IMatcher hgccan1Matcher=null;
    private IMatcher hgccom0Matcher=null;
    private IMatcher hgccom1Matcher=null;

   private IMatcher hgccom2Matcher=null;


    private IMatcher hgccom3Matcher=null;
    private IMatcher hgccom4Matcher=null;
    private IMatcher hgccom5Matcher=null;
    private IMatcher hGCUpgradeMatcher=null;//下位机版本升级
    private IMatcher mcuCmdMatcher=null;
    @Override
    public void parseBuffer(byte[] buffer, int iCount) {
        //   Logger.writeLog("LEVEL2  TcpCmdMatcher::parseBuffer  "+StringHexUtil.ArraytoAsciiString(buffer,0,iCount));
        if(Config.logLevel<3)
        {
            Logger.writeLog("LEVEL2  TcpCmdMatcher::parseBuffer  "+ StringHexUtil.ArraytoAsciiString(buffer,0,iCount));
        }
        if(iCount>9)
        {
            if((buffer[0]=='I')&&(buffer[1]=='N')&&(buffer[2]=='D'))//IND
            {
                if (gpioParser!=null)//解析器 解析指令
                {
                    gpioParser.parse(buffer,iCount);
                }else {
                    gpioParser=new GpioPaser();
                    gpioParser.parse(buffer,iCount);
                }
            }
            else if((buffer[0]=='H')&&(buffer[4]=='A')&&(buffer[6]=='0'))//HGCCAN0
            {
                if (hgccan0Matcher!=null)
                {
                    hgccan0Matcher.parseBuffer(buffer,iCount);
                }else {
                    hgccan0Matcher=new Hgccan0Matcher();
                    hgccan0Matcher.parseBuffer(buffer,iCount);
                }
            }
            else if((buffer[0]=='H')&&(buffer[4]=='A')&&(buffer[6]=='1')) //HGCCAN1
            {
                if (hgccan1Matcher!=null)
                {
                    hgccan1Matcher.parseBuffer(buffer,iCount);
                }else {
                    hgccan1Matcher=new Hgccan1Matcher();
                    hgccan1Matcher.parseBuffer(buffer,iCount);
                }
            }
            else if((buffer[0]=='H')&&(buffer[4]=='O')&&(buffer[6]=='0')) //HGCCOM0     GPS1
            {
                if (hgccom0Matcher!=null)
                {
                    hgccom0Matcher.parseBuffer(buffer,iCount);
                }else {
                    hgccom0Matcher=new Hgccom0Matcher();
                    hgccom0Matcher.parseBuffer(buffer,iCount);
                }
            }
            else if((buffer[0]=='H')&&(buffer[4]=='O')&&(buffer[6]=='1')) //HGCCOM1   GPS2
            {
                if (hgccom1Matcher!=null)
                {
                    //  System.out.println("TcpCmdMatcher::parseBuffer  "+StringHexUtil.ArraytoAsciiString(buffer,0,iCount));
                    hgccom1Matcher.parseBuffer(buffer,iCount);
                }else {
                    hgccom1Matcher=new Hgccom1Matcher();
                    hgccom1Matcher.parseBuffer(buffer,iCount);
                }
            }
            else if((buffer[0]=='H')&&(buffer[4]=='O')&&(buffer[6]=='2')) //HGCCOM2   LED
            {
                if (hgccom2Matcher!=null)
                {
                    hgccom2Matcher.parseBuffer(buffer,iCount);
                }else {
                    hgccom2Matcher=new Hgccom2Matcher();
                    hgccom2Matcher.parseBuffer(buffer,iCount);
                }
            }
            else if((buffer[0]=='H')&&(buffer[4]=='O')&&(buffer[6]=='3')) //HGCCOM3   LED
            {
                if (hgccom3Matcher!=null)
                {
                    hgccom3Matcher.parseBuffer(buffer,iCount);
                }else {
                    hgccom3Matcher=new Hgccom3Matcher();
                    hgccom3Matcher.parseBuffer(buffer,iCount);
                }
            }
            else if((buffer[0]=='H')&&(buffer[4]=='O')&&(buffer[6]=='4')) //HGCCOM4    OBD
            {
                if (hgccom4Matcher!=null)
                {
                    hgccom4Matcher.parseBuffer(buffer,iCount);
                }else {
                    hgccom4Matcher=new Hgccom4Matcher();
                    hgccom4Matcher.parseBuffer(buffer,iCount);
                }
            }
            else if((buffer[0]=='H')&&(buffer[4]=='O')&&(buffer[6]=='5')) //HGCCOM5  串口5  身份证读卡器
            {
                if (hgccom5Matcher!=null)
                {
                    hgccom5Matcher.parseBuffer(buffer,iCount);
                }else {
                    hgccom5Matcher=new Hgccom5Matcher();
                    hgccom5Matcher.parseBuffer(buffer,iCount);
                }
            }
            else if(((buffer[0]=='H')&&(buffer[4]=='p')&&(buffer[6]=='r'))||((buffer[0]=='$')&&(buffer[4]=='C')&&(buffer[6]=='t')))// HGCUpgra   $HG:CSta   下位机板 升级 返回值
            {
                if (hGCUpgradeMatcher!=null)
                {
                    hGCUpgradeMatcher.parseBuffer(buffer,iCount);
                }else {
                    hGCUpgradeMatcher=new HGCUpgradeMatcher();
                    hGCUpgradeMatcher.parseBuffer(buffer,iCount);
                }
            }
            else if((buffer[0]=='$')&&(buffer[1]=='H')&&(buffer[2]=='G'))// $HG:   指令类 命令
            //   else if (sHeader.contains("$HG:CSta"))//下位机板 升级 返回值
            {
                if (mcuCmdMatcher!=null)
                {
                    mcuCmdMatcher.parseBuffer(buffer,iCount);
                }else {
                    mcuCmdMatcher=new McuCmdMatcher();
                    mcuCmdMatcher.parseBuffer(buffer,iCount);
                }
            }
        }
    }



}
