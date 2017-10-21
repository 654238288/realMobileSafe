package com.example.liujingjing.mobilesafe.MyApplication.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by liujingjing on 17-10-9.
 */

public class PhoneAddressDao {
    //做归属地查询操作
    private static String path="data/data/com.example.liujingjing.mobilesafe/files/address.db";

    //根据获取到的手机号进行查询
    public static String getAddress(String phone){
        Log.i("ljj","进入查询方法");
        //定义存储查到的归属地的变量\

        Log.e("test","--------------->");
        String phoneAddress="未知号码";
        //正则表达式匹配
        String regex="^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$";
       /* String regex="^1[3-8]\\d{9}$";*/
        //开启一个数据库链接
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        if (phone.matches(regex)){
            Log.i("ljj","匹配成功");
            //凭借前七位就可以确定归属地，因此之用前七位去查寻
            String num=phone.substring(0,7);
            //先跟据address中data1表中的手机号查到data2表中相关联的外键，再用这个字段去查归属地
            //Cursor cursor=db.query("data1",new String[]{"outkey"},"id=?",new String[]{phone},
                   // null,null,null);

            //3,数据库查询
            Cursor cursor = db.query("data1", new String[]{"outkey"}, "id = ?", new String[]{num}, null, null, null);
            //4,查到即可

            Log.e("test","cursor--------------->"+cursor);

            if(cursor.moveToNext()){

                Log.i("ljj","查到数据");
                String outkey=cursor.getString(0);
                Log.i("ljj","outkey="+outkey);
                Cursor cursor1=db.query("data2",new String[]{"location"},"id=?",
                        new String[]{outkey},null,null,null);
                if (cursor1.moveToNext()){
                    phoneAddress=cursor1.getString(0);
                    Log.i("ljj","phoneAddress="+phoneAddress);
                }
            }else{
                phoneAddress="未知号码";
            }

        }else{
            switch (phone.length()){
                case 3:
                    if(phone.equals("110")||phone.equals("119")||phone.equals("120"))
                    phoneAddress="报警电话";
                    break;
                case 4:
                    phoneAddress="模拟器";
                    break;
                case 5:
                    if(phone.equals("10010")||phone.equals("10086")||phone.equals("99554"))
                    phoneAddress="服务电话";
                    break;
                case 7:
                    phoneAddress="本地电话";
                    break;
                case 8:
                    phoneAddress="本地电话";
                    break;
                case 11://(3+8) 区号+座机号码(外地),查询data2
                    //因为区号前都有0，数据库为了节省空间，统一把第一位的0去掉了
                    String num=phone.substring(1,3);
                    Cursor cursor2=db.query("data2",new String[]{"location"},"area=?",new String[]{num},
                            null,null,null);
                    if (cursor2.moveToNext()){
                    phoneAddress=cursor2.getString(0);
                    }else {
                        phoneAddress="未知号码";
                    }
                    break;
                case 12://(4+8) 区号+座机号码(外地),查询data2
                    //因为区号前都有0，数据库为了节省空间，统一把第一位的0去掉了
                    String num1=phone.substring(1,4);
                    Cursor cursor3=db.query("data2",new String[]{"location"},"area=?",new String[]{num1},
                            null,null,null);
                    if (cursor3.moveToNext()){
                        phoneAddress=cursor3.getString(0);
                    }else {
                        phoneAddress="未知号码";
                    }
                    break;
            }
        }
        return phoneAddress;
    }
}
