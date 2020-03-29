package com.higer.lowermachinelibrary.threads;



public class BaseThread extends Thread {
    private   boolean work;

    public BaseThread() {

    }

    public boolean startWork()
    {
        if (work)
            return true;

        work=true;
        this.start();
        return true;
    }

    public boolean stopWork()
    {
        work=false;
        interrupt();
            try {
                this.join(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }

        return true;
    }
    public void flagStop()
    {
        work=false;
    }
    public boolean isWork()
    {
        return work;
    }
}
