package com.higer.lowermachinelibrary.virtualIo;

import com.higer.lowermachinelibrary.utils.CmdUtil;

import java.util.concurrent.LinkedBlockingQueue;

//控制指令通道
final public class VirtualControllerIo {
    private VirtualControllerIo(){}
    private static VirtualControllerIo instance=null;
    public static VirtualControllerIo getInstance()
    {
        if (instance==null)
            instance=new VirtualControllerIo();
        return instance;
    }

    //通道写
    public void write(String cmd,String data)
    {
        VirtualTcpOutput.getInstance().wirte(CmdUtil.makeControlCmd(cmd,data));
    }
}
