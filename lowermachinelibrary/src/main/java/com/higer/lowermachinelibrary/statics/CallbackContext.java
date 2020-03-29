package com.higer.lowermachinelibrary.statics;

import com.higer.lowermachinelibrary.threads.callback.CallbackThread;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import cn.base.entity.VehicleMcu;

final public class CallbackContext {
    public static Queue<VehicleMcu> dataList=new LinkedBlockingQueue<>();

    public static Queue<String> gpsGpggaRawDataList=new LinkedBlockingQueue<>();//存放GPS原始数据--任海涛要，记录行车数据使用  GPGGA
    public static Queue<String> gpsGphpdRawDataList=new LinkedBlockingQueue<>();//存放GPS原始数据--任海涛要，记录行车数据使用  GPHPD
    public static Queue<String> gpsPtnlRawDataList=new LinkedBlockingQueue<>();//存放GPS原始数据--任海涛要，记录行车数据使用   PTNL
    public static Queue<String> indRawDataList=new LinkedBlockingQueue<>();//存放IND原始数据--任海涛要，记录行车数据使用


    private static final Object lock=new Object();
    private static CallbackThread callbackThreadHandler=null;
    public static CallbackThread getCallbackThreadHandler()
    {
        CallbackThread res=null;
        synchronized (lock)
        {
            res=callbackThreadHandler;
        }
        return res;
    }
    public static void setCallbackThreadHandler(CallbackThread handler)
    {
        synchronized (lock)
        {
            callbackThreadHandler=handler;
        }
    }
}
