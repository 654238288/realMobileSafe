package com.example.liujingjing.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by liujingjing on 17-10-12.
 */

public class AppLockOpenHelper extends SQLiteOpenHelper {
    public AppLockOpenHelper(Context context) {
        //参数2：要创建的数据库名 参数4：版本(调用更新方法时，更新的版本必须高于这里的版本)
        super(context, "appLock.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据表
        db.execSQL("create table appLock (_id integer primary key autoincrement," +
                "packageName varchar(50),psd varchar(12));");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
