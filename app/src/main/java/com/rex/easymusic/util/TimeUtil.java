package com.rex.easymusic.util;

/**
 * 时间工具类
 */
public class TimeUtil {

    /**
     * 获取当前时间时间戳
     * @return
     */
    public static String getTime(){
        long time=System.currentTimeMillis()/1000;//获取系统时间的10位的时间戳
        String  str=String.valueOf(time);
        return str;
    }
}
