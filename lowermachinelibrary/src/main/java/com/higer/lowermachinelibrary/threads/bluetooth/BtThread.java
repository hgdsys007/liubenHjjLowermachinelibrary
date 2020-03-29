package com.higer.lowermachinelibrary.threads.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.higer.lowermachinelibrary.entity.HardwareData;
import com.higer.lowermachinelibrary.functionBlock.IMatcher;
import com.higer.lowermachinelibrary.functionBlock.IReader;
import com.higer.lowermachinelibrary.functionBlock.imp.matcher.GpsBanMatcher;
import com.higer.lowermachinelibrary.log.Logger;
import com.higer.lowermachinelibrary.statics.Cache;
import com.higer.lowermachinelibrary.statics.CallbackContext;
import com.higer.lowermachinelibrary.statics.Config;
import com.higer.lowermachinelibrary.statics.HeadGpsContext;
import com.higer.lowermachinelibrary.threads.callback.CallbackThread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import cn.base.entity.VehicleGps;
import cn.base.entity.VehicleMcu;
import cn.base.entity.VehicleMsg;

final public class BtThread extends Thread {
    BluetoothDevice m_btDevice = null;
    BluetoothSocket mmSocket = null;
    InputStream mmIn = null;
    OutputStream mout = null;
    private IReader gpsReader = null;
    private IMatcher matcher=null;

//    public BtThread(BluetoothDevice btDevice) {
//        if (btDevice != null) {
//            this.m_btDevice = btDevice;
//
//            BluetoothSocket tmp = null;
//            try {
//                String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
//                UUID u = UUID.fromString(SPP_UUID);
//                tmp = btDevice.createRfcommSocketToServiceRecord(u);// new
//            } catch (IOException e) {
//                Logger.writeLog("BtThread类构造方法抛出异常1：" + e.getMessage());
//            }
//            mmSocket = tmp;
//        }
//    }

    public boolean stopWork() {
        this.interrupt();
        return true;
    }

    public boolean startWork(BluetoothDevice btDevice) {
        if (btDevice != null) {
            this.m_btDevice = btDevice;

            BluetoothSocket tmp = null;
            try {
                String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
                UUID u = UUID.fromString(SPP_UUID);
                tmp = btDevice.createRfcommSocketToServiceRecord(u);// new
            } catch (IOException e) {
                Logger.writeLog("BtThread::startWork(BluetoothDevice btDevice) 抛出异常1：" + e.getMessage());
                return false;
            }
            mmSocket = tmp;
        }


        this.start();
        return true;
    }

    //写差分数据
    public void writData(byte[] dataArray) {
        try {
            if (mout != null) {
                mout.write(dataArray);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void notifyError(String sMsg) {
        if (Cache.mainInterface != null) {
            VehicleMsg msg = new VehicleMsg();
            msg.setCode(103);
            msg.setMsg(sMsg);
            Cache.mainInterface.notifyVehicleError(msg);
        }
    }

    public void run() {
        Logger.writeLog("启动BtThread");
        //---------连接蓝牙  连接10次如果连接不上 就退出

        HelpeThread helpeThread = null;
        int iStep = 1;

        int bytes = 0;

        byte[] buffer=new byte[Config.UartCmdLen];
        int iRead=0;
        while (!Thread.currentThread().isInterrupted()) {

            if (iStep == 2) {
                ///////////////进行读写操作///////////////////////
                try {
                    //  bytes = mmIn.read(buffer);
                    bytes=mmIn.read(buffer);
                   // bytes = gpsReader.read();

                    if (bytes < 0) {
                        sleep(80);
                    } else if (bytes == 0) {
                        sleep(20);
                    }else {
                        matcher.parseBuffer(buffer,bytes);
                    }
                } catch (IOException e) {
                    Logger.writeLog("蓝牙意外断开");
                    notifyError("蓝牙意外断开");
                    iStep = 1;

                    if (helpeThread != null) {
                        helpeThread.interrupt();//停止辅助线程
                        helpeThread = null;
                    }

                    //蓝牙意外断开
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (iStep == 1) {

                ////////////---------------------------------------------------------测试代码 去掉
//                {
//                    matcher=new GpsBanMatcher();//设置匹配器
//                    ((GpsBanMatcher) matcher).setGpsContext(HeadGpsContext.getInstance());
//                    for (int i = 0; i < 20; i++) {
//                        try {
//                           HeadGpsContext.getInstance().setGps(new VehicleGps());
//                            sleep(500);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
                //---------------------------------------------------------


                boolean isConnect = false;
                for (int i = 1; i <= 10; i++) {
                    try {
                        mmSocket.connect();
                        Logger.writeLog("蓝牙连接成功");
                        isConnect = true;
                        mmIn = mmSocket.getInputStream();
                        mout = mmSocket.getOutputStream();
                        break;
                    } catch (IOException e) {
                        Logger.writeLog("第" + i + "次连接蓝牙失败");
                        e.printStackTrace();
                    }
                }
                if (!isConnect) {
                    Logger.writeLog("BtThread连接蓝牙设备10次没有连接成功");
                    notifyError("BtThread连接蓝牙设备10次没有连接成功");
                    return;
                }

                matcher=new GpsBanMatcher();//设置匹配器
               ((GpsBanMatcher) matcher).setGpsContext(HeadGpsContext.getInstance());

                if (helpeThread != null) {
                    helpeThread.interrupt();//停止辅助线程
                    helpeThread = null;
                }
                helpeThread = new HelpeThread();  //将数据放到全局队列中去  回掉线程会去调用传递给任海涛
                helpeThread.start();

                iStep = 2;
            }
        }


        if (helpeThread != null) {
            helpeThread.interrupt();//停止辅助线程
            helpeThread = null;
        }

        Logger.writeLog("退出BtThread");
    }


    //将数据放到全局队列中去  回掉线程会去调用传递给任海涛
    class HelpeThread extends Thread {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    sleep(200);
                    HardwareData vehicleMcu = new HardwareData();
                    // writeLog("------------------获取Gps实体 1");
                    VehicleGps gps = HeadGpsContext.getInstance().getGps();
                    vehicleMcu.setGpsHead(gps);//这里将  GPS 线程解析后的数据 放到 回调 实体里面去

                //    System.out.println("------------------------"+vehicleMcu.getGpsHead().getGpsTime());

                    if (!CallbackContext.dataList.offer(vehicleMcu)) {//将处理后是数据 放到全局队列   CallbackThread 回调线程会到里面去取数据 发送给任海涛
                        Logger.writeLog("队列添加失败--队列已满 " + CallbackContext.dataList.size());
                    }
                    CallbackThread callbackThread=CallbackContext.getCallbackThreadHandler();
                    if(callbackThread!=null)
                    {
                        callbackThread.doProcess();//通知回掉线程 去调用任海涛
                   //     System.out.println("------------------------doProcess");
                    }else {
                //        System.out.println("------------------------callbackThread==null");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
