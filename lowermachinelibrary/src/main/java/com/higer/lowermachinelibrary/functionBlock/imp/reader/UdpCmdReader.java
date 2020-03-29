package com.higer.lowermachinelibrary.functionBlock.imp.reader;

import com.higer.lowermachinelibrary.log.Logger;
import com.higer.lowermachinelibrary.statics.Config;
import com.higer.lowermachinelibrary.statics.TcpContext;
import com.higer.lowermachinelibrary.threads.udp.UdpDataThread;
import com.higer.lowermachinelibrary.threads.udp.UdpServer;
import com.higer.lowermachinelibrary.utils.StringHexUtil;

final public class UdpCmdReader {
    private static final int ONCE_NOTIFY_COUNT=1;//4  当接收到 四条数据后  处理线程处理一次
    private UdpDataThread udpDataThread = null;
    private byte[] cmd = null;
    private final int CMD_ASCII = 1;//$开头 0d 结束的 ASCII 指令 控制指令
    private final int CMD_GPIO = 2;
    private final int CMD_HGC = 3;//新下位机板(6代) 协议头
    private int cmdLen = 0;//HGC--新下位机 指令长度
    private boolean isGetCmdLen=false;
    private int cmdType = 0;
    int iPos;
    boolean isHead;

    public void init()
    {
        cmd = new byte[Config.BufferLen];
        iPos = 0;
        isHead = false;
        isGetCmdLen=false;
        startTcpDataThread();
        Logger.writeLog("UdpCmdReader 初始化成功");
    }
    public void release()
    {
        stopTcpDataThread();
        Logger.writeLog("UdpCmdReader 释放成功");
    }
    public void doRead(byte[] buffer,int iLen)
    {

 //   System.out.println(StringHexUtil.ArraytoAsciiString(buffer, 0, iLen));
        if (Config.isDetialLog) {
            Logger.writeLog(StringHexUtil.ArraytoAsciiString(buffer, 0, iLen));
        }

      //  Logger.writeLog("LEVEL1  Recv  ASCII  UdpCmdReader:doRead  iCount="+iLen+"   "+ StringHexUtil.ArraytoAsciiString(buffer,0,iLen));
    //    Logger.writeLog("LEVEL1  Recv  二进制  UdpCmdReader:doRead  iCount="+iLen+"   "+StringHexUtil.ArraytoHexString(buffer,0,iLen));
     //   System.out.println("mmmmm---  "+StringHexUtil.ArraytoHexString(cmd,0,iLen/2));
 //   System.out.println("mmmmm   "+StringHexUtil.ArraytoAsciiString(cmd,0,iLen));
        for (int i = 0; i < iLen; i++) {
            if (iPos >=Config.BufferLen) {
                //  if(Config.logLevel<2)//1级
                //   {
                Logger.writeLog("LEVEL1  Recv  ASCII  UdpCmdReader:doRead  iCount="+iPos+"   "+ StringHexUtil.ArraytoAsciiString(cmd,0,Config.BufferLen));
                Logger.writeLog("LEVEL1  Recv  二进制  UdpCmdReader:doRead  iCount="+iPos+"   "+StringHexUtil.ArraytoHexString(cmd,Config.BufferLen));
                //  }
                iPos = 0;
                isHead=false;
                isGetCmdLen=false;
            }

            cmd[iPos++] = buffer[i];
            if (isHead)//找到协议头
            {
                switch (cmdType) {
                    case CMD_GPIO:
                        if (((cmd[iPos - 1] & 0xff) == 0X64) && ((cmd[iPos - 2] & 0xff) == 0X6E) && ((cmd[iPos - 3] & 0xff) == 0X65))//end  找到协议尾
                        {
                            mcuResurrectionFunction();//单片机 自救
                            sendDataToProcessor(cmd,iPos);

                            isHead=false;
                            iPos=0;
                            continue;
                        }
                        break;
                    case CMD_HGC:
                        if(isGetCmdLen)
                        {
                            if (iPos==cmdLen) // 读取到一条完整指令的长度
                            {
                      //    System.out.println("nnnnnnnnnnnnnnnnnnnnnn               "+StringHexUtil.ArraytoAsciiString(cmd,0,iPos));
                                sendDataToProcessor(cmd,iPos);
                                isHead=false;
                                iPos=0;
                                isGetCmdLen=false;
                                continue;
                            }
                        }else {
                            if(iPos>9)//9  10  两位表示 数据长度
                            {
                               // String sHeader=StringHexUtil.ArraytoAsciiString(cmd,0,9);
                                if((cmd[3]=='U')&&(cmd[4]=='p')&&(cmd[5]=='g')&&(cmd[6]=='r')&&(cmd[7]=='a')&&(cmd[8]=='d'))
                                {
                                    if(iPos>12)
                                    {
                                        cmdLen=((cmd[11]&0xff)<<8)+(cmd[12]&0xff)+13;
                                        isGetCmdLen=true;
                                    }
                                }else{
                                    cmdLen=((cmd[8]&0xff)<<8)+(cmd[9]&0xff)+10;
                                    isGetCmdLen=true;
                                }
                            }
                        }
                        break;
                    case CMD_ASCII:
                        //$HG:CStartUpgrade{OK}
                        //24 48 47 3A 43 53 74 61 72 74 55 70 67 72 61 64 65 7B 4F 4B 7D 0D
                        if (((cmd[iPos - 1] & 0xff) == 0X0D) && ((cmd[iPos - 2] & 0xff) == 0X7D))//找到协议尾
                        {

                     //     System.out.println("Recv Ascii ---------------------- "+StringHexUtil.ArraytoAsciiString(cmd,0,iPos));

                            sendDataToProcessor(cmd,iPos);

                            isHead=false;
                            iPos=0;
                            continue;
                        }
                        break;
                }
            } else {//没有找到协议头
                if (iPos >= 3) {
                    if (((cmd[iPos - 3] & 0xff) == 0x49) && ((cmd[iPos - 2] & 0xff) == 0x4e) && ((cmd[iPos - 1] & 0xff) == 0x44))//IND  49 4E 44
                    {
                        iPos = 0;
                        cmd[iPos++] = (byte) 0x49;
                        cmd[iPos++] = (byte) 0x4e;
                        cmd[iPos++] = (byte) 0x44;
                        isHead = true;
                        cmdType = CMD_GPIO;
                        continue;
                    } else if (((cmd[iPos - 3] & 0xff) == 0x48) && ((cmd[iPos - 2] & 0xff) == 0x47) && ((cmd[iPos - 1] & 0xff) == 0x43))//HGC  48 47 43   新下位机板(6代) 协议
                    {
                        iPos = 0;
                        cmd[iPos++] = (byte) 0x48;
                        cmd[iPos++] = (byte) 0x47;
                        cmd[iPos++] = (byte) 0x43;
                        isHead = true;
                        isGetCmdLen=false;
                        cmdType = CMD_HGC;
                        continue;
                    }else if (((cmd[iPos - 3] & 0xff) == 0x24) && ((cmd[iPos - 2] & 0xff) == 0x48) && ((cmd[iPos - 1] & 0xff) == 0x47))//$HG 24 48 47   ASCII 指令 控制指令
                    {
                        //$HG:CStartUpgrade{OK}
                        //24 48 47 3A 43 53 74 61 72 74 55 70 67 72 61 64 65 7B 4F 4B 7D 0D
                        iPos = 0;
                        cmd[iPos++] = (byte) 0x24;
                        cmd[iPos++] = (byte) 0x48;
                        cmd[iPos++] = (byte) 0x47;
                        isHead = true;
                        cmdType =CMD_ASCII;//$开头 0d 结束的 ASCII 指令 控制指令
                        continue;
                    }
                }
            }
        }

    }



