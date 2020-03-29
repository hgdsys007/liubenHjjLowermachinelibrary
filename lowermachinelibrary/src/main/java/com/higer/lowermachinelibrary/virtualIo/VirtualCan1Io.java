package com.higer.lowermachinelibrary.virtualIo;

import com.higer.lowermachinelibrary.entity.Frame;

import com.higer.lowermachinelibrary.log.Logger;
import com.higer.lowermachinelibrary.statics.Config;

//500k
final public class VirtualCan1Io  {
    private VirtualCan1Io(){}
    private static VirtualCan1Io instance=null;
    public static VirtualCan1Io getInstance()
    {
        if (instance==null)
            instance=new VirtualCan1Io();
        return instance;
    }


    //初始化
    public void init()
    {
        VirtualLeidaIo.getInstance().init();//下级设备 都要这里配置
    }


    //数据解析后的数据 调用
    public void output(Frame data)
    {
        if (Config.logLevel<4)//3级
        {
          Logger.writeLog("LEVEL3   VirtualCan1Io::output  "+data.toString());
        }
        if(data.address==VirtualLeidaIo.LeiDaAddress)
        {
            VirtualLeidaIo.getInstance().output(data);
        }
    }




    //通道写
    public void write(byte[] data)
    {
        VirtualTcpOutput.getInstance().wirte(data);
    }
}
