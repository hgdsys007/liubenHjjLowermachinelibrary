package com.higer.lowermachinelibrary.functionBlock.dataProChannel;

import cn.base.entity.VehicleMcu;

public interface IDataChannel {
    VehicleMcu doDataPro(VehicleMcu raw);
    VehicleMcu doNet(VehicleMcu raw);

}
