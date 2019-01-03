package net.imoran.auto.music.mvp.presenter.impl;

import android.content.Context;
import android.os.Handler;

import net.imoran.auto.music.mvp.base.BasePresenter;
import net.imoran.auto.music.mvp.presenter.ILocalMusicPresenter;
import net.imoran.auto.music.mvp.view.LocalMusicView;
import net.imoran.auto.music.player.model.SongModel;
import net.imoran.auto.music.utils.ListUtils;
import net.imoran.auto.music.utils.LocalMusicUtils;
import net.imoran.auto.music.utils.ThreadPoolUtils;

import java.util.ArrayList;
import java.util.List;

public class LocalMusicPresenter extends BasePresenter<LocalMusicView> implements ILocalMusicPresenter {
    private List<SongModel> songList = new ArrayList<>();
    private List<SongModel> searchList = new ArrayList<>();
    private final int PAGE_NUM = 10;
    private int count, searchCount;
    private static LocalMusicPresenter instance;
    private Handler handler = new Handler();


    public static LocalMusicPresenter newInstance() {
        if (instance == null) {
            synchronized (LocalMusicPresenter.class) {
                if (instance == null) {
                    instance = new LocalMusicPresenter();
                }
            }
        }
        return instance;
    }

    private LocalMusicPresenter() {
        super();
    }

    @Override
    public void loadLocalMusicAll(Context context) {
        LocalMusicUtils.getInstance(context).setMediaScannerListener(new LocalMusicUtils.MediaScannerListener() {
            @Override
            public void onScanCompleted(List<SongModel> list) {
                songList.addAll(list);
                count = songList.size();
                loadLocalMusicByPageNum(1);
            }
        }).startScanMusic();
    }

    @Override
    public void loadLocalMusicByPageNum(final int page) {
        for (SongModel model : songList) {
            model.setPlay(false);
        }
        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                if (count <= 10) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            view.loadLocalMusicSuccess(count, page, songList);
                        }
                    });
                } else if (count > 10) {
                    final List<SongModel> pageList = new ArrayList<>();
                    int max = Math.min(page * PAGE_NUM, count);
                    for (int i = (page - 1) * PAGE_NUM; i < max; i++) {
                        pageList.add(songList.get(i));
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            view.loadLocalMusicSuccess(count, page, pageList);
                        }
                    });
                }
            }
        });

    }

    @Override
    public void searchMusicByKeyWord(final String keyWord) {
        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                searchList.clear();
                for (SongModel model : songList) {
                    if (model.getName().contains(keyWord) ||
                            (ListUtils.isNotEmpty(model.getSinger())) && model.getSinger().get(0).contains(keyWord)) {
                        searchList.add(model);
                    }
                }
                searchCount = searchList.size();
                searchMusicByPageNum(1);
            }
        });
    }

    @Override
    public void searchMusicByPageNum(final int page) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                view.showLoading();
            }
        });
        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                if (searchCount <= 10) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            view.hideLoading();
                            view.loadLocalMusicSuccess(searchCount, page, searchList);
                        }
                    });
                } else if (searchCount > 10) {
                    final List<SongModel> pageList = new ArrayList<>();
                    int max = Math.min(page * PAGE_NUM, searchCount);
                    for (int i = (page - 1) * PAGE_NUM; i < max; i++) {
                        pageList.add(searchList.get(i));
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            view.hideLoading();
                            view.loadLocalMusicSuccess(count, page, pageList);
                        }
                    });
                }
            }
        });

    }
}
