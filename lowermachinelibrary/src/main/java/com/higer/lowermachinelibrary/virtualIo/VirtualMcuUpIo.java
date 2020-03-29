package com.higer.lowermachinelibrary.virtualIo;


import com.higer.lowermachinelibrary.utils.StringHexUtil;


import java.util.concurrent.LinkedBlockingQueue;

final public class VirtualMcuUpIo {
    private LinkedBlockingQueue<byte[]> buffers=new LinkedBlockingQueue<>();
    private VirtualMcuUpIo(){
    }
    private static VirtualMcuUpIo instance=null;
    public static VirtualMcuUpIo getInstance()
    {
        if (instance==null)
            instance=new VirtualMcuUpIo();
        return instance;
    }

    public void clearBuffer()
    {
        buffers.clear();
    }

    //灌数据
    public void out(byte[] data)
    {
        buffers.offer(data);
    }

    public String read()
    {
        byte[] bys=buffers.poll();
        if(bys!=null)
        {
            return StringHexUtil.ArraytoAsciiString(bys,0,bys.length);
        }
        return "";
    }

    //通道写
    public void write(byte[] cmd)
    {
        VirtualTcpOutput.getInstance().wirte(cmd);
    }

    //通道写
    public void write(String cmd)
    {
        VirtualTcpOutput.getInstance().wirte(cmd.getBytes());
    }




}
