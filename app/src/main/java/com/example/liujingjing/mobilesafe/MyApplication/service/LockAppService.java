package com.example.liujingjing.mobilesafe.MyApplication.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import com.example.liujingjing.mobilesafe.MyApplication.activity.EnterAppActivity;
import com.example.liujingjing.mobilesafe.db.dao.AppLockDao;

import java.util.ArrayList;
import java.util.List;

public class LockAppService extends Service {
    private boolean isWatch;
    private AppLockDao mDao;
    private String mSkipPackageName;
    private ContentObserver mDbObserver;//时刻监测数据库变化
    private List<String> mPkgNameList;//数据库中存储的加锁应用的包名集合
    private MyReceiver mReceiver;
    private boolean flag;
    private long id;

    public LockAppService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {

        Log.d("ljj","开启了检测服务");
        mDao=AppLockDao.getInstance(getApplicationContext());
        //监听用户点开的应用
        watchApp();

        //注册广播接收者，如果接收到输入密码且正确时发送的广播，把检测该包名对应的应用跳过
        IntentFilter filter=new IntentFilter();
        filter.addAction("android.intent.action.SKIP");
        mReceiver=new MyReceiver();
        registerReceiver(mReceiver,filter);

        //注册广播接收者，监听数据库变化
        mDbObserver=new MyContentObserver(new Handler());
        //参数1，匹配ContentResolver提供的URI路径，参数2：是否和uri完全一致，观察者对象
        getContentResolver().registerContentObserver(Uri.parse("content://appLock/changed"),true,mDbObserver);
        super.onCreate();
    }


    class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            //收到广播后，拿到Intent传递过来的已经解锁的应用对应的包名
            mSkipPackageName=intent.getStringExtra("packageName");
        }
    }

    //创建时刻监听数据库发生改变的观察者，定义内部数据发生改变时的onChange()方法的处理逻辑
    class MyContentObserver extends ContentObserver{
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            //查询数据库，耗时操作
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mPkgNameList=mDao.query();
                }
            }).start();
            super.onChange(selfChange, uri);
        }
    }




    private void watchApp() {
        isWatch=true;
        //点开应用之后要开启活动界面，监听任务栈，拿出栈顶的活动就是用户点开的应用
        //要一直监听，因此放入循环里，并放入子线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                mPkgNameList=mDao.query();
                while(isWatch){
                    //拿到活动管理者对象
                    ActivityManager am= (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                    //拿到正在点开的那个应用所处的任务栈
                    /*List<RunningTaskInfo> task= am.getRunningTasks(1);
                     //拿到第一个任务栈
                     RunningTaskInfo taskInfo=task.get(0);
                    //拿到第一个应用的包名
                    String pkgName=taskInfo.topActivity.getPackageName();*/


                   List<ActivityManager.RunningAppProcessInfo> infoList=am.getRunningAppProcesses();
                    List<String> pNames=new ArrayList<String>();
                    for(ActivityManager.RunningAppProcessInfo runApp:infoList) {
                        pNames.add(runApp.processName);
                    }

                        if(pNames.contains(mSkipPackageName)){
                            Log.i("lockappservice111","解过锁的应用正在运行");
                            flag=true;
                        } else {
                            Log.i("lockappservice111","解过锁的应用停止运行");
                            flag=false;
                        }


                   String pkgName=infoList.get(0).processName;


                        //如果点开的程序加锁了，进入输入密码的界面
                    if (mPkgNameList.contains(pkgName)){
                            //如果没有输入正确的密码，再进行检测
                            if (!pkgName.equals(mSkipPackageName) && flag) {
                                Log.i("lockappservice111","检测");
                                Intent intent = new Intent(getApplicationContext(), EnterAppActivity.class);
                                intent.putExtra("packageName", pkgName);
                                //给活动开启任务栈
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                            }

                    }
                id=Thread.currentThread().getId();
                    //线程一直运行，就会占用大量的cpu时间片资源，造成手机卡机，因此适当的睡眠一下
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    @Override
    public void onDestroy() {
        
        stopSelf();
        super.onDestroy();
    }
}
