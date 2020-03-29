package com.higer.lowermachinelibrary.adas;

public class BaseCmd {
    private static int _lsh=1;
    private byte[] liuShuiHao=new byte[2];//流水号
    private byte[] changShangCode=new byte[2];//厂商CODE
    private byte waiSheCode=0x64; //外设编码
    private byte gongNengMa=0x50;//功能码
    private byte[] dataArray=null;//数据内容

    private int infoId;//消息ID
    private int mediaId;//媒体ID
    protected byte getJiaoYanMa()
    {
        makeLsh();
        setCsCode();

        int iSum=0;

        for (int i = 0; i <changShangCode.length ; i++) {
            iSum+=changShangCode[i]&0xff;
        }
        iSum+=waiSheCode&0xff;
        iSum+=gongNengMa&0xff;
        if(dataArray!=null)
        {
            for (int i = 0; i <dataArray.length ; i++) {
                iSum+=dataArray[i]&0xff;
            }
        }


        return  (byte)(iSum&0xff);
    }
    protected String ArraytoHexString(byte[] linArray, int iLen)
    {
        StringBuffer stringBuffer=new StringBuffer();
        for(int i=0;i<iLen;i++)
        {
            stringBuffer.append(String.format("%02X ",0xff&linArray[i]));
        }
        return stringBuffer.toString();

    }
    public void setChangShangCode(byte highByte,byte lowByte) {
        this.changShangCode[0]=highByte;
        changShangCode[1]=lowByte;
    }

    public int getInfoId() {
        return infoId;
    }

    public void setInfoId(int infoId) {
        this.infoId = infoId;
    }

    public int getMediaId() {
        return mediaId;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    //外设编码
    protected byte getWaiSheCode() {
        return waiSheCode;
    }
    //外设编码
    protected void setWaiSheCode(int waiSheCode) {
        this.waiSheCode =( byte)(waiSheCode&0xff);
    }
    //功能码
    protected byte getGongNengMa() {
        return gongNengMa;
    }
    //功能码
    protected void setGongNengMa(int gongNengMa) {
        this.gongNengMa = ( byte)(gongNengMa&0xff);
    }

    private void makeLsh()
    {
        liuShuiHao[0]=(byte)((_lsh>>8)&0XFF);
        liuShuiHao[1]=(byte)(_lsh&0XFF);
        _lsh++;
    }

    protected byte[] getLsh()
    {
        return liuShuiHao;
    }

    protected  void setCsCode()
    {
        changShangCode[0]=0;
        changShangCode[1]=1;
    }

    protected  byte[] getCsCode()
    {
        return changShangCode;
    }



    private byte[] getDataArray() {
        return dataArray;
    }

    protected void setDataArray(byte[] dataArray) {
        this.dataArray = dataArray;
    }

    protected String getFileName()
    {
        String res="";

        switch (infoId)
        {
            case 0:
                res=infoId+"_"+mediaId+".jpg";
                break;
            case 1:
                break;
            case 2:
                res=infoId+"_"+mediaId+".mp4";
                break;
        }

        return res;
    }

    private String toHexString(byte[] dataArray)
    {
        StringBuilder res=new StringBuilder();
        if(dataArray!=null)
        {
            for (byte item:
                    dataArray) {
                res.append(String.format("%02x ",item&0xff));
            }
        }
        return res.toString();
    }


    protected byte[] alignCmdArray(byte[] cmdArray)
    {
        int iCount=0;
        for (int i = 1; i <(cmdArray.length-2) ; i++) {
            if((cmdArray[i]&0xff)==0x7e)
            {
                iCount++;
                continue;
            }
            if((cmdArray[i]&0xff)==0x7d)
            {
                iCount++;
                continue;
            }
        }

        if(iCount==0)
        {
            return cmdArray;
        }
        byte[] resArray=new byte[cmdArray.length+iCount];
        int iPos=0;
        resArray[iPos++]=0x7e;
        for (int i = 1; i <(cmdArray.length-1) ; i++) {
            if((cmdArray[i]&0xff)==0x7e)
            {
                resArray[iPos++]=0x7d;
                resArray[iPos++]=0x02;
                continue;
            }else if((cmdArray[i]&0xff)==0x7d)
            {
                resArray[iPos++]=0x7d;
                resArray[iPos++]=0x01;
                continue;
            }else {
                resArray[iPos++]=cmdArray[i];
            }
        }
        resArray[iPos++]=0x7e;


        //   System.out.println("1-----"+toHexString(cmdArray));
        //   System.out.println("2-----"+toHexString(resArray));


        return resArray;
    }
}
