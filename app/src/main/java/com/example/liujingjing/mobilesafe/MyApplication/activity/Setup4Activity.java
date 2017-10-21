package com.example.liujingjing.mobilesafe.MyApplication.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.liujingjing.mobilesafe.R;
import com.example.liujingjing.mobilesafe.MyApplication.util.ConstantValue;
import com.example.liujingjing.mobilesafe.MyApplication.util.SPUtil;

public class Setup4Activity extends BaseSetupActivity {

    private CheckBox cb_setup_over;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);

        //获取控件
        initUI();

    }


    //点击上一页
    @Override
    public void showPrePage() {
        Intent intent=new Intent(getApplicationContext(),Setup3Activity.class) ;
        startActivity(intent);
        finish();
        //平移动画
        overridePendingTransition(R.anim.pre_in_anim,R.anim.pre_out_anim);
    }

    //点击下一页
    @Override
    public void showNextPage() {
        boolean open=SPUtil.getBoolean(getApplicationContext(),ConstantValue.OPEN_SECURITY,false);
        if(open) {
            Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
            startActivity(intent);
            finish();
            SPUtil.putBoolean(this, ConstantValue.SETUP_OVER, true);
            //平移动画
            overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);
        }else{
            Toast.makeText(getApplicationContext(),"请开启防盗保护",Toast.LENGTH_SHORT).show();
        }
    }

    private void initUI() {
        //安全设置开启或者关闭状态的回显
        cb_setup_over= (CheckBox) findViewById(R.id.cb_setup_over);
        boolean open_security=SPUtil.getBoolean(this, ConstantValue.OPEN_SECURITY,false);
        cb_setup_over.setChecked(open_security);
        if(open_security){
            cb_setup_over.setText("安全设置已开启");
        }else{
            cb_setup_over.setText("安全设置已关闭");
        }

        //点击切换开启状态
        cb_setup_over.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //b即为复选框选中与否的状态
                SPUtil.putBoolean(getApplicationContext(),ConstantValue.OPEN_SECURITY,b);
                if(b){
                    cb_setup_over.setText("安全设置已开启");
                }else{
                    cb_setup_over.setText("安全设置已关闭");
                }
            }
        });
    }

}
