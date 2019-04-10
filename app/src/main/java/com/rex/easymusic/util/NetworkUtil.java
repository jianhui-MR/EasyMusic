package com.rex.easymusic.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class NetworkUtil {
    public static boolean isWifiNet(Context context)
    {
        ConnectivityManager connectivityManager=(ConnectivityManager)context.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        int type=networkInfo.getType();
        if (type==ConnectivityManager.TYPE_MOBILE)
            return false;
        else if (type==ConnectivityManager.TYPE_WIFI)
            return true;
        return true;
    }
}
