package com.example.liujingjing.mobilesafe.MyApplication.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liujingjing.mobilesafe.MyApplication.engine.AppInfoProvider;
import com.example.liujingjing.mobilesafe.R;
import com.example.liujingjing.mobilesafe.db.dao.AppLockDao;
import com.example.liujingjing.mobilesafe.db.domain.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class AppLockActivity extends AppCompatActivity {

    private Button bt_unlock,bt_lock;
    private LinearLayout ll_unlock,ll_lock;
    private TextView tv_unlock,tv_lock;
    private ListView lv_unlock,lv_lock;
    private List<AppInfo> mAllAppList;//安装在手机上的所有应用集合
    private List<AppInfo> mLockAppList;//加锁应用集合
    private List<AppInfo> mUnLockAppList;//未加锁应用集合
    private AppLockDao mAppDao;//查询已加锁的数据库中已加锁应用包名
    private MyAdapter mLockAdapter;
    private MyAdapter munLockAdapter;
    private TranslateAnimation mAnimation;

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //给listView设置数据适配器
            mLockAdapter=new MyAdapter(true);
            lv_lock.setAdapter(mLockAdapter);

            munLockAdapter=new MyAdapter(false);
            lv_unlock.setAdapter(munLockAdapter);
        }
    };


    class MyAdapter extends BaseAdapter{

        private boolean isLock;

        public MyAdapter(boolean isLock){
                this.isLock=isLock;
        }

        @Override
        public int getCount() {
            if (isLock){
                //展示的文本内容(应用个数)
                tv_lock.setText("已加锁应用("+mLockAppList.size()+")");
                return mLockAppList.size();
            }else {
                tv_unlock.setText("未加锁应用("+mUnLockAppList.size()+")");
                return mUnLockAppList.size();
            }
        }

        @Override
        public AppInfo getItem(int position) {
            if (isLock) {
                return mLockAppList.get(position);
            }else {
                return mUnLockAppList.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder viewHolder=null;
            if (convertView == null){
                convertView=View.inflate(getApplicationContext(),R.layout.listview_lock_app,null);
                viewHolder=new ViewHolder();
                viewHolder.iv_icon= (ImageView) convertView.findViewById(R.id.iv_icon);
                viewHolder.iv_lock= (ImageView) convertView.findViewById(R.id.iv_lock);
                viewHolder.tv_name= (TextView) convertView.findViewById(R.id.tv_name);
                convertView.setTag(viewHolder);
            }else {
                viewHolder= (ViewHolder) convertView.getTag();
            }

           final AppInfo a=getItem(position);
            viewHolder.iv_icon.setBackgroundDrawable(a.getIcon());
            viewHolder.tv_name.setText(a.getAppName());
            //给控件赋值
            if (isLock) {
                viewHolder.iv_lock.setImageResource(R.drawable.lock);
            }else{
                viewHolder.iv_lock.setImageResource(R.drawable.unlock);
            }

            final View animationView=convertView;
            //点击锁的图片，进行集合和数据库表的变更，以及执行动画
            viewHolder.iv_lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //点击锁之后，需要动画的view对象(convertView)执行移出的动画
                    //因为这是内部方法，需要使用外部变量，要加final,但convertView又要进行赋值，因此把convertView赋给临时变量
                    //执行动画需要时间，但接下来的代码也会同时执行，因此该动画会作用在点击条目的下一条，因为点击条目已经被删除，下一条会顶上来
                    //因此要把刷新页面的代码放在执行动画之后，因此此处要监听
                    animationView.startAnimation(mAnimation);
                    mAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            //动画开始的时候做的事情
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            //动画结束的时候做的事情
                            if (isLock) {
                                //已加锁变未加锁
                                mLockAppList.remove(a);
                                mUnLockAppList.add(0,a);
                                mAppDao.deleteAppLock(a.getPackageName());
                            }else{
                                //未加锁变已加锁
                                mUnLockAppList.remove(a);
                                mLockAppList.add(0,a);
                                showPsdDialog(a);
                            }
                            //通知数据适配器更新数据，刷新页面
                            mLockAdapter.notifyDataSetChanged();
                            munLockAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                            //动画重复的时候做的事情
                        }
                    });
                }
            });
            return convertView;
        }
    }

    //未加锁应用变成已加锁应用时，让用户输入密码
    public void showPsdDialog(final AppInfo a) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        final AlertDialog dialog=builder.create();
        //给对话框设置样式View
        View setPsdView=View.inflate(this,R.layout.activity_enter_app,null);

        ImageView iv_lock_app_icon= (ImageView)setPsdView.findViewById(R.id.iv_lock_app_icon);
        TextView tv_lock_app_name = (TextView)setPsdView. findViewById(R.id.tv_lock_app_name);
        final EditText et_lock_psd = (EditText) setPsdView.findViewById(R.id.et_lock_psd);
        Button bt_lock_psd_submit = (Button) setPsdView.findViewById(R.id.bt_lock_psd_submit);

        iv_lock_app_icon.setBackground(a.getIcon());
        tv_lock_app_name.setText(a.getAppName());

        dialog.setView(setPsdView);
        dialog.show();

        //点击提交之后把该应用的包名和密码存储到数据库里
        bt_lock_psd_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enterPsd=et_lock_psd.getText().toString();
                if(TextUtils.isEmpty(enterPsd)){
                    Toast.makeText(getApplicationContext(),"请为您加锁的应用设置密码！",Toast.LENGTH_SHORT).show();
                }else{
                  mAppDao.addAppLock(a.getPackageName(),enterPsd);
                  dialog.dismiss();
                }
            }
        });

    }

    static class ViewHolder{
        //应用名、图标、锁
        private TextView  tv_name;
        private ImageView iv_icon;
        private ImageView iv_lock;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);

        initUI();
        initData();
        //点击锁，要有条目的移除动画
        initAnimation();
    }


    private void initAnimation() {
        //平移动画,Y轴不变，X轴从自己的起点到终点
       mAnimation=new TranslateAnimation(Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF,1,
                Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF,0);
        //设置时长
        mAnimation.setDuration(500);

    }

    private void initData() {
        //拿到所有应用，耗时操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                //拿到所有应用
               mAllAppList=AppInfoProvider.getAppInfoList(getApplicationContext());
                //拿到数据表中存储的已加锁应用的包名集合
               mAppDao=AppLockDao.getInstance(getApplicationContext());
               List<String> appPkgName=mAppDao.query();


                for (String name: appPkgName) {
                    Log.d("ljj","appName="+name);
                }


                //创建已加锁和未加锁应用集合
               mLockAppList=new ArrayList<AppInfo>();
               mUnLockAppList=new ArrayList<AppInfo>();
                //如果数据表中的包名包含某个应用的包名，添加到已加锁应用的集合里
               for (AppInfo appInfo:mAllAppList) {
                    if (appPkgName.contains(appInfo.getPackageName())){
                        mLockAppList.add(appInfo);
                    }else {
                        mUnLockAppList.add(appInfo);
                    }
               }
               //数据存储完毕，告知主线程更新UI界面
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }


    private void initUI() {
        //点击切换加锁和未加锁的按钮
        bt_unlock = (Button) findViewById(R.id.bt_unlock);
        bt_lock = (Button) findViewById(R.id.bt_lock);

        //显示加锁和未加锁的线性布局
        ll_unlock = (LinearLayout) findViewById(R.id.ll_unlock);
        ll_lock = (LinearLayout) findViewById(R.id.ll_lock);

        //显示加锁和未加锁的标题描述
        tv_unlock = (TextView) findViewById(R.id.tv_unlock);
        tv_lock = (TextView) findViewById(R.id.tv_lock);

        //显示加锁和未加锁的内容展示
        lv_unlock = (ListView) findViewById(R.id.lv_unlock);
        lv_lock = (ListView) findViewById(R.id.lv_lock);

        //点击未加锁和已加锁按钮，切换图片和listView的可见以及不可见状态
        bt_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_lock.setBackgroundResource(R.drawable.tab_right_pressed);
                bt_unlock.setBackgroundResource(R.drawable.tab_left_default);

                ll_lock.setVisibility(View.VISIBLE);
                ll_unlock.setVisibility(View.GONE);
            }
        });

        bt_unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_lock.setBackgroundResource(R.drawable.tab_right_default);
                bt_unlock.setBackgroundResource(R.drawable.tab_left_pressed);

                ll_lock.setVisibility(View.GONE);
                ll_unlock.setVisibility(View.VISIBLE);
            }
        });
    }
}
