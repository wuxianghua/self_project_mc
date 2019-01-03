package net.imoran.auto.music.bean;

import java.io.Serializable;

public class NetTypeBean implements Serializable {
    private int type;// 1：标签  2:收藏
    private String name;
    private String iconResUrl;

    public NetTypeBean() {
        this.type = 2;
        this.name = "";
        this.iconResUrl = "";
    }

    public NetTypeBean(String name, String iconResUrl) {
        this.type = 1;
        this.name = name;
        this.iconResUrl = iconResUrl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconResUrl() {
        return iconResUrl;
    }

    public void setIconResUrl(String iconResId) {
        this.iconResUrl = iconResId;
    }
}
