package com.higer.lowermachinelibrary.adas;

import android.util.Log;

import com.higer.lowermachinelibrary.entity.AdasTableData;
import com.higer.lowermachinelibrary.statics.Config;
import com.higer.lowermachinelibrary.threads.BaseThread;
import com.higer.lowermachinelibrary.utils.ErrorMessageUtil;
import com.higer.lowermachinelibrary.utils.StringHexUtil;

import java.io.OutputStream;

import cn.base.entity.VehicleInput;
import cn.base.entity.VehicleMsg;

public class AdasCmdParser extends BaseThread {
    protected final byte CMD_FLAG = (byte)0X7E;
    private QuzhengCmdInterface quzhengCmdInterface=null;

    public void setQuzhengCmdInterface(QuzhengCmdInterface quzhengCmdInterface) {
        this.quzhengCmdInterface = quzhengCmdInterface;
    }

    protected int transDataArray(byte[] inputArray, int iStart, int iCount, byte[] outArray)
    {
        int iOutPos=0;
        for (int i = iStart; i <(iCount+iStart) ; i++) {
            if((inputArray[i]&0xff)==0x7d)
            {
                if((i+1)<iCount)
                {
                    if((inputArray[i+1]&0xff)==0x01)
                    {
                        outArray[iOutPos++]=0x7d;
                        i++;
                        continue;
                    }else if((inputArray[i+1]&0xff)==0x02)
                    {
                        outArray[iOutPos++]=0x7e;
                        i++;
                        continue;
                    }
                }
            }
            outArray[iOutPos++]=inputArray[i];
        }
        return iOutPos;
    }





    protected void parseCmd(byte[] cmdArray,int iStart, int iStop, OutputStream outputStream) throws Exception
    {
        int iLen=iStop-iStart+1;
        byte[] transReadArray=new byte[iLen];
        int iCount=transDataArray(cmdArray,0,iLen,transReadArray);

        if (iCount > 7) {
            System.out.println("-----------"+ArraytoHexString(transReadArray,iCount));
            if (((transReadArray[6] & 0xff) == 0x64) && ((transReadArray[7] & 0xff) == 0x50))//多媒体请求指令的 应答
            {
                System.out.println("====================================================");
            } else if ((((transReadArray[6] & 0xff) == 0x64)||((transReadArray[6] & 0xff) == 0x65)) && ((transReadArray[7] & 0xff) == 0x51))//多媒体 数据
            {
                System.out.println("------------------------------------iCount="+iCount);
                // System.out.println("-----------"+ArraytoHexString(transReadArray,iCount));
                VideoEntity videoEntity= new VideoEntity();
                if(videoEntity.parseVideoData(transReadArray,iCount))
                {
                    //   System.out.println(videoEntity.toString());
                    videoEntity.wirteFile();//写本地文件
                    int iPacages=videoEntity.getAllPackages();

                    if(videoEntity.getIndex()<(iPacages-1))
                    {
                        byte[] cmdSend=videoEntity.getNextPackgeCmd();
                        //System.out.println("getNextPackageCmd    "+ArraytoHexString(cmdSend));
                        outputStream.write(cmdSend);
                    }
                }else {
                    System.out.println("视频数据解析失败");
                }
                //   System.out.println("------------------------------------");
            }else if (((transReadArray[6] & 0xff) == 0x65) && ((transReadArray[7] & 0xff) == 0x36))//人脸  报警指令
            {
                if(iCount>15)
                {
                    int iType=transReadArray[6]&0xff;
                    int iGn=transReadArray[7]&0xff;//功能码
                    switch (iType)
                    {
                        case 0x64://ADAS
                            Log.i("234","ADAS");
                            //    parseGnm36(true,transReadArray,iCount);
                            parseGnm36(0x64,true,transReadArray,iCount);
                            break;
                        case 0x65:// 人脸
                            Log.i("234","人脸");
                            //   parseGnm36(false,transReadArray,iCount);
                            parseGnm36(0x65,false,transReadArray,iCount);
                            break;
                    }
                }
            }else if (((transReadArray[6] & 0xff) == 0x64) && ((transReadArray[7] & 0xff) == 0x36))//ADAS  报警指令
            {
                if(iCount>15)
                {
                    int iType=transReadArray[6]&0xff;
                    int iGn=transReadArray[7]&0xff;//功能码
                    switch (iType)
                    {
                        case 0x64://ADAS
                            Log.i("234","ADAS");
                            parseGnm36(true,transReadArray,iCount);
                            break;
                        case 0x65:// 人脸
                            Log.i("234","人脸");
                            parseGnm36(false,transReadArray,iCount);
                            break;
                    }
                }
            }
        }


    }

