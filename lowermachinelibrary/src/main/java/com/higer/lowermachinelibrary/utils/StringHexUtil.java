package com.higer.lowermachinelibrary.utils;

final public class StringHexUtil {
    public static String ArraytoHexString(byte[] linArray, int iLen) {
        return ArraytoHexString(linArray,0,iLen);
    }
    public static String ArraytoAsciiString(byte[] linArray,int iStart, int iLen) {
        return new String(linArray,iStart,iLen);
    }

    public static String ArraytoHexString(byte[] linArray) {
        return ArraytoHexString(linArray,0,linArray.length);
    }


    public static String ArraytoHexString(byte[] linArray, int iStart, int iLen) {
        StringBuffer stringBuffer=new StringBuffer();
        for(int i=0;i<iLen;i++)
        {
            stringBuffer.append(String.format("%02X ",0xff&linArray[i+iStart]));
        }
        return stringBuffer.toString();
    }
}
