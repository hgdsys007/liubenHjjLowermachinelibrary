package com.higer.lowermachinelibrary.threads.udp;

import com.higer.lowermachinelibrary.functionBlock.imp.reader.UdpCmdReader;
import com.higer.lowermachinelibrary.log.Logger;
import com.higer.lowermachinelibrary.statics.Config;
import com.higer.lowermachinelibrary.statics.IndCounter;
import com.higer.lowermachinelibrary.threads.BaseThread;
import com.higer.lowermachinelibrary.utils.CmdUtil;
import com.higer.lowermachinelibrary.virtualIo.VirtualTcpOutput;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UdpServer extends NetBaseThread {

    public static final IndCounter indCounter = new IndCounter();
    private DatagramSocket udpServer = null;
    private UdpCmdReader udpCmdReader = null;
    private HeartBeatThread heartBeatThread = null;

    @Override
    public void run() {
        setName("TcpServer");
        try {
            udpServer = new DatagramSocket(Config.UdpServerPoint);
            byte[] recvBuffer = new byte[Config.BufferLen];
            DatagramPacket receivePacket = new DatagramPacket(recvBuffer, Config.BufferLen);

            udpCmdReader = new UdpCmdReader();//读取器---------------------读
            udpCmdReader.init();

            udpServer.setReceiveBufferSize(10240);
            udpServer.setSendBufferSize(10240);

            //启动写数据 线程
            UdpWriteThread udpWriteThread = new UdpWriteThread();//-----------写
            udpWriteThread.startWork(udpServer);
            startHeartBeatThread();//启动心跳
            while (isWork()) {
                try {
                    receivePacket.setLength(Config.BufferLen);//复位接收packet
                    udpServer.receive(receivePacket);//阻塞
                    InetAddress clientAddress = receivePacket.getAddress();
                    if (clientAddress.getHostAddress().equals(Config.udp_client_ip))//IP 地址匹配
                    {
                        if (udpCmdReader != null) {
                            udpCmdReader.doRead(receivePacket.getData(), receivePacket.getLength());
                        }
                    } else {
                        Logger.writeLog("IP地址不匹配，接收到数据IP地址 " + clientAddress.getHostAddress());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Logger.writeLog("UDP--TcpServer:run() IOException  udpServer.receive(receivePacket)  " + e.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.writeLog("UDP--TcpServer:run() Exception  udpServer.receive(receivePacket)  " + e.toString());
                }
            }

            stopHeartBeatThread();//停止心跳

            if (udpCmdReader != null) {
                udpCmdReader.release();
                udpCmdReader = null;
            }

            if (udpWriteThread != null) {
                if (udpWriteThread.isWork()) {
                    udpWriteThread.stopWork();
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
            Logger.writeLog("UDP--TcpServer:run() SocketException  DatagramSocket udpServer=new DatagramSocket(Config.UdpServerPoint)  " + e.toString());
        }
    }


    public boolean write(byte[] data) {
        VirtualTcpOutput.getInstance().wirte(data);
        return true;
    }


    @Override
    public boolean stopWork() {
        super.flagStop();
        if (udpServer != null) {
            udpServer.close();
        }
        this.interrupt();
        return true;
    }


    private boolean startHeartBeatThread() {
        stopHeartBeatThread();
        if (heartBeatThread == null) {
            heartBeatThread = new HeartBeatThread();
        }

        return heartBeatThread.startWork();
    }

    private boolean stopHeartBeatThread() {
        boolean res = true;
        if (heartBeatThread != null) {
            if (heartBeatThread.isWork()) {
                res = heartBeatThread.stopWork();
            }
            heartBeatThread.interrupt();
        }
        heartBeatThread = null;
        return res;
    }

    private class HeartBeatThread extends BaseThread {
        @Override
        public void run() {
            int iLoop = 0;
            Logger.writeLog("启动心跳");
            while (isWork()) {
                try {
                    sleep(1000);
                    {
                        if (indCounter.getNumber() == 0)//1秒内没有收到任何数据 主动断开
                        {
                            Logger.writeLog("1秒内没有收到任何数据");
                            // $HG:SControl{1}\r
                            VirtualTcpOutput.getInstance().wirte(CmdUtil.makeControlCmd("SControl", "1"));//添加启动指令
                        }
                        indCounter.reset();
                    }


                    if (++iLoop >= 3)//3秒 自动添加心跳
                    {
                        iLoop = 0;
                        if (VirtualTcpOutput.getInstance().isEmpty()) {
                            VirtualTcpOutput.getInstance().wirte(CmdUtil.getHeartCmd());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.writeLog("心跳异常  " + e.getMessage());
                }
            }
            Logger.writeLog("停止心跳");
        }
    }
}
