package com.higer.lowermachinelibrary.entity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cn.base.entity.VehicleMcu;

final public class ReflexEntity {
    private Object obj=null;
    private Method method=null;


    public VehicleMcu doDataPro(VehicleMcu raw) {
        if((obj!=null)&&(method!=null))
        {
            try {
                return (VehicleMcu)method.invoke(obj,raw);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return raw;
    }

    public ReflexEntity(Object obj, Method method) {
        this.obj = obj;
        this.method = method;
    }

//    public Object getObj() {
//        return obj;
//    }
//
//    public void setObj(Object obj) {
//        this.obj = obj;
//    }
//
//    public Method getMethod() {
//        return method;
//    }
//
//    public void setMethod(Method method) {
//        this.method = method;
//    }
}
