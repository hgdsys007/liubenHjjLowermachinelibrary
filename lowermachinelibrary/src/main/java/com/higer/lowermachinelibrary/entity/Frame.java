package com.higer.lowermachinelibrary.entity;

import com.higer.lowermachinelibrary.utils.StringHexUtil;

final public class Frame {
    public int address = 0;//地址
    public int iDataLen = 0;//数据长度
    public byte[] data = new byte[8];//数据

    @Override
    public String toString() {
       // return String.format("地址:%d  数据长度:%d  数据{%s}  ASCII{%s}", address, iDataLen, StringHexUtil.ArraytoHexString(data,0,iDataLen), StringHexUtil.ArraytoAsciiString(data,0,iDataLen));

        return String.format("地址:%d  数据长度:%d  数据{%s}", address, iDataLen, StringHexUtil.ArraytoHexString(data,0,iDataLen));
    }

    public boolean parse(byte[] array) {
        if (array.length > 12) {
            address=array[0]&0xff;


            address=((array[0]&0xff)<<24)+((array[1]&0xff)<<16)+((array[2]&0xff)<<8)+(array[3]&0xff);



            iDataLen=array[4]&0xff;
            if(iDataLen<9)
            {
                System.arraycopy(array,5,data,0,iDataLen);
                return true;
            }
        }
        return false;
    }

    public byte[] getCmdBytes()
    {
        byte[] res=new byte[10];
        res[0]=(byte)address;
        res[1]=(byte)iDataLen;
        System.arraycopy(data,0,res,2,8);
        return res;
    }
}
