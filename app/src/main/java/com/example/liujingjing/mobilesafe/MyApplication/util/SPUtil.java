package com.example.liujingjing.mobilesafe.MyApplication.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by liujingjing on 17-9-25.
 */

public class SPUtil {
    //创建该类来存储程序运行过程中的一些状态量

    //存储更新显示关闭或者开启的boolean量
    private static SharedPreferences sp;

    //读取状态量
    public static boolean getBoolean(Context ctx,String key,boolean value){
        //如果SharedPreferences还没有存储值(被创建)，就给它赋初始值
        if(sp==null){
            sp=ctx.getSharedPreferences("config",Context.MODE_PRIVATE);
        }
       return sp.getBoolean(key,value);
    }

    //将状态量写入应用程序中
    public static void putBoolean(Context ctx,String key,boolean value){
        //如果SharedPreferences还没有被创建，创建一个
        if(sp==null){
            sp=ctx.getSharedPreferences("config",Context.MODE_PRIVATE);
        }

         sp.edit().putBoolean(key,value).commit();
    }




    //
    //读取手机防盗模块输入的密码
    public static String getString(Context ctx,String key,String value){
        //如果SharedPreferences还没有存储值(被创建)，就给它赋初始值
        if(sp==null){
            sp=ctx.getSharedPreferences("config",Context.MODE_PRIVATE);
        }
        return sp.getString(key,value);
    }

    //将手机防盗模块输入的密码写入应用程序中
    public static void putString(Context ctx,String key,String value){
        //如果SharedPreferences还没有被创建，创建一个
        //MODE_PRIVATE代表每次存储的内容会覆盖掉之前存储的内容
        if(sp==null){
            sp=ctx.getSharedPreferences("config",Context.MODE_PRIVATE);
        }

        sp.edit().putString(key,value).commit();
    }


    //获取到sim卡未绑定时，把存储sim卡状态的这个节点删掉
    public static void remove(Context ctx, String key) {
        if(sp==null){
            sp=ctx.getSharedPreferences("config",Context.MODE_PRIVATE);
        }

        sp.edit().remove(key).commit();
    }



    //读取状态量
    public static int getInt(Context ctx,String key,int value){
        //如果SharedPreferences还没有存储值(被创建)，就给它赋初始值
        if(sp==null){
            sp=ctx.getSharedPreferences("config",Context.MODE_PRIVATE);
        }
        return sp.getInt(key,value);
    }

    //将状态量写入应用程序中
    public static void putInt(Context ctx,String key,int value){
        //如果SharedPreferences还没有被创建，创建一个
        if(sp==null){
            sp=ctx.getSharedPreferences("config",Context.MODE_PRIVATE);
        }

        sp.edit().putInt(key,value).commit();
    }
}
