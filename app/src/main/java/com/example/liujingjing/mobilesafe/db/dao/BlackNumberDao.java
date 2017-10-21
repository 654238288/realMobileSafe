package com.example.liujingjing.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.liujingjing.mobilesafe.db.BlackNumberOpenHelper;
import com.example.liujingjing.mobilesafe.db.domain.BlackNum;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liujingjing on 17-10-12.
 */

//1.创建db对象 2.封装单例模式 3.写增删改查方法
public class BlackNumberDao{
    private BlackNumberOpenHelper helper;
    private BlackNumberDao(Context ctx){
        helper=new BlackNumberOpenHelper(ctx);
    }

    private static BlackNumberDao blackNumberDao=null;

    public static BlackNumberDao getInstance(Context ctx){
        if (blackNumberDao==null){
            blackNumberDao=new BlackNumberDao(ctx);
        }
        return blackNumberDao;
    }

    public void addBlackNum(String phone,String mode){
        //开启数据库，做写入操作
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put("phone",phone);
        values.put("mode",mode);
        db.insert("blackPhone",null,values);
        db.close();
    }

    public void deleteBlackNum(String phone){
        //开启数据库，做写入操作
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put("phone",phone);
        db.delete("blackPhone","phone=?",new String[]{phone});
        db.close();
    }

    public void updateBlackNum(String phone,String mode){
        //开启数据库，做写入操作
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put("phone",phone);
        values.put("mode",mode);
        db.update("blackPhone",values,"phone=? and mode=?",new String[]{phone,mode});
        db.close();
    }

    //查询所有的数据
    public List<BlackNum> queryAllBlackNum(){
        //开启数据库，做写入操作
        SQLiteDatabase db = helper.getWritableDatabase();
        //(表名，查询的字段，条件，条件的值，分组，分组的条件，排序)
        Cursor cursor=db.query("blackPhone",null,null,null,null,null,"_id desc");
        //查询到一个集合
        List<BlackNum> blackNumList=new ArrayList<>();
        if (cursor!=null){
            while (cursor.moveToNext()){
                BlackNum blackNum=new BlackNum();
                String phone=cursor.getString(0);
                String mode=cursor.getString(1);
                blackNum.setPhone(phone);
                blackNum.setMode(mode);
                blackNumList.add(blackNum);
            }
            cursor.close();
            db.close();
        }
        return blackNumList;
    }


    //分页展示数据，如果用户不需要查看更多，可以节省资源
    public List<BlackNum> queryDataByPager(int showCount){
        //开启数据库，做写入操作
        SQLiteDatabase db = helper.getWritableDatabase();
        //(表名，查询的字段，条件，条件的值，分组，分组的条件，排序)
        Cursor cursor=db.rawQuery("select * from blackPhone order by _id desc limit ?,10",
                new String[]{showCount+""});
        //查询到一个集合
        List<BlackNum> blackNumList=new ArrayList<>();
        if (cursor!=null){
            while (cursor.moveToNext()){
                BlackNum blackNum=new BlackNum();
                String phone=cursor.getString(1);
                String mode=cursor.getString(2);
                blackNum.setPhone(phone);
                blackNum.setMode(mode);
                blackNumList.add(blackNum);
            }
            cursor.close();
            db.close();
        }
        return blackNumList;
    }

    
    
    //获取数据库中的数据总数
    //分页展示数据，如果用户不需要查看更多，可以节省资源
    public int getCount() {
        int count=0;
        //开启数据库，做写入操作
        SQLiteDatabase db = helper.getWritableDatabase();
        //(表名，查询的字段，条件，条件的值，分组，分组的条件，排序)
        Cursor cursor = db.rawQuery("select count(*) from blackPhone;",null);
        if (cursor!=null){
            while(cursor.moveToNext()){
                count=cursor.getInt(0);
            }
            cursor.close();
            db.close();
        }
        return count;
    }


    //挂断电话时，根据电话号码拿拦截类型
    public String queryModeByPhone(String inComingNumber){
        //开启数据库，做写入操作
        SQLiteDatabase db = helper.getWritableDatabase();
        //(表名，查询的字段，条件，条件的值，分组，分组的条件，排序)
        Cursor cursor=db.rawQuery("select mode from blackPhone where phone=?",
                new String[]{inComingNumber});
        //查询到一个集合
        String mode=null;
        if (cursor!=null){
            while (cursor.moveToNext()){
                mode=cursor.getString(0);
            }
            cursor.close();
            db.close();
        }
        return mode;
    }
}
