package com.higer.lowermachinelibrary.virtualIo;

import com.higer.lowermachinelibrary.utils.CmdUtil;

import java.util.concurrent.LinkedBlockingQueue;

public class VirtualCom8Io {
    private VirtualCom8Io(){}
    private static VirtualCom8Io instance=null;
    public static VirtualCom8Io getInstance()
    {
        if (instance==null)
            instance=new VirtualCom8Io();
        return instance;
    }


    private LinkedBlockingQueue<byte[]> inBuffers=new LinkedBlockingQueue<>();

    //初始化
    public void init()
    {
        inBuffers.clear();
    }


    //数据解析后的数据 调用
    public void output(byte[] data)
    {
        inBuffers.offer(data);
    }



    //---------下面的是  身份证读卡器 线程 需要用的  IO 通道
    //通道读
    public byte[] read()
    {
        return inBuffers.poll();
    }
    //通道写
    public void write(byte[] data)
    {
        VirtualTcpOutput.getInstance().wirte(CmdUtil.makeComCmd(CmdUtil.COM8,data));
    }
}
