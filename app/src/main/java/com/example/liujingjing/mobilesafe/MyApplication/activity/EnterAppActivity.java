package com.example.liujingjing.mobilesafe.MyApplication.activity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liujingjing.mobilesafe.R;
import com.example.liujingjing.mobilesafe.db.dao.AppLockDao;
import com.example.liujingjing.mobilesafe.db.domain.AppInfo;

import java.util.List;


//点击的程序加锁之后，输入密码的界面
/*配置该活动启动模式为单例模式，则该活动会在一个单独的任务栈里，而不依赖于手机卫士的应用，
就不会出现输入程序锁密码之后回到手机卫士的页面*/
public class EnterAppActivity extends AppCompatActivity {
    private ImageView iv_lock_app_icon;
    private TextView tv_lock_app_name;
    private EditText et_lock_psd;
    private Button bt_lock_psd_submit;
    private PackageManager mPM;
    private AppLockDao mDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_app);
        Log.d("LJJ","创建了输入密码框的活动");
        initUI();
        initData();
    }

    private void initData() {
        //拿intent传递过来的包名
        String pkgName= getIntent().getStringExtra("packageName");
        Log.d("LJJ","点击的应用包名为："+pkgName);
        mPM=getPackageManager();
        mDao=AppLockDao.getInstance(this);
        try {
            //用包管理者对象来拿到应用
            final ApplicationInfo appInfo = mPM.getApplicationInfo(pkgName, 0);
            Log.d("ljj","进入加锁应用时获取的那个应用："+appInfo.loadIcon(mPM).toString());
            //在输入密码页面上显示该应用的图标、名字
            iv_lock_app_icon.setBackground(appInfo.loadIcon(mPM));
            tv_lock_app_name.setText(appInfo.loadLabel(mPM));

            //点击确定时，检测密码是否与当时设置的密码相同
            bt_lock_psd_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String psd=et_lock_psd.getText().toString();
                    String psdList=mDao.queryPsd(appInfo.packageName);
                    //判断密码是否为当时输入的密码
                    if(TextUtils.isEmpty(psd)){
                        Toast.makeText(getApplicationContext(),"请输入密码",Toast.LENGTH_SHORT).show();
                    }else{
                        if(psdList.equals(psd)){
                            //解锁,进入应用,告知一直监测用户打开应用是否加锁的服务不要再去监听已经解锁的应用,发送广播
                            Intent intent = new Intent("android.intent.action.SKIP");
                            intent.putExtra("packageName",appInfo.packageName);
                            sendBroadcast(intent);
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(),"密码错误,请重新输入!",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void initUI() {
        iv_lock_app_icon= (ImageView) findViewById(R.id.iv_lock_app_icon);
        tv_lock_app_name = (TextView) findViewById(R.id.tv_lock_app_name);
        et_lock_psd = (EditText) findViewById(R.id.et_lock_psd);
        bt_lock_psd_submit = (Button) findViewById(R.id.bt_lock_psd_submit);
    }


    //按回退按钮时跳转回手机桌面
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        super.onBackPressed();
    }
}
