package com.higer.lowermachinelibrary.functionBlock.dataProChannel.imp;

import com.higer.lowermachinelibrary.entity.DoubleGps;
import com.higer.lowermachinelibrary.entity.ReflexEntity;
import com.higer.lowermachinelibrary.log.Logger;
import com.higer.lowermachinelibrary.functionBlock.dataProChannel.IDataPro;
import com.higer.lowermachinelibrary.statics.CallbackContext;
import com.higer.lowermachinelibrary.statics.HeadGpsContext;
import com.higer.lowermachinelibrary.threads.callback.CallbackThread;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import cn.base.entity.VehicleMcu;


final public class DataPro implements IDataPro {
    private List<ReflexEntity> proChannelList = null;
    private DoubleGps gpsDouble = null;
    private int proListSize = 0;
    private boolean isGpsCopy = false;

    public DataPro() {
        proChannelList = new ArrayList<>();
        String methodName = "doDataPro";
        String classNameFlex = "com.higer.lowermachinelibrary.functionBlock.dataProChannel.imp.DataChannel%02d";
        for (int i = 1; i < 100; i++) {
            String s = String.format(classNameFlex, i);
            ReflexEntity item = getMethod(s, methodName);
            if (item == null)
                break;
            proChannelList.add(item);
        }
        proListSize = proChannelList.size();
    }


    @Override
    public void doData(VehicleMcu vehicleMcu) {//GpioPaser  里面调用的 这个函数 进行数据优化
        //数据优化
        for (int i = 0; i < proListSize; i++) {
            vehicleMcu = proChannelList.get(i).doDataPro(vehicleMcu);
        }
        if (vehicleMcu == null) {
            return;
        }

        isGpsCopy = false;
        gpsDouble = HeadGpsContext.getInstance().getListGps();
        do {
            vehicleMcu.setGpsCopy(isGpsCopy);
            isGpsCopy = true;
            if(gpsDouble==null) { ;
                vehicleMcu.setGpsHead(null);//前置 GPS 板---GPS 数据
                vehicleMcu.setGpsTail(null);//后置 GPS 板---GPS 数据
            }else {
                vehicleMcu.setGpsHead(gpsDouble.getHeadGps());//前置 GPS 板---GPS 数据
                vehicleMcu.setGpsTail(gpsDouble.getBackGps());//后置 GPS 板---GPS 数据
            }

//            if ((Cache.gpsPro != null)) {
//                Cache.gpsPro.doGpsAdjust(vehicleMcu);//调用行楷的 GPS 优化
//            }
            if (!CallbackContext.dataList.offer(vehicleMcu.clone())) {//将处理后是数据 放到全局队列   CallbackThread 回调线程会到里面去取数据 发送给任海涛
                Logger.writeLog("队列添加失败--队列已满 " + CallbackContext.dataList.size());
            }
            CallbackThread callbackThread = CallbackContext.getCallbackThreadHandler();
            if (callbackThread != null) {
                callbackThread.doProcess();//通知回掉线程 去调用任海涛
            }
        }while ((gpsDouble = HeadGpsContext.getInstance().getListGps()) != null) ;
    }


    private ReflexEntity getMethod(String className, String methodName) {
        ReflexEntity res = null;
        try {
            Class mClass = Class.forName(className);
            Constructor constructor = mClass.getConstructor();
            Object dataProChannel = constructor.newInstance();
            Method doDataPro = mClass.getDeclaredMethod(methodName, VehicleMcu.class);
            res = new ReflexEntity(dataProChannel, doDataPro);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return res;
    }
}
