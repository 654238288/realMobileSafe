package com.example.liujingjing.mobilesafe.MyApplication.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.liujingjing.mobilesafe.R;



//一次性申请所有权限
public class BaseActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    // 声明一个数组，用来存储所有需要动态申请的权限
    private String[] permissions = new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_SMS,
            Manifest.permission.VIBRATE,
            Manifest.permission.SYSTEM_ALERT_WINDOW};

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base2);
        //申请权限
        if(askForPermission()){
            requestMusicPermissions();
        }

    }



    //申请这个软件需要用到的所有权限
    private Boolean askForPermission() {
        /** * 判断哪些权限未授予 * 以便必要的时候申请 */
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {

        return  true;
            }
        }
        return  false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestMusicPermissions() {
        this.requestPermissions(permissions,
                REQUEST_EXTERNAL_STORAGE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this,"您没有该权限！",Toast.LENGTH_SHORT).show();
            }
        }

    }
}
