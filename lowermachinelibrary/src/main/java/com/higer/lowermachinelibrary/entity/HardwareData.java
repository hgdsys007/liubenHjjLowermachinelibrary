package com.higer.lowermachinelibrary.entity;


import com.higer.lowermachinelibrary.log.Logger;
import com.higer.lowermachinelibrary.statics.ArrayIndex;
import com.higer.lowermachinelibrary.statics.CallbackContext;
import com.higer.lowermachinelibrary.statics.Config;
import com.higer.lowermachinelibrary.utils.CommonUtil;
import com.higer.lowermachinelibrary.utils.CrcUtil;
import com.higer.lowermachinelibrary.utils.StringHexUtil;


import java.util.ArrayList;

import cn.base.entity.VehicleGps;
import cn.base.entity.VehicleMcu;
import cn.base.entity.VehiclePoint;


final public class HardwareData extends VehicleMcu {
    private int[] _num = null;
    private int[] _ad = null;
    private int[] _am = null;
    private boolean[] _bool = null;//ST PL
    private ArrayList<VehiclePoint> _dwList = null;// 新档位


    static final int SD = 1;
    static final int ST = 2;
    static final int SPEED = 3;
    static final int AM = 4;
    static final int AD = 5;
    static final int PL = 6;
    static final int EX = 7;
    static final int VA = 8;
    int iType = 0;

    byte[] value = new byte[10];
    int iPos = 0;
    boolean isHead = false;
    int dwX = 0;
    int iCrcCount = 0;



    public HardwareData() {
        _num = new int[24];
        _ad = new int[20];
        _am = new int[9];
        _bool = new boolean[ArrayIndex.stLen];//ST PL
        _dwList = new ArrayList<VehiclePoint>();// 新档位


        super.setNum(_num);
        super.setAd(_ad);
        super.setAm(_am);
        super.setBool(_bool);
        super.setDwList(null);
        super.setDwList(_dwList);


        _dwList.clear();


    }


    //    IND:45,
//  3  SD:(标识)0,0,0,
//  7  ST:0左,0右,0主,0近,0杀,0驻,0门,0马达,0副刹,0安,0喇,0钥,0制动气压不足,0遮盖摄像头,0后雷达，0前雷达,0备6,0备5,0备4,0备3,0备2,0备1,1无,1无,
//  34  SPEED:0车速,0转速,
//  37  AM:29,-17,1034,-153,-272,-576,606,
//  45  AD:1,1,1,1,1,1,1,1,  档位霍尔
//  54  PL:0,0,0,0,0,0,0,0,
// 63   EX:0001,0002,0003,0004,0005,0006,0007,0008,0009,00010,00011,00012,00013,00014,00015,00016,00017,00018,00019,00020,---------挡位数据 XY坐标
// 84   VA:0000  -----------较验位

    /*  public static void main(String[] args)
      {

      IND:5872,
  SD:0,0,0,
  ST:0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,
  SPEED:0,0,
  AM:0,0,0,0,0,0,0,
  AD:2,2,2,2,2,2,2,2,
  PL:-1,-1,-1,-1,-1,-1,-1,-1,
  end
          String s="\n" +
                  "IND:7525,22,\n" +
                  "SD:0,0,0,\n" +
                  "ST:0,0,0,0,1,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,1,0,1,1,0,0,1,0,0,0,0,\n" +
                  "SPEED:0,1,\n" +
                  "AM:0,0,0,0,0,0,0,\n" +
                  "AD:2469,475,7,7,81,81,7,2,\n" +
                  "PL:-1,-1,-1,-1,-1,-1,-1,-1,\n" +
                  "EX:2469,476,2469,476,2469,476,2469,476,2469,476,2469,476,2469,476,2469,475,2469,475,2468,475,2468,475,\n" +
                  "VA:478F,\n" +
                  "end";

          new HardwareData().parseCmd(s.getBytes(),0,s.length());
      }*/

//    public static void main(String args[])
//    {
//String sCmd="IND:7453,22,\n" +
//        "SD:0,0,0,\n" +
//        "ST:0,0,0,0,1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,1,1,0,0,1,0,0,0,0,\n" +
//        "SPEED:0,1,\n" +
//        "AM:0,0,0,0,0,0,0,\n" +
//        "AD:2759,1801,9,8,79,80,8,2,\n" +
//        "PL:-1,-1,-1,-1,-1,-1,-1,-1,\n" +
//        "EX:2758,1814,2760,1814,2760,1813,2760,1812,2759,1807,2759,1797,2759,1808,2759,1811,2759,1810,2759,1811,2758,1804,2759,1801,\n" +
//        "VA:E4E7,\n" +
//        "end";
//
//    new HardwareData().parseCmd(sCmd.getBytes(),0,sCmd.length());
//
//    }


