package com.example.liujingjing.mobilesafe.db.dao;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.liujingjing.mobilesafe.db.BlackNumberOpenHelper;
import com.example.liujingjing.mobilesafe.db.domain.BlackNum;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liujingjing on 17-10-12.
 */

//1.创建db对象 2.封装单例模式 3.写增删改查方法
public class BlackNumberProvider extends ContentProvider{

    private static BlackNumberOpenHelper helper;
    @Override
    public boolean onCreate() {
        helper=new BlackNumberOpenHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        //开启数据库，做写入操作
        SQLiteDatabase db = helper.getWritableDatabase();
        //(表名，查询的字段，条件，条件的值，分组，分组的条件，排序)
        Cursor cursor=db.query("blackPhone",null,null,null,null,null,"_id desc");
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
