package com.example.liujingjing.mobilesafe.MyApplication.util;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by liujingjing on 17-10-10.
 */

public class ServiceUtils {

    //该类作为判定该应用程序中的服务是否运行
    //ctx 上下文环境   serviceName  需要判断的服务名称  return，true运行  false没运行
    public static boolean isRunning(Context ctx,String serviceName){
        //1.获取服务的运行状态，首先要获取活动的管理者对象
        ActivityManager AM= (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //2.获取正在运行的服务，最多获取100个(手机里可以支持开启的最大服务数100左右，服务占用太多资源，过多会卡机)
        List<ActivityManager.RunningServiceInfo> runningServices = AM.getRunningServices(100);
        //3.遍历集合，判断传入的服务名称和运行中的服务名称是否吻合
        for (ActivityManager.RunningServiceInfo rsi:runningServices) {
            String sn=rsi.service.getClassName();
            if (serviceName.equals(sn)){
                return true;
            }
        }

        return false;
    }
}
