package com.higer.lowermachinelibrary.adas;

import android.content.Intent;

public class AdasGpsUtil {
    public static int transGps(String s)// dddmm.mmmm 度分格式  12123.8925
    {
        try{
            int iLen=s.length();
            for (int i = 0; i < iLen; i++) {
                if(s.charAt(i)=='.')
                {
                    String fen=s.substring(i-2);
                    double dfen=Double.parseDouble(fen)*1000000/60;
                    int du= Integer.parseInt(s.substring(0,i-3))*1000000;
//                System.out.println(fen);
//                System.out.println(s.substring(0,i-3));
                    return (int)(dfen+du);
                }
            }
        }catch (Exception E)
        {

        }

        return -1;
    }
}
