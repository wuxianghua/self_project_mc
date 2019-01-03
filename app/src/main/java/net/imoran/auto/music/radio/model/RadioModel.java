package net.imoran.auto.music.radio.model;

import net.imoran.auto.music.radio.manager.RadioBand;

public class RadioModel {
    private double frequency;
    private String location;
    private String info;
    private String title;
    private String coverUrl;
    private String playUrl;
    private RadioBand band;

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public RadioBand getBand() {
        return band;
    }

    public void setBand(RadioBand band) {
        this.band = band;
    }
}
