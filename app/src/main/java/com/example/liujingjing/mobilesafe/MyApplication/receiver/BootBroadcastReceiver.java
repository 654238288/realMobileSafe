package com.example.liujingjing.mobilesafe.MyApplication.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.liujingjing.mobilesafe.MyApplication.util.ConstantValue;
import com.example.liujingjing.mobilesafe.MyApplication.util.SPUtil;

/**
 * Created by liujingjing on 17-9-28.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {

    //监听sim卡是否被切换，就要监听开关机广播
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("gbgbgbgb","进入接收广播的方法");
        //取出sp中存储的序列卡号
        String sim_serious= SPUtil.getString(context, ConstantValue.SIM_NUMBER,"");
        //拿到当前手机上的sim卡的序列号
        TelephonyManager tm= (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String sinSerial=tm.getSimSerialNumber()+"rrr";
        //比较,如果不同，发送报警短信给安全号码
        if(!(sinSerial.equals(sim_serious))){
            Log.i("gbgbgbgb","序列卡号不一致");
            //获取发送短信的对象
            SmsManager sm=SmsManager.getDefault();
            //发送短信
            sm.sendTextMessage(SPUtil.getString(context,ConstantValue.CONTACT_PHONE,""),
                    null,"sim card is changed",null,null);
        }

        //开启监听短信的服务
        //context.startService(new Intent(context, SmsService.class));
    }

}
