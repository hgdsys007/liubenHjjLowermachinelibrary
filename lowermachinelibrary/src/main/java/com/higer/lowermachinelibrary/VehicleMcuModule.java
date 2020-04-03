package com.higer.lowermachinelibrary;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.higer.lowermachinelibrary.adas.AdasWriter;
import com.higer.lowermachinelibrary.log.LogThread;
import com.higer.lowermachinelibrary.log.Logger;
import com.higer.lowermachinelibrary.statics.Cache;
import com.higer.lowermachinelibrary.statics.CallbackContext;
import com.higer.lowermachinelibrary.statics.Config;
import com.higer.lowermachinelibrary.threads.bluetooth.BtThread;
import com.higer.lowermachinelibrary.threads.callback.CallbackThread;
import com.higer.lowermachinelibrary.threads.mcuUpgrade.MucUpThread;
import com.higer.lowermachinelibrary.threads.tcp.TcpServer;
import com.higer.lowermachinelibrary.threads.udp.UdpServer;
import com.higer.lowermachinelibrary.utils.CmdUtil;
import com.higer.lowermachinelibrary.utils.ErrorMessageUtil;
import com.higer.lowermachinelibrary.utils.FileUtil;
import com.higer.lowermachinelibrary.utils.StringHexUtil;
import com.higer.lowermachinelibrary.virtualIo.VirtualMcuUpIo;
import com.higer.lowermachinelibrary.virtualIo.VirtualTcpOutput;

import java.io.File;
import java.util.Map;
import java.util.Set;

import cn.base.entity.VehicleMcuControl;
import cn.base.entity.VehicleMsg;
import cn.base.iface.gps.VehicleGpsModuleInterface;
import cn.base.iface.main.VehicleMainInterface;
import cn.base.iface.main.VehicleMainLog;
import cn.base.iface.mcu.VehicleMcuModuleInterface;
import cn.base.util.BaseInfoUtil;

import static java.lang.Thread.sleep;

final public class VehicleMcuModule implements VehicleMcuModuleInterface {


    private boolean isBt=false;
    private LogThread logThread = null;
    private UdpServer udpServer = null;
    private TcpServer tcpServer=null;
    private CallbackThread callbackThread = null;//回调线程
    private BtThread btThread = null;
   // private VehicleMcuControl sfzZwyControlData=null;
    private boolean isHaveLoger = false;
    private   MucUpThread upThread = null;





    //初始化接口
    @Override
    public boolean doMcuInit(final byte[] bytes, final Map<String, String> map) {


        Config.isDetialLog = this.isExitFile("/mnt/sdcard/liuLib/abc.txt");

       // doMcuExit();
        doMcuInitFunc(bytes,map);
        return true;
    }





