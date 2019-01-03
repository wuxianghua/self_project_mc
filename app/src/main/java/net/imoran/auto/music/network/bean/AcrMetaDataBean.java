package net.imoran.auto.music.network.bean;

import java.util.List;

public class AcrMetaDataBean {
    private int played_duration;
    private List<AcrMusicBean> music;
    private String timestamp_utc;

    public int getPlayed_duration() {
        return played_duration;
    }

    public void setPlayed_duration(int played_duration) {
        this.played_duration = played_duration;
    }

    public List<AcrMusicBean> getMusic() {
        return music;
    }

    public void setMusic(List<AcrMusicBean> music) {
        this.music = music;
    }

    public String getTimestamp_utc() {
        return timestamp_utc;
    }

    public void setTimestamp_utc(String timestamp_utc) {
        this.timestamp_utc = timestamp_utc;
    }
}
