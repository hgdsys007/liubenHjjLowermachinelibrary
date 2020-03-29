package com.higer.lowermachinelibrary.functionBlock.imp.matcher;

import com.higer.lowermachinelibrary.entity.DoubleGps;
import com.higer.lowermachinelibrary.functionBlock.IGpsContext;
import com.higer.lowermachinelibrary.functionBlock.IMatcher;
import com.higer.lowermachinelibrary.functionBlock.IParser;
import com.higer.lowermachinelibrary.functionBlock.imp.parser.GpggaPaser;
import com.higer.lowermachinelibrary.functionBlock.imp.parser.GphpdPaser;
import com.higer.lowermachinelibrary.functionBlock.imp.parser.Heading3aPaser;
import com.higer.lowermachinelibrary.functionBlock.imp.parser.PtnlPaser;
import com.higer.lowermachinelibrary.log.Logger;
import com.higer.lowermachinelibrary.statics.BackGpsContext;
import com.higer.lowermachinelibrary.statics.Config;
import com.higer.lowermachinelibrary.statics.HeadGpsContext;
import com.higer.lowermachinelibrary.utils.StringHexUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import cn.base.entity.VehicleGps;

final public class GpsBanTeamDataMatcher implements IMatcher {
    // private static int gpsCmdLen=128;
    private byte[] cmd = null;
    private VehicleGps currentGpsPoint;
    private VehicleGps gpggaVehicleGps;
    private boolean isGpggaParsed;
    private int cmdPos = 0;
    private int iEnd = 0;
    private int iGpggaLen = 0;
    private boolean isHead = false;
    private boolean isSecondHead = false;
    private IGpsContext gpsContext = null;
    private IParser gpggaParser = null;
    private IParser heading3aParser = null;
    private IParser gphpdParser = null;
    private IParser ptnlParser = null;
    private boolean isHeadGps=false;

    private List<VehicleGps> pointList=null;

    public GpsBanTeamDataMatcher() {
        isHead = false;
        cmdPos = 0;
        this.cmd = new byte[Config.UartCmdLen];
        isSecondHead = false;
        currentGpsPoint = new VehicleGps();
        gpggaVehicleGps = null;
        isGpggaParsed = false;
        pointList=new ArrayList<>();
    }

/*
 public static void main(String[] args)
    {
        GpsBanTeamDataMatcher paser= new GpsBanTeamDataMatcher();
       // File file=new File("D:\\Work\\higer\\20200221-1347-1359.DAT"); // 20200221-1553-1640
        File file=new File("D:\\Work\\higer\\20200226-1220-1345.DAT"); // 20200221-1553-1640
        if(file.exists())
        {
            try {
                FileInputStream fileInputStream=new FileInputStream(file);
                byte buffer[]=new byte[512];
                int iCount=0;
                while ((iCount=fileInputStream.read(buffer,0,512))>0)
                {
                    paser.parse(buffer,0,iCount);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }catch (Exception e)
            {

            }
        }
    }
*/

    private void addPoint(VehicleGps point)
    {
        pointList.add(point.clone());
    }
    private VehicleGps getOkPoint()
    {
        VehicleGps res=null;
        if(pointList.size()>Config.delayPoints)
        {
            res=pointList.get(0);
            res.setCarAngle(pointList.get(Config.delayPoints).getCarAngle());
            res.setBackGpsFlag(pointList.get(Config.delayPoints).getBackGpsFlag());
            pointList.remove(0);
        }
        return res;
    }





    public void setGpsContext(IGpsContext gpsCon,boolean isHeadGps) {
        gpsContext = gpsCon;
        this.isHeadGps=isHeadGps;
    }

