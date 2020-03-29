package com.higer.lowermachinelibrary.threads.mcuUpgrade;

import com.higer.lowermachinelibrary.log.Logger;
import com.higer.lowermachinelibrary.utils.CmdUtil;
import com.higer.lowermachinelibrary.utils.CrcUtil;
import com.higer.lowermachinelibrary.utils.StringHexUtil;
import com.higer.lowermachinelibrary.virtualIo.VirtualMcuUpIo;

import java.util.ArrayList;
import java.util.List;


// 下位机程序升级虚拟通道
final public class MucUpThread extends Thread {
    private static final String startUpCmd = "$HG:SStartUpgrade{0}\r";//开始升级指令
   private static final String stopUpCmd = "$HG:SStartUpgrade{1}\r";//升级结束指令
    private static final String resOkCmd = "$HG:CStartUpgrade{OK}\r";//升级结束指令
    private byte[] stopCmd=new byte[26];//升级结束指令


    private final int MAX_PACK=1024;//发送一个包的最大字节数
    private List<byte[]> dataList=new ArrayList<>();//升级的数据



    private boolean isRun = false;


    /**
     * 更新下位机程序
     * @param mcuUpData  程序内容
     */
    public void update(byte[] mcuUpData,int iLen)
    {

        //  $HG:SStartUpgrade{1}
        int crc= CrcUtil.sum_crc(mcuUpData,iLen);
        stopCmd[0]='$';
        stopCmd[1]='H';
        stopCmd[2]='G';
        stopCmd[3]=':';
        stopCmd[4]='S';
        stopCmd[5]='S';
        stopCmd[6]='t';
        stopCmd[7]='a';
        stopCmd[8]='r';
        stopCmd[9]='t';
        stopCmd[10]='U';
        stopCmd[11]='p';
        stopCmd[12]='g';
        stopCmd[13]='r';
        stopCmd[14]='a';
        stopCmd[15]='d';
        stopCmd[16]='e';
        stopCmd[17]='{';

        stopCmd[18]=(byte)((iLen>>24)&0xff);
        stopCmd[19]=(byte)((iLen>>16)&0xff);
        stopCmd[20]=(byte)((iLen>>8)&0xff);
        stopCmd[21]=(byte)((iLen)&0xff);
        stopCmd[22]=(byte)((crc>>8)&0xff);
        stopCmd[23]=(byte)((crc)&0xff);

        stopCmd[24]='}';
        stopCmd[25]=0x0d;








       // int iLen=mcuUpData.length;
        int iYs=iLen%MAX_PACK;
        int iFrameCount=iLen/MAX_PACK;

        int iPos=0;
        for (int i = 0; i < iFrameCount; i++) {
            byte[] item=new byte[MAX_PACK];
            System.arraycopy(mcuUpData,iPos,item,0,MAX_PACK);
            iPos+=MAX_PACK;
            dataList.add(item);
        }

        if(iYs>0)
        {
            byte[] item=new byte[iYs];
            System.arraycopy(mcuUpData,iPos,item,0,iYs);
            iPos+=iYs;
            dataList.add(item);
        }
    }

    public boolean isRun() {
        return isRun;
    }
    public boolean startWork()
    {
        isRun=true;
        start();
        return true;
    }

    public boolean stopWork()
    {
        isRun=false;
        interrupt();
        try {
            this.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void run() {
        Logger.writeLog("=============启动MucUpThread  下位机升级线程");
        if(dataList.size()==0)
        {
            isRun=false;
            return;
        }
        VirtualMcuUpIo io = VirtualMcuUpIo.getInstance();
        io.clearBuffer();
        int iStep = 0;
        try {
            while (isRun) {
                switch (iStep) {
                    case 0://开始升级 发送升级指令
                        Logger.writeLog("发送升级指令 ".concat(startUpCmd));
                        io.write(startUpCmd);
                        Thread.sleep(2000);


                        String sRead="";
                        while (!(sRead=io.read()).isEmpty())
                        {
                            if(sRead.equals(resOkCmd))
                            {
                                Logger.writeLog("接收到升级指令返回值  ".concat(resOkCmd));
                                iStep++;
                                continue;
                            }
                        }



                        break;
                    case 1:// 升级过程
                        int iNumber=dataList.size();
                        String okRes="";
                        for (int i = 0; i <iNumber ; ) {
                            okRes=String.valueOf((i<<8)+1);
                            System.out.println("写数据第"+i+"帧------------------------------"+i);
                            io.write(CmdUtil.mekeMcuUpCmd(i,dataList.get(i)));
                            Thread.sleep(200);

                            sRead="";
                           while (!(sRead=io.read()).isEmpty())
                           {
                               System.out.println("RECV_-------"+sRead);
                               if(sRead.equals(okRes))//写入成功
                               {
                                 //  Logger.writeLog("写入第"+i+"帧成功");
                                   System.out.println("写入第"+i+"帧成功"+sRead);
                                   io.clearBuffer();
                                   i++;
                                   break;
                               }else {
                                 //  Logger.writeLog("写入第"+i+"帧失败");
                                   System.out.println("写入第"+i+"帧失败");
                               }
                           }
                        }

                        iStep++;
                        break;
                    case 2://升级结束 发送结束指令
                      //  Logger.writeLog("发送结束指令");
                        System.out.println("发送结束指令");
                     io.write(stopCmd);
                    //    io.write(stopUpCmd.getBytes());
                        System.out.println(StringHexUtil.ArraytoHexString(stopCmd));
                        Thread.sleep(10000);



                         sRead="";
                        while (!(sRead=io.read()).isEmpty())
                        {
                            System.out.println(sRead);
                            if(sRead.equals(resOkCmd))
                            {
                                Logger.writeLog("接收到结束指令返回值  ".concat(resOkCmd));
                                iStep++;
                                break;
                            }
                        }

                        break;
                    case 3:
                        isRun=false;
                        break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Logger.writeLog("=============退出MucUpThread  下位机升级线程");
    }
}
