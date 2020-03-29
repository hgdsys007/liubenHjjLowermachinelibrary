//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.higer.lowermachinelibrary.log;

import com.higer.lowermachinelibrary.threads.BaseThread;
import com.higer.lowermachinelibrary.utils.DateTimeUti;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public final class LogThread extends BaseThread {
    public static Queue<String> queue = new LinkedBlockingQueue();
    private String sdCardPath = "/mnt/sdcard/liuLib/";
    private FileOutputStream fileOutputStream = null;

    public LogThread() {
    }

    public static void write(String s) {
        queue.offer(s);
    }

    public void run() {
        Logger.writeLog("=============启动LogThread线程进入 run()");

        while(this.isWork()) {
            try {
                sleep(1000L);

                while(!queue.isEmpty()) {
                    String swrite = ((String)queue.poll()).concat("\r\n");
                    this.fileOutputStream.write(swrite.getBytes());
                    this.fileOutputStream.flush();
                }
            } catch (InterruptedException var3) {
                var3.printStackTrace();
            } catch (IOException var4) {
                var4.printStackTrace();
            }
        }

        try {
            if (this.fileOutputStream != null) {
                this.fileOutputStream.close();
            }
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    public boolean startWork() {
        try {
            File file1 = new File(this.sdCardPath);
            if (!file1.exists() && !file1.mkdir()) {
                return false;
            }

            this.sdCardPath = this.sdCardPath.concat(DateTimeUti.getFileName());
            File file = new File(this.sdCardPath);
            if (!file.exists() && !file.createNewFile()) {
                return false;
            }

            this.fileOutputStream = new FileOutputStream(file, true);
        } catch (FileNotFoundException var3) {
            var3.printStackTrace();
            return false;
        } catch (IOException var4) {
            var4.printStackTrace();
            return false;
        }

        return super.startWork();
    }

    public boolean stopWork() {
        this.flagStop();
        this.interrupt();
        return true;
    }
}
