package com.example.liujingjing.mobilesafe.db.domain;

import android.graphics.drawable.Drawable;


/**
 * Created by liujingjing on 17-10-17.
 */

public class AppInfo {
    //包名，应用名，图标，是系统应用还是手机应用
    private String appName;
    private String packageName;
    private Drawable icon;
    private boolean isSdCard;
    private boolean isSystem;

    public String getAppName() {
        return appName;
    }
    public void setAppName(String name) {
        this.appName = name;
    }
    public String getPackageName() {
        return packageName;
    }
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    public Drawable getIcon() {
        return icon;
    }
    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
    public boolean isSdCard() {
        return isSdCard;
    }
    public void setSdCard(boolean isSdCard) {
        this.isSdCard = isSdCard;
    }
    public boolean isSystem() {
        return isSystem;
    }
    public void setSystem(boolean isSystem) {
        this.isSystem = isSystem;
    }
}
