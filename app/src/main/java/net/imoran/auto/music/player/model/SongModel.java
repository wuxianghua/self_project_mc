package net.imoran.auto.music.player.model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

public class SongModel implements Serializable {
    private String type = "";
    private String uuid = "";
    private String picUrl = "";
    private String songUrl = "";
    private String name = "";
    private String album = "";
    private String albumId = "";
    private String trackId = "";
    private long duration;
    private boolean isCollection;
    private ArrayList<String> singer = new ArrayList<>();
    private int page;
    private long playPosition;//已经播放的位置
    private float playProgress;//播放进度
    private boolean isPlay = false;
    private Bitmap bmp;//本地音乐封面

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public void setCollection(boolean collection) {
        isCollection = collection;
    }

    public ArrayList<String> getSinger() {
        return singer;
    }

    public void setSinger(ArrayList<String> singer) {
        this.singer = singer;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public long getPlayPosition() {
        return playPosition;
    }

    public void setPlayPosition(long playPosition) {
        this.playPosition = playPosition;
    }

    public float getPlayProgress() {
        return playProgress;
    }

    public void setPlayProgress(float playProgress) {
        this.playProgress = playProgress;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }

    public Bitmap getBmp() {
        return bmp;
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    }
}
