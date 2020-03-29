package com.higer.lowermachinelibrary.functionBlock.dataProChannel.imp;

import com.higer.lowermachinelibrary.entity.HardwareData;
import com.higer.lowermachinelibrary.functionBlock.dataProChannel.IDataChannel;
import com.higer.lowermachinelibrary.statics.ArrayIndex;
import com.higer.lowermachinelibrary.statics.Config;

import java.util.ArrayList;
import java.util.List;

import cn.base.entity.VehicleMcu;

//滤波开关量 和  转速
final public class DataChannel01 implements IDataChannel {
    final int iLoopCount = 4;
    final int iDoubleLoopCount = iLoopCount * 2;
    int iBufferSize = 0;
    List<VehicleMcu> bufferList = new ArrayList<>();
    boolean[] lastStList = new boolean[ArrayIndex.stLen];

    //老下位机 数据优化  参数
    static  class OldMcuParam {
        static   int iSameEvent=2;//连续2个数相同才为有效数字
    }








    @Override
    public VehicleMcu doDataPro(VehicleMcu raw) {


         return doNet(raw);//新下位机板

    }




    @Override
    public VehicleMcu doNet(VehicleMcu raw) {
        if (raw == null) {
            return raw;
        }

        // System.out.println("AAAAAAAAAAA-"+raw.getNum()[ArrayIndex.carTic]+"      "+raw.getNum()[ArrayIndex.motoTic]);

        bufferList.add(raw.clone());
        iBufferSize = bufferList.size();


        if (iBufferSize % iLoopCount != 0)//不是4的倍数 不处理  至少是4个数据才处理
        {
            return null;
        }

        switch (iBufferSize) {
            case iLoopCount://4个数据  ----  程序刚启动时  第一次执行  以后都是8个数据
                for (int i = 0; i < ArrayIndex.stLen; i++) {
                    int iTrueCount = 0;
                    for (int j = 0; j < iLoopCount; j++) {
                        if (bufferList.get(j).getBool()[i]) {
                            iTrueCount++;
                        }
                    }
                    if (iTrueCount > (iLoopCount / 2))// 大于2个才为 true
                    {
                        raw.getBool()[i] = true;
                    } else {
                        raw.getBool()[i] = false;
                    }
                }
                break;
            case iDoubleLoopCount:// iLoopCount*2://8个数据

                for (int i = 0; i < ArrayIndex.stLen; i++) {
                    boolean isOk = false;
                    for (int j = 0; j < iLoopCount; j++) {
                        int iStartPos = iBufferSize - 1 - j;
                        int iSameCount = 0;
                        for (int m = 0; m < (iLoopCount - 1); m++) {
                            int ipos = iStartPos - m;
                            if (bufferList.get(ipos).getBool()[i] == bufferList.get(ipos - 1).getBool()[i]) {
                                iSameCount++;
                            } else {
                                break;
                            }
                        }
                        if (iSameCount == (iLoopCount - 1))//连续 相同  选中
                        {
                            raw.getBool()[i] = bufferList.get(iStartPos).getBool()[i];
                            isOk = true;
                            break;
                        }
                    }
                    if (!isOk)//如果没有找到 连续相同的点  使用历史点
                    {
                        raw.getBool()[i] = lastStList[i];
                    }
                }


                //删除最开始的4个数据  一组数据
                for (int i = 0; i < iLoopCount; i++) {
                    bufferList.remove(0);
                }
                iBufferSize -= iLoopCount;

                break;
        }

        System.arraycopy(raw.getBool(), 0, lastStList, 0, ArrayIndex.stLen);



        //发动机转速 数据优化-------------------------------------------------------发动机转速 数据优化-----------------------------------------------------发动机转速 数据优化

        int zhuanSuMax=0;
        int zhuanSuMin=100000;
        int zhunSuSum=0;

//        int ceSuMax=0;
//        int ceSuMin=100000;
//        int ceSuSum=0;


        for (int i = iLoopCount; i >0 ; i--) {
            VehicleMcu item=bufferList.get(iBufferSize-i);
            raw.getNum()[ArrayIndex.carTic]+=item.getNum()[ArrayIndex.carTic];//车速  累加

            //  raw.getNum()[ArrayIndex.motoTic]+= item.getNum()[ArrayIndex.motoTic];
            int zhuanSu=item.getNum()[ArrayIndex.motoTic];

            //   System.out.println("QQQQ  "+value);

            zhunSuSum+=zhuanSu;
            zhuanSuMax=zhuanSuMax>zhuanSu?zhuanSuMax:zhuanSu;
            zhuanSuMin=zhuanSuMin<zhuanSu?zhuanSuMin:zhuanSu;
        }

        raw.getNum()[ArrayIndex.motoTic]=(zhunSuSum-zhuanSuMax-zhuanSuMin)*iLoopCount/(iLoopCount-2);//4个数据 去掉一个最大值 去掉一个最小值，然后 乘以比例系数

        //  System.out.println("AAAAAAAAAAA---------------------------"+raw.getNum()[ArrayIndex.carTic]+"      "+raw.getNum()[ArrayIndex.motoTic]);
        return raw;
    }

//    public static void main(String args[])
//    {
//  int iLoopCount=4;
//            for (int i = iLoopCount; i >0 ; i--) {
//            System.out.println("index="+i);
//        }
//
//    }


