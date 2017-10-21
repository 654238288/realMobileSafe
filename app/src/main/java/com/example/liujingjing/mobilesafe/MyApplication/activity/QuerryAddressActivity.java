package com.example.liujingjing.mobilesafe.MyApplication.activity;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liujingjing.mobilesafe.R;
import com.example.liujingjing.mobilesafe.MyApplication.engine.PhoneAddressDao;

public class QuerryAddressActivity extends BaseActivity {

    private EditText et_phone;
    private Button bt_query;
    private TextView tv_result;
    private String mPhoneAddress;
    private Handler mHandler=new Handler(){
        public void handleMessage(android.os.Message msg){
            tv_result.setText(mPhoneAddress);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_querry_address);

        //初始化UI
        initUI();
    }

    private void initUI() {
        et_phone= (EditText) findViewById(R.id.et_phone);
        bt_query= (Button) findViewById(R.id.bt_query);
        tv_result= (TextView) findViewById(R.id.tv_result);
        //点击查询时查询归属地
        bt_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(TextUtils.isEmpty(et_phone.getText().toString()))){
                    //查询归属地，属于耗时操作，丢给子线程
                    lookForAddress(et_phone.getText().toString());
                }else{
                    //如果什么也没输入，输入框抖动提示
                    Animation shake= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.shake);
                    et_phone.setAnimation(shake);
                    Toast.makeText(getApplicationContext(),"请输入手机号码",Toast.LENGTH_SHORT).show();
                    //手机震动(震动要加权限)
                    Vibrator vibrator= (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    //数组中参数表示(震动时间，不振动时间，震动时间，不振动时间...)，参数二表示数组中事件重复次数
                    vibrator.vibrate(new long[]{2000,5000},2);
                }
            }
        });

        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //实时查询归属地132
                lookForAddress(et_phone.getText().toString());
            }
        });

    }



    //查询phone的号码归属地
    private void lookForAddress(final String phone) {
        //开启一个子线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                mPhoneAddress= PhoneAddressDao.getAddress(phone);
                //把这个结果填充给文本框，涉及到UI操作，扔回给主线程
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }
}
