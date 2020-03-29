package com.higer.lowermachinelibrary;


import com.higer.lowermachinelibrary.statics.Config;
import com.higer.lowermachinelibrary.utils.CmdUtil;

final public class Subject {
    private static VehicleMcuModule muc=null;
    public static VehicleMcuModule getMuc()
    {
        if (muc==null)
        {
            muc=new VehicleMcuModule();
        }
        return muc;
    }

    public static String getVer()
    {
        return Config.VERSION;
    }


}
