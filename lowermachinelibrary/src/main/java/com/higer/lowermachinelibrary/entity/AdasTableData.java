package com.higer.lowermachinelibrary.entity;

import java.util.ArrayList;

public class AdasTableData {
    private int index=0;
    private int renCode=0;
    private String dateStr="";
    private String eventName="";
    private String imagePath="";
    private ArrayList<String> media=new ArrayList<>();
    private ArrayList<byte[]> cmdList=new ArrayList<>();

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getImagePath() {
        return imagePath;
    }


    public ArrayList<String> getMedias() {
        return media;
    }

    public void setImagePath(String imagePath) {
        //this.imagePath = imagePath;

        media.add(imagePath);
    }

   public int getRenCode()
    {
        return renCode;
    }
    public String getRenStrMsg()
    {
        String image="";
        String mp4="";
        for (int i = 0; i <media.size() ; i++) {
            String item=media.get(i);
            if(item.contains("mp4"))
            {
                mp4=mp4+item;
            }else {
                image=image+item;
                image=image+",";
            }
        }

        if(image.endsWith(","))
        {
            image=image.substring(0,image.length()-2);
        }

        image=image+";";
        image=image+mp4;
        return image;
    }

    public void setRenCode(int code)
    {
        renCode=code;
    }

    public void doRecvMediaError()
    {
        int iPos=media.size()-cmdList.size()-1;
        media.remove(iPos);
    }

    public void addCmd(byte[] cmd)
    {
        cmdList.add(cmd);
    }

    public byte[] getCmd()
    {
        if(cmdList.isEmpty())
        {
            return null;
        }
      //  System.out.println("============================="+cmdList.size());
        byte[] res=cmdList.get(0);
        cmdList.remove(0);
        return res;
    }

    public ArrayList<byte[]> getCmdList()
    {
        return cmdList;
    }
    public String toString()
    {
        String res=eventName+","+media.toString();
        return res;
    }
}
