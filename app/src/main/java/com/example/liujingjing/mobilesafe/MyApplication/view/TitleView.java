package com.example.liujingjing.mobilesafe.MyApplication.view;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by liujingjing on 17-9-23.
 */

public class TitleView extends android.support.v7.widget.AppCompatTextView {

    //在java类中通过代码创建自定义控件
    public TitleView(Context context) {
        super(context);
    }

    //在xml文件中通过配置创建自定义控件
    public TitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //由系统调用带属性+上下文环境+布局文件中定义文件样式
    public TitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //该类的作用是让该文本实现滚动(marquee)效果,因此要让该文本一直得到焦点
    //重写这个方法，返回值改为true,让系统调用该方法时，默认为文本得到焦点
    @Override
    public boolean isFocused() {
        return true;
    }
}
