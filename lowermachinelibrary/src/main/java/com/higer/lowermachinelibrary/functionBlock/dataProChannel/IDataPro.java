package com.higer.lowermachinelibrary.functionBlock.dataProChannel;

import cn.base.entity.VehicleMcu;
import cn.base.iface.gps.VehicleGpsModuleInterface;

//数据处理接口
public interface IDataPro {

    void doData(VehicleMcu vehicleMcu);
  //  boolean controllCallBack(boolean start);
}
