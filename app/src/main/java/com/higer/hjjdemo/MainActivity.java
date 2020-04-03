package com.higer.hjjdemo;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.higer.lowermachinelibrary.Subject;
import com.higer.lowermachinelibrary.VehicleMcuModule;
import com.higer.lowermachinelibrary.activitys.FrontRadarActivity;
import com.higer.lowermachinelibrary.activitys.VideoActivity;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ReceiveModule receiveModule;
    VehicleMcuModule vehicleMcuModule;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        receiveModule = new ReceiveModule();
        findViewById(R.id.id_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Map<String, String> map = new HashMap();
                map.put("McuData", "0"); //0从网络获取下位机数据  1通过串口获取下位机数据 2通过蓝牙获取下位机数据
                //        "DoubleLocate"=>"0" //0单个GPS  1两个GPS(A2车型)
                map.put("DoubleLocate", "0");
                map.put("McuUpdate", "1");//"McuUpdate"=>"0" //0老版本下位机  1新版本下位机
                map.put("SfzReader", "0");//        "SfzReader"=>"0" //0不使用身份证读卡器 1使用串口读卡器 2使用USB读卡器
                map.put("Led", "0");//       Led"=>"0" //0不使用LED  1使用LED
                //  map.put("LOG","ON");
                map.put("LOG", "OFF");


                vehicleMcuModule = Subject.getMuc();
                vehicleMcuModule.doMcuInit(null, map);

                vehicleMcuModule.setMainCallback(receiveModule);




            }
        });

        findViewById(R.id.id_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        findViewById(R.id.id_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, VideoActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.id_leida).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, FrontRadarActivity.class);
                startActivity(intent);
            }
        });
    }
}
