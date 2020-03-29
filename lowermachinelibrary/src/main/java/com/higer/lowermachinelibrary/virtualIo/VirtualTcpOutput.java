package com.higer.lowermachinelibrary.virtualIo;

import java.util.concurrent.LinkedBlockingQueue;

//TCP 虚拟 输出通道
final public class VirtualTcpOutput {
    private static VirtualTcpOutput instance=null;

    private VirtualTcpOutput() {
    }

    public static VirtualTcpOutput getInstance()
    {
        if (instance==null)
            instance=new VirtualTcpOutput();
        return instance;
    }

    private LinkedBlockingQueue<byte[]> buffers=new LinkedBlockingQueue<>();
    //初始化
    public void init()
    {
        buffers.clear();
    }

    public  byte[] getData()
    {
          return buffers.poll();
    }


    //虚拟 out 通道
    public void wirte(byte[] data)
    {
        buffers.offer(data);
    }

    //查看是否数据已经全部发送完毕
    public boolean isEmpty()
    {
        return buffers.isEmpty();
    }
}
