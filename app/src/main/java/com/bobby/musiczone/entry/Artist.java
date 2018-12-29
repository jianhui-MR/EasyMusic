package com.bobby.musiczone.entry;

import com.google.gson.annotations.SerializedName;

/**
 * 歌手
 */
public class Artist {
    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String img1v1Url) {
        this.imgUrl = img1v1Url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @SerializedName("img1v1Url")
    private String imgUrl;
    @SerializedName("name")
    private String name;
    @SerializedName("id")
    private String id;
}
