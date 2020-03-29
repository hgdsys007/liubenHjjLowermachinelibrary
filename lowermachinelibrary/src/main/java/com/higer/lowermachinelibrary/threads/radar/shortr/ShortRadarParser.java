package com.higer.lowermachinelibrary.threads.radar.shortr;

import com.higer.lowermachinelibrary.functionBlock.imp.matcher.Hgccom2Matcher;
import com.higer.lowermachinelibrary.threads.BaseThread;
import com.higer.lowermachinelibrary.virtualIo.VirtualCom3Io;

public final class ShortRadarParser {
    private boolean isHead = false;
    private final byte HEAD_FIX=(byte)0XE5;
    private final byte END_FIX=(byte)0XFE;
    private final Object lock = new Object();
    private final int BUFFER_LEN = 128;

    byte[] recvCmd = new byte[BUFFER_LEN];
    int iPos = 0;
    int recvId = 0;
    int recvLen = 0;
    private byte minId = 1;
    private byte maxId = 16;
    private byte currentId = 1;
    private ShortRadarSender shortRadarSender=null;

    public void startParser()
    {
        stopParser();
        shortRadarSender=new ShortRadarSender();
        shortRadarSender.startWork();
    }
    public void stopParser()
    {
        if(shortRadarSender!=null)
        {
            if(shortRadarSender.isWork())
            {
                shortRadarSender.stopWork();
            }
            shortRadarSender=null;
        }
    }
    public void setIdRage(byte min, byte max)//设置 ID 范围
    {
        minId = min;
        maxId = max;
        currentId = minId;
    }
    public void parseShortLeida(byte[] buffer, int iStart,int iEndPos) {
        //  System.out.println("-------------"+StringHexUtil.ArraytoHexString(buffer,iCount));
        if ((iPos + iEndPos-iStart) > BUFFER_LEN) {
            iPos = 0;
            return;
        }

        for (int i = iStart; i < iEndPos; i++) {
            recvCmd[iPos++] = buffer[i];

            if (isHead)//找到协议头
            {
                if (recvCmd[iPos - 1] == END_FIX) {
                    if (iPos == 6)//长度是6  定长协议
                    {
                        recvId = recvCmd[3];
                        recvLen = recvCmd[4];

//                        if(recvId==16)
//                        {
//                            System.out.println("-----ID=" + recvId + "     LEN=" + recvLen);
//                        }

                        String printStr="ID="+recvId+"     len="+recvLen;
                        System.out.println(printStr);
                        //   System.out.println("-----ID=" + recvId + "     LEN=" + recvLen);

//                        sendToUi(printStr);//发送到 UI 界面
//                        if(recvId==16)
//                        {
//                            sendToUi("clear");
//                        }
//
                        sendCmd();//发送下一个探头的 数据请求 指令

                        isHead=false;
                        iPos=0;
                    }
                }
            } else {
                if (iPos > 1) {
                    if ((recvCmd[iPos - 2] == HEAD_FIX) && (recvCmd[iPos - 1] == HEAD_FIX)) {
                        isHead = true;
                        recvCmd[0] = (byte) 0xe5;
                        recvCmd[1] = (byte) 0xe5;
                        iPos = 2;
                        continue;
                    }
                }
            }

        }

    }



    private void sendCmd() {
        synchronized (lock) {
            lock.notifyAll();
        }
    }


    private final class ShortRadarSender extends BaseThread {
        // AA AA A5 0D AF
        private byte[] cmd = {(byte) 0xaa, (byte) 0xaa, (byte) 0xa5, 0x10, (byte) 0xaf};

        @Override
        public void run() {
            try {
                while (isWork()) {
                    synchronized (lock) {
                        lock.wait(200);
                    }

                    //      sleep(50);
                    VirtualCom3Io.getInstance().write(getCmd());

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception m) {

            }
        }

        private byte[] getCmd()//查询指令
        {
            if (currentId > maxId) {
                currentId = minId;
            }

            cmd[3] = currentId;
            currentId++;

            return cmd;
        }


    }

}
