package com.example.liujingjing.mobilesafe.MyApplication.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liujingjing.mobilesafe.R;
import com.example.liujingjing.mobilesafe.MyApplication.service.SmsService;
import com.example.liujingjing.mobilesafe.MyApplication.util.ConstantValue;
import com.example.liujingjing.mobilesafe.MyApplication.util.Md5Util;
import com.example.liujingjing.mobilesafe.MyApplication.util.SPUtil;

public class HomeActivity extends BaseActivity {

    //存放功能列表的名字
     private String[] mTitleDes;
    //存放功能列表的图片
    private int[] mTitleImage;
    //定义成员变量九宫格
    private GridView gv_ItemView;




    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //初始化UI
        initUI();
        //初始化数据
        initData();

        startService(new Intent(this, SmsService.class));
    }






    //初始化数据
    private void initData() {
        //需要一个九宫格的功能列表展示，需要两个数组分别存放图片和文字
        /*mTitleDes=new String[]{"手机防盗","通信卫士","软件管理","进程管理",
                "流量统计","手机杀毒","缓存清理","高级工具","设置中心"};*/
        Resources res=getResources();
        //获取string.xml中定义的字符串数组
        mTitleDes=res.getStringArray(R.array.arr_title_name);

        mTitleImage=new int[]{R.drawable.home_safe,R.drawable.home_callmsgsafe,
                R.drawable.home_apps,R.drawable.home_taskmanager,
                R.drawable.home_netmanager,R.drawable.home_trojan,
                R.drawable.home_sysoptimize,R.drawable.home_tools,R.drawable.home_settings};

        //显示九宫格数据

        gv_ItemView.setAdapter(new MyAdapter());
        // 给每个子条目写点击事件
        gv_ItemView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        //手机防盗，显示输入密码的对话框
                        showDialog();
                        break;
                    case 1:
                        //跳转到黑名单列表
                        startActivity(new Intent(getApplicationContext(),BlackPhoneActivity.class));
                        break;
                    case 2:
                        //跳转到黑名单列表
                        startActivity(new Intent(getApplicationContext(),AppManagerActivity.class));
                        break;
                    case 6:
                        //点击设置中心时执行
                        startActivity(new Intent(getApplicationContext(),WipeCacheDataActivity.class));
                        break;
                    case 7:
                        //点击设置中心时执行
                        startActivity(new Intent(getApplicationContext(),AToolActivity.class));
                        break;
                    case 8:
                        //点击设置中心时执行
                        Intent intent=new Intent(HomeActivity.this,SettingActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });

    }


    public void showDialog(){
       //把设置过的密码存储到SharedPreferences中，以便判断,最开始默认为没有密码
       String mobile_safe_psd= SPUtil.getString(getApplicationContext(),
               ConstantValue.MOBILE_SAFE_PSD,"");
        //判断
        //1.如果是第一次登录，弹出输入密码并确认密码的对话框
        if(TextUtils.isEmpty(mobile_safe_psd)){
            showSetPsdDialog();
        }else {
            //2.如果是第二次登录，已经设置过密码，弹出确认密码的对话框
            showConfirmDialog();
        }

    }


    //第一次登录手机防盗模块，设置密码
    private void showSetPsdDialog() {
        //对话框必须绑定在活动上
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        //自定义对话框，需要创建出来，方便设置样式
        final AlertDialog dialog=builder.create();
        //给对话框设置一个View布局
        final View view=View.inflate(this,R.layout.dialog_set_psd,null);
        dialog.setView(view);
        dialog.show();

        //给按钮写事件
        Button btn_submit= (Button) view.findViewById(R.id.btn_submit);
        Button btn_cancel= (Button) view.findViewById(R.id.btn_cancel);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断两个密码是否有空的
                EditText et1= (EditText) view.findViewById(R.id.set_psd);
                EditText et2= (EditText) view.findViewById(R.id.confirm_psd);
                String et1_set_psd=et1.getText().toString();
                String et2_confirm_psd=et2.getText().toString();
                if(TextUtils.isEmpty(et1_set_psd)||TextUtils.isEmpty(et2_confirm_psd)){
                    Toast.makeText(getApplicationContext(),"请输入密码",Toast.LENGTH_SHORT).show();
                }else{
                    if(et1_set_psd.equals(et2_confirm_psd)){
                        //进入手机防盗页面
                        Intent intent=new Intent(HomeActivity.this,SetupOverActivity.class);
                        startActivity(intent);
                        //为防止退出防盗页面时退出到确认密码对话框，因此隐藏掉对话框
                        dialog.dismiss();
                        //把设置的密码使用md5算法加密后存入到sp中
                        SPUtil.putString(getApplicationContext(),ConstantValue.MOBILE_SAFE_PSD,
                                Md5Util.encoder(et1_set_psd));
                    }else{
                        Toast.makeText(getApplicationContext(),"两次输入密码不一致",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消设置密码，隐藏对话框，回到九宫格界面
                dialog.dismiss();
            }
        });

    }



    //之后登录手机防盗模块，确认密码
    private void showConfirmDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        final AlertDialog dialog=builder.create();

        final View view=View.inflate(this,R.layout.dialog_confirm_psd,null);
        dialog.setView(view);
        dialog.show();

        //获取按钮并写点击事件
        Button confirm_btn_submit= (Button) view.findViewById(R.id.confirm_btn_submit);
        Button confirm_btn_cancel= (Button) view.findViewById(R.id.confirm_btn_cancel);

        confirm_btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //把输入的密码和存储在sp当中的密码进行比对，如果一致，则进入手机防盗界面
                EditText et_btn_submit= (EditText) view.findViewById(R.id.confirm2_psd);
                String confirm2_psd=et_btn_submit.getText().toString();
                String psd=SPUtil.getString(getApplicationContext(),ConstantValue.MOBILE_SAFE_PSD,"");
                //先判空
                if(TextUtils.isEmpty(confirm2_psd)){
                    Toast.makeText(getApplicationContext(),"请输入密码",Toast.LENGTH_SHORT).show();
                }else{
                    //判断存储在sp中加过密的密码和用户输入的密码加密后的值进行比较
                    if(psd.equals(Md5Util.encoder(confirm2_psd))){
                        //再判断是否开启了防盗保护
                        boolean safe_open=SPUtil.getBoolean(getApplicationContext(),
                                ConstantValue.OPEN_SECURITY,false);
                        if (safe_open) {
                            Intent intent = new Intent(HomeActivity.this, SetupOverActivity.class);
                            startActivity(intent);
                        }else{
                            //如果没有开启防盗保护，再判断设置到哪一步了
                            //判断是否设置了sim卡变更提醒
                            String  sim_number=SPUtil.getString(getApplicationContext(),
                                    ConstantValue.SIM_NUMBER,"");
                            if(TextUtils.isEmpty(sim_number)){
                                Intent intent = new Intent(HomeActivity.this, Setup2Activity.class);
                                startActivity(intent);
                            }
                            //判断是否设置了安全号码
                            String  contact_phone=SPUtil.getString(getApplicationContext(),
                                    ConstantValue.CONTACT_PHONE,"");
                            if(TextUtils.isEmpty(contact_phone)){
                                Intent intent = new Intent(HomeActivity.this, Setup3Activity.class);
                                startActivity(intent);
                            }
                            //第二步和第三步都设置完了就跳转到第四个设置页面
                            Intent intent = new Intent(HomeActivity.this, Setup4Activity.class);
                            startActivity(intent);
                        }
                            dialog.dismiss();
                    }else{
                        Toast.makeText(getApplicationContext(),"密码错误",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        confirm_btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }



    private void initUI() {
        gv_ItemView= (GridView) findViewById(R.id.gv_itemView);
    }



    //定义自己的列表数据适配器
    class  MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            //返回条目总数

            return mTitleDes.length;
        }

        @Override
        public Object getItem(int i) {
            //返回定位到某一个子菜单
            return i;
        }

        @Override
        public long getItemId(int i) {
            //返回子菜单id
            return i;
        }

        //返回设置好数据的view
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            //用inflate()返回一个View对象
            view=View.inflate(getApplicationContext(),R.layout.gridview_itemlist,null);
            TextView tv= (TextView) view.findViewById(R.id.tv_itemDes);
            ImageView iv= (ImageView) view.findViewById(R.id.iv_itemImage);
            //把xml文件里定义好的控件设置进去
            tv.setText(mTitleDes[i]);
            iv.setBackgroundResource(mTitleImage[i]);
            return view;
        }
    }
}
