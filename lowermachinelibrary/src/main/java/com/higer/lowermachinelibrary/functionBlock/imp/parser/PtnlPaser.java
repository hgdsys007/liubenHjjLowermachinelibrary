package com.higer.lowermachinelibrary.functionBlock.imp.parser;

import com.higer.lowermachinelibrary.functionBlock.IGpsContext;
import com.higer.lowermachinelibrary.functionBlock.dataProChannel.IDataPro;
import com.higer.lowermachinelibrary.functionBlock.IParser;
import com.higer.lowermachinelibrary.log.Logger;
import com.higer.lowermachinelibrary.statics.CallbackContext;
import com.higer.lowermachinelibrary.statics.Config;
import com.higer.lowermachinelibrary.statics.HeadGpsContext;
import com.higer.lowermachinelibrary.utils.CommonUtil;
import com.higer.lowermachinelibrary.utils.CrcUtil;
import com.higer.lowermachinelibrary.utils.StringHexUtil;

import cn.base.entity.VehicleGps;

final public class PtnlPaser implements IParser {
    VehicleGps gps = new VehicleGps();
    byte[] buffer = new byte[16];
    int iPos = 0;
    int iNumber = 0;
    IGpsContext gpsContext=null;
    public void setGpsContext(IGpsContext gpsCon)
    {
        gpsContext=gpsCon;
    }
    @Override
    public boolean parse(byte[] cmd, int iLen) {
        return parse(cmd,0,iLen);
      //  System.out.println("---------------------------"+StringHexUtil.ArraytoAsciiString(cmd,0,iLen));
       /* try{
            if (CrcUtil.checkGps(cmd, iLen)) {
                iNumber = 0;
                iPos=0;
                for (int i = 0; i < iLen; i++) {
                    buffer[iPos++] = cmd[i];
                    if (cmd[i] == ',') {
                        switch (iNumber) {
                            case 2://
                              //  System.out.println("----"+StringHexUtil.ArraytoAsciiString(buffer,0,iPos-1));
                                gps.setCarAngle(CommonUtil.getFwj(StringHexUtil.ArraytoAsciiString(buffer, 0, iPos-1)));
                                break;
                            case 9:
                                if(buffer[iPos-1]=='3')
                                {
                                    gps.setBackGpsFlag(4);
                                }else if(buffer[iPos-1]=='4')
                                {
                                    gps.setBackGpsFlag(3);
                                }


                                if(gpsContext!=null)
                                {
                                    gpsContext.setPtnl(gps);
                                }
                                return true;
                        }
                        iNumber++;
                        iPos = 0;
                    }
                }
            } else {
                Logger.writeLog("GphpdPaser:parse   GPHPD  校验错误 ASC  len=" + iLen + "   " + StringHexUtil.ArraytoAsciiString(cmd, 0, iLen));
                //     Logger.writeLog("GphpdPaser:parse   GPHPD  数据长度异常 HEX  len=" + array.length + "   " + StringHexUtil.ArraytoHexString(cmd,iStart,iLen));
                return false;
            }
        }catch (Exception e)
        {
            Logger.writeLog("PtnlPaser:parse  Exception "+e.toString());
            Logger.writeLog("buffer="+StringHexUtil.ArraytoAsciiString(cmd,0,iLen));
            Logger.writeLog("iPos="+iPos+"  iNumber="+iNumber);
            Logger.writeLog("buffer="+StringHexUtil.ArraytoAsciiString(buffer,0,16));
            iNumber=0;
            iPos=0;
            return false;
        }

        return true;*/
    }

    @Override
    public boolean parse(byte[] cmd, int iStart, int iLen) {

        try{
            if (CrcUtil.checkGps(cmd,iStart, iLen)) {
                iNumber = 0;
                iPos=0;
                iLen+=iStart;
                for (int i = iStart; i < iLen; i++) {
                    buffer[iPos++] = cmd[i];
                    if (cmd[i] == ',') {
                     //   System.out.println("--iNumber="+iNumber+"    "+StringHexUtil.ArraytoAsciiString(buffer,0,iPos-1));

                        switch (iNumber) {
                            case 3://
                            //    System.out.println("----"+StringHexUtil.ArraytoAsciiString(buffer,0,iPos-1));
                                gps.setCarAngle(CommonUtil.getFwj(StringHexUtil.ArraytoAsciiString(buffer, 0, iPos-1)));
                                break;
                            case 10:
                           //     System.out.println("------flag="+(char)buffer[iPos-2]);
                                if(buffer[iPos-2]=='3')
                                {
                                    gps.setBackGpsFlag(4);
                                }else if(buffer[iPos-2]=='4')
                                {
                                    gps.setBackGpsFlag(3);
                                }


                                if(gpsContext!=null)
                                {
                                    gpsContext.setPtnl(gps);
                                }
                               return true;
                        }
                        iNumber++;
                        iPos = 0;
                    }
                }
            } else {
                Logger.writeLog("GphpdPaser:parse   GPHPD  校验错误 ASC  len=" + iLen + "   " + StringHexUtil.ArraytoAsciiString(cmd, 0, iLen));
                //     Logger.writeLog("GphpdPaser:parse   GPHPD  数据长度异常 HEX  len=" + array.length + "   " + StringHexUtil.ArraytoHexString(cmd,iStart,iLen));
                return false;
            }
        }catch (Exception e)
        {
            Logger.writeLog("PtnlPaser:parse  Exception "+e.toString());
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
    public void setDataPro(IDataPro dataPro) {

    }
    @Override
    public VehicleGps getGpsData() {
        return  gps;
    }
    @Override
    public IDataPro getDataPro() {
        return null;
    }
}
