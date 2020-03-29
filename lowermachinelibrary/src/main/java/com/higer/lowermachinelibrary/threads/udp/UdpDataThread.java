package com.higer.lowermachinelibrary.threads.udp;

import com.higer.lowermachinelibrary.functionBlock.IMatcher;
import com.higer.lowermachinelibrary.functionBlock.imp.matcher.UdpCmdMatcher;
import com.higer.lowermachinelibrary.log.Logger;
import com.higer.lowermachinelibrary.statics.TcpContext;
import com.higer.lowermachinelibrary.threads.BaseThread;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

final public class UdpDataThread extends BaseThread {
    private Object lock=new Object();
    @Override
    public void run() {
        setName("UdpDataThread");
        Logger.writeLog("UdpDataThread 启动成功");
        IMatcher matcher=new UdpCmdMatcher();
        Queue<byte[]> tempDataList=new LinkedBlockingQueue<>();
        while (isWork()) {
            try {
                synchronized (lock) {
                    lock.wait();
                    while (!TcpContext.tcpDataList.isEmpty()) {
                        ((LinkedBlockingQueue<byte[]>) tempDataList).offer(TcpContext.tcpDataList.poll());
                    }
                }


                while (!tempDataList.isEmpty()) {
                    byte[] res = tempDataList.poll();

                    //  System.out.println("MMMMMMMM  "+ StringHexUtil.ArraytoAsciiString(res,0,res.length));

                    matcher.parseBuffer(res, res.length);
//
//                    if (matcher != null) {
//                        matcher.parseBuffer(res, res.length);
//                    } else {
//                        Logger.writeLog(" UdpDataThread:run()  matcher==null");
//                    }
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        Logger.writeLog("UdpDataThread 关闭成功");
    }

    public void doProcess()
    {
        synchronized (lock)
        {
            lock.notifyAll();
        }
    }
}
