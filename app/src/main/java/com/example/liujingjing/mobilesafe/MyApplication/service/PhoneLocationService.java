package com.example.liujingjing.mobilesafe.MyApplication.service;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.liujingjing.mobilesafe.R;
import com.example.liujingjing.mobilesafe.MyApplication.engine.PhoneAddressDao;
import com.example.liujingjing.mobilesafe.MyApplication.util.ConstantValue;
import com.example.liujingjing.mobilesafe.MyApplication.util.SPUtil;

public class PhoneLocationService extends Service {

    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    private TelephonyManager mTM;
    private MyPhoneStateListener mPL;
    private View mViewToast;
    private TextView tv_toast;
    private int[] mDrawableIds;
    private WindowManager mWM;
    private String mPhoneAddress;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            tv_toast.setText(mPhoneAddress);
        }
    };

    //打电话界面显示归属地土司的左上角X坐标
    private int startX;
    //打电话界面显示归属地土司的左上角Y坐标
    private int startY;
    //屏幕宽度
    private int mScreenWidth;
    //屏幕宽度
    private int mScreenHeight;
    //监听去电的广播接收者
    private OutGoingCallReceiver mReceiver;

    //因为服务无论开启多少次，都只创建一个对象
    @Override
    public void onCreate() {
        //1.监听电话状态，判断是响铃、空闲还是摘机(进行任何一种活动)
        //拿到电话管理者
         mTM= (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
         mPL=new MyPhoneStateListener();
        //监听电话状态
        mTM.listen(mPL,PhoneStateListener.LISTEN_CALL_STATE);

        //获取窗体对象
        mWM= (WindowManager) getSystemService(WINDOW_SERVICE);
        mScreenWidth=mWM.getDefaultDisplay().getWidth();
        mScreenHeight=mWM.getDefaultDisplay().getHeight();


        //监听去电归属地，1.注册广播接收者，拿到去电电话号码2.添加权限
        IntentFilter filter=new IntentFilter();
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);//监听去电

        mReceiver=new OutGoingCallReceiver();
        //注册广播界接收者
        registerReceiver(mReceiver,filter);

        super.onCreate();
    }


    //监听去电号码监听的广播接收者
    class OutGoingCallReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //收到此广播后，获取去电号码
            String outPhone=getResultData();
            //调用显示土司的方法
            showToast(outPhone);
        }
    }

   class MyPhoneStateListener extends PhoneStateListener{
       @Override
       public void onCallStateChanged(int state, String incomingNumber) {
           switch(state){
               case TelephonyManager.CALL_STATE_IDLE://空闲
                   //挂断电话时，土司要被从窗体上移除掉
                   //还没有打电话时也会调用这个方法，因此进行判空处理
                   if(mWM!=null && mViewToast!=null){
                   mWM.removeView(mViewToast);
                   }
                   break;
               case TelephonyManager.CALL_STATE_OFFHOOK://摘机
                   Log.i("ljj","摘机");
                   break;
               case TelephonyManager.CALL_STATE_RINGING://响铃
                   Log.i("ljj","响铃");
                   showToast(incomingNumber);
                   break;
           }
           super.onCallStateChanged(state, incomingNumber);
       }
   }


   //显示自定义土司
    public void showToast(String incomingNumber){
        final WindowManager.LayoutParams params = mParams;
        //定义土司的宽高
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        //定义土司显示位置
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE	默认能够被触摸
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
       //固定格式
        params.format = PixelFormat.TRANSLUCENT;
        //在响铃的时候显示吐司,和电话类型一致
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.setTitle("Toast");

        //指定吐司的所在位置(将吐司指定在左上角)
        params.gravity = Gravity.LEFT+Gravity.TOP;

        //吐司显示效果(吐司布局文件),xml-->view(吐司),将吐司挂载到windowManager窗体上
        mViewToast = View.inflate(this, R.layout.toast_view, null);
        tv_toast = (TextView) mViewToast.findViewById(R.id.tv_toast);

       //给土司设置拖拽事件
        mViewToast.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    //每个事件都是从按下开始
                    case MotionEvent.ACTION_DOWN:
                        //获取按下时该控件所在的x.y轴坐标
                        startX = (int) event.getRawX();
                        startY= (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //移动到某一点停下时移动的坐标
                        int moveX= (int) event.getRawX();
                        int moveY= (int) event.getRawY();

                        //移动的距离
                        int distanceX=moveX- startX;
                        int distanceY=moveY- startY;
                        //设置当前控件拖拽后左上角xy坐标位置
                        params.x=params.x+distanceX;
                        params.y=params.y+distanceY;

                        //容错处理，不能将控件拖拽出屏幕
                        if (params.x<0){params.x=0;}
                        if (params.y<0){params.y=0;}
                        //如果控件左上角X位置超出屏幕宽度-控件宽度
                        if (params.x>(mScreenWidth-mViewToast.getWidth())){
                            params.x=mScreenWidth-mViewToast.getWidth();
                        }
                        //如果控件左上角X位置超出屏幕高度-通知栏高度-控件高度
                        if (params.y>(mScreenHeight-mViewToast.getHeight()-22)){
                            params.y=mScreenHeight-mViewToast.getHeight()-22;
                        }

                        //告知窗体去做控件位置的更新
                        mWM.updateViewLayout(mViewToast,params);


                        //将当前位置设为下一次拖拽的起始位置
                        startX= (int) event.getRawX();
                        startY= (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        SPUtil.putInt(getApplicationContext(),ConstantValue.TOAST_LOCATION_LEFT,params.x);
                        SPUtil.putInt(getApplicationContext(),ConstantValue.TOAST_LOCATION_TOP,params.y);
                        break;
                }

                //只需响应拖拽事件时，返回true
                return true;
            }
        });

        //从sp中把左上角的x.y轴坐标拿出来赋给params
        params.x=SPUtil.getInt(getApplicationContext(),ConstantValue.TOAST_LOCATION_LEFT,0);
        params.y=SPUtil.getInt(getApplicationContext(),ConstantValue.TOAST_LOCATION_TOP,0);


        //从sp中获取色值文字的索引,匹配图片,用作展示，和settingActivity中的风格数组描述顺序一致
        mDrawableIds = new int[]{
                R.drawable.call_locate_white,
                R.drawable.call_locate_orange,
                R.drawable.call_locate_blue,
                R.drawable.call_locate_gray,
                R.drawable.call_locate_green};
        //拿出存储在sp当中的风格数组的索引位置
        int toastStyleIndex = SPUtil.getInt(getApplicationContext(), ConstantValue.TOAST_STYLE, 0);
        //给电话归属地土司设置成用户选择的样式
        tv_toast.setBackgroundResource(mDrawableIds[toastStyleIndex]);

        //在窗体上挂在一个view(权限)，将设置好的土司显示在电话界面
        mWM.addView(mViewToast,mParams);

        //获取到了来电号码以后,需要做来电号码查询
        query(incomingNumber);
    }


    //来电归属地查询
    private void query(final String incomingNumber) {
        //耗时操作,放入子线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                mPhoneAddress=PhoneAddressDao.getAddress(incomingNumber);
                //这个归属地结果要显示在土司上，属于UI操作，采用消息处理机制
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        //因为在设置界面stop服务没有取消掉服务监听电话状态的动作，因此在此处取消监听状态
        //为提高安全性，在这里对视用到的对象进行判空处理
        if(mTM!=null && mPL!=null){
            mTM.listen(mPL,PhoneStateListener.LISTEN_NONE);//取消监听
        }
        super.onDestroy();
    }
}
