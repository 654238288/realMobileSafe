package com.example.liujingjing.mobilesafe.MyApplication.activity;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.liujingjing.mobilesafe.R;

public class AToolActivity extends BaseActivity {

    private TextView tv_phone_address;
    private TextView tv_lock;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atool);

        //电话归属地查询
        initPhoneAddress();
        //程序锁
        initAppLock();
    }

    private void initAppLock() {
        //设置程序锁的控件
        tv_lock= (TextView) findViewById(R.id.tv_lock);
        tv_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到新界面
                startActivity(new Intent(getApplicationContext(),AppLockActivity.class));
            }
        });
    }


    private void initPhoneAddress() {
        //获取号码归属地查询的控件
        tv_phone_address= (TextView) findViewById(R.id.tv_phone_address);
        tv_phone_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           //跳转到新界面
           startActivity(new Intent(getApplicationContext(),QuerryAddressActivity.class));
            }
        });
    }
}
