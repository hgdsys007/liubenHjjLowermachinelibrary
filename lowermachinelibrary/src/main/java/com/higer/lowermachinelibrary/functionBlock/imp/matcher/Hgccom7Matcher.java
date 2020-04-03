package com.higer.lowermachinelibrary.functionBlock.imp.matcher;

import com.higer.lowermachinelibrary.functionBlock.IMatcher;
import com.higer.lowermachinelibrary.log.Logger;
import com.higer.lowermachinelibrary.statics.Config;
import com.higer.lowermachinelibrary.utils.ErrorMessageUtil;
import com.higer.lowermachinelibrary.utils.StringHexUtil;
import com.higer.lowermachinelibrary.virtualIo.VirtualCom5Io;

import cn.base.entity.VehicleInput;
import cn.base.entity.VehicleMsg;

//扫描枪
public final class Hgccom7Matcher implements IMatcher {
    private final int HEADER_LEN=10;//通道协议头长度 HGCCOM5:+两个字节长度  总共10个字节

    public Hgccom7Matcher() {}
    @Override
    public void parseBuffer(byte[] buffer, int iCount) {
        parseSfz(buffer,iCount);//规定的 COM5 外接 身份证读卡器,如更换设备，可增加一个解析 方法  修改这里
    }



    //规定的 CO7 扫描枪
    //传递过来的数据就是 一条完整的数据
    private void parseSfz(byte[] buffer,int iLen)
    {
//        if (Config.logLevel<3)//2级
//        {
//            Logger.writeLog("LEVEL2   Hgccom5Matcher::parseBuffer  "+ StringHexUtil.ArraytoAsciiString(buffer,0,8)+StringHexUtil.ArraytoHexString(buffer, 8, iLen-8));
//        }




//        byte[] data=new byte[iLen-HEADER_LEN];
//        System.arraycopy(buffer,HEADER_LEN,data,0,iLen-HEADER_LEN);
//        System.out.println("扫描枪数据---"+StringHexUtil.ArraytoAsciiString(data,0,iLen-HEADER_LEN));


        //把数据通知给 任海涛
        String smsg=StringHexUtil.ArraytoAsciiString(buffer,HEADER_LEN,iLen-HEADER_LEN);
        System.out.println("扫描枪数据---"+smsg);

        VehicleInput vehicleInput = new VehicleInput();
        vehicleInput.setType(200);
        vehicleInput.setData(smsg.getBytes());//扫描枪数据
        ErrorMessageUtil.getInstance().notifyMsg(vehicleInput);


        // Logger.writeLog(ArraytoHexString(data));
//        VirtualCom5Io.getInstance().output(data);//直接输送到  虚拟IO口中去
    }


}
