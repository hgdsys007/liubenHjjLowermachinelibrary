package com.higer.lowermachinelibrary.utils;

import java.util.Random;

final public class CommonUtil {
    public static int strToIn(String s)
    {

       int iRes=0;
       try {
           iRes=Integer.parseInt(s);
       }catch (Exception e)
       {
           iRes=-1;
       }
       return iRes;
    }
    public static int hexStrToIn(String s)
    {

        int iRes=0;
        try {
            iRes=Integer.parseInt(s,16);
        }catch (Exception e)
        {
            iRes=-1;
        }
        return iRes;
    }
    public static boolean strToBoolean(String s)
    {
       if (s.trim().equals("1"))
       {
           return true;
       }
        return false;
    }

     public static double strToDoubleDef(String str, double def) {
        try {
            return Double.valueOf(str);
        } catch (Exception e) {
            return def;
        }

    }


    public static int getFwj(String hxj)
    {
        double angle = CommonUtil.strToDoubleDef(hxj, 0);
        double angleSave = angle;
        angle -= 90;
        if (angle < 0)
            angle = angle + 360;
        int carAngle = (int) (angle * 100); //TODO:  这里赋值哪个角度 是这个角度还是下面这个角度啊？
        String fwj = String.valueOf(angleSave - 180);
        return carAngle;
    }



    public static String getRandomString(int length) {
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(3);
            long result = 0;
            switch (number) {
                case 0:
                    result = Math.round(Math.random() * 25 + 65);
                    sb.append(String.valueOf((char) result));
                    break;
                case 1:
                    result = Math.round(Math.random() * 25 + 97);
                    sb.append(String.valueOf((char) result));
                    break;
                case 2:
                    sb.append(String.valueOf(new Random().nextInt(10)));
                    break;
            }
        }
        return sb.toString();
    }
}