    //用于解决 单片机 运行一段时间后 数据不发送的 BUG，我主动断开连接，使单片机 重连
    private void mcuResurrectionFunction()
    {
     //   TcpServer.IND_TIME=System.currentTimeMillis();
        UdpServer.indCounter.add();;
    }




    private void sendDataToProcessor(byte[] buffer,int iCount)
    {
        //将读取到的数据放到队列里面去
        byte[] memBuffer=new byte[iCount];
        System.arraycopy(buffer,0,memBuffer,0,iCount);
        TcpContext.tcpDataList.offer(memBuffer);


        //   System.out.println("MMM  生产 "+TcpContext.tcpDataList.size());
        if (TcpContext.tcpDataList.size()>=ONCE_NOTIFY_COUNT)
        {
            //   System.out.println("MMM  通知消费者 ");
            udpDataThread.doProcess();
        }
    }


    private void startTcpDataThread() {
        stopTcpDataThread();
        udpDataThread = new UdpDataThread();
        udpDataThread.startWork();
    }

    private void stopTcpDataThread() {
        if (udpDataThread != null) {
            if (udpDataThread.isWork()) {
              //  tcpDataThread.stopWork();
                udpDataThread.flagStop();
                udpDataThread.doProcess();
                //   tcpDataThread.interrupt();
            }
            udpDataThread = null;
        }
    }
}
