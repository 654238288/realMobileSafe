package com.example.liujingjing.mobilesafe.MyApplication.service;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.example.liujingjing.mobilesafe.db.dao.BlackNumberDao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class BlackPhoneService extends Service {
    private MyPhoneStateListener mStateListenner;
    private TelephonyManager mTM;
    //因为服务无论开启多少次，都只创建一个对象
    @Override
    public void onCreate() {
        //监听接收短信的广播
        IntentFilter intentFilter=new IntentFilter();


        //监听电话状态
        //获取电话管理者对象
        mTM= (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

        mStateListenner=new MyPhoneStateListener();
        mTM.listen(mStateListenner,PhoneStateListener.LISTEN_CALL_STATE);
        super.onCreate();
    }


    class MyPhoneStateListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state){
                case TelephonyManager.CALL_STATE_IDLE://空闲
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK://摘机
                break;
                case TelephonyManager.CALL_STATE_RINGING://响铃
                    Log.d("LJJ","进入响铃方法");
                  //挂断,endcall()方法被隐藏了，要调用系统源码中的调用endcall()方法的对象ITelephony来调用
                  //使用到aidl文件，追踪到TelephonyManager类中搜索获取ITelephony的方法，发现下面这行代码，
                  //但ServiceManager不对安卓开发人员公开，因此使用反射机制调用此方法
                  //ITelephony t=ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE))
                  endcall(incomingNumber);
                break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    //挂断电话
    private void endcall(String incomingNumber) {
        Log.d("LJJ","准备拦截");
        //先获取拦截类型mode，如果为2或者3，则拦截
        String mode=BlackNumberDao.getInstance(getApplicationContext()).queryModeByPhone(incomingNumber);
            Integer M=Integer.parseInt(mode);

        if (M == 2 || M == 3){
            Log.d("LJJ","挂断电话");
            //反射机制调用ITelephony.endcall();
            try {
                Class<?> clazz=Class.forName("android.os.ServiceManager");
                Method m=clazz.getMethod("getService",String.class);
                IBinder ibinder= (IBinder) m.invoke(null,Context.TELEPHONY_SERVICE);
                ITelephony telephony=ITelephony.Stub.asInterface(ibinder);
                telephony.endCall();

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
    }
}
