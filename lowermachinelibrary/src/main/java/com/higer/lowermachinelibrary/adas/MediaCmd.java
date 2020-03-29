package com.higer.lowermachinelibrary.adas;

public final class MediaCmd extends BaseCmd implements AdasCmdInterface {

    private byte[] dataArray=new byte[5];
    private byte[] cmdArray=new byte[14];
    @Override
    public byte[] getCmd() {
        //   setWaiSheCode(0x65);//设置外设  ADAS
        //  setWaiSheCode(cmdArray[39]);
        setGongNengMa(0x50);//设置功能码  0X50  请求多媒体数据指令
        setDataArray(dataArray);
        cmdArray[0]=0x7e;
        cmdArray[1]=getJiaoYanMa();
        cmdArray[2]=getLsh()[0];
        cmdArray[3]=getLsh()[1];

        cmdArray[4]=getCsCode()[0];
        cmdArray[5]=getCsCode()[1];

        cmdArray[6]=getWaiSheCode();

        cmdArray[7]=getGongNengMa();

        cmdArray[8]=dataArray[0];
        cmdArray[9]=dataArray[1];
        cmdArray[10]=dataArray[2];
        cmdArray[11]=dataArray[3];
        cmdArray[12]=dataArray[4];

        cmdArray[13]=0x7e;


        return alignCmdArray(cmdArray);
    }




    //设置消息 ID
    public void setInfoId(byte infoId) {
        dataArray[0]=infoId;
    }


    //设置多媒体 ID
    public void setMediaId(byte[] inputArray,int iPos) {
        if(inputArray.length>(iPos+4))
        {
            for (int i = 0; i < 4; i++) {
                dataArray[i+1]=inputArray[iPos+i];
            }
        }

    }
}
