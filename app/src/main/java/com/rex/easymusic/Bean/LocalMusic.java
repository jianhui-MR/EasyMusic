package com.rex.easymusic.Bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Rex on 2018/7/13.
 */

public class LocalMusic extends DataSupport implements Serializable {

    public String time;

    public String getAlbumName() {
        return AlbumName;
    }

    public void setAlbumName(String albumName) {
        AlbumName = albumName;
    }

    public String AlbumName;

    public String getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }

    public String albumArt;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getSrcPath() {
        return srcPath;
    }

    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    public long getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String size) {
        this.time = size;
    }

    private long id;
    private String name;
    private String singer;
    private String srcPath;
}
