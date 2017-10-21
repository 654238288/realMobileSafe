package com.example.liujingjing.mobilesafe.MyApplication.service;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.liujingjing.mobilesafe.R;
import com.example.liujingjing.mobilesafe.MyApplication.receiver.DeviceAdmin;
import com.example.liujingjing.mobilesafe.MyApplication.util.ConstantValue;
import com.example.liujingjing.mobilesafe.MyApplication.util.SPUtil;

public class SmsService extends Service {


    public SmsService() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("ljj","开启了短信监听服务");
        SmsContent content1 = new SmsContent(new Handler(),this);
        //注册内容提供者的解析对象，监听短信收件箱里的内容
        getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, content1);
        Log.i("ljj","开启了短信监听服务");
    }
    }




     class SmsContent extends ContentObserver {
         private Context mContext;

         private ContentResolver mContentResolver;//获取短信内容

         private ComponentName mDeviceAdmin;//本应用添加到设备管理器中的组件对象，可以作为是否被激活的标志
         private DevicePolicyManager mDPM;//设备管理者对象
         private Uri mUri= Uri.parse("content://sms/");

         public SmsContent(Handler handler,Context context){
             super(handler);
             this.mContext=context;
             mContentResolver=mContext.getContentResolver();
         }

        @Override
        public void onChange(boolean selfChange) {
            Log.i("LJJ","来短信了！");
            super.onChange(selfChange);
            //content://sms/inbox收件箱
            //content://sms/全部短信
            //content://sms/sent发件箱
            Cursor c = mContentResolver.query(mUri, null, null, null, null);
           if (c != null) {
                while (c.moveToNext()) {
                    boolean set_safe_security= SPUtil.getBoolean(mContext, ConstantValue.OPEN_SECURITY,false);
                    if(set_safe_security){
                        //监听短信内容，匹配#*alarm*#,一旦匹配上，播放报警音乐
                        //获取短信数组
                        /* Object[] ms= (Object[]) new Intent().getExtras().get("pdus");
                            for (Object m:ms) {
                            //获取短信对象
                            SmsMessage s=SmsMessage.createFromPdu((byte[]) m);
                            *//*s.getOriginatingAddress();获取发送这个短信的手机号*//*
                            //获取短信对象的基本信息
                            String messageContent=s.getMessageBody();*/
                        String telephone= c.getString(c.getColumnIndex("address"));
                        if(telephone.contains("+86"+SPUtil.getString(mContext,ConstantValue.CONTACT_PHONE,"") )){
                            Log.i("ljj","获取到了联系人电话"+c.getString(c.getColumnIndex("address")));
                        if(c.getString(c.getColumnIndex("body")).contains("#*alarm*#")){
                            Log.i("ljj","body="+c.getString(c.getColumnIndex("body")));
                            //获取播放器
                            MediaPlayer mediaPlayer = MediaPlayer.create(mContext, R.raw.ylzs);
                            //循环播放
                            mediaPlayer.setLooping(true);
                            mediaPlayer.start();
                        }
                        //一旦安全号码给你的手机发送了包含#*location*#内容的短信，手机就要去定位，
                        // 因此不依赖于活动，而要写在服务里
                        if(c.getString(c.getColumnIndex("body")).contains("#*location*#")){

                            android.util.Log.e("ljj","location");
                            //开启服务,因为是在广播接收器里，不能直接开启服务，因此要用context去开启服务
                            mContext.startService(new Intent(mContext, LocationService.class));
                        }
                            //如果收到#*lockscreen*#,则实现远程锁屏
                            if(c.getString(c.getColumnIndex("body")).contains("#*lockscreen*#")){

                                android.util.Log.e("ljj","lockscreen");
                                //获取本应用的设备组件对象
                                mDeviceAdmin=new ComponentName(mContext,DeviceAdmin.class);
                                //获取设备管理器对象
                                mDPM= (DevicePolicyManager) mContext.getSystemService(mContext.DEVICE_POLICY_SERVICE);
                                //把本应用激活到设备管理器上
                                Intent intent=new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                                //给本组件添加激活
                                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,mDeviceAdmin);
                                //把本组件添加到设备管理器中
                                intent.putExtra(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN,"设备管理器");
                                //在服务中开启活动，因为旧的任务栈已经结束，所以必须开启一个新的任务栈
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                //打开设备管理器界面
                                mContext.startActivity(intent);
                                android.util.Log.e("ljj","已开启任务管理器");
                                //如果开启了设备管理器，则可以进行锁屏
                                if (mDPM.isAdminActive(mDeviceAdmin)){
                                    android.util.Log.e("ljj","已激活");
                                    mDPM.lockNow();
                                    mDPM.resetPassword("",0);
                                }else{
                                    Toast.makeText(mContext,"请先激活",Toast.LENGTH_SHORT).show();
                                }
                            }
                    }
                }
                }


                c.close();
            }
        }


     }


