package com.higer.lowermachinelibrary.threads.udp;


import com.higer.lowermachinelibrary.threads.BaseThread;

public class NetBaseThread extends BaseThread {
    protected int port;
    public boolean startWork(int port)
    {
        this.port=port;
       return super.startWork();
    }


}
