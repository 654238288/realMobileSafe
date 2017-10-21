package com.example.liujingjing.mobilesafe.db.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.liujingjing.mobilesafe.db.AppLockOpenHelper;
import com.example.liujingjing.mobilesafe.db.BlackNumberOpenHelper;
import com.example.liujingjing.mobilesafe.db.domain.BlackNum;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liujingjing on 17-10-12.
 */

//1.创建db对象 2.封装单例模式 3.写增删改查方法
public class AppLockDao {
    private AppLockOpenHelper helper;
    private Context context;

    private AppLockDao(Context ctx){
        helper=new AppLockOpenHelper(ctx);
        this.context=ctx;
    }

    private static AppLockDao appLockDao=null;

    public static AppLockDao getInstance(Context ctx){
        if (appLockDao==null){
            appLockDao=new AppLockDao(ctx);
        }
        return appLockDao;
    }



    public void addAppLock(String packageName,String psd){
        //开启数据库，做写入操作
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put("packageName",packageName);
        values.put("psd",psd);
        db.insert("appLock",null,values);
        db.close();
    //时刻监听数据库变化，一旦插入或者删除数据，都要及时反映到加锁和不加锁的处理上去，否则，服务一开启，新加锁的应用还是不用输密码
      context.getContentResolver().notifyChange(Uri.parse("content://appLock/changed"),null);
    }

    public void deleteAppLock(String packageName){
        //开启数据库，做写入操作
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("appLock","packageName=?",new String[]{packageName});
        db.close();
        //时刻监听数据库变化，一旦插入或者删除数据，都要及时反映到加锁和不加锁的处理上去，否则，服务一开启，新加锁的应用还是不用输密码
        context.getContentResolver().notifyChange(Uri.parse(""),null);
    }



    //查询所有的数据
    public List<String> query(){
        //开启数据库，做写入操作
        SQLiteDatabase db = helper.getWritableDatabase();
        //(表名，查询的字段，条件，条件的值，分组，分组的条件，排序)
        Cursor cursor=db.query("appLock",null,null,null,null,null,null);
        //查询到一个集合
        List<String> pkgNameList=new ArrayList<>();
        if (cursor!=null){
            while (cursor.moveToNext()){
                String packageName=cursor.getString(1);
                pkgNameList.add(packageName);
            }
            cursor.close();
            db.close();
        }
        return pkgNameList;
    }


    //用应用名查找密码的方法
    public String queryPsd(String pkgName){
        //开启数据库，做写入操作
        SQLiteDatabase db = helper.getWritableDatabase();
        //(表名，查询的字段，条件，条件的值，分组，分组的条件，排序)
        Cursor cursor=db.query("appLock",new String[]{"psd"},"packageName=?",
                new String[]{pkgName},null,null,null);
        //查询到一个集合
        String psd="";
        if (cursor!=null){
            while (cursor.moveToNext()){
                psd=cursor.getString(0);
            }
            cursor.close();
            db.close();
        }
        return psd;
    }

}
