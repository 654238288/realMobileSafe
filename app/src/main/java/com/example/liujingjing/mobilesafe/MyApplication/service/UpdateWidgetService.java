package com.example.liujingjing.mobilesafe.MyApplication.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.example.liujingjing.mobilesafe.R;
import com.example.liujingjing.mobilesafe.MyApplication.activity.HomeActivity;
import com.example.liujingjing.mobilesafe.MyApplication.receiver.MyAppWidgetProvider;

public class UpdateWidgetService extends Service {
    public UpdateWidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        updateAppWidget();
        super.onCreate();
    }

    protected void updateAppWidget() {
        //1.获取AppWidget对象
        AppWidgetManager aWM = AppWidgetManager.getInstance(this);
        //2.获取窗体小部件布局转换成的view对象(定位应用的包名,当前应用中的那块布局文件)
        RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.widget_desktop);
        /*//3.给窗体小部件布view对象,内部控件赋值
        remoteViews.setTextViewText(R.id.tv_process_count, "进程总数:"+ProcessInfoProvider.getProcessCount(this));
        //4.显示可用内存大小
        String strAvailSpace = Formatter.formatFileSize(this, ProcessInfoProvider.getAvailSpace(this));
        remoteViews.setTextViewText(R.id.tv_process_memory, "可用内存:"+strAvailSpace);*/



        //点击窗体小部件,进入应用
        //1:在那个控件上响应点击事件2:延期的隐式意图(匹配homeActivity)
        /*Intent intent = new Intent("android.intent.action.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        //点击后进入主界面*/
        Intent intent = new Intent(this,HomeActivity.class);//显示意图跳转
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_root, pendingIntent);

        /*//通过延期意图发送广播,在广播接受者中杀死进程,匹配规则看action
        Intent broadCastintent = new Intent("android.intent.action.KILL_BACKGROUND_PROCESS");
        PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, broadCastintent, PendingIntent.FLAG_CANCEL_CURRENT);
        //remoteViews.setOnClickPendingIntent(R.id.btn_clear,broadcast);*/

        //上下文环境,窗体小部件对应广播接受者的字节码文件
        ComponentName componentName = new ComponentName(this,MyAppWidgetProvider.class);
        //更新窗体小部件
        aWM.updateAppWidget(componentName, remoteViews);
    }
}
