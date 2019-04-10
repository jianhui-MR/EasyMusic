package com.rex.easymusic.videomodule.Bean;

import java.util.List;

/**
 * Created by Rex on 2019/2/20
 */
public class video {

    /**
     * id : 10842099
     * cover : http://p1.music.126.net/kw0dT3FWHKGL75HObSt59g==/109951163738936452.jpg
     * name : 沙漠骆驼
     * playCount : 2598988
     * briefDesc : null
     * desc : null
     * artistName : 展展与罗罗
     * artistId : 12475735
     * duration : 0
     * mark : 0
     * lastRank : 1
     * score : 37967
     * subed : false
     * artists : [{"id":12475735,"name":"展展与罗罗"}]
     */

    private int id;
    private String cover;
    private String name;
    private int playCount;
    private Object briefDesc;
    private Object desc;
    private String artistName;
    private int artistId;
    private int duration;
    private int mark;
    private int lastRank;
    private int score;
    private boolean subed;
    private String playUrl;
    private List<ArtistsBean> artists;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCoverUrl() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getVideoName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public Object getBriefDesc() {
        return briefDesc;
    }

    public void setBriefDesc(Object briefDesc) {
        this.briefDesc = briefDesc;
    }

    public Object getDesc() {
        return desc;
    }

    public void setDesc(Object desc) {
        this.desc = desc;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public int getLastRank() {
        return lastRank;
    }

    public void setLastRank(int lastRank) {
        this.lastRank = lastRank;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isSubed() {
        return subed;
    }

    public void setSubed(boolean subed) {
        this.subed = subed;
    }

    public void setPlayUrl(String playUrl){
        this.playUrl=playUrl;
    }

    public String getPlayUrl(){
        return playUrl;
    }

    public List<ArtistsBean> getArtists() {
        return artists;
    }

    public void setArtists(List<ArtistsBean> artists) {
        this.artists = artists;
    }

    public static class ArtistsBean {
        /**
         * id : 12475735
         * name : 展展与罗罗
         */

        private int id;
        private String name;

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
    }
}
