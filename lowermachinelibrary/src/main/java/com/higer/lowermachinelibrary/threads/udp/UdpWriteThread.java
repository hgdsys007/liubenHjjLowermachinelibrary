package com.higer.lowermachinelibrary.threads.udp;

import com.higer.lowermachinelibrary.log.Logger;
import com.higer.lowermachinelibrary.statics.Config;
import com.higer.lowermachinelibrary.threads.BaseThread;
import com.higer.lowermachinelibrary.utils.CmdUtil;
import com.higer.lowermachinelibrary.utils.StringHexUtil;
import com.higer.lowermachinelibrary.virtualIo.VirtualTcpOutput;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

final public class UdpWriteThread extends BaseThread {
    private DatagramSocket socket = null;


    public boolean startWork(DatagramSocket datagramSocket) {
        this.socket = datagramSocket;
        return super.startWork();
    }

    @Override
    public boolean stopWork() {
        super.flagStop();
        if(socket!=null)
        {
            socket.close();
        }
        return true;
    }

    @Override
    public void run() {
        InetAddress clientAddress = null;
        try {
            clientAddress = InetAddress.getByName(Config.udp_client_ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            Logger.writeLog("UdpWriteThread InetAddress创建失败 ".concat(Config.udp_client_ip));
            return;
        }
        if (clientAddress == null) {
            return;
        }
        Logger.writeLog("UdpWriteThread 启动 "+this.getId());
        VirtualTcpOutput.getInstance().wirte(CmdUtil.makeControlCmd("SControl","1"));//添加启动指令

     DatagramPacket sendPscket = new DatagramPacket(new byte[0],0, clientAddress, Config.udp_client_point);

        while (isWork()) {
            try {
                byte[] sendData = VirtualTcpOutput.getInstance().getData();
                if (sendData != null) {
                    if (Config.logLevel < 2)//1级
                    {
                        Logger.writeLog("LEVEL1  Send  ASCII  UdpWriteThread::run  " + StringHexUtil.ArraytoAsciiString(sendData, 0, sendData.length));
                        Logger.writeLog("LEVEL1  Send  二进制  UdpWriteThread::run  " + StringHexUtil.ArraytoHexString(sendData, sendData.length));
                    }
                    //发送数据
         //    DatagramPacket sendPscket = new DatagramPacket(sendData, sendData.length, clientAddress, Config.udp_client_point);
                    sendPscket.setData(sendData);

              //      System.out.println("send------------ "+StringHexUtil.ArraytoAsciiString(sendData,0,sendData.length));

                   // sendPscket.setLength(sendData.length);


                    if (socket != null) {
                        try {
                            socket.send(sendPscket);
                            Thread.sleep(5);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Logger.writeLog("UdpWriteThread::run IOException UDP写入失败");
                            if(!isWork())
                            {
                                break;
                            }
                        }
                    }
                } else {
                 //   System.out.println("UdpWriteThread  11111");
                    Thread.sleep(50);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Logger.writeLog("UdpWriteThread 退出 "+this.getId());
    }
}
