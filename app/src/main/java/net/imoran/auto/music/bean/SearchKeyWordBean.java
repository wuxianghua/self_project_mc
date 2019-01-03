package net.imoran.auto.music.bean;

import net.imoran.sdk.bean.bean.SongBean;

import java.io.Serializable;


public class SearchKeyWordBean implements Serializable {
    private String type;//history,hot
    private String searchName;

    public SearchKeyWordBean(String type, String searchName) {
        this.type = type;
        this.searchName = searchName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }
}
