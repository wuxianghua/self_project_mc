package net.imoran.auto.music.player.manager;


import net.imoran.auto.music.player.model.SongModel;

import java.util.ArrayList;
import java.util.List;

public class MusicPlayModeData {
    private List<SongModel> songList = new ArrayList<>();
    private SongModel playSong;

    public SongModel getPlaySong() {
        return playSong;
    }

    public List<SongModel> getSongList() {
        return songList;
    }

    public void setSongList(List<SongModel> songList) {
        this.songList = songList;
    }

    public void setPlaySong(SongModel playSong) {
        this.playSong = playSong;
    }

}
