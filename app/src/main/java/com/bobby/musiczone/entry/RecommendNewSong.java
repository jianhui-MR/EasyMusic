package com.bobby.musiczone.entry;

import com.google.gson.annotations.SerializedName;

/**
 * 推荐新音乐
 * Created by Bobby on 2018/12/29
 */
public class RecommendNewSong {
    private String name;
    private String id;
    private String picUrl;
    @SerializedName("song")
    private Song song;

    public class Song{
        @SerializedName("album")
        public Album album;

        public class Album{
            public String picUrl;
        }
    }


    public String getName() {
        return name;
    }
    public String getId() {
        return id;
    }

    public String getPicUrl() {
        return song.album.picUrl;
    }

}
