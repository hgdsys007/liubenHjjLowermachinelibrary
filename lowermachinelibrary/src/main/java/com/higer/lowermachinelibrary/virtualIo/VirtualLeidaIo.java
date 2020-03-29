package com.higer.lowermachinelibrary.virtualIo;

import com.higer.lowermachinelibrary.entity.Frame;
import com.higer.lowermachinelibrary.utils.CmdUtil;
import com.higer.lowermachinelibrary.utils.StringHexUtil;

import java.util.concurrent.LinkedBlockingQueue;

//雷达虚拟 IO口
final public class VirtualLeidaIo extends CanBase{
    public static int LeiDaAddress=251;
    private VirtualLeidaIo(){init();}
    private static VirtualLeidaIo instance=null;
    public static VirtualLeidaIo getInstance()
    {
        if (instance==null)
            instance=new VirtualLeidaIo();
        return instance;
    }



    //通道写
    public void write(byte[] data,int iStart,int iLen)
    {
        write(CmdUtil.CAN1,LeiDaAddress,data,iStart,iLen);
    }
    //通道写
    public void write(String cmd)
    {
        write(CmdUtil.CAN1,LeiDaAddress,cmd);
    }
}
