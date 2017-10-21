package com.example.liujingjing.mobilesafe.MyApplication.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.liujingjing.mobilesafe.R;

/**
 * Created by liujingjing on 17-9-25.
 */

public class SettingClickView extends RelativeLayout {

    private ImageView  iv_click;
    private TextView   tv_toast_des;
    private TextView   tv_title;


    //在此构造方法中调用第二个构造方法，在第二个构造方法中调用第三个，使得无论调用哪个，最终都调用第三个
    public SettingClickView(Context context) {
        this(context,null);
    }

    //在布局文件中带属性时要用到的构造方法
    public SettingClickView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    //在布局文件中带样式时要用到的构造方法
    //自定义组合控件，把xml文件的布局引用过来，xml-view
    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //把布局文件转换成的view对象挂载到SettingItemsView中来，最后再把SettingItemsView挂载到设置页面去
        View.inflate(context, R.layout.activity_setting_click,this);

        //因为两个标题描述和复选框被复用时要做改变，因此在这里把它们都获取出来
        tv_title= (TextView) findViewById(R.id.tv_items_title);
        tv_toast_des= (TextView) findViewById(R.id.tv_items_des);

    }

    //设置标题
    public void setTitle(String title){
        tv_title.setText(title);
    }

    //设置描述
    public void setDes(String description){
        tv_toast_des.setText(description);
    }
}
