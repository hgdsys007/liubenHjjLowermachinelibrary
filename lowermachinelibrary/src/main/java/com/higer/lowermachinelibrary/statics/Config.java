//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.higer.lowermachinelibrary.statics;

public final class Config {
   public static final String VERSION = "1.50";
   public static final String BASE_JAR_VERSION = "1.14";
   public static boolean isDetialLog = false;
   public static String mcuId = "";
   public static String mcuVersion = "";
   public static String gkjAppSaveDir = "/sdcard/higer/mcu";
   public static int logLevel = 3;
   public static int TcpServerPoint = 8087;
   public static int UdpServerPoint = 8087;
   public static String udp_client_ip = "192.168.7.201";
  //public static String udp_client_ip = "192.168.100.101";
   public static int udp_client_point = 8087;
   public static int delayPoints = 0;
   public static int UartCmdLen = 2048;
   public static int BufferLen = 2048;
   public static int Can0BufferLen = 128;


   //雷达 ID 配置
   public static byte com2MinId=1;
    public static byte com2MaxId=8;
    public static byte com3MinId=9;
    public static byte com3MaxId=16;


    public static final String adasMediaPath="/mnt/sdcard/adas/";
    public static String prefixStr="";
    public static boolean isUsbUsbSfz = false;


   public Config() {
   }
}