    private void doMedia(int waisheCode,byte[] cmdArray,int iLen)
    {
        if(iLen>43)
        {
            int mediumType=cmdArray[39]&0xff;//媒体类型  00图片  1音频 2视频
            int mediumId=((cmdArray[40]&0xff)<<24)+((cmdArray[41]&0xff)<<16)+((cmdArray[42]&0xff)<<8)+(cmdArray[43]&0xff);

            MediaCmd mediaCmd= new MediaCmd();
            mediaCmd.setWaiSheCode(waisheCode);
            mediaCmd.setInfoId(cmdArray[39]);
            mediaCmd.setMediaId(cmdArray,40);
            System.out.println("获取 取证 指令："+ArraytoHexString(mediaCmd.getCmd()));
            if(quzhengCmdInterface!=null)
            {
                quzhengCmdInterface.quzhengCmdArray(mediaCmd.getCmd());
            }



            switch (mediumType)
            {
                case 0://图片
                    System.out.println("====图片");
                    break;
                case 1://音频
                    System.out.println("===音频");
                    break;
                case 2://视频
                    System.out.println("===视频");
                    break;

            }
            System.out.println("媒体ID="+mediumId);
        }

    }

    private void doMedia(byte[] cmdArray,int iLen)
    {
        if(iLen>43)
        {
            int mediumType=cmdArray[39]&0xff;//媒体类型  00图片  1音频 2视频
            int mediumId=((cmdArray[40]&0xff)<<24)+((cmdArray[41]&0xff)<<16)+((cmdArray[42]&0xff)<<8)+(cmdArray[43]&0xff);

            MediaCmd mediaCmd= new MediaCmd();
            // mediaCmd.setWaiSheCode();
            mediaCmd.setInfoId(cmdArray[39]);
            mediaCmd.setMediaId(cmdArray,40);
            System.out.println("获取 取证 指令："+ArraytoHexString(mediaCmd.getCmd()));
            if(quzhengCmdInterface!=null)
            {
                quzhengCmdInterface.quzhengCmdArray(mediaCmd.getCmd());
            }



            switch (mediumType)
            {
                case 0://图片
                    System.out.println("图片");
                    break;
                case 1://音频
                    System.out.println("音频");
                    break;
                case 2://视频
                    System.out.println("视频");
                    break;

            }
            System.out.println("媒体ID="+mediumId);
        }

    }

