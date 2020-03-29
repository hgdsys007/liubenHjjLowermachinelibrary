package com.higer.lowermachinelibrary.statics;

import com.higer.lowermachinelibrary.functionBlock.imp.gpscontext.BaseGpsContext;


final public class HeadGpsContext extends BaseGpsContext {

    //GPS 数据 上下文
    private static HeadGpsContext instance=null;
    public static HeadGpsContext getInstance()
    {
        if (instance==null)
        {
            instance=new HeadGpsContext();
        }
        return instance;
    }

    private HeadGpsContext() {}


}