    public boolean doMcuInitFunc(byte[] bytes, Map<String, String> map) {
        if (!isHaveLoger) {
            startMyLogger();//启动内部 LOG 写入器
            Logger.writeLog("加载内置LOG写入器");
        }

        Logger.writeLog("map---" + map.toString());

        if (!BaseInfoUtil.getVer().equals(Config.BASE_JAR_VERSION)) {
            Logger.writeLog("base.jar  版本不匹配   jarVersion=" + BaseInfoUtil.getVer() + "   SetVersion=" + Config.BASE_JAR_VERSION);

            VehicleMsg msg = new VehicleMsg();
            msg.setCode(2);
            msg.setMsg("base.jar  版本不匹配");
            ErrorMessageUtil.getInstance().notifyError(msg);

            return false;
        }

        if (map != null) {
            String logValue = (String) map.get("LOG");
            Config.logLevel = 100;
            if (logValue != null) {
                if (logValue.trim().toUpperCase().equals("ON")) {
                    Config.logLevel = 3;//3
                    Config.logLevel = 100;//3
                }
            }

            String value = (String) map.get("AngleDelay");//gps角度延迟定位点的点数 xy数组向前推几个+1点（3表示4个点 0表示1个点  -1表示当前点）
            if (value != null) {
                Config.delayPoints = 1 + Integer.parseInt(value);
            }


            value = (String) map.get("McuData");//0从网络获取下位机数据  1通过串口获取下位机数据 2通过蓝牙获取下位机数据
            if (value != null) {
                if (value.equals("0"))//网络获
                {
               //     startUdpServer();
                  startTcpServer();
                }  else if (value.equals("2"))//通过蓝牙获取下位机数据
                {
                    if (startBluetooth()) {
                        if (startCallBackThread()) {
                            //  Logger.writeLog("回调线程启动成功");
                            isBt=true;
                        } else {
                            Logger.writeLog("回调线程启动失败");
                            stopBluetooth();
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }
        }

        Logger.writeLog("----------------------启动Jar包--------------------------------------------");
        return true;
    }




    //向下传输，给单片机指令
//private int type;//0表示场地盒控制 1表示LED灯控制  2表示GPS差分数据
//    private byte[] data;//数据

    //    type=4
//    data中数据为逗号分隔的字符串
//    类似0,0
//    第一位标示是不是使用身份证 第二位标示是不是指纹仪
//0表示不启用 1表示启用
    @Override
    public boolean doMcuControl(VehicleMcuControl vehicleMcuControl) {
        byte[] data = vehicleMcuControl.getData();
        switch (vehicleMcuControl.getType()) {
            case 0://0表示场地盒控制
                VirtualTcpOutput.getInstance().wirte(data);
                return true;
            // break;
            case 1://1表示LED灯控制
             //   VirtualTcpOutput.getInstance().wirte(CmdUtil.makeComCmd(CmdUtil.COM3, data));

                return false;
            //   break;
            case 2://2表示GPS差分数据

                if(isBt)
                {
                    if (btThread != null) {
                        btThread.writData(data);
                    }
                }else {
                    VirtualTcpOutput.getInstance().wirte(CmdUtil.makeComCmd(CmdUtil.GPS1, data));
                    VirtualTcpOutput.getInstance().wirte(CmdUtil.makeComCmd(CmdUtil.GPS2, data));
                    return true;
                }
            case 3://下位机升级
                final String serverMcu = StringHexUtil.ArraytoAsciiString(data, 0, data.length);
                Logger.writeLog("服务器McuVersion=" + serverMcu);
                Config.mcuVersion = "";
                new Thread(new Runnable() {
                    byte[] getIdCmdBytes = CmdUtil.makeControlCmd("SQyCfg", "All");//$HG:SQyCfg{All}\r  查询下位机配置
                    @Override
                    public void run() {
                        for (int i = 0; i < 120; i++) {
                            try {
                                VirtualMcuUpIo.getInstance().write(getIdCmdBytes);
                                sleep(1000);
                                if (!Config.mcuVersion.isEmpty()) {
                                    //   System.out.println("下位机版本:"+Config.mcuVersion);
                                    Logger.writeLog("下位机版本=" + Config.mcuVersion);
                                    startUpdateThread(serverMcu, Config.mcuVersion);
                                    break;
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (Exception m) {

                            }
                        }
                    }
                }).start();
                return true;
            case 100://表示下位机配置命令
              //  notifyVehicleError 返回code为100表示配置命令返回，我这边提示一下

                String strParm=StringHexUtil.ArraytoAsciiString(data,0,data.length);
                Logger.writeLog("给下位机下发参数："+strParm);
                //  Config.isSetMcuParmOk=false;
                VirtualTcpOutput.getInstance().wirte(CmdUtil.makeControlCmd("SSetIODly", strParm));//添加启动指令
                break;
            case 10://启动ADAS功能：
                AdasWriter.getInstance().write("".getBytes());
                break;
            case 11://关闭ADAS功能：
                AdasWriter.getInstance().write("".getBytes());
                break;
        }
        return true;
    }





    //信息回掉接口设置--数据给 任海涛
//    private int[] num;// 数字类型
//    private int[] ad;// 霍尔数据
//    private boolean[] bool;// bool 类型
//    private int carState;// stop=0 run=1;back=2;
//    private VehicleGps gpsHead;
//    private VehicleGps gpsHeadAdjust;//纠正的头gps
//    private VehicleGps gpsTail;
//    private VehicleGps gpsTailAdjust;//纠正的尾gps

    /*   public class VehicleMsg {
           private int code;//0表示没有错误
           private String msg;//错误信息*/
    @Override
    public void setMainCallback(VehicleMainInterface vehicleMainInterface) {
        Cache.mainInterface = vehicleMainInterface;
    }



    //GPS 控制句柄 设置  刑凯GPS数据优化用的
    @Override
    public void setGpsCallback(VehicleGpsModuleInterface vehicleGpsModuleInterface) {
        if (vehicleGpsModuleInterface != null) {
            Cache.gpsPro = vehicleGpsModuleInterface;
        }
    }

    //Log 注入
    @Override
    public void setLogCallback(VehicleMainLog vehicleMainLog) {
        Logger.writeLog("加载外部LOG--------------------------------");

        stopMyLogger();
        Logger.setLogger(vehicleMainLog);
        isHaveLoger = true;
        Logger.writeLog("加载外部LOG写入器");
    }


    //开启 内部log写入器
    private boolean startMyLogger() {
        stopMyLogger();
        logThread = new LogThread();
        return logThread.startWork();
    }

    //关闭 内部log写入器
    private boolean stopMyLogger() {
        boolean res = true;
        if (logThread != null) {
            if (logThread.isWork()) {
                res = logThread.stopWork();
            }
        }
        logThread = null;
        return res;
    }



    private boolean startTcpServer() {

        stopTcpServer();
        if (tcpServer == null) {
            tcpServer = new TcpServer();
            return tcpServer.startWork();
        }
        return true;
    }

    private boolean stopTcpServer() {
        boolean res = true;
        if (tcpServer != null) {
            if (tcpServer.isWork()) {
                res = tcpServer.stopWork();
            }
            tcpServer = null;
        }
        return res;
    }


    private boolean startUdpServer() {

        stopUdpServer();
        if (udpServer == null) {
            udpServer = new UdpServer();
            return udpServer.startWork(Config.TcpServerPoint);
        }
        return true;
    }

    private boolean stopUdpServer() {
        boolean res = true;
        if (udpServer != null) {
            if (udpServer.isWork()) {
                res = udpServer.stopWork();
            }
            udpServer = null;
        }
        return res;
    }








    //启动蓝牙
    private boolean startBluetooth() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> devices = adapter.getBondedDevices();
        for (BluetoothDevice btdevice : devices) {
            System.out.println("设备地址——————：" + btdevice.getAddress());
            btThread = new BtThread();
            if (btThread.startWork(btdevice)) {
                return true;
            }
            btThread = null;
        }
        return false;
    }

    //关闭蓝牙
    private boolean stopBluetooth() {
        if (btThread != null) {
            btThread.stopWork();
        }
        btThread = null;
        return true;
    }


    //启动回调线程
    private boolean startCallBackThread() {
        boolean res = false;
        stopCallBackThread();
        if (callbackThread == null) {
            callbackThread = new CallbackThread();
            res = callbackThread.startWork();
        }
        if (res) {
            CallbackContext.setCallbackThreadHandler(callbackThread);
        } else {
            stopCallBackThread();
        }

        return res;
    }

    //关闭回调线程
    private boolean stopCallBackThread() {
        boolean res = true;

        if (callbackThread != null) {
            if (callbackThread.isWork()) {
                callbackThread.flagStop();
                callbackThread.doProcess();
                callbackThread.interrupt();
                res = callbackThread.stopWork();
            }
        }
        CallbackContext.setCallbackThreadHandler(null);
        callbackThread = null;
        return res;
    }










    //参数   从下位机读取的版本号
    public boolean startUpdateThread(String serverVersion, String gkjVersion) {
        if (null == gkjVersion || "".equals(gkjVersion))
            return false;
        stopUpThread();
       String fullPath = Config.gkjAppSaveDir + "/" + com.higer.lowermachinelibrary.utils.Base64.encode(gkjVersion.getBytes()) + ".hex";
        if (!serverVersion.equals(fullPath)) {
            Logger.writeLog("服务器版本和下位机版本不一致，进行升级操作");
            upThread = new MucUpThread();
            byte[] dataArray = FileUtil.hexToBinFile(serverVersion);
            try {
                if (dataArray != null) {
                    upThread.update(dataArray, dataArray.length);
                    return upThread.startWork();
                }
            } catch (Exception e) {
                Logger.writeLog("升级Exception:" + e.toString());
            }
        } else {
            Logger.writeLog("服务器版本和下位机版本一致，不升级");
        }
        return false;
    }

    private boolean stopUpThread() {
        if (upThread != null) {
            if (upThread.isRun()) {
                upThread.stopWork();
            }
        }
        upThread = null;
        return true;
    }

    private boolean isExitFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            file = null;
            return true;
        } else {
            file = null;
            return false;
        }
    }
    //资源释放
    @Override
    public boolean doMcuExit() {
        boolean res = true;
        res = res && stopCallBackThread();
        res = res && stopUdpServer();
        res = res && stopBluetooth();
        res = res && stopMyLogger();
        res = res && stopUpThread();
        res = res && stopTcpServer();
        return res;
    }
}
