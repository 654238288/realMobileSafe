package com.example.liujingjing.mobilesafe.MyApplication.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.liujingjing.mobilesafe.R;
import com.example.liujingjing.mobilesafe.MyApplication.util.ConstantValue;
import com.example.liujingjing.mobilesafe.MyApplication.util.SPUtil;

public class Setup3Activity extends BaseSetupActivity {

    private Button btn_contact_choose;
    private EditText et_safe_number;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);

        //拿到选择联系人的按钮
        initUI();
    }


    //点击上一页
    @Override
    public void showPrePage() {
        Intent intent=new Intent(Setup3Activity.this,Setup2Activity.class) ;
        startActivity(intent);
        finish();
        //平移动画
        overridePendingTransition(R.anim.pre_in_anim,R.anim.pre_out_anim);
    }


    //点击下一页
    @Override
    public void showNextPage() {
        //先判断编辑框的电话号是否存在，存在则进入下一步，不存在则提示请输入安全号码
        String phone=et_safe_number.getText().toString();
        if(!TextUtils.isEmpty(phone)) {
            Intent intent = new Intent(getApplicationContext(), Setup4Activity.class);
            startActivity(intent);
            finish();
            //如果这个号码是输入的，也要存储起来
            SPUtil.putString(getApplicationContext(),ConstantValue.CONTACT_PHONE,phone);
            //平移动画
            overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);
        }else{
            Toast.makeText(this,"请选择联系人",Toast.LENGTH_SHORT).show();
        }
    }

    private void initUI() {
        btn_contact_choose = (Button) findViewById(R.id.btn_contact_choose);
        et_safe_number = (EditText) findViewById(R.id.et_safe_number);

        //如果已经设置过安全号码，进入该界面时要回显
        String phoneNum=SPUtil.getString(this,ConstantValue.CONTACT_PHONE,"");
        et_safe_number.setText(phoneNum);

        //给按钮注册点击事件
        btn_contact_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //要获取到手机通讯录的联系人列表，要跳转到下一个活动
                Intent intent=new Intent(Setup3Activity.this,ContactsActivity.class);
                startActivityForResult(intent,0);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //把带结果的intent中的值取出来，设置给编辑框，并存储到sp当中
        if(data!=null){
            String phone=data.getStringExtra("phone");
            //去除空格和-字符
            phone=phone.replace("-","").replace(" ","").trim();
            et_safe_number.setText(phone);
            SPUtil.putString(this, ConstantValue.CONTACT_PHONE,phone);
        }
    }


}
