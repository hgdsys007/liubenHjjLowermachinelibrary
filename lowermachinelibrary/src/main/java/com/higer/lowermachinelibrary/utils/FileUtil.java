package com.higer.lowermachinelibrary.utils;

import com.higer.lowermachinelibrary.log.Logger;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;


final public class FileUtil {




    public static byte[] hexToBinFile(String hexFileName)
    {
        ByteArrayOutputStream recvBuffer=new ByteArrayOutputStream();
        File file=new File(hexFileName);
        if(!file.exists())
        {
            Logger.writeLog("找不到升级文件："+hexFileName);
            return null;
        }

        try {
            BufferedReader reader=new BufferedReader(new FileReader(file));
            String sLine="";
            while ((sLine=reader.readLine())!=null)
            {
                System.out.println(sLine);
                if(sLine.equals(":00000001FF"))
                {
                    System.out.println("------------------------------------- 读取结束");
                    return recvBuffer.toByteArray();
                }

                if(!getLineData(sLine,recvBuffer))
                {
                    return null;
                }
            }
        }catch (Exception e)
        {
            System.out.println("=======================================error-----------error");
            System.out.println(e.toString());
            Logger.writeLog("升级文件读取失败："+e.toString());
        }

        recvBuffer=null;
        return null;
    }




    public static boolean getLineData(String s,ByteArrayOutputStream out)
    {
   /*   :020000040000FA , 我把它看做 0x02 0x00 0x00 0x04 0x00 0x00 0xFA

   <0x3a>就是 ：号
[数据长度1Byte]
[数据地址2Byte]
[数据类型1Byte]
[数据nByte]
[校验1Byte]  cc 是效验和域，表示记录的效验和，计算方法是将本条记录冒号开始的所有字母对<不包括本效验字和冒号>
所表示的十六进制数字<一对字母表示一个十六进制数，这样的一个十六进制数为一个字节>都加起来然后模除256得到的余数，
最后求出余数的补码，即是本效验字节cc。
<0x0d>
<0x0a>

      第一个 0x02 为数据长度。
      紧跟着后面的0x00 0x00 为地址。
      再后面的0x04为数据类型，类型共分以下几类：
      '00' Data Record//数据记录
      '01' End of File Record//文件结束记录
      '02' Extended Segment Address Record//扩展段地址记录
      '03' Start Segment Address Record//开始段地址记录
      '04' Extended Linear Address Record//扩展线性地址记录
      '05' Start Linear Address Record//开始线性地址记录*/

        int iSum=0;
        int iLen=s.length();
        byte[] tem=new byte[2];
        if(iLen>11)
        {
            if(s.charAt(0)==':')
            {
                tem[0]=(byte) s.charAt(1);
                tem[1]=(byte) s.charAt(2);
                int iDataLen=2*Integer.parseInt(StringHexUtil.ArraytoAsciiString(tem,0,2),16);

                iSum+=(byte) Integer.parseInt(StringHexUtil.ArraytoAsciiString(tem,0,2),16);

                tem[0]=(byte) s.charAt(3);
                tem[1]=(byte) s.charAt(4);
                iSum+=(byte) Integer.parseInt(StringHexUtil.ArraytoAsciiString(tem,0,2),16);
                tem[0]=(byte) s.charAt(5);
                tem[1]=(byte) s.charAt(6);
                iSum+=(byte) Integer.parseInt(StringHexUtil.ArraytoAsciiString(tem,0,2),16);
                tem[0]=(byte) s.charAt(7);
                tem[1]=(byte) s.charAt(8);
                iSum+=(byte) Integer.parseInt(StringHexUtil.ArraytoAsciiString(tem,0,2),16);

                if(iDataLen+11==iLen)
                {
                    if((s.charAt(7)=='0')&&(s.charAt(8)=='0'))//数据类型   记录数据
                    {
                        if(((iLen-11)%2)==0)//双数
                        {
                            int iTemp=0;
                            for (int i = 0; i < (iLen-11)/2; i++) {
                                iTemp=9+i*2;
                                tem[0]=(byte) s.charAt(iTemp);
                                tem[1]=(byte) s.charAt(iTemp+1);

                                byte mmmm=(byte) Integer.parseInt(StringHexUtil.ArraytoAsciiString(tem,0,2),16);
                                out.write(mmmm);
                                iSum+=mmmm;
                            }

                            tem[0]=(byte) s.charAt(iLen-2);
                            tem[1]=(byte) s.charAt(iLen-1);
                            //   iSum+=Byte.parseByte(StringHexUtil.ArraytoAsciiString(tem,0,2),16);

                            iSum+=(byte) Integer.parseInt(StringHexUtil.ArraytoAsciiString(tem,0,2),16);
                            if((iSum&0xff)==0)//CRC校验通过
                            {
                                System.out.println("校验成功");
                                return true;
                            }else {
                                Logger.writeLog("升级文件CRC 校验失败");
                            }
                        }
                    }else{
                        return true;
                    }
                }
            }
        }


        Logger.writeLog("升级文件  行校验失败："+s);
        return false;
    }

}
