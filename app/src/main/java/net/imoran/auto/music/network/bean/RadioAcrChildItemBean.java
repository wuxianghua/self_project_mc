package net.imoran.auto.music.network.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RadioAcrChildItemBean {
    private String start_time_utc;
    private List<String> anchors;
    private boolean is_playing;
    private String end_time_utc8;
    private String name;
    private String end_time_utc;
    private String start_time_utc8;
    private long start_time_l;
    private long end_time_l;
    private String fmName;

    public String getStart_time_utc() {
        return start_time_utc;
    }

    public void setStart_time_utc(String start_time_utc) {
        this.start_time_utc = start_time_utc;
    }

    public List<String> getAnchors() {
        return anchors;
    }

    public void setAnchors(List<String> anchors) {
        this.anchors = anchors;
    }

    public boolean isIs_playing() {
        return is_playing;
    }

    public void setIs_playing(boolean is_playing) {
        this.is_playing = is_playing;
    }

    public String getEnd_time_utc8() {
        return end_time_utc8;
    }

    public void setEnd_time_utc8(String end_time_utc8) {
        this.end_time_utc8 = end_time_utc8;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnd_time_utc() {
        return end_time_utc;
    }

    public void setEnd_time_utc(String end_time_utc) {
        this.end_time_utc = end_time_utc;
    }

    public String getStart_time_utc8() {
        return start_time_utc8;
    }

    public void setStart_time_utc8(String start_time_utc8) {
        this.start_time_utc8 = start_time_utc8;
    }

    public long getStart_time_l() {
        return start_time_l;
    }

    public void setStart_time_l(long start_time_l) {
        this.start_time_l = start_time_l;
    }

    public long getEnd_time_l() {
        return end_time_l;
    }

    public void setEnd_time_l(long end_time_l) {
        this.end_time_l = end_time_l;
    }

    public String getFmName() {
        return fmName;
    }

    public void setFmName(String fmName) {
        this.fmName = fmName;
    }
}
