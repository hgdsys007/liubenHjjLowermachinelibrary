package com.higer.lowermachinelibrary.virtualIo;

import com.higer.lowermachinelibrary.entity.Frame;
import com.higer.lowermachinelibrary.utils.CmdUtil;

import java.util.concurrent.LinkedBlockingQueue;

public class CanBase {
    private static final int bufferLen=1024;
    private byte[] buffer=new byte[bufferLen];
    private LinkedBlockingQueue<Frame> inBuffers=new LinkedBlockingQueue<>();

    //初始化
    public void init()
    {
        inBuffers.clear();
    }

    //数据解析后的数据 调用
    public void output(Frame data)
    {
        inBuffers.offer(data);
    }


    public String readStr()
    {
        return new String(readByteArray());
    }

    public byte[] readByteArray()
    {
        int iPos=0;
        while (!inBuffers.isEmpty())
        {
            if((iPos+8)<=bufferLen)
            {
                Frame frame=readFrame();
                System.arraycopy(frame.data,0,buffer,iPos,frame.iDataLen);
                iPos+=frame.iDataLen;
            }else {
                break;
            }
        }
        byte[] res=new byte[iPos];
        if (iPos>0)
        {
            System.arraycopy(buffer,0,res,0,iPos);
        }
        return res;
    }

    //通道读
    public Frame readFrame()
    {
        return inBuffers.poll();
    }
    //通道写
    public void write(Frame data)
    {
        VirtualTcpOutput.getInstance().wirte(CmdUtil.makeCanCmd(CmdUtil.CAN1,data));
    }

    //通道写
    protected void write(int canNumber,int address,byte[] data,int iStart,int iLen)
    {
        VirtualTcpOutput.getInstance().wirte(CmdUtil.makeCanCmd(canNumber,address,data,iStart,iLen));
    }
    //通道写
    protected void write(int canNumber,int address,String cmd)
    {
        byte[] aaa=CmdUtil.makeCanCmd(canNumber,address,cmd);
        //   System.out.println("MMMMMMMM   "+StringHexUtil.ArraytoHexString(aaa));
        VirtualTcpOutput.getInstance().wirte(aaa);
    }
}
