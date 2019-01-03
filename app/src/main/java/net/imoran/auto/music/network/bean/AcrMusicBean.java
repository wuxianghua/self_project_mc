package net.imoran.auto.music.network.bean;

import java.util.List;

public class AcrMusicBean {
    private String label;
    private int play_offset_ms;
    private List<AcrArtistsBean> artists;
    private int result_from;
    private String title;
    private int duration_ms;
    private AcrAlbumBean album;
    private int score;
    private String acrid;
    private String release_date;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getPlay_offset_ms() {
        return play_offset_ms;
    }

    public void setPlay_offset_ms(int play_offset_ms) {
        this.play_offset_ms = play_offset_ms;
    }

    public List<AcrArtistsBean> getArtists() {
        return artists;
    }

    public void setArtists(List<AcrArtistsBean> artists) {
        this.artists = artists;
    }

    public int getResult_from() {
        return result_from;
    }

    public void setResult_from(int result_from) {
        this.result_from = result_from;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration_ms() {
        return duration_ms;
    }

    public void setDuration_ms(int duration_ms) {
        this.duration_ms = duration_ms;
    }

    public AcrAlbumBean getAlbum() {
        return album;
    }

    public void setAlbum(AcrAlbumBean album) {
        this.album = album;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getAcrid() {
        return acrid;
    }

    public void setAcrid(String acrid) {
        this.acrid = acrid;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }
}
