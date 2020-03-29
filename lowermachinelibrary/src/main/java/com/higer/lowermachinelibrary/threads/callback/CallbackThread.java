package com.higer.lowermachinelibrary.threads.callback;

import com.higer.lowermachinelibrary.log.Logger;
import com.higer.lowermachinelibrary.statics.ArrayIndex;
import com.higer.lowermachinelibrary.statics.Cache;
import com.higer.lowermachinelibrary.statics.CallbackContext;
import com.higer.lowermachinelibrary.threads.BaseThread;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import cn.base.entity.VehicleMcu;

final public class CallbackThread extends BaseThread {
    private final Object lock = new Object();
    @Override
    public void run() {
        Logger.writeLog("=============启动回掉线程进入 run()");
        setName("CallbackThread");
        try {
            //     Queue<VehicleMcu> callbackList = new LinkedBlockingQueue<>();
            Queue<VehicleMcu> callbackList = new ConcurrentLinkedQueue<>();
            while (isWork()) {
                synchronized (lock) {
                    lock.wait();
                    while (!CallbackContext.dataList.isEmpty()) {
                        callbackList.offer(CallbackContext.dataList.poll());
                    }
                }
                if (Cache.mainInterface != null) {
                    while (!callbackList.isEmpty()) {
                        VehicleMcu sendData = callbackList.poll();
                        //      addRawData(sendData);

                        //      Logger.writeLog("CallBack getBackGpsFlag=" + sendData.getGpsHead().getBackGpsFlag());
                        //    System.out.println("CallBack getBackGpsFlag=" + sendData.getGpsHead().getBackGpsFlag());
                        // getBackGpsFlag
                        //      System.out.println("CallBack gpggaTime=" + sendData.getGpsHead().getGpsTime());
//             Logger.writeLog("CallBack gpggaTime1=" + sendData.getGpsHead().getGpsTime()+"   "+sendData.getGpsHead().getGpsJd()+"  "+sendData.getGpsHead().getGpsWd()+"  "+sendData.getGpsHead().getGpsFlag());//-----------------------------GPS  TIME
//                        if(sendData.getGpsTail()!=null)
//                        {
//                            Logger.writeLog("CallBack gpggaTime2=" + sendData.getGpsTail().getGpsTime()+"   "+sendData.getGpsTail().getGpsJd()+"  "+sendData.getGpsTail().getGpsWd()+"   "+sendData.getGpsTail().getGpsFlag());//-----------------------------GPS  TIME
//                        }else {
//                            Logger.writeLog("CallBack gpggaTime2=null");//-----------------------------GPS  TIME
//                        }

                        //                Logger.writeLog("-------------5 "+sendData.getGpsTail().getGpsTime());



                        //     System.out.println("speed="+sendData.getNum()[ArrayIndex.carTic]+"  "+sendData.getNum()[ArrayIndex.motoTic]);

//                      boolean[] ba=sendData.getBool();
//                      String s="";
//                        for (int i = 0; i < ba.length; i++) {
//                            if(ba[i])
//                            {
//                                s=s+"1 ";
//                            }else{
//                                s=s+"0 ";
//                            }
//                        }
//                        System.out.println("------------  "+s);

                        //     System.out.println("---------------------"+System.currentTimeMillis());
                        Cache.mainInterface.notifyVehicleData(sendData);
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Logger.writeLog("CallbackThread:run  InterruptedException  "+e.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog("CallbackThread:run  Exception  "+e.toString());
        }
        Logger.writeLog("============退出回掉线程");
    }



//    //添加行车数据   原始数据
//    private void addRawData(VehicleMcu mcu) {
//        StringBuffer stringBuffer = new StringBuffer();
//        while (!CallbackContext.gpsGpggaRawDataList.isEmpty()) {
//            stringBuffer.append(CallbackContext.gpsGpggaRawDataList.poll()).append("\r\n");
//        }
//        mcu.getGpsHead().setGpgga(stringBuffer.toString());
//
//        stringBuffer = new StringBuffer();
//        while (!CallbackContext.gpsPtnlRawDataList.isEmpty()) {
//            stringBuffer.append(CallbackContext.gpsPtnlRawDataList.poll()).append("\r\n");
//        }
//        mcu.getGpsHead().setPtnl(stringBuffer.toString());
//
//
//        stringBuffer = new StringBuffer();
//        while (!CallbackContext.gpsGphpdRawDataList.isEmpty()) {
//            stringBuffer.append(CallbackContext.gpsGphpdRawDataList.poll()).append("\r\n");
//        }
//        mcu.getGpsHead().setGphpd(stringBuffer.toString());
//
//
//        stringBuffer = new StringBuffer();
//        while (!CallbackContext.indRawDataList.isEmpty()) {
//            stringBuffer.append(CallbackContext.indRawDataList.poll()).append("\r\n");
//        }
//        mcu.setPlc(stringBuffer.toString());
//    }



    public void doProcess() {
        synchronized (lock) {
            lock.notifyAll();
        }
    }
}