    private void parseGnm36(int waisheCode,boolean isAdas,byte[] cmdArray,int iLen)
    {
//        0x01 前向碰撞报警
//        0x02 车道偏离报警
//        0x03 车距过近报警
//        0x04 行人碰撞 报警
//        0x05 频繁变道报警
//        0x06 道路标识超限报警
//        0x10 道路 标志识别事件
//        0x11 主动 抓拍事件
        int eventType=cmdArray[13]&0xff;//报警/事件类型

        int preSpeed=cmdArray[14]&0xff;//前车 车速   单位 Km/h，仅报警类型为 仅报警类型为 0x01 和 0x02时有效
        int prePersionLen=cmdArray[15]&0xff;//前车 /行人距离 仅报警类型为 0x01 和 0x02 和 0x04 时有效
        int plType=cmdArray[16]&0xff;//偏离类型  0x01 左侧偏离   0x02 右侧偏离

        AdasTableData adasTableData=new AdasTableData();
        String strTemp="";
        if(iLen>43)
        {
            int tempInforId=(cmdArray[39]&0xff);
            int tempMediaId=((cmdArray[40]&0xff)<<24)+((cmdArray[41]&0xff)<<16)+((cmdArray[42]&0xff)<<8)+(cmdArray[43]&0xff);

            switch (tempInforId)
            {
                case 0:
                    strTemp=tempInforId+"_"+tempMediaId+".jpg";
                    break;
                case 1:
                    break;
                case 2:
                    strTemp=tempInforId+"_"+tempMediaId+".mp4";
                    break;
            }
        }
        String imagePath= Config.prefixStr+strTemp;
        System.out.println("======================="+imagePath);
        if(isAdas)
        {
            baseInt=100;
            switch (eventType)
            {
                case 0x01:
                    adasTableData.setEventName("前向碰撞报警(请注意车距)");
                    adasTableData.setImagePath(imagePath);
                    //                                                               MyApplication.getInstance().addAdasData(adasTableData);
                    Log.i("234","前向碰撞报警(请注意车距)");
                    break;
                case 0x02:

                    switch (plType)
                    {
                        case 0x01:
                            baseInt+=5;
                            adasTableData.setEventName("左侧车道偏离报警");
                            adasTableData.setImagePath(imagePath);
                            //                                                                MyApplication.getInstance().addAdasData(adasTableData);
                            Log.i("234","左侧车道偏离报警");
                            break;
                        case 0x02:
                            baseInt+=6;
                            adasTableData.setEventName("右侧车道偏离报警");
                            adasTableData.setImagePath(imagePath);
                            //                                                               MyApplication.getInstance().addAdasData(adasTableData);
                            Log.i("234","右侧车道偏离报警");
                            break;
                    }
                    break;
                case 0x03:
                    adasTableData.setEventName("车距过近报警");
                    adasTableData.setImagePath(imagePath);
                    //                                                              MyApplication.getInstance().addAdasData(adasTableData);
                    Log.i("234","车距过近报警");
                    break;
                case 0x04:
                    adasTableData.setEventName("行人碰撞报警");
                    adasTableData.setImagePath(imagePath);
                    //                                                               MyApplication.getInstance().addAdasData(adasTableData);
                    Log.i("234","行人碰撞报警");
                    break;
                case 0x05:
                    adasTableData.setEventName("频繁变道报警");
                    adasTableData.setImagePath(imagePath);
                    //                                                               MyApplication.getInstance().addAdasData(adasTableData);
                    Log.i("234","频繁变道报警");
                    break;
                case 0x06:
                    adasTableData.setEventName("道路标识超限报警");
                    adasTableData.setImagePath(imagePath);
                    //                                                              MyApplication.getInstance().addAdasData(adasTableData);
                    Log.i("234","道路标识超限报警");
                    break;
                case 0x10:
                    adasTableData.setEventName("道路标志识别事件");
                    adasTableData.setImagePath(imagePath);
                    //                                                              MyApplication.getInstance().addAdasData(adasTableData);
                    Log.i("234","道路标志识别事件");
                    break;
                case 0x11:
                    adasTableData.setEventName("主动抓拍事件");
                    adasTableData.setImagePath(imagePath);
                    //                                                              MyApplication.getInstance().addAdasData(adasTableData);
                    Log.i("234","主动抓拍事件");
                    break;
            }
        }else {
//            0x01：疲劳驾驶报警
//            0x02：接打电话报警
//            0x03：抽烟报警
//            0x04：分神驾驶报警
//            0x05：驾驶员 异常报警
//            0x10 ：主动抓拍事件
//            0x11 ：驾驶员变更 事件
            baseInt=120;
            switch (eventType)
            {
                case 0x01:
                    adasTableData.setEventName("疲劳驾驶报警");
                    adasTableData.setImagePath(imagePath);
                    //                                                              MyApplication.getInstance().addAdasData(adasTableData);
                    Log.i("234","疲劳驾驶报警");
                    break;
                case 0x02:
                    adasTableData.setEventName("接打电话报警");
                    adasTableData.setImagePath(imagePath);
                    //                                                              MyApplication.getInstance().addAdasData(adasTableData);
                    Log.i("234","接打电话报警");
                    break;
                case 0x03:
                    adasTableData.setEventName("抽烟报警");
                    adasTableData.setImagePath(imagePath);
                    //                                                             MyApplication.getInstance().addAdasData(adasTableData);
                    Log.i("234","抽烟报警");
                    break;
                case 0x04:
                    adasTableData.setEventName("分神驾驶报警");
                    adasTableData.setImagePath(imagePath);
                    //                                                             MyApplication.getInstance().addAdasData(adasTableData);
                    Log.i("234","分神驾驶报警");
                    break;
                case 0x05:
                    adasTableData.setEventName("驶员异常报警---遮挡");
                    adasTableData.setImagePath(imagePath);
                    //                                                             MyApplication.getInstance().addAdasData(adasTableData);
                    Log.i("234","驾驶员异常报警---遮挡");
                    break;
                case 0x06:
                    adasTableData.setEventName("打哈欠");
                    adasTableData.setImagePath(imagePath);
                    //                                                             MyApplication.getInstance().addAdasData(adasTableData);
                    Log.i("234","打哈欠");
                    break;
                case 0x10:
                    adasTableData.setEventName("主动抓拍事件");
                    adasTableData.setImagePath(imagePath);
                    //                                                              MyApplication.getInstance().addAdasData(adasTableData);
                    Log.i("234","主动抓拍事件");
                    break;
                case 0x11:
                    adasTableData.setEventName("驾驶员变更 事件");
                    adasTableData.setImagePath(imagePath);
                   //                                                              MyApplication.getInstance().addAdasData(adasTableData);
                    Log.i("234","驾驶员变更 事件");
                    break;
                case 0x12:
                    adasTableData.setEventName("驾驶员识别");
                    adasTableData.setImagePath(imagePath);
                    //                                                             MyApplication.getInstance().addAdasData(adasTableData);
                    Log.i("234","驾驶员识别");
                    break;

            }
        }

        senRenMsg(baseInt+eventType,adasTableData.getImagePath());
        System.out.println("=================  "+adasTableData.toString());//打印内容   驶员异常报警---遮挡   /mnt/sdcard/adas/k77G_0_1353.jpg

        doMedia(waisheCode,cmdArray,iLen);//取证
    }

