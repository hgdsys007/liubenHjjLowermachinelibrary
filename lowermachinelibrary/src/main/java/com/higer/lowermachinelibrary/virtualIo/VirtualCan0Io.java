package com.higer.lowermachinelibrary.virtualIo;

import com.higer.lowermachinelibrary.entity.Frame;
import com.higer.lowermachinelibrary.log.Logger;
import com.higer.lowermachinelibrary.statics.Config;


//250k
final public class VirtualCan0Io  {
    private VirtualCan0Io(){}
    private static VirtualCan0Io instance=null;
    public static VirtualCan0Io getInstance()
    {
        if (instance==null)
            instance=new VirtualCan0Io();
        return instance;
    }
    //初始化
    public void init()
    {
       //下级设备 都要这里配置
    }


    //数据解析后的数据 调用
    public void output(Frame data)
    {
        if (Config.logLevel<4)//3级
        {
            Logger.writeLog("LEVEL3   VirtualCan0Io::output  "+data.toString());
        }
//        if(data.address==VirtualLeidaIo.LeiDaAddress)
//        {
//            VirtualLeidaIo.getInstance().output(data);
//        }
    }




    //通道写
    public void write(byte[] data)
    {
         VirtualTcpOutput.getInstance().wirte(data);
    }

}
