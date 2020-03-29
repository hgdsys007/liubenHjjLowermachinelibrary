package com.higer.lowermachinelibrary.functionBlock;

import com.higer.lowermachinelibrary.entity.DoubleGps;

import cn.base.entity.VehicleGps;

public interface IGpsContext {
    public  void setListGps(DoubleGps gps);
    public  DoubleGps getListGps();
    public  void setStateGps(VehicleGps gps);
    public  VehicleGps getStateGps();


    public  void setGps(VehicleGps gps);

    public  VehicleGps getGps();

    //#HEADING3A 解析完毕 赋值
    public  void setHeading3A(VehicleGps gps);

    //GPGGA 解析完毕 赋值
    public  void setGpgga(VehicleGps gps);

    //GPHPD 解析完毕 赋值
    public  void setGphpd(VehicleGps gps);

    //PTNL 解析完毕 赋值
    public  void setPtnl(VehicleGps gps);
}
