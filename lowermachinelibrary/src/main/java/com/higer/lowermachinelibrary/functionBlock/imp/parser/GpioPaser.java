package com.higer.lowermachinelibrary.functionBlock.imp.parser;

import com.higer.lowermachinelibrary.entity.HardwareData;
import com.higer.lowermachinelibrary.functionBlock.dataProChannel.IDataPro;
import com.higer.lowermachinelibrary.functionBlock.IParser;
import com.higer.lowermachinelibrary.functionBlock.dataProChannel.imp.DataPro;
import com.higer.lowermachinelibrary.log.Logger;
import com.higer.lowermachinelibrary.statics.ArrayIndex;
import com.higer.lowermachinelibrary.statics.Config;
import com.higer.lowermachinelibrary.utils.StringHexUtil;

import cn.base.entity.VehicleGps;

final public class GpioPaser  implements IParser {
    private HardwareData hardwareData = null;
    private IDataPro dataPro=null;

    public GpioPaser() {
        this.hardwareData = new HardwareData();
        dataPro=new DataPro();
    }



    @Override
    public boolean parse(byte[] cmd, int iLen) {
        return parse(cmd,0,iLen);
    }
    @Override
    public boolean parse(byte[] cmd, int iStart, int iLen) {
        if (Config.logLevel<4)//3级
        {
            Logger.writeLog("LEVEL3   GpioPaser::parse  ".concat(StringHexUtil.ArraytoAsciiString(cmd, 0, iLen)) );
        }

        boolean iRes = false;
        if (hardwareData != null) {
            iRes = hardwareData.parseCmd(cmd,iStart,iLen);
        } else {
            Logger.writeLog("GpioPaser  hardwareData=null");
        }
        if (iRes) {
            if (dataPro!=null)
            {
                // Logger.writeLog("GpioPaser  数据解析成功 传递给 优化器");
                dataPro.doData(hardwareData);
            }else {
                Logger.writeLog("GpioPaser 数据优化器为空");
            }
        }else {
            Logger.writeLog("GpioPaser  数据解析失败");
        }

        return iRes;
    }

    @Override
    public void setDataPro(IDataPro dataPro) {
        this.dataPro=dataPro;
    }

    @Override
    public IDataPro getDataPro() {
        return dataPro;
    }

    @Override
    public VehicleGps getGpsData() {
        return  null;
    }
}
