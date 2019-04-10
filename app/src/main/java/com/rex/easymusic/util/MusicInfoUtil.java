package com.rex.easymusic.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * 传入id值,获取相对应的资源信息
 * Created by Bobby on 2019/1/2
 */
public class MusicInfoUtil {
    private static final String PLAY_URL="http://music.163.com/song/media/outer/url?id=";
    private static final String INFO_URL="http://106.13.36.192:3000/song/detail?ids=";
    private static final String LRC_URL="http://music.163.com/api/song/lyric?id=";
    private JSONObject jsonObject;

    public MusicInfoUtil(int id){
        try {
            jsonObject=new JSONObject(HttpUtil.sendOkHttpRequest(INFO_URL+id))
                    .getJSONArray("songs")
                    .getJSONObject(0);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取专辑名称
     */
    public String getAlbumName() throws JSONException {
        try{
            return  jsonObject.getJSONObject("al").getString("name");
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }

    }

    /**
     * 获取图片封面
     * @return
     * @throws JSONException
     */
    public String getPicUrl() throws JSONException {
        try{
            return  jsonObject.getJSONObject("al").getString("picUrl");
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }

    }

    /**
     * 获取歌手
     * @return
     * @throws JSONException
     */
    public String getSinger() throws JSONException {
        StringBuilder singer=new StringBuilder();
        try{
            JSONArray array=jsonObject.getJSONArray("ar");
            for (int i=0;i<array.length();i++){
                singer=singer.append(array.getJSONObject(i).getString("name")).append(" ");
            }
            return singer.toString();
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }

    }

    /**
     * 获取歌曲名称
     * @return
     */
    public String getMusicName()   {
        try {
             String MusicName=jsonObject.getString("name");
             return MusicName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }



    /**
     * 获取歌曲播放资源
     */
    public static String getAudioUrl(int id)
    {
        return PLAY_URL+id+".mp3";
    }

    /**
     * 获取歌曲歌词链接
     */
    public static String getLrcUrl(int id)
    {
        return LRC_URL+id+"&lv=-1&kv=-1";
    }

    /**
     * 获取歌曲封面Url
     */
    public static String getPicUrl(int id) throws IOException, JSONException {
        return new JSONObject(HttpUtil.sendOkHttpRequest(INFO_URL+id))
                .getJSONArray("songs")
                .getJSONObject(0)
                .getJSONObject("al")
                .getString("picUrl");
    }
}
