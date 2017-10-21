package com.example.liujingjing.mobilesafe.MyApplication.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

import com.example.liujingjing.mobilesafe.R;

public class Setup1Activity extends BaseSetupActivity {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
    }

    @Override
    public void showPrePage() {

    }

    //点击下一页
    @Override
    public void showNextPage() {
        Intent intent=new Intent(getApplicationContext(),Setup2Activity.class) ;
        startActivity(intent);
        finish();

        //平移效果
        overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);
    }

}
