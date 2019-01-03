package net.imoran.auto.music.vui;

import net.imoran.auto.music.player.model.SongModel;
import net.imoran.sdk.bean.bean.AudioAlbumBean;
import net.imoran.sdk.bean.bean.AudioProgramBean;

import java.util.List;

public interface IVUICallBack {

    void vuiPause();

    void vuiPlay();

    void vuiNext();

    void vuiPrevious();

    void vuiPlayIndex(int index);

    void vuiChangeRepeatMode(int mode);

    void vuiChangeSpeed(float speed);

    void vuiNextPage();

    void vuiPreviousPage();

    void vuiFastForward(long milliseconds);

    void vuiFastBack(long milliseconds);

    void vuiPlayList(int total, List<SongModel> list);

    void vuiSearch(String searchKey);

    void openPlayList(String queryId);

    void vuiCollectSong();

    void vuiCancelCollecSong();

    void vuiAlbumList(AudioAlbumBean audioAlbumBean, String queryId);

    void vuiProgramList(AudioProgramBean audioProgramBean, String queryId);

    void vuiListQueryId(String queryId);

    void vuiHotWords(String words);
}
