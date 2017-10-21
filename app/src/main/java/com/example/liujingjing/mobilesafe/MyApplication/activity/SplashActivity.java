package com.example.liujingjing.mobilesafe.MyApplication.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.liujingjing.mobilesafe.R;
import com.example.liujingjing.mobilesafe.MyApplication.util.StreamUtil;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends BaseActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int UPDATE_VERSION =100 ;
    private static final int ENTER_HOME =101 ;
    private static final int URL_ERROR =102 ;
    private static final int IO_ERROR =103 ;
    private static final int JSON_ERROR =104 ;
    private TextView tv_version_name;
    private int mLocalVersionCode;
    private String mVersionName;
    private String mVersionDes;
    private String mVersionCode;
    private String mDownloadUrl;



    //通过message的what值的变化来提示自己出现了哪些异常，以及把UI操作转到主线程中来(弹出提示对话框)
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATE_VERSION:
                    //发现新版本，提示更新
                    showUpdateDialog();
                    break;
                case ENTER_HOME:
                    //无须更新，进入主页面
                    enterHome();
                    break;
                case URL_ERROR:
                    //提示出现了URL地址解析异常
                    Log.i("jj","url解析异常");
                    enterHome();
                    break;
                case IO_ERROR:
                    //提示出现了文件下载异常
                    Log.i("jj","文件下载异常");
                    enterHome();
                    break;
                case JSON_ERROR:
                    //提示出现Json字符串解析异常
                    Log.i("jj","Json字符串解析异常");
                    enterHome();
                    break;
            }
        }
    };


    //显示提示更新的对话框
    private void showUpdateDialog() {
        //对话框是依赖于活动存在的，必须挂载到活动上
        final AlertDialog.Builder updateDialog=new AlertDialog.Builder(this);
        //更新对话框的标题
        updateDialog.setTitle("更新提醒");
        //图标
        updateDialog.setIcon(R.drawable.ic_launcher);
        //提示更新的描述
        updateDialog.setMessage(mVersionDes);
        //确定按钮
        updateDialog.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //下载新版本的apk
               downloadNewAPK();

            }
        });
        //取消按钮
        updateDialog.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //进入主页面
                enterHome();
            }
        });
        //如果用户按了返回键，也要进入主页面
        updateDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                enterHome();
                dialogInterface.dismiss();//关闭该对话框
            }
        });
        updateDialog.show();
    }



    //当发现新版本之后，下载新版本
    private void downloadNewAPK() {

        /*RequestParams params = new RequestParams(mDownloadUrl);
//自定义保存路径，Environment.getExternalStorageDirectory()：SD卡的根目录
        File apkFile = new File(Environment.getExternalStorageDirectory()+"/myapp/");
        if (!apkFile.exists()) {
            apkFile.mkdirs();
            Log.i("ljj","aaaaa1");
        }
        Log.i("ljj","aaaaa2");
        File file = new File(apkFile, "test.apk");
        Uri contentUri = FileProvider.getUriForFile(this, "com.example.liujingjing.mobilesafe.fileprovider", file);
        Log.i("ljj","aaaaa3"+contentUri);
        params.setSaveFilePath(contentUri.getPath());
//自动为文件命名
        Log.i("ljj","bbbb");
        params.setAutoRename(true);
        Log.i("ljj","ccccc");
        x.http().get(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onSuccess(File result) {
                //apk下载完成后，调用系统的安装方
                //安装这个文件
                installAPK(result);

            }
        });
        Log.i("ljj","ddddd");*/
    }


    //安装下载好的文件
    private void installAPK(File file) {
        //安装界面是安卓源码中写好的一个activity,因此要去开启一个隐式意图来匹配源码中定义好的activity
        Intent intent=new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        //Uri.fromFile(file)把文件解析成Uri
        intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
        //因为安装界面有安装和取消两个按钮，点击安装时用新版本覆盖旧版本，
        // 但点击取消时，要关掉对话框并结束SplashActivity回到主页面，因此开启活动时要选用有返回结果的
        startActivityForResult(intent,0);
    }



    //开启一个活动后，返回结果调用的方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterHome();
        super.onActivityResult(requestCode, resultCode, data);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initUI();
        initData();
        //初始化数据，号码归属地查询时的数据库表
        initPhoneAddress("address.db");
        //initAnimation();//添加淡入淡出效果
        enterHome();
    }

    private void initPhoneAddress(String dbName) {
        //获取到数据库表文件的存放目录
        File file1=getFilesDir();
        //用输入流拷贝到files文件夹下
        File file2=new File(file1,dbName);
        //该文件只读取一次，因此判断一下是否存在
        if (file2.exists()){


            return;
        }
        //用输出流写入到files文件夹下的文件里
        InputStream stream=null;
        FileOutputStream fos=null;
        //每次读取的大小
        byte[] bt=new byte[1024];
        //读取进度
        int temp = -1;
        try {
            //明确读取Assets下的名字为dbName的文件
            stream=getAssets().open(dbName);
            fos=new FileOutputStream(file2);
            while((temp=stream.read(bt))!=-1){
                fos.write(bt,0,temp);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (stream!=null && fos!=null){
                try {
                    stream.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void initData() {
        /*获取版本名称*/
        tv_version_name.setText("版本名称："+getVersionName());
        /*加载进度条时检测是否有新版本，当本地版本号小于服务器端版本号时，提示用户更新*/
        //先获取本地版本号
        //mLocalVersionCode=getVersionCode();
        /*获取服务器上的版本号
        * 1.客户端发请求给服务器，服务器端给响应
        * 2.商议将数据以什么格式返回回来，通常是以json或者xml格式，现多用json
        * 将请求发送到一个服务器的网址上，如http://www.djsk.com/xxx.json(?key=value 传递参数时用到)
        * 如果服务器返回200，则为成功，这时我们用流的方式将数据下载下来
        * 3.一个提示更新的json内容应该包含
        * 1>新版本名称：versionName   2>新版本描述信息:VersionDes
        * 3>版本号:VersionCode       4>新版本的下载地址:downloadUrl*/
        //发送请求给服务器属于耗时操作，要放在子线程中进行
        //checkVersionCode();

    }

    private RelativeLayout rl_root;

    /**
     * 添加淡入动画效果
     */
    private void initAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(3000);
        rl_root.startAnimation(alphaAnimation);
    }


    /*检测版本号*/
    private void checkVersionCode() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                //创建Message对象，用obtain比new高效
                Message msg=Message.obtain();
                //进入该线程的时间
                long preTime=System.currentTimeMillis();
                try {
                //1.要向服务器发送请求，首先要封装一个url

                 URL url=new URL("http://10.10.11.133:8080/versionUpdate.json");
                //2.开启一个链接
                    HttpURLConnection httpConnection= (HttpURLConnection) url.openConnection();
                //3.设置常见请求参数(请求头)
                    httpConnection.setConnectTimeout(10000);//超过3秒没有连接上，连接超时
                    httpConnection.setReadTimeout(10000);//超过3秒没有读取到数据，读取超时
                //设置请求方式，默认为GET httpConnection.setRequestMethod("POST");

                //4.判断获取到的响应码是否成功

                    if(httpConnection.getResponseCode()==200){
                        //5.以流的形式将数据读取下来
                        InputStream is=httpConnection.getInputStream();
                        //6.封装将流转换成字符串的工具类

                        String json= StreamUtil.streamToString(is);
                        Log.i("ljj",json);
                        //解析json
                        JSONObject J=new JSONObject(json);
                        //获取内容
                        mVersionName=J.getString("versionName");
                        mVersionDes=J.getString("versionDes");
                        mVersionCode=J.getString("versionCode");
                        mDownloadUrl=J.getString("downloadUrl");


                        //比较版本号
                        if(mLocalVersionCode<Integer.parseInt(mVersionCode)){
                            //如果本地版本号小于服务器版本号，弹出对话框，提示下载
                            //弹出对话框属于UI操作，不能在子线程中完成，要借助Handler的处理信息方法
                            //通过改变Message的What变量的值来传递信息
                            msg.what=UPDATE_VERSION;

                        }else{
                            //进入主界面
                            msg.what=ENTER_HOME;
                            enterHome();
                        }
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    msg.what=URL_ERROR;
                }catch (IOException e) {
                    e.printStackTrace();
                    msg.what=IO_ERROR;
                }catch (JSONException e) {
                    e.printStackTrace();
                    msg.what=JSON_ERROR;
                }finally {
                    /* //要让加载界面有缓冲时间，否则用户看不到
                    //获取线程运行结束时的事件和进入时间做比较
                    long endTime=System.currentTimeMillis();
                   //如果小于3秒，就让线程睡眠到3秒
                    if((endTime-preTime)<1000){
                        try {
                            Thread.sleep(1000-(endTime-preTime));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }*/
                    //把UI操作扔回主线程
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }


    //进入主界面
    private void enterHome() {
        Intent intent=new Intent(SplashActivity.this,HomeActivity.class);
        startActivity(intent);
        //跳转到主页面活动后，关闭启动页面的活动
        finish();
    }

    /*获取版本名称*/
    private String getVersionName() {
        //获取PackageManager对象
        PackageManager pm=getPackageManager();
        //获取基本信息(基本信息中包含版本名称)
        try {
            //每个应用都有唯一对应包名，第二个参数有权限等参数，0表示基本信息
            PackageInfo pi=pm.getPackageInfo(getPackageName(),0);
            return pi.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*获取版本号*/
    private int getVersionCode() {
        //获取PackageManager对象
        PackageManager pm=getPackageManager();
        //获取基本信息(基本信息中包含版本名称)
        try {
            //每个应用都有唯一对应包名，第二个参数有权限等参数，0表示基本信息
            PackageInfo pi=pm.getPackageInfo(getPackageName(),0);
            return pi.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void initUI() {
        tv_version_name=(TextView)findViewById(R.id.tv_version_name);
    }
}
