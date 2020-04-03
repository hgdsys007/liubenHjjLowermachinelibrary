package com.higer.hjjdemo;

import android.util.Log;

import cn.base.entity.VehicleInput;
import cn.base.entity.VehicleMcu;
import cn.base.entity.VehicleMsg;
import cn.base.iface.main.VehicleMainInterface;
public class ReceiveModule implements VehicleMainInterface {

    @Override
    public boolean notifyVehicleData(VehicleMcu vehicleMcu) {//接受到 GPIO 数据和 GPS 数据
        // Log.i("aaaa","---"+vehicleMcu.getNum()[0]);
        return false;
    }

    @Override
    public boolean notifyVehicleError(VehicleMsg vehicleMsg) {
        if (vehicleMsg!=null)
        {
            Log.i("aaaa","error"+vehicleMsg.getMsg());
        }
        return false;
    }

    @Override
    public boolean notifyVehicleInput(VehicleInput vehicleInput) {
        return false;
    }
}