    public void parse(byte[] buffer, int iStart, int iCount) {
  //  System.out.println("---------gpsban read------------"+StringHexUtil.ArraytoAsciiString(buffer,iStart,iCount));
        try {
            if ((cmdPos + iCount - iStart) > Config.UartCmdLen) {
                Logger.writeLog("  Recv  ASCII  GpsBanTeamDataMatcher:parse  iCount=" + iCount + "   cmdPos=" + cmdPos + "   Config.UartCmdLen=" + Config.UartCmdLen + "  " + StringHexUtil.ArraytoAsciiString(buffer, iStart, iCount));
                Logger.writeLog("  Recv  二进制  GpsBanTeamDataMatcher:parse  iCount=" + iCount + "   cmdPos=" + cmdPos + "   Config.UartCmdLen=" + Config.UartCmdLen + "  " + StringHexUtil.ArraytoHexString(buffer, iStart, iCount));
                cmdPos = 0;
                iGpggaLen = 0;
                isHead = false;
                isSecondHead = false;

                if ((iCount - iStart) > Config.UartCmdLen) {
                    Logger.writeLog("  Recv  ASCII  GpsBanTeamDataMatcher:parse  iCount>" + Config.UartCmdLen);
                    return;
                }
            }

            iEnd = iStart + iCount;
            for (int i = iStart; i < iEnd; i++) {
                cmd[cmdPos++] = buffer[i];
                if (isHead)//找到协议头
                {
                    if (isSecondHead) {
                        if (cmd[cmdPos - 1] == 0X0D) // 0X0D 找到协议尾
                        {
                            isGpggaParsed=false;
                       //     System.out.println("------------ "+isHeadGps);
                  //    System.out.println("-----------" + StringHexUtil.ArraytoAsciiString(cmd, 0, cmdPos));


                        //    System.out.println("-----------"+StringHexUtil.ArraytoAsciiString(cmd,0,iGpggaLen));
                  //     Logger.writeLog(StringHexUtil.ArraytoAsciiString(cmd, 0, cmdPos));//TODO:------去掉
                            //   System.out.println("=============" + StringHexUtil.ArraytoAsciiString(cmd, iGpggaLen, cmdPos - iGpggaLen));
                            if ((cmd[1] == 71) && (cmd[3] == 71))// $GNGGA
                            {
                    //       System.out.println("-----------"+StringHexUtil.ArraytoAsciiString(cmd,0,iGpggaLen));
                                if (gpggaParser != null) {
                                    isGpggaParsed = gpggaParser.parse(cmd, 0, iGpggaLen);

                                 /*   if(!isHeadGps)
                                    {
                                        Logger.writeLog("-------------1 "+StringHexUtil.ArraytoAsciiString(cmd,0,iGpggaLen));
                                       // Logger.writeLog("-------------2 "+);
                                    }*/

//                                    if(isHeadGps)
//                                    {
//                                        if(isGpggaParsed)
//                                        {
//                                            Logger.writeLog("前GPS-GPGGA-true---GpsBanTeamDataMatcher:gpggaParser.parse(cmd, 0, iGpggaLen)  "+gpggaParser.getGpsData().getGpsJd()+"  "+gpggaParser.getGpsData().getGpsWd()+"  flag="+gpggaParser.getGpsData().getGpsFlag());
//                                        }else{
//                                            Logger.writeLog("前GPS-GPGGA-false---GpsBanTeamDataMatcher:gpggaParser.parse(cmd, 0, iGpggaLen)  ");
//                                        }
//                                    }else {
//                                        if(isGpggaParsed)
//                                        {
//                                            Logger.writeLog("后GPS-GPGGA-true---GpsBanTeamDataMatcher:gpggaParser.parse(cmd, 0, iGpggaLen)  "+gpggaParser.getGpsData().getGpsJd()+"  "+gpggaParser.getGpsData().getGpsWd()+"  flag="+gpggaParser.getGpsData().getGpsFlag());
//                                        }else{
//                                            Logger.writeLog("后GPS-GPGGA-false---GpsBanTeamDataMatcher:gpggaParser.parse(cmd, 0, iGpggaLen)");
//                                        }
//                                    }

                                    if (isGpggaParsed) {
                                        gpggaVehicleGps = gpggaParser.getGpsData();
                                        currentGpsPoint.setGpsTime(gpggaVehicleGps.getGpsTime());
                                        currentGpsPoint.setGpsWd(gpggaVehicleGps.getGpsWd());
                                        currentGpsPoint.setGpsJd(gpggaVehicleGps.getGpsJd());
                                        currentGpsPoint.setGpsFlag(gpggaVehicleGps.getGpsFlag());
                                        currentGpsPoint.setGpsStars(gpggaVehicleGps.getGpsStars());
                                        currentGpsPoint.setGpsGd(gpggaVehicleGps.getGpsGd());


//                                        if(!isHeadGps)
//                                        {
//                                            Logger.writeLog("-------------2 "+currentGpsPoint.getGpsTime());
//                                        }
                                    }
                                } else {
                                    gpggaParser = new GpggaPaser();
                                    isGpggaParsed = gpggaParser.parse(cmd, 0, iGpggaLen);
                                    if (isGpggaParsed) {
                                        gpggaVehicleGps = gpggaParser.getGpsData();
                                        currentGpsPoint.setGpsTime(gpggaVehicleGps.getGpsTime());
                                        currentGpsPoint.setGpsWd(gpggaVehicleGps.getGpsWd());
                                        currentGpsPoint.setGpsJd(gpggaVehicleGps.getGpsJd());
                                        currentGpsPoint.setGpsFlag(gpggaVehicleGps.getGpsFlag());
                                        currentGpsPoint.setGpsStars(gpggaVehicleGps.getGpsStars());
                                        currentGpsPoint.setGpsGd(gpggaVehicleGps.getGpsGd());
                                    }
                                }
                            }

                            if ((cmd[iGpggaLen + 1] == 71) && (cmd[iGpggaLen + 3] == 72))// $GPHPD   南方
                            {

                     //           Logger.writeLog("=============" + StringHexUtil.ArraytoAsciiString(cmd, iGpggaLen, cmdPos - iGpggaLen));//TODO:------去掉

               //   System.out.println("=============" + StringHexUtil.ArraytoAsciiString(cmd, iGpggaLen, cmdPos - iGpggaLen));
                                if (gphpdParser != null) {
                                    if (isGpggaParsed&&(gphpdParser.parse(cmd, iGpggaLen, cmdPos - iGpggaLen))) {
                                //        System.out.println("=============" + StringHexUtil.ArraytoAsciiString(cmd, iGpggaLen, cmdPos - iGpggaLen));
                                        if (gpsContext != null)//   HeadGpsContext.getInstance()
                                        {
                                            currentGpsPoint.setCarAngle(gphpdParser.getGpsData().getCarAngle());
                                            currentGpsPoint.setBackGpsFlag(gphpdParser.getGpsData().getBackGpsFlag());

                                 //       System.out.println("----------------  "+currentGpsPoint.getGpsTime());



                                        //    gpsContext.setGps(currentGpsPoint);
                                            if(isHeadGps)
                                            {
                                            //    Logger.writeLog("前GPS----GpsBanTeamDataMatcher:gpsContext.setListGps(currentGpsPoint);");
                                                DoubleGps doubleGps=new DoubleGps(currentGpsPoint.clone(),BackGpsContext.getInstance().getStateGps());
//                                                doubleGps.setBackGps(BackGpsContext.getInstance().getStateGps());
//                                                doubleGps.setHeadGps(currentGpsPoint.clone());


                                        //        Logger.writeLog("-------------3 "+doubleGps.getBackGps().getGpsTime());

                                                gpsContext.setListGps(doubleGps);
                                            }else {
                                             //   Logger.writeLog("后GPS----GpsBanTeamDataMatcher:gpsContext.setStateGps(currentGpsPoint);");
                                                gpsContext.setStateGps(currentGpsPoint);
                                            }



//                                            addPoint(currentGpsPoint);
//                                            gpsContext.setGps(getOkPoint());

                                        }
                                    }
                                } else {
                                    gphpdParser = new GphpdPaser();
                                    if (isGpggaParsed&&(gphpdParser.parse(cmd, iGpggaLen, cmdPos - iGpggaLen))) {
                                        if (gpsContext != null)//   HeadGpsContext.getInstance()
                                        {
                                            currentGpsPoint.setCarAngle(gphpdParser.getGpsData().getCarAngle());
                                            currentGpsPoint.setBackGpsFlag(gphpdParser.getGpsData().getBackGpsFlag());


                                            if(isHeadGps)
                                            {
                                                //gpsContext.setListGps(currentGpsPoint);
                                                DoubleGps doubleGps=new DoubleGps(currentGpsPoint.clone(),BackGpsContext.getInstance().getStateGps());
                                                gpsContext.setListGps(doubleGps);
                                            }else {
                                                gpsContext.setStateGps(currentGpsPoint);
                                            }
//                                            addPoint(currentGpsPoint);
//                                            gpsContext.setGps(getOkPoint());

                                        }
                                    }
                                }
                            } else if ((cmd[iGpggaLen + 1] == 72) && (cmd[iGpggaLen + 3] == 65))//#HEADING3A
                            {
                    //      System.out.println("=============" + StringHexUtil.ArraytoAsciiString(cmd, iGpggaLen, cmdPos - iGpggaLen));

                            //    System.out.println("=============" + StringHexUtil.ArraytoHexString(cmd, iGpggaLen, cmdPos - iGpggaLen));
                                if (heading3aParser != null) {
                                    if (isGpggaParsed&&(heading3aParser.parse(cmd, iGpggaLen, cmdPos - iGpggaLen))) {
                                        if (gpsContext != null)//   HeadGpsContext.getInstance()
                                        {
                                        //    currentGpsPoint.setPtnl(heading3aParser.getGpsData().getPtnl());
                                            currentGpsPoint.setCarAngle(heading3aParser.getGpsData().getCarAngle());
                                            currentGpsPoint.setBackGpsFlag(heading3aParser.getGpsData().getBackGpsFlag());



                                            if(isHeadGps)
                                            {
                                             //   gpsContext.setListGps(currentGpsPoint);
                                                DoubleGps doubleGps=new DoubleGps(currentGpsPoint.clone(),BackGpsContext.getInstance().getStateGps());
                                                gpsContext.setListGps(doubleGps);
                                            }else {
                                                gpsContext.setStateGps(currentGpsPoint);
                                            }

//                                            addPoint(currentGpsPoint);
//                                            gpsContext.setGps(getOkPoint());


                                        }
                                    }
                                } else {
                                    heading3aParser = new Heading3aPaser();
                                    if (isGpggaParsed&&(heading3aParser.parse(cmd, iGpggaLen, cmdPos - iGpggaLen))) {
                                        if (gpsContext != null)//   HeadGpsContext.getInstance()
                                        {
                                         //   currentGpsPoint.setPtnl(heading3aParser.getGpsData().getPtnl());
                                            currentGpsPoint.setCarAngle(heading3aParser.getGpsData().getCarAngle());
                                            currentGpsPoint.setBackGpsFlag(heading3aParser.getGpsData().getBackGpsFlag());



                                            if(isHeadGps)
                                            {
                                             //   gpsContext.setListGps(currentGpsPoint);
                                                DoubleGps doubleGps=new DoubleGps(currentGpsPoint.clone(),BackGpsContext.getInstance().getStateGps());
                                                gpsContext.setListGps(doubleGps);
                                            }else {
                                                gpsContext.setStateGps(currentGpsPoint);
                                            }
//                                            addPoint(currentGpsPoint);
//                                            gpsContext.setGps(getOkPoint());

                                        }
                                    }
                                }
                            } else if ((cmd[iGpggaLen + 1] == 80) && (cmd[iGpggaLen + 3] == 78))// $PTNL
                            {//$PTNL

                     //           System.out.println("=============" + StringHexUtil.ArraytoAsciiString(cmd, iGpggaLen, cmdPos - iGpggaLen));

                             //    System.out.println("=============" + StringHexUtil.ArraytoHexString(cmd, iGpggaLen, cmdPos - iGpggaLen));
                                if (ptnlParser != null) {
                                    if (isGpggaParsed&&(ptnlParser.parse(cmd, iGpggaLen, cmdPos - iGpggaLen))) {
                                        if (gpsContext != null)
                                        {
                                      //      currentGpsPoint.setPtnl(ptnlParser.getGpsData().getPtnl());
                                            currentGpsPoint.setCarAngle(ptnlParser.getGpsData().getCarAngle());
                                            currentGpsPoint.setBackGpsFlag(ptnlParser.getGpsData().getBackGpsFlag());



                                            if(isHeadGps)
                                            {
                                                DoubleGps doubleGps=new DoubleGps(currentGpsPoint.clone(),BackGpsContext.getInstance().getStateGps());
                                                gpsContext.setListGps(doubleGps);
                                            }else {
                                                gpsContext.setStateGps(currentGpsPoint);
                                            }

//                                            addPoint(currentGpsPoint);
//                                            gpsContext.setGps(getOkPoint());

                                        }
                                    }
                                } else {
                                    ptnlParser = new PtnlPaser();
                                    if (isGpggaParsed&&(ptnlParser.parse(cmd, iGpggaLen, cmdPos - iGpggaLen))) {
                                        if (gpsContext != null)//   HeadGpsContext.getInstance()
                                        {
                                         //   currentGpsPoint.setPtnl(ptnlParser.getGpsData().getPtnl());
                                            currentGpsPoint.setCarAngle(ptnlParser.getGpsData().getCarAngle());
                                            currentGpsPoint.setBackGpsFlag(ptnlParser.getGpsData().getBackGpsFlag());


                                            if(isHeadGps)
                                            {
                                              //  gpsContext.setListGps(currentGpsPoint);
                                                DoubleGps doubleGps=new DoubleGps(currentGpsPoint.clone(),BackGpsContext.getInstance().getStateGps());
                                                gpsContext.setListGps(doubleGps);
                                            }else {
                                                gpsContext.setStateGps(currentGpsPoint);
                                            }
//                                            addPoint(currentGpsPoint);
//                                            gpsContext.setGps(getOkPoint());


                                        }
                                    }
                                }
                            }
                            isSecondHead = false;
                            isHead = false;
                            cmdPos = 0;
                            continue;
                        }
                    } else {
                        //   $GPHPD  $PTNL    #HEADING3A
                        if (((cmd[cmdPos - 1] == 'H') && (cmd[cmdPos - 2] == 'P') && (cmd[cmdPos - 3] == 'G') && (cmd[cmdPos - 4] == '$')) || ((cmd[cmdPos - 1] == 'N') && (cmd[cmdPos - 2] == 'T') && (cmd[cmdPos - 3] == 'P') && (cmd[cmdPos - 4] == '$')) || ((cmd[cmdPos - 1] == 'A') && (cmd[cmdPos - 2] == 'E') && (cmd[cmdPos - 3] == 'H') && (cmd[cmdPos - 4] == '#')))// #HEADING3A
                        {
                            isSecondHead = true;
                            iGpggaLen = cmdPos - 4;
                        }
                    }
                } else {//没有找到协议头
                    if (cmdPos > 3) {
                        if ((cmd[cmdPos - 1] == 'G') && (cmd[cmdPos - 3] == 'G') && (cmd[cmdPos - 4] == '$'))//协议头   $GNGGA
                        {
                            cmd[0] = cmd[cmdPos - 4];
                            cmd[1] = cmd[cmdPos - 3];
                            cmd[2] = cmd[cmdPos - 2];
                            cmd[3] = cmd[cmdPos - 1];
                            cmdPos = 4;
                            isHead = true;
                            isSecondHead = false;
                            iGpggaLen = 0;
                            continue;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.writeLog("GpsBanTeamDataMatcher:parse  Exception "+e.toString());
            Logger.writeLog("cmdPos="+cmdPos+"  iStart="+iStart+"  iCoun="+iCount);
            Logger.writeLog("cmd="+StringHexUtil.ArraytoAsciiString(cmd,0,cmdPos));
            Logger.writeLog("buffer="+StringHexUtil.ArraytoAsciiString(buffer,iStart,iCount));
            isHead = false;
            isSecondHead = false;
            iGpggaLen = 0;
        }
    }

    @Override
    public void parseBuffer(byte[] buffer, int iCount) {
        parse(buffer, 0, iCount);
    }


}
