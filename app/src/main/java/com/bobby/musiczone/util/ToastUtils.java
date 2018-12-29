package com.bobby.musiczone.util;

import android.content.Context;
import android.widget.Toast;



/**
 * 提示工具类
 */
public class ToastUtils {
    private static Toast toast;

    public static void show(Context context, String info) {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(context, info, Toast.LENGTH_SHORT);
        toast.show();
    }

//    public static void show(String info) {
//        if (toast != null)
//            toast.cancel();
//        toast = Toast.makeText(MusicApp.getAppContext(), info, Toast.LENGTH_SHORT);
//        toast.show();
//    }


    public static void show(Context context, int resId) {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT);
        toast.show();
    }
}
