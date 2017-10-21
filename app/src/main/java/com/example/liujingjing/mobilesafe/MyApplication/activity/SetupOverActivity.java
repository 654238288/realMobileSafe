package com.example.liujingjing.mobilesafe.MyApplication.activity;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.liujingjing.mobilesafe.R;
import com.example.liujingjing.mobilesafe.MyApplication.util.ConstantValue;
import com.example.liujingjing.mobilesafe.MyApplication.util.SPUtil;

public class SetupOverActivity extends BaseActivity {

    private TextView reset_setup;//重新设置安全向导
    private TextView tv_safe_number;//显示安全号码
    private ImageView iv_lock;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //确认密码完成之后，判断有没有设置过手机防盗，
        boolean set_up_over= SPUtil.getBoolean(this, ConstantValue.SETUP_OVER,false);
        if(set_up_over){
            // 如果四个界面都设置完了，停留在该功能列表界面
           setContentView(R.layout.activity_setup_over);
            //处理页面控件的事件
            initUI();
        }else {
        // 如果没有将四个界面设置完成，进入手机防盗设置界面1
            Intent intent=new Intent(this,Setup1Activity.class);
            startActivity(intent);
            finish();
        }

    }

    private void initUI() {
        //获取安全号码予以显示
        String safe_phone=SPUtil.getString(this,ConstantValue.CONTACT_PHONE,"");
        tv_safe_number = (TextView) findViewById(R.id.tv_safe_number);
        tv_safe_number.setText(safe_phone);
        //锁
        iv_lock= (ImageView) findViewById(R.id.iv_lock);
        final boolean safe_open=SPUtil.getBoolean(getApplicationContext(),
                ConstantValue.OPEN_SECURITY,false);
        if (!safe_open) {
            iv_lock.setImageResource(R.drawable.unlock);
        }

        //重新进入设置向导
        reset_setup= (TextView) findViewById(R.id.reset_setup);
        reset_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),Setup1Activity.class);
                startActivity(intent);
                finish();
            }
        });

        //点击锁的图片，改变开启和关闭安全设置
        iv_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果安全设置未开启，点击之后为已开启
                if (!safe_open){
                    //把sharedPreference中的开启状态改为false
                    SPUtil.putBoolean(getApplicationContext(),ConstantValue.OPEN_SECURITY,true);
                    //图片换成锁住
                    iv_lock.setImageResource(R.drawable.lock);
                }else{
                    SPUtil.putBoolean(getApplicationContext(),ConstantValue.OPEN_SECURITY,false);
                    iv_lock.setImageResource(R.drawable.unlock);
                }
            }
        });
    }
}
