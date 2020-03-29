package com.higer.lowermachinelibrary.utils;

import com.higer.lowermachinelibrary.log.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

final public class CrcUtil {
    public static int make_crc16(byte[] data, int iLen) {
        // System.out.println(StringHexUtil.ArraytoHexString(data,0,iLen)+"                  iLen="+iLen);
        if (iLen > data.length)
            return -1;
        int i, j;
        int crc = 0xffff;
        for (i = 0; i < iLen; i++) {
            crc ^= data[i];
            for (j = 0; j < 8; j++) {
                if ((crc & 0x01) == 1) {
                    crc = (crc >> 1) ^ 0xa001;
                } else {
                    crc >>= 1;
                }
            }
        }
        return crc;
    }





    public static int sum_crc(byte[] data, int iLen) {
        //   System.out.println(StringHexUtil.ArraytoHexString(data,0,iLen));
        long sum = 0;
        for (int i = 0; i < iLen; i++) {
            sum += (data[i] & 0xff);
        }
        return (int) (sum & 0xffff);
    }


/*
public static void main(String[] args)
    {
        String ws="06";
        Integer.parseInt(ws,16);

        String s="$GPGGA,064156.80,3733.9949004,N,12114.4126395,E,1,17,1.0,7.588,M,,,,*36";

               s="$GPGGA,064209.60,3733.9695697,N,12114.4096499,E,1,17,1.0,8.415,M,,,,*38";
               s="$GPGGA,064230.20,3733.858572,N,12114.3916460,E,1,16,1.1,12.665,M,,,,*00";
               s="$GPGGA,064247.00,3733.800181,N,12114.3812522,E,1,16,1.1,12.779,M,,,,*09";
               s="$GPGGA,064804.20,3733.750292,N,12114.3645609,E,1,18,0.8,13.726,M,,,,*04";



               s="$GPGGA,063527.80,3733.965856,N,12114.7780063,E,1,15,1.2,11.189,M,,,,*0F";
               s="$GPGGA,063527.80,3733.9658565,N,12114.7780063,E,1,15,1.2,11.189,M,,,,*0F";


               s="$GPGGA,054750.60,2310.8856705,N,11325.0005004,E,1,21,0.7,49.012,M,,,,*04";

               s="$GPGGA,054804.40,2310.885235,N,11325.0005816,E,1,21,0.7,50.023,M,,,,*0F";

               s="$GPGGA,082257.40,2310.8854958,N,11325.0011289,E,4,21,0.7,42.659,M,,,02,0468*06";
               s="$GPGGA,044604.80,3615.3273294,N,11309.3364326,E,1,22,0.7,900.807,M,,,,*30";

                if(checkGps(s.getBytes(),0,s.length()))
        {
            System.out.println("1111111111111111111111111111");
        }else {
            System.out.println("2222222222222222222222");
        }


  */
/*File file=new File("D:\\Work\\higer\\20200221-1347-1359.DAT");
        try {
            BufferedReader reader=new BufferedReader(new FileReader(file));
            String sLine="";
            while ((sLine=reader.readLine())!=null)
            {
                if(sLine.contains("GPGGA"))
                {
                    if(checkGps(sLine.getBytes(),0,sLine.length()))
                    {
                       System.out.println("1111111111111111111111111111");
                    }else {
                        System.out.println(sLine);
                        System.out.println("2222222222222222222222");
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (Exception e)
        {

        }*//*

    }
*/







