package com.higer.lowermachinelibrary.functionBlock.imp.parser;

import com.higer.lowermachinelibrary.functionBlock.IGpsContext;
import com.higer.lowermachinelibrary.functionBlock.dataProChannel.IDataPro;
import com.higer.lowermachinelibrary.functionBlock.IParser;

import com.higer.lowermachinelibrary.log.Logger;
import com.higer.lowermachinelibrary.statics.Config;
import com.higer.lowermachinelibrary.statics.HeadGpsContext;
import com.higer.lowermachinelibrary.utils.CommonUtil;
import com.higer.lowermachinelibrary.utils.CrcUtil;
import com.higer.lowermachinelibrary.utils.StringHexUtil;


import cn.base.entity.VehicleGps;

final public class Heading3aPaser implements IParser {
    VehicleGps gps = new VehicleGps();
    byte[] buffer = new byte[24];
    int iPos = 0;
    int iNumber = 0;
    IGpsContext gpsContext = null;

    public void setGpsContext(IGpsContext gpsCon) {
        gpsContext = gpsCon;
    }

    @Override
    public boolean parse(byte[] cmd, int iLen) {
        return parse(cmd, 0, iLen);
    }
//#HEADING3A,COM1,0,62.0,FINESTEERING,2084,288973.200,02008000,d3de,15227;SOL_COMPUTED,NARROW_INT,1.331216693,0.440198153,0.014018374,0.200,0.157729775,0.241520703,"Z88G",27,24,24,21,04,01,30,33*cfdca387


    //  CalculateBlockCRC32

  @Override
    public boolean parse(byte[] cmd, int iStart, int iLen) {
        try {
            if (CrcUtil.checkHeading8a(cmd, iStart, iLen)) {
                iNumber = 0;
                iPos = 0;
                iLen+=iStart;
                for (int i = iStart; i < iLen; i++) {
                    buffer[iPos++] = cmd[i];
                    if ((cmd[i] == ',')||(cmd[i] == ';')||(cmd[i] == '*')) {
                 //     System.out.println("===================="+StringHexUtil.ArraytoAsciiString(buffer, 0, iPos - 1)+"-----------------"+iNumber);
                        switch (iNumber) {
                            case 11://
                                gps.setBackGpsFlag(getHeading3aFlag(StringHexUtil.ArraytoAsciiString(buffer, 0, iPos - 1)));

                                break;
                            case 13:
                                gps.setCarAngle(CommonUtil.getFwj(StringHexUtil.ArraytoAsciiString(buffer, 0, iPos - 1)));

                                if (gpsContext != null) {
                                    gpsContext.setPtnl(gps);
                                }
                                return true;
                        }
                        iNumber++;
                        iPos = 0;
                    }
                }
            } else {
                Logger.writeLog("Heading3aPaser:parse     校验错误 ASC  len=" + iLen + "   " + StringHexUtil.ArraytoAsciiString(cmd, iStart, iLen));
                //     Logger.writeLog("GphpdPaser:parse   GPHPD  数据长度异常 HEX  len=" + array.length + "   " + StringHexUtil.ArraytoHexString(cmd,iStart,iLen));
                return false;
            }
        } catch (Exception e) {
            Logger.writeLog("Heading3aPaser:parse  Exception " + e.toString());
            Logger.writeLog("buffer=" + StringHexUtil.ArraytoAsciiString(cmd, 0, iLen));
            Logger.writeLog("iPos=" + iPos + "  iNumber=" + iNumber);
            Logger.writeLog("buffer=" + StringHexUtil.ArraytoAsciiString(buffer, 0, 24));
            iNumber = 0;
            iPos = 0;
            return false;
        }

        return true;
    }


/*
   @Override
    public boolean parse(byte[] cmd, int iStart, int iLen) {
        String sCmd= StringHexUtil.ArraytoAsciiString(cmd,iStart,iLen);
        if (Config.logLevel<4)//3级
        {
          Logger.writeLog("LEVEL3   Heading3aPaser::parse  ".concat(sCmd));
        }
        String array[]=sCmd.split("[;,]");

       for (int i = 0; i <27 ; i++) {
           System.out.println(array[i]+"--------------------------"+i);
       }

        //TODO:CRC 32校验
        if (array.length==27)
        {
            gps.setPtnl(sCmd);
            gps.setBackGpsFlag(getHeading3aFlag(array[11]));
            //13  航向角：0-360度
            gps.setCarAngle(CommonUtil.getFwj(array[13]));

            if(gpsContext!=null)
            {
                gpsContext.setHeading3A(gps);
            }
            return true;
        }else {
          Logger.writeLog("Heading3aPaser:parse  数据长度异常  array.length="+array.length+"   "+sCmd);
        }
        return false;
    }
*/


    @Override
    public void setDataPro(IDataPro dataPro) {

    }

    @Override
    public VehicleGps getGpsData() {
        return gps;
    }

    @Override
    public IDataPro getDataPro() {
        return null;
    }


    int getHeading3aFlag(String str) {
        if (str.equals("NARROW_INT"))
            return 4;
        if (str.equals("L1_FLOAT "))
            return 5;
        return 0;
    }
}
