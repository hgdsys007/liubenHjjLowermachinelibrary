package com.higer.lowermachinelibrary.entity;

public class AdasTableData {
    private int index=0;
    private String dateStr="";
    private String eventName="";
    private String imagePath="";

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

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }


    public String toString()
    {
        String res=dateStr+"  "+eventName+"   "+imagePath;
        return res;
    }
}
