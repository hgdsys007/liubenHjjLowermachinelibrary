package com.higer.lowermachinelibrary.statics;

final public class IndCounter {
    private int number=0;

    public synchronized void add()
    {
        number++;
    }

    public synchronized void reset()
    {
        number=0;
    }

    public synchronized int getNumber()
    {
        return number;
    }
}