  //  @Override
    public VehicleMcu doDataPro1(VehicleMcu raw) {

        if (raw == null) {
            return raw;
        }

        bufferList.add(raw.clone());
        iBufferSize = bufferList.size();


        if (iBufferSize % iLoopCount != 0)//不是4的倍数 不处理  至少是4个数据才处理
        {
            return null;
        }

        switch (iBufferSize) {
            case iLoopCount://4个数据  ----  程序刚启动时  第一次执行  以后都是8个数据
                for (int i = 0; i < ArrayIndex.stLen; i++) {
                    int iTrueCount = 0;
                    for (int j = 0; j < iLoopCount; j++) {
                        if (bufferList.get(j).getBool()[i]) {
                            iTrueCount++;
                        }
                    }
                    if (iTrueCount > (iLoopCount / 2))// 大于2个才为 true
                    {
                        raw.getBool()[i] = true;
                    } else {
                        raw.getBool()[i] = false;
                    }
                }
                break;
            case iDoubleLoopCount:// iLoopCount*2://8个数据

                for (int i = 0; i < ArrayIndex.stLen; i++) {
                    boolean isOk = false;
                    for (int j = 0; j < iLoopCount; j++) {
                        int iStartPos = iBufferSize - 1 - j;
                        int iSameCount = 0;
                        for (int m = 0; m < (iLoopCount - 1); m++) {
                            int ipos = iStartPos - m;
                            if (bufferList.get(ipos).getBool()[i] == bufferList.get(ipos - 1).getBool()[i]) {
                                iSameCount++;
                            } else {
                                break;
                            }
                        }
                        if (iSameCount == (iLoopCount - 1))//连续 相同  选中
                        {
                            raw.getBool()[i] = bufferList.get(iStartPos).getBool()[i];
                            isOk = true;
                            break;
                        }
                    }
                    if (!isOk)//如果没有找到 连续相同的点  使用历史点
                    {
                        raw.getBool()[i] = lastStList[i];
                    }
                }


                //删除最开始的4个数据  一组数据
                for (int i = 0; i < iLoopCount; i++) {
                    bufferList.remove(0);
                }
                iBufferSize -= iLoopCount;

                break;
        }


        System.arraycopy(raw.getBool(), 0, lastStList, 0, ArrayIndex.stLen);


//     if(raw.getBool()[0])
//     {
//         stringBuilder.append(" 1");
//     }else {
//         stringBuilder.append(" 0");
//     }
//     System.out.println("BBBBBBBBBBBBB  "+stringBuilder.toString());


        return raw;
    }


/*    public static void main(String args[])
    {
        // 0 1 0 0 1 1 0 1 0 1 0 0 1 1 1 0 1 0 1 0 1 1 1 0 0 0 1 1 1 1 0 0 0 0 0 1 1 0 1 0 0 1 1 1
         //0 0 0 0 0 1 1 1 1 1 1 0 0 1 1 1 1 1 1 1 1 1 1 1 0 0 0 1 1 1 1 0 0 0 0 0 1 1 1 1 0 0 1 1
         //0 0 0 0 0 1 1 1 1 1 1 0 0 1 1 1 1 1 1 1 1 1 1 1 0 0 0 1 1 1 1 0 0 0 0 0 1 1 1 1 0 0 1 1

        List<VehicleMcu> list=new ArrayList<>();
        list.add(makeData(false));
        list.add(makeData(true));
        list.add(makeData(false));
        list.add(makeData(false));
        list.add(makeData(true));
        list.add(makeData(true));
        list.add(makeData(false));
        list.add(makeData(true));
        list.add(makeData(false));
        list.add(makeData(true));
        list.add(makeData(false));
        list.add(makeData(false));
        list.add(makeData(true));
        list.add(makeData(true));
        list.add(makeData(true));
        list.add(makeData(false));
        list.add(makeData(true));
        list.add(makeData(false));
        list.add(makeData(true));
        list.add(makeData(false));
        list.add(makeData(true));
        list.add(makeData(true));
        list.add(makeData(true));
        list.add(makeData(false));
        list.add(makeData(false));
        list.add(makeData(false));
        list.add(makeData(true));
        list.add(makeData(true));
        list.add(makeData(true));
        list.add(makeData(true));
        list.add(makeData(false));
        list.add(makeData(false));
        list.add(makeData(false));
        list.add(makeData(false));
        list.add(makeData(false));
        list.add(makeData(true));
        list.add(makeData(true));
        list.add(makeData(false));
        list.add(makeData(true));
        list.add(makeData(false));
        list.add(makeData(false));
        list.add(makeData(true));
        list.add(makeData(true));
        list.add(makeData(true));

        StringBuffer cc=new StringBuffer();
        StringBuffer sb=new StringBuffer();
        DataChannel01 dd=new DataChannel01();
        for (int i = 0; i <list.size() ; i++) {
            VehicleMcu mm=list.get(i);
            if(mm.getBool()[0])
            {
                cc.append(" 1");
            }else {
                cc.append(" 0");
            }
            VehicleMcu item=dd.doCom(mm);
            if(item.getBool()[0])
            {
                sb.append(" 1");
            }else {
                sb.append(" 0");
            }
        }
        System.out.println(cc.toString());
       System.out.println(sb.toString());
    }

    public static VehicleMcu makeData(boolean value)
    {
        VehicleMcu item=new VehicleMcu();
        item.setBool(new boolean[ArrayIndex.stLen]);
        for (int i = 0; i <ArrayIndex.stLen ; i++) {
            item.getBool()[i]=value;
        }
        return item;
    }*/

}
