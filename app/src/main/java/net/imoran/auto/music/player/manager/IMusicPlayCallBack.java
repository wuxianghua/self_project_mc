package net.imoran.auto.music.player.manager;

import net.imoran.auto.music.player.model.SongModel;

public interface IMusicPlayCallBack {
    void onBuffer();

    void onPlay();

    //一页的歌曲播放完毕，需要加载下一页数据
    void onPlayEnd();

    void onPause(long playPosition);

    void onError(String error);

    void onProgress(float progress,long playPosition);

    void onPlaySongChange(SongModel song, int position);

    void onRepeatModeChange();
}
