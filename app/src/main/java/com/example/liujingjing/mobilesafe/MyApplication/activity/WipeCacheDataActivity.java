package com.example.liujingjing.mobilesafe.MyApplication.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.liujingjing.mobilesafe.IPackageStatsObserver;
import com.example.liujingjing.mobilesafe.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

public class WipeCacheDataActivity extends AppCompatActivity {

    private Chronometer timer;
    private long t1;//记录下来的总时间
    private Button bt_clear_now;//一键清理缓存的按钮
    private ProgressBar pg_cache_clean_progressbar;//清理缓存进度条
    private TextView tv_name;//需要清理的应用名称
    private PackageManager mPM;//应用包名的管理者对象
    private int mIndex = 0;//进度条显示进度
    private static final int CHECK_APP_CACHE = 100;//查询到了有缓存的应用
    private static final int UPDATE_APP_CACHE=101;//得到了该应用的名字，图标，缓存等信息
    private LinearLayout ll_add_text;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_APP_CACHE:
                    //获取到了有缓存的应用，在页面显示该应用的缓存信息
                    View view=View.inflate(getApplicationContext(),
                            R.layout.activity_clear_cache_item,null);
                    //获取该布局文件的控件
                    ImageView iv_icon= (ImageView) view.findViewById(R.id.iv_icon);
                    TextView tv_app_name= (TextView) view.findViewById(R.id.tv_app_name);
                    TextView tv_app_cache= (TextView) view.findViewById(R.id.tv_app_cache);
                    ImageView iv_clear_single_cache= (ImageView) view.findViewById(R.id.iv_clear_single_cache);

                    //给控件赋值
                    CacheInfo cacheInfo= (CacheInfo) msg.obj;
                    //iv_icon.setBackgroundDrawable(cacheInfo.cacheIcon);
                    iv_icon.setBackground(cacheInfo.cacheIcon);
                    tv_app_name.setText(cacheInfo.cacheName);
                    tv_app_cache.setText(Formatter.formatFileSize(getApplicationContext(),
                            cacheInfo.cacheSize));
                    //把新搜索出来的具有缓存的应用加到相对布局的第一个
                    ll_add_text.addView(view,0);

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wipe_cache_data);
        initUI();
        timer = (Chronometer) findViewById(R.id.timer);
        timer.setBase(SystemClock.elapsedRealtime());//计时器清零
        //SystemClock.elapsedRealtime()意为系统启动到现在的毫秒数
        //(SystemClock.elapsedRealtime() - timer.getBase())从开始计时到现在
        int hour = (int) ((SystemClock.elapsedRealtime() - timer.getBase()) / 1000 / 60);
        //设置格式":%s"为mm:ss
        timer.setFormat("0" + String.valueOf(hour) + ":%s");
        timer.start();
        initData();

    }


    private void initData() {
        //遍历手机所有应用属于耗时操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                //1.获取包名的管理者对象
                mPM = getPackageManager();
                //2.获取安装在手机上的所有应用
                List<PackageInfo> installedPackages = mPM.getInstalledPackages(0);
                //3.给进度条设值
                pg_cache_clean_progressbar.setMax(installedPackages.size());
                //4.遍历应用，获取包名
                for (PackageInfo pi : installedPackages) {
                    String packageName = pi.packageName;
                    //5.用包名去获取应用的缓存等信息
                    getAppCache(packageName);
                    //获取应用时一个一个显示，让线程睡眠
                    try {
                        Thread.sleep(100 + new Random().nextInt(50));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mIndex++;
                    //把改变的进度条进度值赋给进度条
                    pg_cache_clean_progressbar.setProgress(mIndex);
                    //每循环一次就把查询出来的应用信息返回给主线程，显示在页面上
                    Message msg = Message.obtain();
                    msg.what = CHECK_APP_CACHE;//状态
                    //获取有缓存的应用信息名
                    String name = null;
                    try {
                        name = mPM.getApplicationInfo(packageName, 0).loadLabel(mPM).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    msg.obj = name;
                    mHandler.sendMessage(msg);
                }
            }
        }).start();

    }

    private void getAppCache(String packageName) {

        //获取其他应用的信息，跨进程通信，使用到aidl文件
        //根据系统源码获取应用缓存的方法来获取应用缓存
         IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {
            public void onGetStatsCompleted(PackageStats stats,boolean succeeded) {
                //1.获取指定包名的缓存大小
                long cacheSize = stats.cacheSize;
                Log.d("ljj","cacheSize="+cacheSize);
                //如果有缓存，才需要显示
                if (cacheSize>0){
                    //2.告知主线程进行UI更新
                    Message msg=Message.obtain();
                    msg.what=UPDATE_APP_CACHE;
                    CacheInfo cacheInfo=null;
                    try {
                        cacheInfo=new CacheInfo();
                        cacheInfo.cacheSize=cacheSize;
                        cacheInfo.packagename=stats.packageName;
                        cacheInfo.cacheIcon=mPM.getApplicationInfo(stats.packageName,0).loadIcon(mPM);
                        cacheInfo.cacheName=mPM.getApplicationInfo(stats.packageName,0).loadLabel(mPM).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    msg.obj=cacheInfo;
                    mHandler.sendMessage(msg);
                }
            }
        };

        try {
            //获取应用包名的字节码文件
            Class<?> clazz=Class.forName("android.content.pm.PackageManager");
            //2.获取调用方法对象
            Method method = clazz.getMethod("getPackageSizeInfo", String.class,IPackageStatsObserver.class);
            //3.获取对象调用方法
            method.invoke(mPM, packageName,mStatsObserver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    class CacheInfo{
        private String cacheName;
        private Drawable cacheIcon;
        public String packagename;
        public long cacheSize;
    }

    private void initUI() {
        //获取控件
        bt_clear_now = (Button) findViewById(R.id.bt_clear_now);
        pg_cache_clean_progressbar = (ProgressBar) findViewById(R.id.pg_cache_clean_progressbar);
        tv_name = (TextView) findViewById(R.id.tv_name);
        ll_add_text= (LinearLayout) findViewById(R.id.ll_add_text);

    }


    @Override
    public void onStart() {
        //跳过已经记录下来的时间，相当于跳过中断活动时记录的时间，起到继续计时的作用
        timer.setBase(SystemClock.elapsedRealtime() - t1);
        timer.start();
        super.onStart();
    }

    @Override
    protected void onPause() {
        //停止计时
        timer.stop();
        //存储记录的总时间
        t1 = SystemClock.elapsedRealtime() - timer.getBase();
        super.onPause();
    }

    @Override
    protected void onStop() {
        timer.stop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        //记录时间为0，计时器也清零
        t1 = 0;
        timer.setBase(SystemClock.elapsedRealtime() - timer.getBase());
        super.onDestroy();
        timer.stop();
    }
}
