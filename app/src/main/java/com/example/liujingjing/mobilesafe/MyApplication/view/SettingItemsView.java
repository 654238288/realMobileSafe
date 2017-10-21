package com.example.liujingjing.mobilesafe.MyApplication.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.liujingjing.mobilesafe.R;

/**
 * Created by liujingjing on 17-9-25.
 */

public class SettingItemsView extends RelativeLayout {

    private static final String NAMESPACE="http://schemas.android.com/apk/res/com.example.liujingjing.mobilesafe";
    private CheckBox   cb_check;
    private TextView   tv_items_des;
    private TextView   tv_title;
    private String destitle;
    private String deson;
    private String desoff;

    //在此构造方法中调用第二个构造方法，在第二个构造方法中调用第三个，使得无论调用哪个，最终都调用第三个
    public SettingItemsView(Context context) {
        this(context,null);
    }

    //在布局文件中带属性时要用到的构造方法
    public SettingItemsView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    //在布局文件中带样式时要用到的构造方法
    //自定义组合控件，把xml文件的布局引用过来，xml-view
    public SettingItemsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //把布局文件转换成的view对象挂载到SettingItemsView中来，最后再把SettingItemsView挂载到设置页面去
        View.inflate(context, R.layout.activity_setting_items,this);

        //因为两个标题描述和复选框被复用时要做改变，因此在这里把它们都获取出来
        tv_title= (TextView) findViewById(R.id.tv_items_title);
        tv_items_des= (TextView) findViewById(R.id.tv_items_des);
        cb_check= (CheckBox) findViewById(R.id.cb_check);

        //初始化自定义控件的属性
        unitAttrs(attrs);

        //设置描述标题的内容
        tv_title.setText(destitle);
    }


    //初始化自定义控件的属性值
    private void unitAttrs(AttributeSet attrs) {
        //通过命名空间，属性名来获取属性值
        destitle=attrs.getAttributeValue(NAMESPACE,"destitle");
        deson=attrs.getAttributeValue(NAMESPACE,"deson");
        desoff=attrs.getAttributeValue(NAMESPACE,"desoff");

        Log.i("hj",destitle);
    }



    //判断CheckBox的选中状态
    public boolean isCheck(){
        return cb_check.isChecked();
    }

    //根据选中的状态来更改CheckBox的选中状态
    public  void setCheck(boolean ischeck){
        cb_check.setChecked(ischeck);
        if(ischeck){
            tv_items_des.setText(deson);
        }else {
            tv_items_des.setText(desoff);
        }
    }
}