    public boolean parseCmd(byte[] array, int iStart, int iLen) {
// Logger.writeLog("HardwareData:parseCmd  " + StringHexUtil.ArraytoAsciiString(array, iStart, iLen));
      //  System.out.println("cmd=" + StringHexUtil.ArraytoAsciiString(array, 0, iLen));

        try {
            //CRC校验
            isHead = false;
            for (int i = iStart; i < iLen; i++) {
                if (isHead) {
                    if (array[i] == ',') {
                        int iCrc = CrcUtil.make_crc16(array, iCrcCount);
                        int Crc = CommonUtil.hexStrToIn(StringHexUtil.ArraytoAsciiString(value, 0, iPos));
                        if (iCrc != Crc) {
                            Logger.writeLog("crc=" + iCrc + "   recv=" + Crc + "  " + StringHexUtil.ArraytoAsciiString(array, iStart, iLen));
                            Logger.writeLog("cmd=" + StringHexUtil.ArraytoHexString(array, 0, iLen));
                            return false;
                        } else {
                            break;
                        }
                    } else {
                        value[iPos++] = array[i];
                    }
                } else {
                    if (array[i] == ':') {
                        if ((array[i - 2] == 'V') && (array[i - 1] == 'A')) {
                            iPos = 0;
                            isHead = true;
                            iCrcCount = i - 2;
                            continue;
                        }
                    }
                }
            }

//数据解析
            isHead = false;
            iPos = 0;
            int iIndex = 0;
            for (int i = 0; i < iLen; i++) {
                if (isHead) {
                    // if(array[i]==0x0d)
                    if (array[i] == 13) {
                        isHead = false;
                        iPos = 0;
                        continue;
                    }
                    switch (iType) {
                        case SD:
                            if (array[i] == ',') {
                                //   System.out.println("SD----" + StringHexUtil.ArraytoAsciiString(value, 0, iPos));
                                iPos = 0;
                                iIndex++;
                                continue;
                            } else {
                                value[iPos++] = array[i];
                            }
                            break;
                        case ST:
                            if (array[i] == ',') {
                                // System.out.println("ST----"+ StringHexUtil.ArraytoAsciiString(value,0,iPos));
                                if (value[0] == '1') {
                                    _bool[iIndex] = true;
                                } else {
                                    _bool[iIndex] = false;
                                }
                                iPos = 0;
                                iIndex++;
                                continue;
                            } else {
                                value[iPos++] = array[i];
                            }
                            break;
                        case SPEED:
                            if (array[i] == ',') {
                                //    System.out.println("SPEED----"+ StringHexUtil.ArraytoAsciiString(value,0,iPos));
                                if (iIndex == 0) {
                                    _num[ArrayIndex.carTic] = CommonUtil.strToIn(StringHexUtil.ArraytoAsciiString(value, 0, iPos));
                                } else if (iIndex == 1) {
                                    _num[ArrayIndex.motoTic] = CommonUtil.strToIn(StringHexUtil.ArraytoAsciiString(value, 0, iPos));
                                }


                                iPos = 0;
                                iIndex++;
                                continue;
                            } else {
                                value[iPos++] = array[i];
                            }
                            break;
                        case AM:
                            if (array[i] == ',') {
                                //   System.out.println("AM----"+ StringHexUtil.ArraytoAsciiString(value,0,iPos));
                                //  System.out.println("index=------------------------------------"+iIndex);
                                _am[iIndex] = CommonUtil.strToIn(StringHexUtil.ArraytoAsciiString(value, 0, iPos));

                                iPos = 0;
                                iIndex++;
                                continue;
                            } else {
                                value[iPos++] = array[i];
                            }
                            break;
                        case AD:
                            if (array[i] == ',') {
                                //  System.out.println("AD----"+ StringHexUtil.ArraytoAsciiString(value,0,iPos));
                                _ad[iIndex] = CommonUtil.strToIn(StringHexUtil.ArraytoAsciiString(value, 0, iPos));


                                iPos = 0;
                                iIndex++;
                                continue;
                            } else {
                                value[iPos++] = array[i];
                            }
                            break;
                        case PL:
                            if (array[i] == ',') {
                                // System.out.println("PL----"+ StringHexUtil.ArraytoAsciiString(value,0,iPos));
                                if (iIndex < 6) {
                                //    System.out.println("=== " + (ArrayIndex.plStart + 5 - iIndex) + "    " + iIndex);
                                    _num[ArrayIndex.plStart + 5 - iIndex] = CommonUtil.strToIn(StringHexUtil.ArraytoAsciiString(value, 0, iPos));
                                    //   _ad[ArrayIndex.plStart + iIndex] = CommonUtil.strToIn(StringHexUtil.ArraytoAsciiString(value, 0, iPos));
                                }
                                iPos = 0;
                                iIndex++;
                                continue;
                            } else {
                                value[iPos++] = array[i];
                            }
                            break;
                        case EX:
                            if (array[i] == ',') {
                                // System.out.println("EX----"+ StringHexUtil.ArraytoAsciiString(value,0,iPos));
                                if ((iIndex % 2) == 0) {
                                    dwX = CommonUtil.strToIn(StringHexUtil.ArraytoAsciiString(value, 0, iPos));
                                } else {
                                    VehiclePoint dwPoint = new VehiclePoint(dwX, CommonUtil.strToIn(StringHexUtil.ArraytoAsciiString(value, 0, iPos)));
                                    _dwList.add(dwPoint);
                                }


                                iPos = 0;
                                iIndex++;
                                continue;
                            } else {
                                value[iPos++] = array[i];
                            }
                            break;
                        case VA:
                            if (array[i] == ',') {
                                //  System.out.println("VA----" + StringHexUtil.ArraytoAsciiString(value, 0, iPos));
                                iPos = 0;
                                iIndex++;
                                continue;
                            } else {
                                value[iPos++] = array[i];
                            }
                            break;
                    }
                } else {
                    if (array[i] == ':') {
                        if ((array[i - 2] == 'S') && (array[i - 1] == 'D')) {
                            isHead = true;
                            iType = SD;
                            iPos = 0;
                            iIndex = 0;
                            continue;
                        } else if ((array[i - 2] == 'S') && (array[i - 1] == 'T')) {
                            isHead = true;
                            iType = ST;
                            iPos = 0;
                            iIndex = 0;
                            continue;
                        } else if ((array[i - 2] == 'E') && (array[i - 1] == 'D')) {
                            isHead = true;
                            iType = SPEED;
                            iPos = 0;
                            iIndex = 0;
                            continue;
                        } else if ((array[i - 2] == 'A') && (array[i - 1] == 'M')) {
                            isHead = true;
                            iType = AM;
                            iIndex = 0;
                            iPos = 0;
                            continue;
                        } else if ((array[i - 2] == 'A') && (array[i - 1] == 'D')) {
                            isHead = true;
                            iType = AD;
                            iPos = 0;
                            iIndex = 0;
                            continue;
                        } else if ((array[i - 2] == 'P') && (array[i - 1] == 'L')) {
                            isHead = true;
                            iType = PL;
                            iPos = 0;
                            iIndex = 0;
                            continue;
                        } else if ((array[i - 2] == 'E') && (array[i - 1] == 'X')) {
                            _dwList.clear();
                            isHead = true;
                            iType = EX;
                            iIndex = 0;
                            iPos = 0;
                            continue;
                        } else if ((array[i - 2] == 'V') && (array[i - 1] == 'A')) {
                            isHead = true;
                            iType = VA;
                            iPos = 0;
                            iIndex = 0;
                            continue;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.writeLog("HardwareData 数据解析失败：ASCI=" + StringHexUtil.ArraytoAsciiString(array, 0, iLen));
            Logger.writeLog("HardwareData 数据解析失败：HEX=" + StringHexUtil.ArraytoHexString(array, 0, iLen));
        }


        return true;
    }



    protected void writeLog(String msg) {

        Logger.writeLog(msg);

    }




}