    public static boolean checkGps(byte[] buf, int istart, int length) {

//        System.out.println("=========================="+StringHexUtil.ArraytoAsciiString(buf,istart,length));
//        System.out.println("len="+length);


        int iEnd=istart+length;
        try {
            int x=(buf[istart+1]&0xff);
            for (int i = istart+2; i <iEnd ; i++) {
                if(buf[i]=='*')
                {

//                   System.out.println(StringHexUtil.ArraytoHexString(buf,i+1,length-i-1));
//                   Logger.writeLog("crc-hex------------  "+StringHexUtil.ArraytoHexString(buf,i+1,length-i-1));
             //     if(Integer.parseInt(StringHexUtil.ArraytoAsciiString(buf,i+1,length-i-1),16)==x)
                  if(Integer.parseInt(StringHexUtil.ArraytoAsciiString(buf,i+1,2),16)==x)
                    {
        //         System.out.println("-----------------true");
                        return true;
                    }else
                    {
                  //      System.out.println("-----------------false");
                        return false;
                    }
                }else {
                    x=x^buf[i];
                }
            }
        }catch (Exception e)
        {
            System.out.println("exception  "+e.toString());
            Logger.writeLog("CrcUtil:checkGps  Exception  "+e.toString());
        }
        return false;
    }
 /*   public static boolean checkGps(byte[] buf, int istart, int length) {
        if (length == -1)
            length = buf.length;
        int res = buf[istart + 1];
        int b;
        int i = istart + 2;
        length += istart;
        while (true) {
            if (i >= length) {
                return false;
            }
            b = buf[i];
            if (b == (int) '*') {
                break;
            }
            res = res ^ b;
            i++;
        }
        if (i + 2 < length) {
            int ck = byteTo16(buf[i + 1]) * 16;
            ck += byteTo16(buf[i + 2]);

            if (res == ck) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
        // return false;
    }*/


    public static boolean checkGps(byte[] buf, int length) {
        if (length == -1)
            length = buf.length;
        // byte res=0;
        int res = buf[1];
        int b;
        int i = 2;
        while (true) {
            if (i >= length) {
                return false;
            }
            b = buf[i];
            if (b == (int) '*') {
                break;
            }
            res = res ^ b;
            i++;
        }
        if (i + 2 < length) {
            int ck = byteTo16(buf[i + 1]) * 16;
            ck += byteTo16(buf[i + 2]);

            if (res == ck) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
        // return false;
    }

    public static int byteTo16(byte c) {// asc16转int
        if (c <= 57) {
            return c - 48;
        } else {
            return c - 48 - 7;
        }
    }


    public static boolean checkHeading8a(byte[] buf, int iStart, int length) {
        try {
            long ulTemp1;
            long ulTemp2;
            long ulCRC = 0;
            length += iStart;
            for (int i = iStart + 1; i < length; i++) {
                if (buf[i] == '*') {
                    if (Long.parseLong(StringHexUtil.ArraytoAsciiString(buf, i + 1, length - i - 1).trim(), 16) == ulCRC) {
                        return true;
                    }
                }
                ulTemp1 = (ulCRC >> 8) & 0x00FFFFFFL;
                ulTemp2 = CRC32Value(((int) ulCRC ^ buf[i]) & 0xff);
                ulCRC = ulTemp1 ^ ulTemp2;
            }
        } catch (Exception e) {
            Logger.writeLog("CrcUtil:checkHeading8a  Exception " + e.toString());
        }
        return false;
    }


    public static long CalculateBlockCRC32(byte[] ucBuffer, long ulCount) {
        long ulTemp1;
        long ulTemp2;
        long ulCRC = 0;

        for (int i = 0; i < ulCount; i++) {
            ulTemp1 = (ulCRC >> 8) & 0x00FFFFFFL;
            ulTemp2 = CRC32Value(((int) ulCRC ^ ucBuffer[i]) & 0xff);
            ulCRC = ulTemp1 ^ ulTemp2;
        }

        return (ulCRC);
    }

    private static long CRC32Value(int i) {
        int j;
        long ulCRC;
        ulCRC = i;
        for (j = 8; j > 0; j--) {
            if ((ulCRC & 1) == 1)
                ulCRC = (ulCRC >> 1) ^ 0xEDB88320L;
            else
                ulCRC >>= 1;
        }
        return ulCRC;
    }
}
