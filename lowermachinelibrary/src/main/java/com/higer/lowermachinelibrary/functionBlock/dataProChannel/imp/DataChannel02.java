package com.higer.lowermachinelibrary.functionBlock.dataProChannel.imp;

import com.higer.lowermachinelibrary.functionBlock.dataProChannel.IDataChannel;
import com.higer.lowermachinelibrary.statics.ArrayIndex;
import com.higer.lowermachinelibrary.statics.Config;

import cn.base.entity.VehicleMcu;

//车速优化
final public class DataChannel02 implements IDataChannel {
    private final int wendingTime = 10000;//开机启动前10秒 不评判
    private final int YU_VALUE = 3;//阈值 倍数
    boolean isRun = false;
    boolean isStart = false;
    long lStartTime = 0;
    int speed[] = new int[2];//上一组数据
    int xishu[] = new int[2];//阈值系数
    int yu[] = new int[2];//阈值


    @Override
    public VehicleMcu doDataPro(VehicleMcu raw) {
        return raw;
//        if(Config.IS_NET)
//        {
//            return doNet(raw);//新下位机板
//        }else{
//            return  doCom(raw);//老下位机板
//
//        }
    }
    @Override
    public VehicleMcu doNet(VehicleMcu raw)
    {
        if (raw == null) {
            return raw;
        }
        if (isRun)//启动 运行
        {
            if (Math.abs(raw.getNum()[ArrayIndex.carTic] - speed[0]) > (yu[0] * xishu[0])) {//超出阈值
                xishu[0]=xishu[0]+1;

                raw.getNum()[ArrayIndex.carTic]=speed[0];//使用上一个正常点
            } else {//正常
                yu[0]=Math.abs(raw.getNum()[ArrayIndex.motoTic]-speed[0])*YU_VALUE;//将阈值 设置为 连续两个点的变化值的 YU_VALUE 倍
                speed[0] = raw.getNum()[ArrayIndex.carTic];
                xishu[0] = 1;
            }
//           if (Math.abs(raw.getNum()[ArrayIndex.motoTic] - speed[1]) > (yu[1] * xishu[1])) {//超出阈值
//               xishu[1] = xishu[1]+1;
//
//               raw.getNum()[ArrayIndex.motoTic]=speed[1];//使用上一个正常点
//           } else {//正常
//               yu[1]=Math.abs(raw.getNum()[ArrayIndex.motoTic]-speed[1])*YU_VALUE;//将阈值 设置为 连续两个点的变化值的 YU_VALUE 倍
//               speed[1] = raw.getNum()[ArrayIndex.motoTic];
//               xishu[1] = 1;
//           }
        } else {
//           if (isStart) {
//               long startRunTime = System.currentTimeMillis() - lStartTime;
//               if (startRunTime > wendingTime) {
//                   //  System.out.println("AAAAAAAAAAAAAAA  启动10秒");
//                   isRun = true;
//               }
//           } else {
//               isStart = true;
//               lStartTime = System.currentTimeMillis();
//               //   System.out.println("AAAAAAAAAAAAAAA  启动");
//           }
            isRun = true;

            speed[0] = raw.getNum()[ArrayIndex.carTic];//            carTic=9;  车速霍尔
            //  speed[1] = raw.getNum()[ArrayIndex.motoTic];//           motoTic=10;  马达霍尔

            xishu[0] = 1;
            //   xishu[1] = 1;

            yu[0] = speed[0];//初始化为本身大小
            //  yu[1] = speed[1];//初始化为本身大小
        }
        return raw;
    }


   // @Override
    public VehicleMcu doDataPro1(VehicleMcu raw) {
        if (raw == null) {
            return raw;
        }
        if (isRun)//启动10秒后 稳定 运行
        {
            if (Math.abs(raw.getNum()[ArrayIndex.carTic] - speed[0]) > (yu[0] * xishu[0])) {//超出阈值
                xishu[0]=xishu[0]+1;

                raw.getNum()[ArrayIndex.carTic]=speed[0];//使用上一个正常点
            } else {//正常
                yu[0]=Math.abs(raw.getNum()[ArrayIndex.motoTic]-speed[0])*YU_VALUE;//将阈值 设置为 连续两个点的变化值的 YU_VALUE 倍
                speed[0] = raw.getNum()[ArrayIndex.carTic];
                xishu[0] = 1;
            }
            if (Math.abs(raw.getNum()[ArrayIndex.motoTic] - speed[1]) > (yu[1] * xishu[1])) {//超出阈值
                xishu[1] = xishu[1]+1;

                raw.getNum()[ArrayIndex.motoTic]=speed[1];//使用上一个正常点
            } else {//正常
                yu[1]=Math.abs(raw.getNum()[ArrayIndex.motoTic]-speed[1])*YU_VALUE;//将阈值 设置为 连续两个点的变化值的 YU_VALUE 倍
                speed[1] = raw.getNum()[ArrayIndex.motoTic];
                xishu[1] = 1;
            }
        } else {
            if (isStart) {
                long startRunTime = System.currentTimeMillis() - lStartTime;
                if (startRunTime > wendingTime) {
                  //  System.out.println("AAAAAAAAAAAAAAA  启动10秒");
                    isRun = true;
                }
            } else {
                isStart = true;
                lStartTime = System.currentTimeMillis();
             //   System.out.println("AAAAAAAAAAAAAAA  启动");
            }

            speed[0] = raw.getNum()[ArrayIndex.carTic];//            carTic=9;  车速霍尔
            speed[1] = raw.getNum()[ArrayIndex.motoTic];//           motoTic=10;  马达霍尔

            xishu[0] = 1;
            xishu[1] = 1;

            yu[0] = speed[0];//初始化为本身大小
            yu[1] = speed[1];//初始化为本身大小
        }

    //    System.out.println("AAAAAAAAAAAAAAA  "+raw.getNum()[ArrayIndex.motoTic]);

        return raw;
    }
}
