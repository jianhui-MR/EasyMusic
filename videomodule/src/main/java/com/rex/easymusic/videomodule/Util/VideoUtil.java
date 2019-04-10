package com.rex.easymusic.videomodule.Util;

import com.rex.easymusic.videomodule.Bean.videoUrl;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * 传入视频Id,获取视频信息
 */
public class VideoUtil {
    private String getVideoUrl="http://106.13.36.192:3000/mv/url?id=";
    private videoUrl videoUrl;
    public VideoUtil(int id) {
        String responsebody= null;
        try {
            responsebody = HttpUtil.sendOkHttpRequest(getVideoUrl+id);
            JSONObject jsonObject=new JSONObject(responsebody).getJSONObject("data");
            videoUrl=new Gson().fromJson(jsonObject.toString(),videoUrl.class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getGetVideoUrl(){
        return videoUrl.getUrl();
    }
}
