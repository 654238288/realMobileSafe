package com.example.liujingjing.mobilesafe.MyApplication.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by liujingjing on 17-9-29.
 */

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //如果开启了防盗保护
        android.util.Log.e("xuzhi","33333333333333333333");

    }
}
