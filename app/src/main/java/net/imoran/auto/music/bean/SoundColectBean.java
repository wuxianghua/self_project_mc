package net.imoran.auto.music.bean;

/**
 * Created by xinhuashi on 2018/7/20.
 */

public class SoundColectBean {
    private String type;
    private String albumId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    private String trackId;
}
