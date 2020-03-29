package com.higer.lowermachinelibrary.adas;

import com.higer.lowermachinelibrary.statics.Config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public final class VideoEntity extends BaseCmd{
    private int allPackages;//总包数
    private int index;//包序号
    private int lsh;//流水号

    private byte[] yingdaArrayData=new byte[10];
    private byte[] videoData;


    public int getAllPackages() {
        return allPackages;
    }

    public void setAllPackages(int allPackages) {
        this.allPackages = allPackages;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public byte[] getVideoData() {
        return videoData;
    }

    public void setVideoData(byte[] videoData) {
        this.videoData = videoData;
    }

    public boolean parseVideoData(byte[] array,int iLen) {
        if (array != null) {
            //    System.out.println("====="+ArraytoHexString(array,iLen));
            if (iLen > 17) {
                if (((array[0] & 0xff) == 0x7e) && ((array[iLen - 1] & 0xff) == 0x7e)) {
                    lsh=((array[2] & 0xff) << 8) + (array[3] & 0xff)+1;
                    setChangShangCode(array[4],array[5]);

                    setWaiSheCode(array[6]);
                    setGongNengMa(array[7]);

                    yingdaArrayData[0]=array[8];//消息ID
                    yingdaArrayData[1]=array[9];//多媒体ID
                    yingdaArrayData[2]=array[10];//多媒体ID
                    yingdaArrayData[3]=array[11];//多媒体ID
                    yingdaArrayData[4]=array[12];//多媒体ID


                    setInfoId(array[8]&0xff);
                    int mediaId=((array[9]&0xff)<<24)+((array[10]&0xff)<<16)+((array[11]&0xff)<<8)+(array[12]&0xff);
                    setMediaId(mediaId);

                    yingdaArrayData[5]=array[13];
                    yingdaArrayData[6]=array[14];
                    yingdaArrayData[7]=array[15];
                    yingdaArrayData[8]=array[16];

                    allPackages = ((array[13] & 0xff) << 8) + (array[14] & 0xff);
                    index = ((array[15] & 0xff) << 8) + (array[16] & 0xff);

                    videoData = new byte[iLen - 18];
                    for (int i = 17; i < (iLen - 1); i++) {
                        videoData[i - 17] = array[i];
                    }
                    return true;
                }
            }
        }
        return false;
    }



    public void wirteFile(){

        try {
            String strFileName= Config.prefixStr+getFileName();
          System.out.println(strFileName);
            File file=new File(strFileName);
            if(!file.exists())
            {
                file.createNewFile();
            }
            FileOutputStream fileOutputStream=new FileOutputStream(file,true);

            fileOutputStream.write(videoData);
            fileOutputStream.flush();
            fileOutputStream.close();

            System.out.println("=====写入 SD 卡  "+strFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public String toString() {
        return "index="+index+"  allPages="+allPackages+"  mediaId="+getMediaId()+"   infoId="+getInfoId();
    }

    public  byte[] getNextPackgeCmd()
    {
        byte[] cmdArray=new byte[19];
        yingdaArrayData[9]=0;
        setDataArray(yingdaArrayData);
        cmdArray[0]=0x7e;
        cmdArray[1]=getJiaoYanMa();
        cmdArray[2]=getLsh()[0];
        cmdArray[3]=getLsh()[1];

        cmdArray[4]=getCsCode()[0];
        cmdArray[5]=getCsCode()[1];

        cmdArray[6]=getWaiSheCode();

        cmdArray[7]=getGongNengMa();

        for (int i = 0; i <10 ; i++) {
            cmdArray[8+i]=yingdaArrayData[i];
        }
        cmdArray[18]=0x7e;
        return alignCmdArray(cmdArray);
    }
}
