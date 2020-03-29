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


final public class GpggaPaser  implements IParser {

    VehicleGps gps=new VehicleGps();
    byte[] buffer=new byte[16];
    int iPos=0;
    IGpsContext gpsContext=null;
    int iNumber=0;
    public void setGpsContext(IGpsContext gpsCon)
    {
        gpsContext=gpsCon;
    }
    @Override
    public boolean parse(byte[] cmd, int iLen) {
        try{
            if(CrcUtil.checkGps(cmd,iLen))
            {
//   $GPGGA,,,,,,0,,,,,,,,*66
                iNumber=0;
                iPos=0;
                for (int i = 0; i < iLen; i++) {
                    buffer[iPos++]=cmd[i];
                    if(cmd[i]==',')
                    {
                        iNumber++;
                        switch (iNumber)
                        {
                            case 2://时间  UTC时间，hhmmss（时分秒）格式
                                String s=StringHexUtil.ArraytoAsciiString(buffer,0,iPos-1);
                                gps.setGpsTime(s);
                              //  Logger.writeLog("GpggaParser gpstime "+s);
                                //  System.out.println(s);
                                //   gps.setGpsTime(StringHexUtil.ArraytoAsciiString(buffer,0,iPos-1));
                                break;
                            case 3://纬度ddmm.mmmm（度分）格式（前面的0也将被传输）
                                gps.setGpsWd(StringHexUtil.ArraytoAsciiString(buffer,0,iPos-1));
                                break;
                            case 5:// 经度dddmm.mmmm（度分）格式（前面的0也将被传输）
                                gps.setGpsJd(StringHexUtil.ArraytoAsciiString(buffer,0,iPos-1));
                                break;
                            case 7://<6> GPS状态， 0未定位， 1非差分定位， 2差分定位， 3无效PPS，4 固定解 5 非固定解  6正在估算
                                gps.setGpsFlag(CommonUtil.strToIn(StringHexUtil.ArraytoAsciiString(buffer,0,iPos-1)));
                                break;
                            case 8://正在使用解算位置的卫星数量（00~12）（前面的0也将被传输）
                                //    String ss=StringHexUtil.ArraytoAsciiString(buffer,0,iPos-1);
                                //     System.out.println("number="+ss);
                                gps.setGpsStars(CommonUtil.strToIn(StringHexUtil.ArraytoAsciiString(buffer,0,iPos-1)));
                                break;
                            case 10://<9> 海拔高度（-9999.9~99999.9）
                                gps.setGpsGd(StringHexUtil.ArraytoAsciiString(buffer,0,iPos-1));
                                // HeadGpsContext.getInstance().setGpgga(gps);
                                if(gpsContext!=null)
                                {
                                //    System.out.println("------------------ gpsContext.setGpgga(gps)");
                                    gpsContext.setGpgga(gps);
                                }else{
                                 //   System.out.println("------------------ content = null");
                                }
                                return true;
                        }
                        iPos=0;
                    }
                }
            }else {
                Logger.writeLog("GpggaPaser:parse   GPGGA  校验错误 ASC   " +  StringHexUtil.ArraytoAsciiString(cmd,0,iLen));
                //     Logger.writeLog("GphpdPaser:parse   GPHPD  数据长度异常 HEX  len=" + array.length + "   " + StringHexUtil.ArraytoHexString(cmd,iStart,iLen));
                return false;
            }
        }catch (Exception e)
        {
            Logger.writeLog("GpggaPaser:parse  Exception "+e.toString());
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

        try{
            if(CrcUtil.checkGps(cmd,iStart,iLen))
            {
//   $GPGGA,,,,,,0,,,,,,,,*66
                iNumber=0;
                iPos=0;
                iLen=iStart+iLen;
                for (int i = iStart; i < iLen; i++) {
                    buffer[iPos++]=cmd[i];
                    if(cmd[i]==',')
                    {
                        iNumber++;
                        switch (iNumber)
                        {
                            case 2://时间  UTC时间，hhmmss（时分秒）格式
                                String s=StringHexUtil.ArraytoAsciiString(buffer,0,iPos-1);
                                gps.setGpsTime(s);
                         //       Logger.writeLog("GpggaParser gpstime "+s);
                                //  System.out.println(s);
                                //   gps.setGpsTime(StringHexUtil.ArraytoAsciiString(buffer,0,iPos-1));
                                break;
                            case 3://纬度ddmm.mmmm（度分）格式（前面的0也将被传输）
                                gps.setGpsWd(StringHexUtil.ArraytoAsciiString(buffer,0,iPos-1));
                                break;
                            case 5:// 经度dddmm.mmmm（度分）格式（前面的0也将被传输）
                                gps.setGpsJd(StringHexUtil.ArraytoAsciiString(buffer,0,iPos-1));
                                break;
                            case 7://<6> GPS状态， 0未定位， 1非差分定位， 2差分定位， 3无效PPS，4 固定解 5 非固定解  6正在估算
                                gps.setGpsFlag(CommonUtil.strToIn(StringHexUtil.ArraytoAsciiString(buffer,0,iPos-1)));
                                break;
                            case 8://正在使用解算位置的卫星数量（00~12）（前面的0也将被传输）
                                //    String ss=StringHexUtil.ArraytoAsciiString(buffer,0,iPos-1);
                                //     System.out.println("number="+ss);
                                gps.setGpsStars(CommonUtil.strToIn(StringHexUtil.ArraytoAsciiString(buffer,0,iPos-1)));
                                break;
                            case 10://<9> 海拔高度（-9999.9~99999.9）
                                gps.setGpsGd(StringHexUtil.ArraytoAsciiString(buffer,0,iPos-1));
                                // HeadGpsContext.getInstance().setGpgga(gps);
                                if(gpsContext!=null)
                                {
                                    //    System.out.println("------------------ gpsContext.setGpgga(gps)");
                                    gpsContext.setGpgga(gps);
                                }else{
                                    //   System.out.println("------------------ content = null");
                                }
                                return true;
                        }
                        iPos=0;
                    }
                }
            }else {
                System.out.println("GpggaPaser:parse   GPGGA  校验错误 ASC");
                Logger.writeLog("GpggaPaser:parse   GPGGA  校验错误 ASC   " +  StringHexUtil.ArraytoAsciiString(cmd,0,iLen));
                //     Logger.writeLog("GphpdPaser:parse   GPHPD  数据长度异常 HEX  len=" + array.length + "   " + StringHexUtil.ArraytoHexString(cmd,iStart,iLen));
                return false;
            }
        }catch (Exception e)
        {
            Logger.writeLog("GpggaPaser:parse  Exception "+e.toString());
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
    public VehicleGps getGpsData() {
        return gps;
    }


    @Override
    public void setDataPro(IDataPro dataPro) {

    }

    @Override
    public IDataPro getDataPro() {
        return null;
    }
}
