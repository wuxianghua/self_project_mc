package net.imoran.auto.music.player.manager;


import net.imoran.auto.music.player.model.SongModel;

import java.util.List;

public interface IMusicPlayManger {

    List<SongModel> getPlayList();

    SongModel getPlaySong();

    void prepare(List<SongModel> list);

    void play();

    void pause();

    void resume();

    void stop();

    void next();

    void previous();

    void playByUuid(String uuid);

    void playIndex(int index, long positionMs);

    void setSpeed(float speed);

    void release();

    boolean isPlaying();

    void fastForward(long milliseconds);

    void fastBack(long milliseconds);

    void seekTo(long position);

    void setRepeatMode(int mode);

    long getPlayPosition();
}
