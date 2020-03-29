package com.higer.lowermachinelibrary.functionBlock.imp.parser;

import com.higer.lowermachinelibrary.functionBlock.IParser;
import com.higer.lowermachinelibrary.functionBlock.dataProChannel.IDataPro;
import com.higer.lowermachinelibrary.statics.Config;

import cn.base.entity.VehicleGps;

final public class GpsTeamDataParser implements IParser {
    private IParser gpggaParser = null;
    private IParser heading3aParser=null;
    private IParser gphpdParser=null;
    private IParser ptnlParser=null;

    private byte[] cmd = null;
    //   private byte[] gpsCmd = null;
    private int cmdPos = 0;
    private int iEnd=0;
    private boolean isHead = false;
    private boolean isSecondHead=false;

    public GpsTeamDataParser() {
        isHead = false;
        cmdPos = 0;
        this.cmd = new byte[Config.UartCmdLen];
        isSecondHead=false;
    }
    @Override
    public boolean parse(byte[] cmd, int iLen) {
        return false;
    }

    @Override
    public boolean parse(byte[] cmd, int iStart, int iLen) {
        return false;
    }

    @Override
    public void setDataPro(IDataPro dataPro) {

    }
    @Override
    public VehicleGps getGpsData() {
        return  null;
    }
    @Override
    public IDataPro getDataPro() {
        return null;
    }
}
