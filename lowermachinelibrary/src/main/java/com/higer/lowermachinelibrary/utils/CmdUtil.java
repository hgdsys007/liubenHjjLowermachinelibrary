package com.higer.lowermachinelibrary.utils;

import com.higer.lowermachinelibrary.entity.Frame;


final public class CmdUtil {
    private static int heartIndex=0;
    private static final int MAX=65535;
    public static final int GPS1=0;
    public static final int GPS2=1;
    public static final int COM2=2;
    public static final int COM3=3;
    public static final int COM4=4;
    public static final int COM5=5;
    public static final int COM6=6;
    public static final int COM7=7;
    public static final int COM8=8;
    public static final int CAN0=9;
    public static final int CAN1=10;

    public static byte[] makeCanCmd(int canNumber,int address,String cmd)
    {
        byte[] strBytes=cmd.getBytes();
        return makeCanCmd(canNumber,address,strBytes,0,cmd.length());
    }

    public static byte[] makeCanCmd(int canNumber,int address,byte[] data,int iStart,int iLen)
    {
        int iQueshao=((iLen%8)==0)?0:(8-(iLen%8));
        int dataLen=iLen+iQueshao;
        int iCount=dataLen/8;


        int iResLen=dataLen+10+iCount*2;
        byte[] res=new byte[iResLen];
        res[0]='H';
        res[1]='G';
        res[2]='S';
        res[3]='C';
        res[4]='A';
        res[5]='N';
        switch (canNumber)
        {
            case CAN0:
                res[6]='0';
                break;
            case CAN1:
                res[6]='1';
                break;
        }
        res[7]=':';
        res[8]=(byte)(((dataLen+iCount*2)>>8)&0xff);
        res[9]=(byte)((dataLen+iCount*2)&0xff);

        int iPos=10;
        int iDataPos=iStart;


        int iLast=iLen-iDataPos+iStart;
        while (iLast>=8)
        {
            res[iPos++]=(byte)address;
            res[iPos++]=(byte)8;
            System.arraycopy(data,iDataPos,res,iPos,8);
            iDataPos+=8;
            iPos+=8;

            iLast=iLen-iDataPos+iStart;
        }
        if (iLast>0)
        {
            res[iPos++]=(byte)address;
            res[iPos++]=(byte)iLast;
            System.arraycopy(data,iDataPos,res,iPos,iLast);
        }

        return res;
    }


    public static byte[] makeCanCmd(int canNumber, Frame frame)
    {
        int iLen=10;
        byte[] res=new byte[iLen+10];
        res[0]='H';
        res[1]='G';
        res[2]='S';
        res[3]='C';
        res[4]='A';
        res[5]='N';
       switch (canNumber)
       {
           case CAN0:
               res[6]='0';
               break;
           case CAN1:
               res[6]='1';
               break;
       }
        res[7]=':';
        res[8]=(byte)((iLen>>8)&0xff);
        res[9]=(byte)(iLen&0xff);
        byte[] cmdArray=frame.getCmdBytes();
        System.arraycopy(cmdArray,0,res,10,iLen);
        return res;
    }



    public static byte[] makeComCmd(int type,String cmd)
    {
        int iLen=cmd.length();
        byte[] res=new byte[iLen+10];
        res[0]='H';
        res[1]='G';
        res[2]='S';
        res[3]='C';
        res[4]='O';
        res[5]='M';
      switch (type)
      {
          case GPS1:
              res[6]='0';
              break;
          case GPS2:
              res[6]='1';
              break;
          case COM2:
              res[6]='2';
              break;
          case COM3:
              res[6]='3';
              break;
          case COM4:
              res[6]='4';
              break;
          case COM5:
              res[6]='5';
              break;
          case COM6:
              res[6]='6';
              break;
          case COM7:
              res[6]='7';
              break;
          case COM8:
              res[6]='8';
              break;
      }
        res[7]=':';
        res[8]=(byte)((iLen>>8)&0xff);
        res[9]=(byte)(iLen&0xff);
        byte[] cmdArray=cmd.getBytes();
        System.arraycopy(cmdArray,0,res,10,iLen);
        return res;
    }
    public static byte[] makeComCmd(int type,byte[] cmd)
    {
        int iLen=cmd.length;
        byte[] res=new byte[iLen+10];
        res[0]='H';
        res[1]='G';
        res[2]='S';
        res[3]='C';
        res[4]='O';
        res[5]='M';
        switch (type)
        {
            case GPS1:
                res[6]='0';
                break;
            case GPS2:
                res[6]='1';
                break;
            case COM2:
                res[6]='2';
                break;
            case COM3:
                res[6]='3';
                break;
            case COM4:
                res[6]='4';
                break;
            case COM5:
                res[6]='5';
                break;
            case COM6:
                res[6]='6';
                break;
            case COM7:
                res[6]='7';
                break;
            case COM8:
                res[6]='8';
                break;
        }
        res[7]=':';
        res[8]=(byte)((iLen>>8)&0xff);
        res[9]=(byte)(iLen&0xff);
        System.arraycopy(cmd,0,res,10,iLen);
        return res;
    }

//生成MCU 升级数据包
    public static byte[] mekeMcuUpCmd(int iNmuber,byte[] data)
    {
        int iLen=data.length+4;
        byte[] res=new byte[iLen+13];
        res[0]='H';
        res[1]='G';
        res[2]='S';
        res[3]='U';
        res[4]='p';
        res[5]='g';
        res[6]='r';
        res[7]='a';
        res[8]='d';
        res[9]='e';
        res[10]=':';
        //数据长度
        res[11]=(byte)((iLen>>8)&0xff);
        res[12]=(byte)(iLen&0xff);
        //帧序号
        res[13]=(byte)((iNmuber>>8)&0xff);
        res[14]=(byte)(iNmuber&0xff);
//CRC
        int iCrc=CrcUtil.sum_crc(data,data.length);
        res[15]=(byte)((iCrc>>8)&0xff);
        res[16]=(byte)(iCrc&0xff);


 //System.out.println("crc="+iCrc+"   "+StringHexUtil.ArraytoHexString(res,15,2)+"   iLen="+data.length);

        System.arraycopy(data,0,res,17,iLen-4);
        return res;
    }


    //指令集
//协议头 ($HG:) $HG:)+指令集（ 指令集（ SetBaud SetBaud ）+ {指令内容 指令内容 }+ox0D+ox0D+
    //$HG: SetBaud {0, 9600,8,n,1}
    public static byte[] makeControlCmd(String cmd,String data) //$HG:SQyCfg{All}\r
    {
        int iLen1=cmd.length();
        int iLen2=data.length();
        byte[] res=new byte[iLen1+iLen2+7];
        res[0]='$';
        res[1]='H';
        res[2]='G';
        res[3]=':';
        byte[] array1=cmd.getBytes();
        byte[] array2=data.getBytes();

        System.arraycopy(array1,0,res,4,iLen1);
        res[iLen1+4]='{';
        System.arraycopy(array2,0,res,5+iLen1,iLen2);
        res[res.length-2]='}';
        res[res.length-1]=0x0d;
        return res;
    }


    public static byte[] getHeartCmd()
    {
        String sHeartIndex=String.format("%05d",heartIndex);
        if (heartIndex==MAX)
        {
            heartIndex=0;
        }else {
            heartIndex++;
        }
        return makeControlCmd("Heart",sHeartIndex);
    }





}
