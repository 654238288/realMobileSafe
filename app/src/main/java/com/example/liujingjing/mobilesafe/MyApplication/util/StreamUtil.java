package com.example.liujingjing.mobilesafe.MyApplication.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by liujingjing on 17-9-20.
 */

public class StreamUtil {

    public static String streamToString(InputStream is) {
        //将读取到的内容存入，以便一次性转换成字符串
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        //定义一次读多少
        byte[] b=new byte[1024];
        //循环读取，直到读完为止
        int temp=-1;
        try {
            while((temp=is.read(b))!=-1){
                //读多少，写多少
                bos.write(b,0,temp);
            }
            //把写好的流转换成字符串返回回去
            return bos.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                is.close();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
