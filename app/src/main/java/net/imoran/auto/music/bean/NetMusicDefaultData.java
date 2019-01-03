package net.imoran.auto.music.bean;

import net.imoran.auto.music.player.model.SongModel;

import java.io.Serializable;
import java.util.List;

public class NetMusicDefaultData implements Serializable {
    public List<SongModel> list;

    public NetMusicDefaultData(List<SongModel> list) {
        this.list = list;
    }

    public List<SongModel> getList() {
        return list;
    }

    public void setList(List<SongModel> list) {
        this.list = list;
    }
}
