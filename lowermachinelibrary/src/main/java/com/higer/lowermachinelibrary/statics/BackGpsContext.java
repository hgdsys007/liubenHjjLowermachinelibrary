package com.higer.lowermachinelibrary.statics;

import com.higer.lowermachinelibrary.functionBlock.imp.gpscontext.BaseGpsContext;


final public class BackGpsContext extends BaseGpsContext {

    //GPS 数据 上下文
    private static BackGpsContext instance=null;
    public static BackGpsContext getInstance()
    {
        if (instance==null)
        {
            instance=new BackGpsContext();
        }
        return instance;
    }

    private BackGpsContext() {}
}
