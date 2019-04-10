package com.rex.easymusic.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Rex on 2019/2/25
 * MD5加密工具类
 */
public class Md5Util {

    /**
     * 传入字符串，返回加密字符串
     * @param password
     * @return
     */
    public static String md5Password(String password){
        StringBuffer stringBuffer=new StringBuffer();
        try{
            MessageDigest digest=MessageDigest.getInstance("md5");
            byte[] result=digest.digest(password.getBytes());
            for (byte b:result){
                int number=b & 0xff;
                String str=Integer.toHexString(number);
                if (str.length()==1){
                    stringBuffer.append("0");
                }
                stringBuffer.append(str);
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return stringBuffer.toString();
    }
}
