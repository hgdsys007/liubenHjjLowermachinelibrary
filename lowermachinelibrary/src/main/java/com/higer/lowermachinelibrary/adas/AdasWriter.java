package com.higer.lowermachinelibrary.adas;

public final class AdasWriter {
    private QuzhengCmdInterface quzhengCmdInterface=null;
    private AdasWriter(){}
    private static AdasWriter instance;
    public static AdasWriter getInstance()
    {
        if(instance==null)
        {
            instance=new AdasWriter();
        }
        return instance;
    }
    public void setQuzhengCmdInterface(QuzhengCmdInterface inter)
    {
        quzhengCmdInterface=inter;
    }
    public void write(byte[] data)
    {
        if(quzhengCmdInterface!=null)
        {
            quzhengCmdInterface.quzhengCmdArray(data);
        }
    }

}
