package com.higer.lowermachinelibrary.adas;

//实时数据传输 给 ADAS 速度和经纬度
public final class RealTimeCmd extends BaseCmd implements AdasCmdInterface{
    private byte[] dataArray=new byte[27];
    private byte[] cmdArray=new byte[36];
    @Override
    public byte[] getCmd() {
        setGongNengMa(0x31);//设置功能码  0X31    5.5.3  实时数据指令
        setDataArray(dataArray);

        cmdArray[1]=getJiaoYanMa();
        cmdArray[2]=getLsh()[0];
        cmdArray[3]=getLsh()[1];
        cmdArray[4]=getCsCode()[0];
        cmdArray[5]=getCsCode()[1];
        cmdArray[6]=getWaiSheCode();
        cmdArray[7]=getGongNengMa();


//10-17 经纬度  给假数据
        dataArray[10]=0;
        dataArray[11]=0;
        dataArray[12]=0;
        dataArray[13]=0;

        dataArray[14]=0;
        dataArray[15]=0;
        dataArray[16]=0;
        dataArray[17]=0;


        for (int i = 0; i < 27; i++) {
            cmdArray[i+8]=dataArray[i];
        }
        cmdArray[35]=0x7e;

        return alignCmdArray(cmdArray);
    }

    //设置数据--- 从 CAN总线获取数据
    public void setParm(byte carSpeed)
    {
        dataArray[0]=carSpeed;
    }

 /*   //设置数据
    public void setParm(byte carSpeed,int weidu,int jingdu)
    {
        dataArray[0]=carSpeed;

//        dataArray[10]=(byte) (weidu&0xff);
//        dataArray[11]=(byte) ((weidu>>8)&0xff);
//        dataArray[12]=(byte) ((weidu>>16)&0xff);
//        dataArray[13]=(byte) ((weidu>>24)&0xff);
//
//        dataArray[14]=(byte) (jingdu&0xff);
//        dataArray[15]=(byte) ((jingdu>>8)&0xff);
//        dataArray[16]=(byte) ((jingdu>>16)&0xff);
//        dataArray[17]=(byte) ((jingdu>>24)&0xff);


        dataArray[10]=(byte) ((weidu>>24)&0xff);
        dataArray[11]=(byte) ((weidu>>16)&0xff);
        dataArray[12]=(byte) ((weidu>>8)&0xff);
        dataArray[13]=(byte) (weidu&0xff);


        dataArray[14]=(byte) ((jingdu>>24)&0xff);
        dataArray[15]=(byte) ((jingdu>>16)&0xff);
        dataArray[16]=(byte) ((jingdu>>8)&0xff);
        dataArray[17]=(byte) (jingdu&0xff);

    }*/
}
