package com.example.liujingjing.mobilesafe.MyApplication.activity;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

/**
 * Created by liujingjing on 17-10-16.
 */

public class MessageObserver extends ContentObserver {

    private Context mContext;
    public MessageObserver(Context context,Handler handler){
        super(handler);
        mContext=context;
        mContext.getContentResolver().registerContentObserver(
                Uri.parse("content://sms/"), true, this);
    }
    @Override
    public void onChange(boolean selfChange) {
        Log.d("lwx","on change 1");
        super.onChange(selfChange);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        Log.d("lwx","on change 2");
        super.onChange(selfChange, uri);
    }
}
