package com.example.liujingjing.mobilesafe.MyApplication.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;

import com.example.liujingjing.mobilesafe.MyApplication.util.ConstantValue;
import com.example.liujingjing.mobilesafe.MyApplication.util.SPUtil;

//只要手机收到包含#*location*#的短信，就开启此服务，把定位到的经纬度坐标发给安全号码
public class LocationService extends Service {

    //因为服务无论开启多少次，都只创建一个对象，因此定位实现写在创建方法里
    @Override
    public void onCreate() {
        Log.d("ljj","开启了定位服务");
        //先拿到定位管理者
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //因为所在地点的环境不一定，室内网络好，室外GPS好，因此此处以最优方式获取经纬度坐标
        Criteria criteria = new Criteria();
        //设置criteria的一些配置，
        // 允许花费流量
        criteria.setCostAllowed(true);
        //指定获取经纬度的精确性是低、高、还是差不多
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //允许匹配(或者计算)出的在最优方式下获取到的定位方式来执行定位
        String bestProvider = lm.getBestProvider(criteria, true);

        //定位需要的定位监听接口的实现类
        MyLocationListener ml = new MyLocationListener();
        //用定位对象实施定位
        //获取经纬度坐标

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(bestProvider, 0, 0, ml);
        Log.d("ljj","结束了定位服务");
        super.onCreate();
    }




       class MyLocationListener implements LocationListener{

           //定位发生改变时
           @Override
           public void onLocationChanged(Location location) {
               Log.d("ljj","进入开启经纬度定位的方法");
               //获取经度
               double longitude=location.getLongitude();
               //获取纬度
               double latitude=location.getLatitude();

               //把获取到的经纬度发送给你的安全号码
               SmsManager sm=SmsManager.getDefault();
               android.util.Log.e("xuzhi","1111111111111111111");
               sm.sendTextMessage(SPUtil.getString(getApplicationContext(), ConstantValue.CONTACT_PHONE,""),null,"longtitude="+longitude+",latitude="+latitude,null,null);
           }


           
           @Override
           public void onStatusChanged(String s, int i, Bundle bundle) {

           }

           @Override
           public void onProviderEnabled(String s) {

           }

           @Override
           public void onProviderDisabled(String s) {

           }
       }





    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
