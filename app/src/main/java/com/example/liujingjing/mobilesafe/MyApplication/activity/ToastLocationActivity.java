package com.example.liujingjing.mobilesafe.MyApplication.activity;

import android.app.Activity;
import android.os.SystemClock;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.liujingjing.mobilesafe.R;
import com.example.liujingjing.mobilesafe.MyApplication.util.ConstantValue;
import com.example.liujingjing.mobilesafe.MyApplication.util.SPUtil;

//设置电话归属地土司拖拽位置
public class ToastLocationActivity extends Activity {

    //获取顶部的按钮
    private Button bt_remind_drag_top;
    //获取用于拖拽来设置归属地显示土司的图片
    private ImageView iv_drag;
    //获取底部的按钮
    private Button bt_remind_drag_bottom;
    //屏幕管理者
    private WindowManager mWM;
    //屏幕宽度
    private int mPrintScreenWidth;
    //屏幕宽度
    private int mPrintScreenHeight;
    //存放点击事件的数组
    private long[] mHits = new long[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast_location);

        initUI();
    }

    private void initUI() {
        //1.获取布局文件中的控件
        bt_remind_drag_top= (Button) findViewById(R.id.bt_remind_drag_top);
        bt_remind_drag_bottom= (Button) findViewById(R.id.bt_remind_drag_bottom);
        iv_drag= (ImageView) findViewById(R.id.iv_drag);
        mWM= (WindowManager) getSystemService(WINDOW_SERVICE);
        //获取屏幕宽度
        mPrintScreenWidth=mWM.getDefaultDisplay().getWidth();
        //获取屏幕高度
        mPrintScreenHeight=mWM.getDefaultDisplay().getHeight();

        //2.把存储好的控件左上角的位置拿出来
        int X=SPUtil.getInt(getApplicationContext(),ConstantValue.TOAST_LOCATION_LEFT,0);
        int Y=SPUtil.getInt(getApplicationContext(),ConstantValue.TOAST_LOCATION_TOP,0);

        //3.设置给该控件
        //3.1给布局文件里的控件设置位置参数时，需要用布局去设置
        RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        //3.2把左上角的位置设置给相对布局参数
        layoutParams.leftMargin=X;
        layoutParams.topMargin=Y;

        //3.3把设置好的布局参数设置给被拖拽的控件
        iv_drag.setLayoutParams(layoutParams);
        //下一次进入该页面，保存之前设置好的状态，如果拖拽控件到屏幕中间偏下，下面的button隐藏，上面的显示
        if (Y>(mPrintScreenWidth-22)/2){
            bt_remind_drag_bottom.setVisibility(View.INVISIBLE);
            bt_remind_drag_top.setVisibility(View.VISIBLE);
        }else{
            bt_remind_drag_bottom.setVisibility(View.VISIBLE);
            bt_remind_drag_top.setVisibility(View.INVISIBLE);
        }


        //设置多击事件
        iv_drag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //根据谷歌源码，先定义一个存放点击次数的数组mHits
                //按照系统拷贝数组的方法来处理这个数组
                //(被拷贝的数组，从第二位开始拷贝，拷贝到这个数组，的第一位，拷贝长度)
                System.arraycopy(mHits,1,mHits,0,mHits.length-1);
                //空出来的最后一个位置存放点击的时间
                mHits[mHits.length-1]= SystemClock.uptimeMillis();
                if (mHits[mHits.length-1]-mHits[0]<500){
                    //如果满足双击事件，图片控件居中
                    //计算上下左右坐标
                    int left=mPrintScreenWidth/2-iv_drag.getWidth()/2;
                    int top=mPrintScreenHeight/2-iv_drag.getHeight()/2;
                    int right=mPrintScreenWidth/2+iv_drag.getWidth()/2;
                    int bottom=mPrintScreenHeight/2+iv_drag.getHeight()/2;

                    //将计算出来的位置坐标设置给iv_drag
                    iv_drag.layout(left,top,right,bottom);

                    //存储起来
                    SPUtil.putInt(getApplicationContext(),ConstantValue.TOAST_LOCATION_LEFT,left);
                    SPUtil.putInt(getApplicationContext(),ConstantValue.TOAST_LOCATION_TOP,top);

                }

            }
        });

        //4.设置图片的拖拽事件
        iv_drag.setOnTouchListener(new View.OnTouchListener() {
            //该控件的起始位置
            private  int startX;
            private  int startY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //event的事件动作里分别处理相应的逻辑
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
                        //设置当前控件拖拽后的上下左右位置
                        int left=iv_drag.getLeft()+distanceX;
                        int top=iv_drag.getTop()+distanceY;
                        int right=iv_drag.getRight()+distanceX;
                        int bottom=iv_drag.getBottom()+distanceY;


                        //展示之前，要约束左上右下的移动都在手机屏幕内部，不能移出屏幕
                        //左边不能移出屏幕(起始坐标大于0)，右边不能移出屏幕(右边坐标小于屏幕宽度)
                        //上边不能移出屏幕(起始坐标大于0)，底边不能移出屏幕(坐标小于屏幕宽度-通知栏宽度)
                        if (left<0 || right>mPrintScreenWidth || top<0 || bottom>mPrintScreenHeight-22){
                            return true;
                        }

                        //如果拖拽控件到屏幕中间偏下，下面的button隐藏，上面的显示
                        if (top>(mPrintScreenWidth-22)/2){
                            bt_remind_drag_bottom.setVisibility(View.INVISIBLE);
                            bt_remind_drag_top.setVisibility(View.VISIBLE);
                        }else{
                            bt_remind_drag_bottom.setVisibility(View.VISIBLE);
                            bt_remind_drag_top.setVisibility(View.INVISIBLE);
                        }
                        //告知被拖拽的控件，按计算出来的坐标来展示
                        iv_drag.layout(left,top,right,bottom);


                        //将当前位置设为下一次拖拽的起始位置
                        startX= (int) event.getRawX();
                        startY= (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        //弹起时，记录控件左边和上边的位置
                        SPUtil.putInt(getApplicationContext(), ConstantValue.TOAST_LOCATION_LEFT,iv_drag.getLeft());
                        SPUtil.putInt(getApplicationContext(), ConstantValue.TOAST_LOCATION_TOP,iv_drag.getTop());
                        break;
                }
                //返回真，拖拽才有作用
                //但当该控件还有点击事件时，就需要返回false，否则后面的代码不会实现
                return false;
            }
        });
    }
}
