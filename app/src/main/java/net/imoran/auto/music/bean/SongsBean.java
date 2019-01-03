package net.imoran.auto.music.bean;

import net.imoran.sdk.bean.bean.SongBean;

import java.util.ArrayList;

/**
 * Created by love on 2018/5/3.
 * 这个类主要的作用是负责音乐播放的bean，由AudioAlbumBean.AudioAlbumEntity和SongBean.SongEntity转化而来。
 */

public class SongsBean {
    private String songsType;//netMusic,localMusic,
    private String music_id;
    private String pic_url;
    private String song_url;
    private String name;
    private String album;
    private String album_id;

    public String getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(String album_id) {
        this.album_id = album_id;
    }

    public String getTrack_id() {
        return track_id;
    }

    public void setTrack_id(String track_id) {
        this.track_id = track_id;
    }

    private String track_id;
    private long duration;
    private ArrayList<String> singer;
    private boolean is_in_user_collection;

    private int page;


    public SongsBean() {

    }

    public static SongsBean convertOfSongEntity(SongBean.SongEntity songEntity) {
        SongsBean songsBean = new SongsBean();

        return songsBean;
    }


    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getSongsType() {
        return songsType;
    }

    public void setSongsType(String songsType) {
        this.songsType = songsType;
    }

    public String getMusic_id() {
        return music_id;
    }

    public void setMusic_id(String music_id) {
        this.music_id = music_id;
    }

    public String getPic_url() {
        return pic_url;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getSinger() {
        return singer;
    }

    public void setSinger(ArrayList<String> singer) {
        this.singer = singer;
    }

    public String getSong_url() {
        return song_url;
    }

    public void setSong_url(String song_url) {
        this.song_url = song_url;
    }

    public boolean isIs_in_user_collection() {
        return is_in_user_collection;
    }

    public void setIs_in_user_collection(boolean is_in_user_collection) {
        this.is_in_user_collection = is_in_user_collection;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public boolean isNetMusic() {
        return "netMusic".equals(songsType);
    }

    public boolean isLocalMusic() {
        return "localMusic".equals(songsType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SongsBean songsBean = (SongsBean) o;

        return music_id != null ? music_id.equals(songsBean.music_id) : songsBean.music_id == null;
    }

    @Override
    public int hashCode() {
        return music_id != null ? music_id.hashCode() : 0;
    }
}
