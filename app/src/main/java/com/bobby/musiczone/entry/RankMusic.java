package com.bobby.musiczone.entry;

import com.bobby.musiczone.entry.OnlineMusic;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class RankMusic implements Serializable {

    public int id;
    public String album;
    public String lrcUrl;
    public String audio;
}
