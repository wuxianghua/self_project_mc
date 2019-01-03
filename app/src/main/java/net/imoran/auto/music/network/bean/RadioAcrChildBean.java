package net.imoran.auto.music.network.bean;

import java.util.List;

public class RadioAcrChildBean {
    private String logo;
    private List<String> short_name;
    private List<RadioAcrChildItemBean> playlist;
    private String freq;
    private String name;
    private String region;
    private String type;

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public List<String> getShort_name() {
        return short_name;
    }

    public void setShort_name(List<String> short_name) {
        this.short_name = short_name;
    }

    public List<RadioAcrChildItemBean> getPlaylist() {
        return playlist;
    }

    public void setPlaylist(List<RadioAcrChildItemBean> playlist) {
        this.playlist = playlist;
    }

    public String getFreq() {
        return freq;
    }

    public void setFreq(String freq) {
        this.freq = freq;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
