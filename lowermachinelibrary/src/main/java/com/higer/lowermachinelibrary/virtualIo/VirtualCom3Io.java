package com.higer.lowermachinelibrary.virtualIo;

import com.higer.lowermachinelibrary.utils.CmdUtil;

import java.util.concurrent.LinkedBlockingQueue;

public final class VirtualCom3Io implements IVritualIoInterface{
    private VirtualCom3Io(){}
    private static VirtualCom3Io instance=null;
    public static VirtualCom3Io getInstance()
    {
        if (instance==null)
            instance=new VirtualCom3Io();
        return instance;
    }


    private LinkedBlockingQueue<byte[]> inBuffers=new LinkedBlockingQueue<>();

    //初始化
    @Override
    public void init()
    {
        inBuffers.clear();
    }


    //数据解析后的数据 调用
    @Override
    public void output(byte[] data)
    {
        inBuffers.offer(data);
    }



    //---------下面的是  身份证读卡器 线程 需要用的  IO 通道
    //通道读
    @Override
    public byte[] read()
    {
        return inBuffers.poll();
    }
    //通道写
    @Override
    public void write(byte[] data)
    {
        VirtualTcpOutput.getInstance().wirte(CmdUtil.makeComCmd(CmdUtil.COM3,data));
    }
}
