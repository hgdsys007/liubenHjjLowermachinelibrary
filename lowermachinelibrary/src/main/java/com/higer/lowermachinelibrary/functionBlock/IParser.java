package com.higer.lowermachinelibrary.functionBlock;

import com.higer.lowermachinelibrary.functionBlock.dataProChannel.IDataPro;

import cn.base.entity.VehicleGps;

//下位机 GPIO 信号 解析器接口
public interface IParser {
    boolean parse(byte[] cmd,int iLen);//解析一条指令
    boolean parse(byte[] cmd,int iStart,int iLen);//解析一条指令
    VehicleGps getGpsData();
    void setDataPro(IDataPro dataPro);//设置 数据优化器
    IDataPro getDataPro();
}
