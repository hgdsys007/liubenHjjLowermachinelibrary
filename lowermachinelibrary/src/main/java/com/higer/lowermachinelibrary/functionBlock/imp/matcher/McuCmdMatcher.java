package com.higer.lowermachinelibrary.functionBlock.imp.matcher;

import com.higer.lowermachinelibrary.functionBlock.IMatcher;
import com.higer.lowermachinelibrary.functionBlock.IParser;
import com.higer.lowermachinelibrary.log.Logger;
import com.higer.lowermachinelibrary.statics.Config;
import com.higer.lowermachinelibrary.utils.ErrorMessageUtil;
import com.higer.lowermachinelibrary.utils.StringHexUtil;
import com.higer.lowermachinelibrary.virtualIo.VirtualMcuUpIo;

import cn.base.entity.VehicleMsg;

final public class McuCmdMatcher implements IMatcher {
    boolean isHead=false;
    int iPos=0;
    @Override
    public void parseBuffer(byte[] buffer, int iCount) {
     //  System.out.println("McuCmdMatcher:parseBuffer   "+ StringHexUtil.ArraytoAsciiString(buffer,0,iCount));
        //$HG:CControl{Ok}
        //$HG:CCheckID{GetID_3532343830385102003A0016}
        //$HG:CQyCfg{COM0:115200.8.N.1,COM1:115200.8.N.1,COM2:115200.8.N.1,COM3:9600.8.N.1,COM4:115200.8.N.1,COM5:115200.8.N.1,COM6:9600.8.N.1,COM7:115200.8.N.1,ServerIp:192.168.1.100,ProgPeriod:50ms}
        if((buffer[5]=='C')&&(buffer[6]=='h')&&(buffer[7]=='e'))// $HG:CCheckID{GetID_3532343830385102003A0016}
        {
            isHead=false;
            iPos=0;
            for (int i = 0; i <iCount ; i++) {
                if(buffer[i]=='_')
                {
                    isHead=true;
                    continue;
                }
                if(buffer[i]=='}')
                {
                    isHead=false;
                    Config.mcuId=StringHexUtil.ArraytoAsciiString(buffer,0,iPos);
                 //   System.out.println("McuCmdMatcher:parseBuffer   "+StringHexUtil.ArraytoAsciiString(buffer,0,iPos));
                    return;
                }
                if(isHead)
                {
                    buffer[iPos++]=buffer[i];
                }
            }
        }else if((buffer[5]=='Q')&&(buffer[6]=='y')&&(buffer[7]=='C'))//$HG:CQyCfg{COM0
        {
            String strTemp=StringHexUtil.ArraytoAsciiString(buffer,0,iCount);
            // $HG:CQyCfg{COM0:115200.8.N.1,COM1:115200.8.N.1,COM2:115200.8.N.1,COM3:9600.8.N.1,COM4:115200.8.N.1,COM5:115200.8.N.1,COM6:9600.8.N.1,COM7:115200.8.N.1,ServerIp:172.16.16.100,ProgPeriod:50ms,Version:2.22}
            int iStart=strTemp.indexOf("{")+1;
            int iStop=strTemp.indexOf("}");
            strTemp=strTemp.substring(iStart,iStop);
            String[] array=strTemp.split(",");
            for (String item:array ) {
                //  System.out.println(item);
                if(item.startsWith("Version"))
                {
                    Config.mcuVersion=item.substring(item.indexOf(":")+1);
                    Logger.writeLog("---下位机返回版本号："+Config.mcuVersion);
                    break;
                }
            }
        }else if((buffer[5]=='S')&&(buffer[6]=='e')&&(buffer[7]=='t'))//   $HG:CSetIODly{Ok}
        {
            String strTemp=StringHexUtil.ArraytoAsciiString(buffer,0,iCount);

            Logger.writeLog("接收到下位机返回结果："+strTemp);
//   $HG:CSetIODly{Ok}
            strTemp=strTemp.toLowerCase();
            if(strTemp.contains("ok"))
            {
                //  notifyVehicleError

                VehicleMsg msg = new VehicleMsg();
                msg.setCode(100);
                msg.setMsg("设置成功");
                ErrorMessageUtil.getInstance().notifyMsg(msg);
            }else{
                VehicleMsg msg = new VehicleMsg();
                msg.setCode(100);
                msg.setMsg("设置失败");
                ErrorMessageUtil.getInstance().notifyMsg(msg);
            }
        }
    }

}
