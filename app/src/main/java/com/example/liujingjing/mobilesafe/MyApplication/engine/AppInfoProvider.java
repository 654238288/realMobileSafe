package com.example.liujingjing.mobilesafe.MyApplication.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.example.liujingjing.mobilesafe.db.domain.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liujingjing on 17-10-17.
 */

public class AppInfoProvider {

    public static List<AppInfo> getAppInfoList(Context context) {
        //获取包的管理者对象
        PackageManager mPM = context.getPackageManager();
        //获取到的安装在手机里的全部应用信息
        List<PackageInfo> mApps = mPM.getInstalledPackages(0);
        //存放设置好值的应用对象集合
        List<AppInfo> mAppInfo = new ArrayList<>();

        for (PackageInfo app : mApps) {
            AppInfo a = new AppInfo();
            //给应用对象设置包名
            a.setPackageName(app.packageName);
            ApplicationInfo applicationInfo=app.applicationInfo;
            //给应用对象设置应用名称
            a.setAppName(applicationInfo.loadLabel(mPM).toString());
            //给应用对象设置应用图标
            a.setIcon(applicationInfo.loadIcon(mPM));
            //给应用对象设置属于系统应用还是手机应用
            //每个应用只有唯一对应的flag
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)
                    == ApplicationInfo.FLAG_SYSTEM) {
                //是系统应用
                a.setSystem(true);
            } else {
                //a.setSystem(false);
                a.setSdCard(true);
            }

            /*if ((applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE)
                    ==ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
                Log.i("ljj","设为true时获取到的应用："+applicationInfo.loadLabel(mPM).toString());
                //是手机应用
                a.setSdCard(true);
            } else {
                Log.i("ljj","设为false时获取到的应用："+applicationInfo.loadLabel(mPM).toString());
                a.setSdCard(false);
            }*/


            //把设置好值的应用信息对象添加到集合中去
            mAppInfo.add(a);
        }
        return  mAppInfo;
    }
}