    int baseInt;
    //给任海涛发数据
    private void senRenMsg(int code,String strMsg)
    {
        //把数据通知给 任海涛
        VehicleInput vehicleInput = new VehicleInput();
        vehicleInput.setType(code);
        vehicleInput.setData(strMsg.getBytes());//mnt/sdcard/adas/k77G_0_1353.jpg
        ErrorMessageUtil.getInstance().notifyMsg(vehicleInput);
    }
    //事件 /报
    private void parseGnm36(boolean isAdas,byte[] cmdArray,int iLen)
    {
//        0x01 前向碰撞报警
//        0x02 车道偏离报警
//        0x03 车距过近报警
//        0x04 行人碰撞 报警
//        0x05 频繁变道报警
//        0x06 道路标识超限报警
//        0x10 道路 标志识别事件
//        0x11 主动 抓拍事件
        int eventType=cmdArray[13]&0xff;//报警/事件类型

        int preSpeed=cmdArray[14]&0xff;//前车 车速   单位 Km/h，仅报警类型为 仅报警类型为 0x01 和 0x02时有效
        int prePersionLen=cmdArray[15]&0xff;//前车 /行人距离 仅报警类型为 0x01 和 0x02 和 0x04 时有效
        int plType=cmdArray[16]&0xff;//偏离类型  0x01 左侧偏离   0x02 右侧偏离

        AdasTableData adasTableData=new AdasTableData();
        String strTemp="";
        if(iLen>43)
        {
            int tempInforId=(cmdArray[39]&0xff);
            int tempMediaId=((cmdArray[40]&0xff)<<24)+((cmdArray[41]&0xff)<<16)+((cmdArray[42]&0xff)<<8)+(cmdArray[43]&0xff);

            switch (tempInforId)
            {
                case 0:
                    strTemp=tempInforId+"_"+tempMediaId+".jpg";
                    break;
                case 1:
                    break;
                case 2:
                    strTemp=tempInforId+"_"+tempMediaId+".mp4";
                    break;
            }
        }
        String imagePath=Config.prefixStr+strTemp;
        System.out.println("======================="+imagePath);
        if(isAdas)
        {
            switch (eventType)
            {
                case 0x01:
                    adasTableData.setEventName("前向碰撞报警(请注意车距)");
                    adasTableData.setImagePath(imagePath);
                    //                                                             MyApplication.getInstance().addAdasData(adasTableData);
                    Log.i("234","前向碰撞报警(请注意车距)");
                    break;
                case 0x02:
                    switch (plType)
                    {
                        case 0x01:
                            adasTableData.setEventName("左侧车道偏离报警");
                            adasTableData.setImagePath(imagePath);
                            //                                                             MyApplication.getInstance().addAdasData(adasTableData);
                            Log.i("234","左侧车道偏离报警");
                            break;
                        case 0x02:
                            adasTableData.setEventName("右侧车道偏离报警");
                            adasTableData.setImagePath(imagePath);
                            //                                                              MyApplication.getInstance().addAdasData(adasTableData);
                            Log.i("234","右侧车道偏离报警");
                            break;
                    }
                    break;
                case 0x03:
                    adasTableData.setEventName("车距过近报警");
                    adasTableData.setImagePath(imagePath);
                    //                                                              MyApplication.getInstance().addAdasData(adasTableData);
                    Log.i("234","车距过近报警");
                    break;
                case 0x04:
                    adasTableData.setEventName("行人碰撞报警");
                    adasTableData.setImagePath(imagePath);
                    //                                                                MyApplication.getInstance().addAdasData(adasTableData);
                    Log.i("234","行人碰撞报警");
                    break;
                case 0x05:
                    adasTableData.setEventName("频繁变道报警");
                    adasTableData.setImagePath(imagePath);
                    //                                                              MyApplication.getInstance().addAdasData(adasTableData);
                    Log.i("234","频繁变道报警");
                    break;
                case 0x06:
                    adasTableData.setEventName("道路标识超限报警");
                    adasTableData.setImagePath(imagePath);
                    //                                                              MyApplication.getInstance().addAdasData(adasTableData);
                    Log.i("234","道路标识超限报警");
                    break;
                case 0x10:
                    adasTableData.setEventName("道路标志识别事件");
                    adasTableData.setImagePath(imagePath);
                    //                                                              MyApplication.getInstance().addAdasData(adasTableData);
                    Log.i("234","道路标志识别事件");
                    break;
                case 0x11:
                    adasTableData.setEventName("主动抓拍事件");
                    adasTableData.setImagePath(imagePath);
                    //                                                              MyApplication.getInstance().addAdasData(adasTableData);
                    Log.i("234","主动抓拍事件");
                    break;
            }
        }else {
//            0x01：疲劳驾驶报警
//            0x02：接打电话报警
//            0x03：抽烟报警
//            0x04：分神驾驶报警
//            0x05：驾驶员 异常报警
//            0x10 ：主动抓拍事件
//            0x11 ：驾驶员变更 事件
            switch (eventType)
            {
                case 0x01:
                    adasTableData.setEventName("疲劳驾驶报警");
                    adasTableData.setImagePath(imagePath);
                    //                                                              MyApplication.getInstance().addAdasData(adasTableData);
                    Log.i("234","疲劳驾驶报警");
                    break;
                case 0x02:
                    adasTableData.setEventName("接打电话报警");
                    adasTableData.setImagePath(imagePath);
                    //                                                              MyApplication.getInstance().addAdasData(adasTableData);
                    Log.i("234","接打电话报警");
                    break;
                case 0x03:
                    adasTableData.setEventName("抽烟报警");
                    adasTableData.setImagePath(imagePath);
                    //                                                               MyApplication.getInstance().addAdasData(adasTableData);
                    Log.i("234","抽烟报警");
                    break;
                case 0x04:
                    adasTableData.setEventName("分神驾驶报警");
                    adasTableData.setImagePath(imagePath);
                    //                                                              MyApplication.getInstance().addAdasData(adasTableData);
                    Log.i("234","分神驾驶报警");
                    break;
                case 0x05:
                    adasTableData.setEventName("驶员异常报警---遮挡");
                    adasTableData.setImagePath(imagePath);
                    //                                                               MyApplication.getInstance().addAdasData(adasTableData);
                    Log.i("234","驾驶员异常报警---遮挡");
                    break;
                case 0x06:
                    adasTableData.setEventName("打哈欠");
                    adasTableData.setImagePath(imagePath);
                    //                                                               MyApplication.getInstance().addAdasData(adasTableData);
                    Log.i("234","打哈欠");
                    break;
                case 0x10:
                    adasTableData.setEventName("主动抓拍事件");
                    adasTableData.setImagePath(imagePath);
                    //                                                               MyApplication.getInstance().addAdasData(adasTableData);
                    Log.i("234","主动抓拍事件");
                    break;
                case 0x11:
                    adasTableData.setEventName("驾驶员变更 事件");
                    adasTableData.setImagePath(imagePath);
                    //                                                               MyApplication.getInstance().addAdasData(adasTableData);
                    Log.i("234","驾驶员变更 事件");
                    break;
                case 0x12:
                    adasTableData.setEventName("驾驶员识别");
                    adasTableData.setImagePath(imagePath);
                    //                                                              MyApplication.getInstance().addAdasData(adasTableData);
                    Log.i("234","驾驶员识别");
                    break;

            }
        }
        doMedia(cmdArray,iLen);//取证
    }
    protected String ArraytoHexString(byte[] linArray)
    {
        int iLen=linArray.length;
        StringBuffer stringBuffer=new StringBuffer();
        for(int i=0;i<iLen;i++)
        {
            stringBuffer.append(String.format("%02X ",0xff&linArray[i]));
        }
        return stringBuffer.toString();

    }
    protected String ArraytoHexString(byte[] linArray, int iLen)
    {
        StringBuffer stringBuffer=new StringBuffer();
        for(int i=0;i<iLen;i++)
        {
            stringBuffer.append(String.format("%02X ",0xff&linArray[i]));
        }
        return stringBuffer.toString();

    }

    protected String ArraytoHexString(byte[] linArray,int iStart ,int iLen)
    {
        StringBuffer stringBuffer=new StringBuffer();
        for(int i=0;i<iLen;i++)
        {
            stringBuffer.append(String.format("%02X ",0xff&linArray[i+iStart]));
        }
        return stringBuffer.toString();

    }
}
