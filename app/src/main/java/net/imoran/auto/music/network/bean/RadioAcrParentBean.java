package net.imoran.auto.music.network.bean;

import java.util.List;

public class RadioAcrParentBean {
    private String timestamp_utc;
    private int size;
    private String params_gps;
    private List<RadioAcrChildBean> radio_list;

    public String getTimestamp_utc() {
        return timestamp_utc;
    }

    public void setTimestamp_utc(String timestamp_utc) {
        this.timestamp_utc = timestamp_utc;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getParams_gps() {
        return params_gps;
    }

    public void setParams_gps(String params_gps) {
        this.params_gps = params_gps;
    }

    public List<RadioAcrChildBean> getRadio_list() {
        return radio_list;
    }

    public void setRadio_list(List<RadioAcrChildBean> radio_list) {
        this.radio_list = radio_list;
    }
}
