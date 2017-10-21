package com.example.liujingjing.mobilesafe.MyApplication.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.liujingjing.mobilesafe.R;
import com.example.liujingjing.mobilesafe.MyApplication.view.SettingItemsView;
import com.example.liujingjing.mobilesafe.MyApplication.util.ConstantValue;
import com.example.liujingjing.mobilesafe.MyApplication.util.SPUtil;

public class Setup2Activity extends BaseSetupActivity {

    private SettingItemsView siv_phoneCard_bound;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
        //初始化sim卡绑定与否的组合控件
        InitUi();
    }


    //点击上一页
    @Override
    public void showPrePage() {
        Intent intent=new Intent(getApplicationContext(),Setup1Activity.class) ;
        startActivity(intent);
        finish();
        //平移动画
        overridePendingTransition(R.anim.pre_in_anim,R.anim.pre_out_anim);
    }

    //点击下一页
    @Override
    public void showNextPage() {
        //获取是否绑定了sim卡，绑定则进入下一页，未绑定则提示需要绑定sim卡
        boolean b=siv_phoneCard_bound.isCheck();
        if(b) {
            Intent intent = new Intent(getApplicationContext(), Setup3Activity.class);
            startActivity(intent);
            finish();
            //平移动画
            overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);
        }else{
            Toast.makeText(this,"您需要绑定sim卡",Toast.LENGTH_SHORT).show();
        }
    }


    //拿到组合控件，显示它选中或者未选中的状态，给它写点击事件,若改变状态，存储起来
    private void InitUi() {

        //1.进入该页面时,显示绑定的状态
        //1.1拿到组合控件
        siv_phoneCard_bound=(SettingItemsView)findViewById(R.id.siv_phoneCard_bound);
        //1.2从sp中将它绑定的序列卡号拿出来
        String sim_serialNum= SPUtil.getString(this, ConstantValue.SIM_NUMBER,"");
        //1.3如果它为空，说明还未绑定，把绑定状态设置为未绑定
        if(TextUtils.isEmpty(sim_serialNum)){
            siv_phoneCard_bound.setCheck(false);
        }else {
            //1.4如果它不为空，说明已绑定，把绑定状态设置为绑定
            siv_phoneCard_bound.setCheck(true);
        }

        //2.点击时改变状态
        siv_phoneCard_bound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        //获取原有的状态，点击就取反
        boolean state=siv_phoneCard_bound.isCheck();
        siv_phoneCard_bound.setCheck(!state);
        //一旦状态改变了，就要把sim卡的序列号存储或者删除
        //存储
        if(!state){
            saveSimCard();

        }else{
            //删除
            SPUtil.remove(getApplicationContext(),ConstantValue.SIM_NUMBER);
                }
            }
        });

    }


   /* //申请运行时权限回调此方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    //获取sim卡的序列号
                    saveSimCard();
                }
                break;
            default:
        }
    }*/



    public void saveSimCard(){
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        String simNumber = manager.getSimSerialNumber();
        //存储
        SPUtil.putString(getApplicationContext(), ConstantValue.SIM_NUMBER, simNumber);
    }
}
