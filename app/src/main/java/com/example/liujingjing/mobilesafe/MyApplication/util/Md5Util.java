package com.example.liujingjing.mobilesafe.MyApplication.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by liujingjing on 17-9-26.
 */

public class Md5Util {


    public static String encoder(String psd){
        psd=psd+"mobilesafe";//给密码加盐，增加md5解密的难度，提高密码的安全性
        //设置加密算法为MD5
        try {
            MessageDigest digest=MessageDigest.getInstance("MD5");
            byte[] bs=digest.digest(psd.getBytes());
            StringBuffer sb=new StringBuffer();
            //把加盐后的密码转换成MD5算法需要的32位十六进制数
            for (byte b:bs) {
                int i=b & 0xff;//md5规定写法
                //把int类型的数字转换为16机制字符,每个i将转换成2位的16机制字符
                String hexString=Integer.toHexString(i);
                //但有时也会出现一位的16机制字符，因此判断补0
                if(hexString.length()<2){
                    hexString="0"+hexString;
                }
                //拼接这16个2位数，组成MD5需要的32位16进制字符
                sb.append(hexString);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
