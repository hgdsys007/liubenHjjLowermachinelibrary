package com.higer.lowermachinelibrary.functionBlock.imp.matcher;

import com.higer.lowermachinelibrary.entity.Frame;
import com.higer.lowermachinelibrary.functionBlock.IMatcher;
import com.higer.lowermachinelibrary.functionBlock.IParser;

import com.higer.lowermachinelibrary.log.Logger;
import com.higer.lowermachinelibrary.statics.Config;
import com.higer.lowermachinelibrary.utils.StringHexUtil;
import com.higer.lowermachinelibrary.virtualIo.VirtualCan1Io;

final public class Hgccan1Matcher  implements IMatcher {


    public Hgccan1Matcher() {

    }
    @Override
    public void parseBuffer(byte[] buffer, int iCount) {
        if(iCount<=0)
        {
            return;
        }

     // Logger.writeLog("LEVEL2   Hgccan1Matcher::parseBuffer  "+StringHexUtil.ArraytoAsciiString(buffer, 0, 8)+ArraytoHexString(buffer, 8, iCount-8));

        if (Config.logLevel<3)//2级
        {
            Logger.writeLog("LEVEL2   Hgccan1Matcher::parseBuffer  "+ StringHexUtil.ArraytoAsciiString(buffer, 0, 8)+StringHexUtil.ArraytoHexString(buffer, 8, iCount-8));
        }

   //    System.out.println("LEVEL2   Hgccan1Matcher::parseBuffer  "+ StringHexUtil.ArraytoAsciiString(buffer, 0, 8)+StringHexUtil.ArraytoHexString(buffer, 8, iCount-8));
    //    System.out.println("LEVEL2   Hgccan1Matcher::parseBuffer  "+ StringHexUtil.ArraytoAsciiString(buffer, 0, 8)+StringHexUtil.ArraytoAsciiString(buffer, 12, iCount-12));

//        byte[] data=new byte[iCount-10];
//        System.arraycopy(buffer,10,data,0,iCount-10);
//
//        Frame frame=new Frame();
//        if (frame.parse(data))
//        {
//            VirtualCan1Io.getInstance().output(frame);
//        }





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
                 System.out.println("LEVEL2  "+frame.toString());
                    parseLeiDa(frame);
               //     VirtualCan1Io.getInstance().output(frame);
                }else {
                  Logger.writeLog("CAN1 Hgccan1Matcher::parseBuffer  帧数据解析失败");
                }
            }


        }else {
          Logger.writeLog("CAN1 Hgccan1Matcher::parseBuffer  接收到的 数据长度不是10的倍数  len="+cmdLen+"   cmd="+StringHexUtil.ArraytoAsciiString(buffer, 0, 8)+StringHexUtil.ArraytoHexString(buffer, 8, iCount-8));
        }
    }

    private void parseLeiDa(Frame frame)
    {
        byte[] data=frame.data;

        //     System.out.println(StringHexUtil.ArraytoHexString(data));

        double dLat=((data[0]&0XFF)/10D)-12.7;// 左正右负  目标横向距离  单位  米
        double dLong=(((data[2]&7)<<8)+(data[1]&0xff))*0.1;//   目标纵向距离  单位  米

        int state=(data[2]>>3)&7;//【目标 xx 的运动状态】   【0：未知目标】、【1：静态目标】、【2：同向运动目标】、【3：反向运动目标】、【4：停 止目标】、【5：高处目标】
        double accel= (((data[3]&0xff)<<2)+((data[2]&0xff)>>6))*0.1-12.7;//目标 xx 的纵向加速度  [m/s2]---------加速度
        double vLat= ((((data[4]&0xff)<<2)+((data[3]&0xff)>>6))&0x7f)*0.1-6.4;//目标 xx 的横向速度  m/s
        double vLong=(((data[4]&0xff)>>5)+((data[5]&0xff)<<3))*0.1-102.4;//目标 xx 的纵向速度  m/s
        //   int rcs=(data[6]&0x3f)-20;//目标 xx 的 RCS
        double high=(((data[6]&0xff)>>6)+((data[7]&0xf)<<2))*0.1;//目标 xx 的高度   单位  米
        double dLen=((data[6]&0xff)>>4)*0.2;//目标 xx 的长度

        double len=(Math.sqrt(Math.pow(dLat,2)+Math.pow(dLong,2)));


        if(frame.address==0x490)
        {
            System.out.println("---------------------------------------------");

          //  MyApplication.getInstance().sendLocalBroadcast(CLEAN_POINTS,"CMD","clean");
        }
        if(len>0)
        {
            //   System.out.println("距离:"+(Math.sqrt(Math.pow(dLat,2)+Math.pow(dLong,2)))+"   "+dLat+"   "+dLong);
            //    System.out.println("----------------------------------------------"+(frame.getId()-0x490));
               System.out.println("远距离雷达   距离:"+len+"     ID="+(frame.address-0x490));

            //    System.out.println("-----------"+high+"         "+dLen);

         //   MyApplication.getInstance().sendLocalBroadcast(ADD_POINTS,"CMD",dLong+","+dLat);
        }

    }


}
