package com.higer.lowermachinelibrary.utils;

import com.higer.lowermachinelibrary.log.Logger;
import com.higer.lowermachinelibrary.statics.Cache;

import cn.base.entity.VehicleInput;
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


    public synchronized void notifyMsg(VehicleInput msg)
    {
        if (Cache.mainInterface != null) {
            Cache.mainInterface.notifyVehicleInput(msg);
        }
    }

    public synchronized void notifyError(VehicleMsg msg)
    {
        if (Cache.mainInterface != null) {
            Cache.mainInterface.notifyVehicleError(msg);
        }
    }

    private ErrorMessageUtil() {
    }
}
