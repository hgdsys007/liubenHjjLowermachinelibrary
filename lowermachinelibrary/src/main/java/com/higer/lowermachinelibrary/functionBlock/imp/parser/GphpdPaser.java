package com.higer.lowermachinelibrary.functionBlock.imp.parser;

import com.higer.lowermachinelibrary.functionBlock.IGpsContext;
import com.higer.lowermachinelibrary.functionBlock.dataProChannel.IDataPro;
import com.higer.lowermachinelibrary.functionBlock.IParser;

import com.higer.lowermachinelibrary.log.Logger;
import com.higer.lowermachinelibrary.statics.HeadGpsContext;
import com.higer.lowermachinelibrary.utils.CommonUtil;
import com.higer.lowermachinelibrary.utils.CrcUtil;
import com.higer.lowermachinelibrary.utils.StringHexUtil;

import cn.base.entity.VehicleGps;

final public class GphpdPaser  implements IParser {
    VehicleGps gps = new VehicleGps();
    byte[] buffer=new byte[16];
    int iPos=0;
    int iNumber=0;
    IGpsContext gpsContext=null;
    public void setGpsContext(IGpsContext gpsCon)
    {
        gpsContext=gpsCon;
    }
    @Override
    public boolean parse(byte[] cmd, int iLen) {
        try{
            if(CrcUtil.checkGps(cmd,iLen))
            {
                iNumber=0;
                iPos=0;
                for (int i = 0; i < iLen; i++) {
                    buffer[iPos++]=cmd[i];
                    if((cmd[i]==',')||(cmd[i]=='*'))
                    {
                        iNumber++;
                        switch (iNumber)
                        {
                            case 4://
                                gps.setCarAngle(CommonUtil.getFwj(StringHexUtil.ArraytoAsciiString(buffer,0,iPos-1)));
                                break;
                            case 22:
                                gps.setBackGpsFlag(CommonUtil.strToIn(StringHexUtil.ArraytoAsciiString(buffer,0,iPos-1)));
                                // HeadGpsContext.getInstance().setGphpd(gps);
                                if(gpsContext!=null)
                                {
                                    gpsContext.setGphpd(gps);
                                }
                                return true;
                        }
                        iPos=0;
                    }
                }
            }else {
                Logger.writeLog("GphpdPaser:parse   GPHPD  校验错误 ASC  len=" + iLen + "   " + StringHexUtil.ArraytoAsciiString(cmd,0,iLen));
                //     Logger.writeLog("GphpdPaser:parse   GPHPD  数据长度异常 HEX  len=" + array.length + "   " + StringHexUtil.ArraytoHexString(cmd,iStart,iLen));
                return false;
            }
        }catch (Exception e)
        {
            Logger.writeLog("GphpdPaser:parse  Exception "+e.toString());
            Logger.writeLog("buffer="+StringHexUtil.ArraytoAsciiString(cmd,0,iLen));
            Logger.writeLog("iPos="+iPos+"  iNumber="+iNumber);
            Logger.writeLog("buffer="+StringHexUtil.ArraytoAsciiString(buffer,0,16));
            iNumber=0;
            iPos=0;
            return false;
        }

        return true;
    }



    @Override
    public boolean parse(byte[] cmd, int iStart, int iLen) {

    //    System.out.println("======================================================================");
//        String ss="$GPHPD,2085,467288.600,92.26,1.17,0.00,34.7897313,115.5495137,40.785,10765067.260,20502643.277,40.785,0.002,0.003,0.002,0.023,0.033,0.024,0.000,23,24,4*74\n";
//        cmd=ss.getBytes();
//        iStart=0;
//        iLen=ss.length();
      //  System.out.println("GPHPD---"+StringHexUtil.ArraytoAsciiString(cmd,iStart,iLen));
        try{
            if(CrcUtil.checkGps(cmd,iStart,iLen))
            {
                //  CallbackContext.gpsGphpdRawDataList.offer(sCmd);
                iNumber=0;
                iPos=0;
                iLen+=iStart;
                for (int i = iStart; i < iLen; i++) {
                    buffer[iPos++]=cmd[i];
                    if((cmd[i]==',')||(cmd[i]=='*'))
                    {
                        iNumber++;
                        switch (iNumber)
                        {
                            case 4://
                                gps.setCarAngle(CommonUtil.getFwj(StringHexUtil.ArraytoAsciiString(buffer,0,iPos-1)));
                                break;
                            case 22:

                           //     Logger.writeLog("GphpdPaser:parse  "+"----------- "+StringHexUtil.ArraytoAsciiString(buffer,0,iPos-1)+"   "+CommonUtil.strToIn(StringHexUtil.ArraytoAsciiString(buffer,0,iPos-1)));

                                gps.setBackGpsFlag(CommonUtil.strToIn(StringHexUtil.ArraytoAsciiString(buffer,0,iPos-1)));
                                if(gpsContext!=null)
                                {
                                    gpsContext.setGphpd(gps);
                                }
                                return true;
                        }
                        iPos=0;
                    }
                }
            }else {
                Logger.writeLog("GphpdPaser:parse   GPHPD  校验错误 ASC  len=" + iLen + "   " + StringHexUtil.ArraytoAsciiString(cmd,iStart,iLen));
                //     Logger.writeLog("GphpdPaser:parse   GPHPD  数据长度异常 HEX  len=" + array.length + "   " + StringHexUtil.ArraytoHexString(cmd,iStart,iLen));
                return false;
            }
        }catch (Exception e)
        {
            Logger.writeLog("GphpdPaser:parse  Exception "+e.toString());
            Logger.writeLog("buffer="+StringHexUtil.ArraytoAsciiString(cmd,iStart,iLen));
            Logger.writeLog("iPos="+iPos+"  iNumber="+iNumber+" iStart="+iStart);
            Logger.writeLog("buffer="+StringHexUtil.ArraytoAsciiString(buffer,0,16));
            iNumber=0;
            iPos=0;
            return false;
        }

        return true;
    }

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
}
