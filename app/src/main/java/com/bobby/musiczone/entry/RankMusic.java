package com.bobby.musiczone.entry;

import com.bobby.musiczone.entry.OnlineMusic;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class RankMusic implements Serializable {
    @SerializedName("name")
    public String name;
    @SerializedName("id")
    public int id;
    @SerializedName("ar")
    public List<OnlineMusic.artists> artistList;
    @SerializedName("al")
    public album album;
    public class album{
        @SerializedName("name")
        public String albumName;
        @SerializedName("picUrl")
        public String picUrl;
    }
    public String lrcUrl;
    public String audio;
}
