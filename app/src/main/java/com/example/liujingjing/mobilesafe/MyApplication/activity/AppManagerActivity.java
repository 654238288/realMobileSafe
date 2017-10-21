package com.example.liujingjing.mobilesafe.MyApplication.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liujingjing.mobilesafe.MyApplication.engine.AppInfoProvider;
import com.example.liujingjing.mobilesafe.R;
import com.example.liujingjing.mobilesafe.db.domain.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class AppManagerActivity extends AppCompatActivity implements View.OnClickListener{


    //查询到的安装在手机中的所有应用
    private List<AppInfo> mAllApps;
    //存储系统应用的集合
    private List<AppInfo> mSysApps;
    //存储手机应用的集合
    private List<AppInfo> mSdApps;
    private MyAdapter mAdapter;
    private ListView lv_app_manager_item;//显示在页面上的应用
    private TextView tv_app_des;//描述该应用属于哪种类型
    private AppInfo mAppInfo;//判断点击的是那个条目
    private List<AppInfo> mSharedApps;//用作分享的第三方软件
    private MyAdapter2 mAdapter2;
    private GridView gv_shared_app;
    private View mView;//gridView
    private PopupWindow mPopup;



    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //把数据适配器设置给listView
            mAdapter=new MyAdapter();
            lv_app_manager_item.setAdapter(mAdapter);

            if (tv_app_des !=null && mSdApps !=null){
                tv_app_des.setText("手机应用("+mSdApps.size()+")");
            }
            mAdapter2=new MyAdapter2();
            if(mSharedApps!=null){
                gv_shared_app.setAdapter(mAdapter2);
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        mView=View.inflate(this,R.layout.dialog_shared_app,null);
        gv_shared_app= (GridView) mView.findViewById(R.id.gv_shared_app);
        //初始化标题信息
        initTitle();
        //初始化页面内容信息
        initContent();
    }



    //给控件设置点击事件有两种方法1.setOnClickListener
    //2.让活动实现OnClickListener,重写它的click方法，用R.id判断是哪个控件触发了点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_uninstall:
                //卸载应用
                //通过配置系统卸载应用时开启的活动意图，来调出系统卸载应用的界面
                //源码中关于卸载配置的action
                if (mAppInfo.isSystem()){
                    //如果是系统应用，提示用户不能卸载
                    Toast.makeText(getApplicationContext(),"该应用不能卸载！",Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent("android.intent.action.DELETE");
                    intent.addCategory("android.intent.category.DEFAULT");
                    //根据包名卸载，因为手机里每个应用都有唯一包名
                    intent.setData(Uri.parse("package:" + mAppInfo.getPackageName()));
                    startActivity(intent);
                }
                break;
            case R.id.tv_start:

                //开启应用
                PackageManager pm=getPackageManager();
                //通过桌面开启包名对应的应用
                Intent launchIntentForPackage = pm.getLaunchIntentForPackage(mAppInfo.getPackageName());
                //如果该应用可以开启
                if (launchIntentForPackage!=null){
                    startActivity(launchIntentForPackage);
                }else {
                    //提示不能开启
                    Toast.makeText(getApplicationContext(),"该应用不能开启！",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_share:
                //需要去调第三方接口，这里展示假页面
                mSharedApps=new ArrayList<>();

                for (AppInfo app:mSdApps) {
                    if(app.getAppName().equals("微信") ||
                            app.getAppName().equals("知乎") ||
                            app.getAppName().equals("微博") ||
                            app.getAppName().equals("百度网盘") ||
                            app.getAppName().equals("电子邮件") ||
                            app.getAppName().equals("全民k歌") ){
                        mSharedApps.add(app);
                    }
                }
                showShareDialog();
                break;
        }
        //点击跳转之后让悬浮框隐藏
        mPopup.dismiss();
    }


    class MyAdapter extends BaseAdapter{

        //返回划动到某一种类型的索引(0,系统应用，1，手机应用)
        @Override
        public int getItemViewType(int position) {
            if (position ==0 || position == mSdApps.size()+1){
                //返回0,代表划动到系统应用和手机应用的标题栏的状态码
                return 0;
            }else{
                //返回1,代表图片+文本条目状态码，即应用条目
                return 1;
            }
        }

        //本页面上的listView有两种形式，因此返回类型数
        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount()+1;
        }

        //除了系统应用和手机应用还有两个条目显示标题
        @Override
        public int getCount() {
            return mSdApps.size()+mSysApps.size()+2;
        }

        @Override
        public AppInfo getItem(int position) {
            //页面展示时，先展示手机应用，后展示系统应用
            //如果划动到了标题栏，没有返回对象
            if (position == 0 || position == mSdApps.size()+1){
                return null;
            }else{
                //如果划动到了手机应用
                if (position < mSdApps.size()+1){
                    //返回手机应用减去手机应用的标题栏
                    return mSdApps.get(position-1);
                }else{
                    //显示系统应用-上面显示的手机应用-两个标题栏
                    return mSysApps.get(position-mSdApps.size()-2);
                }
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //先判断划动到哪个类型，如果是标题类型，创建标题viewHolder
            int type=getItemViewType(position);
            if (type == 0){
                ViewTitleHolder titleHolder=null;
                if (convertView == null){
                    titleHolder=new ViewTitleHolder();
                    convertView=View.inflate(getApplicationContext(),
                            R.layout.listview_app_manager_title_item,null);
                    titleHolder.tv_app_title= (TextView) convertView.findViewById(R.id.tv_app_title);
                    convertView.setTag(titleHolder);
                }else{
                    titleHolder= (ViewTitleHolder) convertView.getTag();
                }if (position == 0){
                    titleHolder.tv_app_title.setText("用户应用("+mSdApps.size()+")");
                }else{
                    titleHolder.tv_app_title.setText("系统应用("+mSysApps.size()+")");
                }
                return convertView;
            }else{
                //展示应用
                ViewContentHolder contentHolder=null;
                if (convertView == null){
                    contentHolder=new ViewContentHolder();
                    convertView=View.inflate(getApplicationContext(),R.layout.listview_app_manager_item,null);
                    contentHolder.iv_app_icon= (ImageView) convertView.findViewById(R.id.iv_app_icon);
                    contentHolder.tv_application_name= (TextView) convertView.findViewById(R.id.tv_application_name);
                    contentHolder.tv_app_path= (TextView) convertView.findViewById(R.id.tv_app_path);
                    convertView.setTag(contentHolder);
                }else{
                    contentHolder= (ViewContentHolder) convertView.getTag();
                }
                //给控件赋值
                contentHolder.iv_app_icon.setBackgroundDrawable(getItem(position).getIcon());
                contentHolder.tv_application_name.setText(getItem(position).getAppName());
                if (getItem(position).isSdCard()){
                    contentHolder.tv_app_path.setText("手机应用");
                }if (getItem(position).isSystem()){
                    contentHolder.tv_app_path.setText("系统应用");
                }
            return convertView;
            }
        }
    }


    static class ViewContentHolder{
         ImageView iv_app_icon;
         TextView tv_application_name;
         TextView tv_app_path;
    }
    static class ViewTitleHolder{
         TextView tv_app_title;
    }

    private  void initContent() {
        lv_app_manager_item= (ListView) findViewById(R.id.lv_app_manager_item);
        tv_app_des= (TextView) findViewById(R.id.tv_app_des);

        lv_app_manager_item.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                //滚动过程中调用方法
                //AbsListView中view就是listView对象
                //firstVisibleItem第一个可见条目索引值
                //visibleItemCount当前一个屏幕的可见条目数
                //总共条目总数
                if(mSdApps!=null && mSysApps!=null){
                    if(firstVisibleItem>=mSdApps.size()+1){
                        //滚动到了系统条目
                        tv_app_des.setText("系统应用("+mSysApps.size()+")");
                    }else{
                        //滚动到了用户应用条目
                        tv_app_des.setText("用户应用("+mSdApps.size()+")");
                    }
                }

            }
        });


        lv_app_manager_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //如果点击标题栏，无作用，只有点击了应用，才弹出悬浮框
                if (position == 0 || position == mSdApps.size()+1){
                    return;
                }else{
                    //点击的位置是用户应用
                    if (position < mSdApps.size()+1){
                        mAppInfo=mSdApps.get(position-1);
                    }else {
                        //是系统应用
                        mAppInfo=mSysApps.get(position-mSdApps.size()-2);
                    }
                    //点击到了应用，弹出悬浮框，可以进行下一步操作
                    showPopupWindows(view);
                }
            }
        });
    }


    //在软件管理界面点击了应用之后，可以对该应用进行的操作
    private void showPopupWindows(View view) {
        //把此悬浮框的布局文件转换成View控件
        View popupView=View.inflate(this,R.layout.popupwindow_app_manager,null);

        //把控件找出来
        TextView tv_start= (TextView)popupView.findViewById(R.id.tv_start);
        TextView tv_uninstall= (TextView)popupView. findViewById(R.id.tv_uninstall);
        TextView tv_share= (TextView)popupView.findViewById(R.id.tv_share);

        tv_uninstall.setOnClickListener(this);
        tv_start.setOnClickListener(this);
        tv_share.setOnClickListener(this);

        //1.创建PopupWindow对象(所显示的布局,宽，高，是否获取焦点)
        mPopup=new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT
                ,LinearLayout.LayoutParams.WRAP_CONTENT,true);
        //2.给popup设置背景，避免back键取消掉该悬浮框
        //因为该悬浮框的布局文件中已经设置了背景，此处调用无参构造设置透明背景
        mPopup.setBackgroundDrawable(new ColorDrawable());
        //3.设置popup悬浮窗的挂载位置(挂载在哪个控件上，X轴偏移量，Y轴偏移量)
        mPopup.showAsDropDown(view,450,-view.getHeight());

        //给窗体设置动画效果
        //1.设置淡入淡出动画
        AlphaAnimation alpha=new AlphaAnimation(0,1);//从透明到不透明
        alpha.setDuration(500);//给动画设置显示时间
        alpha.setFillAfter(true);//动画执行完会停留在最后一帧的效果，不然会恢复成没有设置之前的样子

        //2.设置透明到不透明的动画
        //(从x轴的起点，终点，  Y轴起点，终点   从控件自己开始，移动到X轴中心位置，    从控件自己开始，移动到Y轴中心位置，)
        ScaleAnimation scale=new ScaleAnimation(0,1,
                0,1,
                Animation.RELATIVE_TO_SELF,0.5f,
                Animation.RELATIVE_TO_SELF,0.5f);
        scale.setDuration(500);//设置显示时间
        scale.setFillAfter(true);

        //想让两个动画同时显示，放入AnimationSet中，让布局View来开启
        AnimationSet set=new AnimationSet(true);//true 代表存入其中的动画使用算法相同的插补器
        set.addAnimation(alpha);
        set.addAnimation(scale);

        //悬浮框的view对象开启动画
        popupView.startAnimation(set);
    }


    //卸载以后需要刷新页面，活动再次可见时，加载数据


    @Override
    protected void onPostResume() {

        //遍历手机里所有应用，属于耗时操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAllApps=AppInfoProvider.getAppInfoList(getApplicationContext());
                mSysApps=new ArrayList<AppInfo>();
                mSdApps=new ArrayList<AppInfo>();
                for (AppInfo app:mAllApps) {
                    if (app.isSystem()){
                        mSysApps.add(app);
                    }else if(app.isSdCard()){
                        mSdApps.add(app);
                    }
                }
                //发送消息告知主线程把这些应用显示在界面上
                mHandler.sendEmptyMessage(0);
            }
        }).start();

        super.onPostResume();
    }

    //在点击分享时，弹出对话框，展示可以分享到的应用图标和应用名
    private void showShareDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        final AlertDialog dialog=builder.create();
        Button btn_cancel1= (Button)mView.findViewById(R.id.btn_cancel1);
        dialog.setView(mView);
        mSharedApps=new ArrayList<>();
        for (AppInfo app:mSdApps) {
            if(app.getAppName().equals("微信") ||
                    app.getAppName().equals("知乎") ||
                    app.getAppName().equals("微博") ||
                    app.getAppName().equals("百度网盘") ||
                    app.getAppName().equals("电子邮件") ||
                    app.getAppName().equals("全民k歌") ){
                mSharedApps.add(app);
            }
        }
        mHandler.sendEmptyMessage(0);
        btn_cancel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消设置密码，隐藏对话框，回到九宫格界面
                dialog.dismiss();
            }
        });
        dialog.show();
    }





    class MyAdapter2 extends BaseAdapter {

        @Override
        public int getCount() {
            return mSharedApps.size();
        }

        @Override
        public AppInfo getItem(int position) {
            return mSharedApps.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
           View  mItemview = View.inflate(getApplicationContext(), R.layout.dialog_shared_app_items,null);
            ImageView iv_share_app_icon= (ImageView) mItemview.findViewById(R.id.iv_share_app_icon);
            TextView tv_share_app_name= (TextView) mItemview.findViewById(R.id.tv_share_app_name);

            iv_share_app_icon.setBackgroundDrawable(mSharedApps.get(position).getIcon());
            tv_share_app_name.setText(mSharedApps.get(position).getAppName());
            return mItemview;
        }
    }







    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initTitle() {
        //1.获取内存大小
        String path = Environment.getDataDirectory().getAbsolutePath();
        //2.获取sd卡大小
        String path1 = Environment.getExternalStorageDirectory().getAbsolutePath();
        //获取可用的存储空间
        String save= Formatter.formatFileSize(this,getUsageSpace(path));
        Log.i("LJJ","save="+save);
        String sdsave= Formatter.formatFileSize(this,getUsageSpace(path1));
        Log.i("LJJ","sdsave="+sdsave);

        //获取控件
        TextView tv_container= (TextView) findViewById(R.id.tv_container);
        TextView tv_sd_container= (TextView) findViewById(R.id.tv_sd_container);
        tv_container.setText("磁盘可用："+ save);
        tv_sd_container.setText("sd卡可用："+sdsave);

    }

    //int类型的数最多可以分配2147483647个byte约等于2GB,不够存储，因此这里的返回值类型可以设置成long
    private long getUsageSpace(String path1) {
        //StatFs(android.os)获取可用磁盘(内存)大小的类
        StatFs statfs=new StatFs(path1);
        //获取可用区块的个数,手机分配内存给应用或者文档是以4kb为单位的，没超过的就按4kb(4096字节)来计算
        //超过一点就会又叠加4kb，此处区块就指的是4kb
        long  count=statfs.getAvailableBlocks();
        //获取区块的大小
        long  size=statfs.getBlockSize();
        return count*size;
    }


}
