package com.higer.lowermachinelibrary.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


final public class DateTimeUti {
 /*   public static void main(String[] args)
    {
        //System.out.println(getLogFlex());
    }*/

    public static String getFileName()
    {
        long currentMiss=System.currentTimeMillis();
        return new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(currentMiss)+".txt";
    }

    public static String getLogFlex()
    {
        long currentMiss=System.currentTimeMillis();
        return new StringBuffer().append("Liu#").append(currentMiss).append("#").append(new SimpleDateFormat("HH:mm:ss:SS").format(currentMiss)).append("  ").toString();
    }

    public static String getCurrentTongbuTime()
    {
        long currentMiss=System.currentTimeMillis();
        return new SimpleDateFormat("yy_MM_dd_").format(currentMiss)+getXinqi(currentMiss)+new SimpleDateFormat("_HH_mm_ss").format(currentMiss);
    }
    private static String getXinqi(long currentMiss)
    {
        String res="07";
        String xingqi=new SimpleDateFormat("EEEE").format(currentMiss);
        if(xingqi.equals("星期一"))
        {
            res="01";
        }else if(xingqi.equals("星期二"))
        {
            res="02";
        }else if(xingqi.equals("星期三"))
        {
            res="03";
        }else if(xingqi.equals("星期四"))
        {
            res="04";
        }else if(xingqi.equals("星期五"))
        {
            res="05";
        }else if(xingqi.equals("星期六"))
        {
            res="06";
        }else if(xingqi.equals("星期日"))
        {
            res="07";
        }

        return res;
    }


    /**
     * 获取两个日期之间的间隔天数
     * @return
     */
    public static int getGapCount(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);

        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
    }

    public static long parseStringDateTime(String strDate)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH");
        try {
            Date d=format.parse(strDate);
            long second=d.getTime()/1000;//毫秒数
            return second;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long parseStringDateTimeYYYYMMDDHHMMSS(String strDate)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        try {
            Date d=format.parse(strDate);
            long second=d.getTime()/1000;//毫秒数
            return second;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long parseStringDateTimeYYYYMMDDHHMMSSsss(String strDate)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
        try {
            Date d=format.parse(strDate);
            long second=d.getTime();//毫秒数
            return second;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
