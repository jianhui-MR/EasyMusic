package com.bobby.musiczone.util;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Http工具类
 * Created by Bobby on 2018/7/10.
 */

public class HttpUtil {
    /**
     * http异步请求(不带参数)
     * @param address
     * @param callback
     */
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * http异步请求(带参数)
     * @param address
     * @param requestBody
     * @param callback
     */
    public static void sendOkHttpRequest(String address, RequestBody requestBody,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder()
                .post(requestBody)
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * htttp同步请求
     * @param address
     * @return
     * @throws IOException
     */
    public static String sendOkHttpRequest(String address) throws IOException {
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        Call call=client.newCall(request);
        return call.execute().body().string();
    }
}
