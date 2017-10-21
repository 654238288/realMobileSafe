package com.example.liujingjing.mobilesafe.MyApplication.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.View;

import com.example.liujingjing.mobilesafe.MyApplication.service.LockAppService;
import com.example.liujingjing.mobilesafe.R;
import com.example.liujingjing.mobilesafe.MyApplication.service.BlackPhoneService;
import com.example.liujingjing.mobilesafe.MyApplication.view.SettingClickView;
import com.example.liujingjing.mobilesafe.MyApplication.view.SettingItemsView;
import com.example.liujingjing.mobilesafe.MyApplication.service.PhoneLocationService;
import com.example.liujingjing.mobilesafe.MyApplication.util.ConstantValue;
import com.example.liujingjing.mobilesafe.MyApplication.util.SPUtil;
import com.example.liujingjing.mobilesafe.MyApplication.util.ServiceUtils;

public class SettingActivity extends BaseActivity {


    //土司样式数组
    private String[] mToastStyle;
    //数组索引(sp中存储的用户选中的)
    private int mToastStyleIndex;
    //土司样式组合控件
    private SettingClickView scv_toast_style;
    //土司放置位置组合控件
    private SettingClickView scv_toast_location;
    //拦截黑名单组合控件
    private SettingItemsView siv_black_phone;
    //程序锁组合控件
    private SettingItemsView siv_app_lock;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //初始化自动更新
        initUpdate();
        //初始化来电归属地查询
        initPhoneLocation();
        //初始化来电归属地土司样式
        initToastStyle();
        //初始化土司防止位置设置
        initToastLocation();
        //初始化拦截电话和短信
        initBlackNumber();
        //初始化程序锁
        initLockApp();
    }


    //初始化自动更新
    private void initUpdate() {
        //获取到SettingItemView对象
        final SettingItemsView itemView=(SettingItemsView)findViewById(R.id.siv_update);

        //获取已有的开关状态，用作显示,这里需要传入一个静态常量字符串，并且始终不变，可以封装到一个类中
        boolean updateState= SPUtil.getBoolean(this, ConstantValue.OPEN_UPDATE,false);
        //把获取到的状态设置给CheckBox
        itemView.setCheck(updateState);

        //给条目写点击事件
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取之前的选中状态，点击就取反
                boolean isCheck=itemView.isCheck();

                itemView.setCheck(!isCheck);

                //设置完之后再把当前状态设置给SharedPreferences
                SPUtil.putBoolean(getApplicationContext(),ConstantValue.OPEN_UPDATE,!isCheck);
            }
        });

    }



    //初始化来电归属地查询
    private void initPhoneLocation() {
        //获取到SettingItemView对象
        final SettingItemsView itemView=(SettingItemsView)findViewById(R.id.siv_phone_location);

        //因为该状态和服务挂钩，系统在内存不足时会自动关闭一些服务，为了保持UI页面显示和业务逻辑一致
        //因此这里的状态要和服务的运行状态绑定
        boolean isRunning= ServiceUtils.isRunning(this,"com.example.liujingjing.mobilesafe.activity.service.PhoneLocationService");

        //把获取到的状态设置给CheckBox
        itemView.setCheck(isRunning);

        //给条目写点击事件
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取之前的选中状态，点击就取反
                boolean isCheck=itemView.isCheck();

                itemView.setCheck(!isCheck);

                //如果开启了来电显示，显示土司
                if(!isCheck){
                    //因为土司显示在别的应用中，不依赖于本应用的活动，因此写在服务中
                    startService(new Intent(getApplicationContext(),PhoneLocationService.class));
                }
            }
        });

    }



    //初始化来电归属地显示土司样式
    private void initToastStyle() {
        //先拿到组合控件
        scv_toast_style= (SettingClickView) findViewById(R.id.scv_toast_style);
        //设置标题
        scv_toast_style.setTitle("设置归属地显示风格");
        //在sp中存储的样式拿出来
        mToastStyleIndex=SPUtil.getInt(this,ConstantValue.TOAST_STYLE,0);
        mToastStyle=new String[]{"透明","橙色","蓝色","灰色","绿色"};
        //设置选中的样式
        scv_toast_style.setDes(mToastStyle[mToastStyleIndex]);

        //设置点击事件
        scv_toast_style.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //弹出对话框，让用户选择
                showDialog();
            }
        });
    }


    public void  showDialog(){
        final AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        //给对话框设置图标
        dialog.setIcon(R.drawable.ic_launcher);
        //给对话框设置标题
        dialog.setTitle("请选择归属地样式");
        //给对话框设置单条目选中事件(数组，索引，监听器)
        dialog.setSingleChoiceItems(mToastStyle, mToastStyleIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //把选中的i值存储到sp中
                SPUtil.putInt(getApplicationContext(),ConstantValue.TOAST_STYLE,i);
                //关闭对话框
                dialogInterface.dismiss();
                //给控件设置描述值
                scv_toast_style.setDes(mToastStyle[i]);
            }
        });

        //不想重设风格
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialog.show();
    }




    //初始化土司放置位置
    private void initToastLocation() {
        //1.拿到该组合控件，作为成员变量
        scv_toast_location= (SettingClickView) findViewById(R.id.scv_toast_location);
        //2.设置标题
        scv_toast_location.setTitle("归属地提示框的位置");
        //3.设置描述内容
        scv_toast_location.setDes("设置归属地提示框的位置");
        //4.设置点击事件
        scv_toast_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开启一个半透明的活动
                startActivity(new Intent(getApplicationContext(),ToastLocationActivity.class));
            }
        });
    }


    private void initBlackNumber() {
        //拿控件
        siv_black_phone= (SettingItemsView) findViewById(R.id.siv_black_phone);
        //用服务是否正在运行的工具类来决定该控件的选中状态
        boolean t=ServiceUtils.isRunning(this,"com.example.liujingjing.mobilesafe.MyApplication.service.BlackPhoneService");
        siv_black_phone.setCheck(t);

        siv_black_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击切换选中状态
                boolean b=siv_black_phone.isCheck();
                siv_black_phone.setCheck(!b);
                //如果为选中状态
                if (!b){
                    startService(new Intent(getApplicationContext(), BlackPhoneService.class));
                }else {
                    stopService(new Intent(getApplicationContext(), BlackPhoneService.class));
                }
            }
        });
    }



    //处理程序锁
    private void initLockApp() {
        //拿控件
        siv_app_lock= (SettingItemsView) findViewById(R.id.siv_app_lock);
        //处理点击事件
        //用服务是否正在运行的工具类来决定该控件的选中状态
        boolean t=ServiceUtils.isRunning(this,"com.example.liujingjing.mobilesafe.MyApplication.service.LockAppService");
        siv_app_lock.setCheck(t);

        siv_app_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击切换选中状态
                boolean b=siv_app_lock.isCheck();
                siv_app_lock.setCheck(!b);
                //如果为选中状态,开启服务监听点击的应用是不是在已加锁的数据库里
                if (!b){
                    startService(new Intent(getApplicationContext(), LockAppService.class));
                }else {
                    stopService(new Intent(getApplicationContext(), LockAppService.class));
                }
            }
        });
    }


}
