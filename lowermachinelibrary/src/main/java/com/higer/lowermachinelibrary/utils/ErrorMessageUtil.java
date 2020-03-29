package com.higer.lowermachinelibrary.utils;

import com.higer.lowermachinelibrary.log.Logger;
import com.higer.lowermachinelibrary.statics.Cache;

import cn.base.entity.VehicleMsg;

public final class ErrorMessageUtil {
    private static ErrorMessageUtil instance=null;
    public static ErrorMessageUtil getInstance()
    {
        if(instance==null)
        {
            instance=new ErrorMessageUtil();
        }
        return instance;
    }


    public synchronized void notifyMsg(VehicleMsg msg)
    {


        switch (msg.getCode())
        {
            case 3:
                Logger.writeLog("===================================== 身份证打开失败");
               break;
            case 5:
                Logger.writeLog("===================================== 身份证打开成功");
                break;

            case 4:
                Logger.writeLog("===================================== 指纹仪打开失败");
                break;
            case 6:
                Logger.writeLog("===================================== 指纹仪打开成功");
                break;

        }
        /*
        VehicleMsg 其中code为
        1标示播报语音
        2标示显示信息 播报和显示的均为msg的内容
        3标示打开身份证读卡器失败（未插，但是串口可以打开或者其他原因）
        4标示指纹仪无法打开
        5标示身份证打开成功
        6标示指纹仪打开成功
         */
        if (Cache.mainInterface != null) {
            Cache.mainInterface.notifyVehicleError(msg);
        }
    }


    private ErrorMessageUtil() {
    }
}
