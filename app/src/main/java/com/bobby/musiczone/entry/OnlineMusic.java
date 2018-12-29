package com.bobby.musiczone.entry;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class OnlineMusic implements Serializable {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OnlineMusic.album getAlbum() {
        return album;
    }

    public void setAlbum(OnlineMusic.album album) {
        this.album = album;
    }

    @SerializedName("id")
    public int id;
    @SerializedName("name")
    public String name;
    @SerializedName("album")
    public album album;
    @SerializedName("artists")
    public List<artists> artistsList;
    public class artists{
        @SerializedName("name")
        public String singer;
    }
    public static class album{
        @SerializedName("name")
        public String albumname;
    }
    public String audio;
    public String picUrl;
    public String lrcUrl;

}
