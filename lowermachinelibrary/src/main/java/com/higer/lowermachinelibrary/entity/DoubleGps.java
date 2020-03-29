package com.higer.lowermachinelibrary.entity;

import cn.base.entity.VehicleGps;

final public class DoubleGps {
    private VehicleGps headGps=null;
    private VehicleGps backGps=null;

    public DoubleGps(VehicleGps headGps, VehicleGps backGps) {
        this.headGps = headGps;
        this.backGps = backGps;
    }

    public VehicleGps getHeadGps() {
        return headGps;
    }

    public void setHeadGps(VehicleGps headGps) {
        this.headGps = headGps;
    }

    public VehicleGps getBackGps() {
        return backGps;
    }

    public void setBackGps(VehicleGps backGps) {
        this.backGps = backGps;
    }
}
