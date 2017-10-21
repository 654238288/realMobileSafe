package com.example.liujingjing.mobilesafe.MyApplication.util;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import java.util.List;

/**
 * Created by liujingjing on 17-10-20.
 */

//获取当前运行应用
public class ForegroundAppUtil {
    private static final long END_TIME=System.currentTimeMillis();
    private static final long TIME_INTERVAL=7*24*60*60*1000L;
    private static long START_TIME=END_TIME-TIME_INTERVAL;


    //获取栈顶的应用包名
    public static String getForegroundActivityPackageName(Context context){
        String currentClassName="";
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP){
            ActivityManager manager= (ActivityManager) context.getApplicationContext().
                    getSystemService(Context.ACTIVITY_SERVICE);
            currentClassName=manager.getRunningTasks(1).get(0).topActivity.getPackageName();
        }else {
            UsageStats initStats=getForegroundUsageStats(context,START_TIME,END_TIME);
            if (initStats != null){
                currentClassName=initStats.getPackageName();
            }
        }
        return currentClassName;
    }

    //获取记录前台应用的UsageStats对象
    private static UsageStats getForegroundUsageStats(Context context, long startTime, long endTime) {
        UsageStats usageStatsResults=null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            List<UsageStats> usageStatses=getUsageStatsList(context,startTime,endTime);
            if (usageStatses == null || usageStatses.isEmpty())return null;
            for (UsageStats u:usageStatses){
                if (usageStatsResults == null || usageStatsResults.getLastTimeUsed() <
                        u.getLastTimeUsed()){
                    usageStatsResults=u;
                }
            }
        }
        return usageStatsResults;
    }


    //获取记录当前应用的UsageStats对象
    private static UsageStats getCurrentUsageStats(Context context, long startTime, long endTime) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            List<UsageStats> usageStatses=getUsageStatsList(context,startTime,endTime);
            if (usageStatses == null || usageStatses.isEmpty())return null;
            for (UsageStats u:usageStatses){
                if (TextUtils.equals(u.getPackageName(),context.getPackageName())){
                    return u;
                }
            }
        }
        return null;
    }



    private static List<UsageStats> getUsageStatsList(Context context, long startTime, long endTime) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            UsageStatsManager manager= (UsageStatsManager) context.getApplicationContext().
                    getSystemService(Context.USAGE_STATS_SERVICE);
            List<UsageStats> usagestates=manager.queryUsageStats(UsageStatsManager.INTERVAL_BEST,
                    startTime,endTime);
            //没有获取到权限，没查到数据
            if(usagestates == null || usagestates.size() == 0){
                Intent intent=new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.getApplicationContext().startActivity(intent);
                return null;
            }
            return usagestates;
        }
        return null;
    }
}
