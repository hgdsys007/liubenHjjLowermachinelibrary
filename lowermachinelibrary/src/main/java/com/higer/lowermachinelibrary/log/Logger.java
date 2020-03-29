//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.higer.lowermachinelibrary.log;

import cn.base.iface.main.VehicleMainLog;
import com.higer.lowermachinelibrary.utils.DateTimeUti;

public final class Logger {
    private static VehicleMainLog renLogger = null;

    public Logger() {
    }

    public static void setLogger(VehicleMainLog logger) {
        renLogger = logger;
    }

    public static void writeLog(String msg) {
        System.out.println(msg);
        if (renLogger == null) {
            LogThread.write(getFlex().concat(msg));
        } else {
            renLogger.printErrorToSdCard("Liu", getFlex().concat(msg));
        }

    }

    private static String getFlex() {
        return DateTimeUti.getLogFlex();
    }
}
