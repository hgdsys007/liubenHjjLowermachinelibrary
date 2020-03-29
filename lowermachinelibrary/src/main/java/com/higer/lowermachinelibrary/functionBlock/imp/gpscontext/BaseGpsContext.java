package com.higer.lowermachinelibrary.functionBlock.imp.gpscontext;

import com.higer.lowermachinelibrary.entity.DoubleGps;
import com.higer.lowermachinelibrary.functionBlock.IGpsContext;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import cn.base.entity.VehicleGps;

 public class BaseGpsContext implements IGpsContext {
    private VehicleGps gps=new VehicleGps();
private Queue<DoubleGps> gpsList=new LinkedBlockingQueue<>();
    @Override
    public void setListGps(DoubleGps gps) {
       gpsList.offer(gps);
    }

    @Override
    public DoubleGps getListGps() {
        if(gpsList.isEmpty())
        {
            return null;
        }
        return gpsList.poll();
    }

    @Override
    public void setStateGps(VehicleGps gps) {
        setGps(gps);
    }

    @Override
    public VehicleGps getStateGps() {
        return getGps();
    }

    @Override
    public synchronized void setGps(VehicleGps gps1)
    {
        gps.setGpsFlag(gps1.getGpsFlag());
        gps.setBackGpsFlag(gps1.getBackGpsFlag());
        gps.setGpsStars(gps1.getGpsStars());
        gps.setCarAngle(gps1.getCarAngle());
        gps.setGpsTime(gps1.getGpsTime());
        gps.setGpsJd(gps1.getGpsJd());
        gps.setGpsWd(gps1.getGpsWd());
        gps.setGpsGd(gps1.getGpsGd());
        gps.setGpsSpeed(gps1.getGpsSpeed());
        gps.setGpgga(gps1.getGpgga());
        gps.setGphpd(gps1.getGphpd());
        gps.setPtnl(gps1.getPtnl());
        gps.setErrorStr(gps1.getErrorStr());
    }
    @Override
    public synchronized VehicleGps getGps()
    {
        if (gps!=null)
        {
            return this.gps.clone();
        }
        return null;
    }

    //#HEADING3A 解析完毕 赋值
    @Override
    public synchronized void setHeading3A(VehicleGps gps)
    {
        //   Logger.writeLog("HeadGpsContext  设置 Heading3A");

        this.gps.setPtnl(gps.getPtnl());
        this.gps.setBackGpsFlag(gps.getBackGpsFlag());
        this.gps.setCarAngle(gps.getCarAngle());

        // this.gps=gps.clone();
    }

    //GPGGA 解析完毕 赋值
    @Override
    public synchronized void setGpgga(VehicleGps gps)
    {
        // Logger.writeLog("HeadGpsContext  设置 GPGGA");
        this.gps.setGpsTime(gps.getGpsTime());
        this.gps.setGpsWd(gps.getGpsWd());
        this.gps.setGpsJd(gps.getGpsJd());
        this.gps.setGpsFlag(gps.getGpsFlag());
        this.gps.setGpsStars(gps.getGpsStars());
        this.gps.setGpsGd(gps.getGpsGd());
    }

    //GPHPD 解析完毕 赋值
    @Override
    public synchronized void setGphpd(VehicleGps gps)
    {
        this.gps.setCarAngle(gps.getCarAngle());
        this.gps.setBackGpsFlag(gps.getBackGpsFlag());
    }

    //PTNL 解析完毕 赋值
    @Override
    public synchronized void setPtnl(VehicleGps gps)
    {
        this.gps.setCarAngle(gps.getCarAngle());
        this.gps.setBackGpsFlag(gps.getBackGpsFlag());
    }
}
