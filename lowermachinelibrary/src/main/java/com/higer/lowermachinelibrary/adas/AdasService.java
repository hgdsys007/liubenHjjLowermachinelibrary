package com.higer.lowermachinelibrary.adas;

import com.higer.lowermachinelibrary.statics.Config;
import com.higer.lowermachinelibrary.utils.CommonUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public final class AdasService extends AdasCmdParser implements QuzhengCmdInterface {
    private Socket socket;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;

    public AdasService(Socket socket) {


        File file = new File(Config.adasMediaPath);
        if (!file.exists()) {
            file.mkdir();
        }

        Config.prefixStr = Config.adasMediaPath + CommonUtil.getRandomString(4) + "_";

        System.out.println(Config.prefixStr);
        this.socket = socket;
    }

    private void setQuzheng() {
        setQuzhengCmdInterface(this);
        AdasWriter.getInstance().setQuzhengCmdInterface(this);
    }

    @Override
    public void run() {
        //  int cmdArrayLen = 15360;
        int cmdArrayLen = 1024 * 65;
        int bufferSize = cmdArrayLen * 2;
        byte[] readArray = new byte[cmdArrayLen];
        byte[] bufferArray = new byte[bufferSize];
        int iBufferPos = 0;

        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            setQuzheng();



            while (isWork()) {
                int iCount = inputStream.read(readArray, 0, cmdArrayLen);//读取数据

           //     System.out.println("icount=" + iCount);
                if (iCount < 0) {
                    //   System.out.println("iCout="+iCount);
                    continue;
                }
                // System.out.println("iCout="+iCount);
                //   System.out.println("2   iBufferPos="+iBufferPos);
                if ((iBufferPos + iCount) > bufferSize) {
                    iBufferPos = 0;
                }

                System.arraycopy(readArray, 0, bufferArray, iBufferPos, iCount);
                iBufferPos += iCount;


                //   System.out.println(ArraytoHexString(bufferArray,iBufferPos));


                if (bufferArray[iBufferPos - 1] == CMD_FLAG)//0X7E;
                {
                    boolean startFlag = false;
                    boolean stopFlag = false;
                    int iStart = 0;
                    int iStop = 0;
                    for (int i = 0; i < iBufferPos; i++) {
                        if ((!startFlag) && (bufferArray[i] == CMD_FLAG)) {
                            startFlag = true;
                            iStart = i;
                            continue;
                        }

                        if (startFlag && (!stopFlag)) {
                            if (bufferArray[i] == CMD_FLAG) {
                                iStop = i;
                                stopFlag = true;
                            }
                        }

                        if (startFlag && stopFlag) {

                            parseCmd(bufferArray, iStart, iStop, outputStream);
                            startFlag = false;
                            stopFlag = false;
                        }
                    }
                    iBufferPos = 0;
                }
            }
        } catch (Exception e) {
            System.out.println("error---" + e.toString());
        }
    }





    @Override
     public void quzhengCmdArray(byte[] cmdArray) {
           System.out.println("ADAS_SERVICE    "+ArraytoHexString(cmdArray));
        if((outputStream!=null)&&(cmdArray!=null))
        {
            try {
                outputStream.write(cmdArray);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


}
