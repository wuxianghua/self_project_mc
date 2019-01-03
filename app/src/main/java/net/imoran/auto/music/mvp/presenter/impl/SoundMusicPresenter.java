package net.imoran.auto.music.mvp.presenter.impl;

import android.os.Bundle;
import android.util.Log;

import net.imoran.auto.music.constant.RrClientWhat;

import net.imoran.auto.music.app.MusicApp;
import net.imoran.auto.music.mvp.base.BasePresenter;
import net.imoran.auto.music.mvp.presenter.ISoundMusicPresenter;
import net.imoran.auto.music.mvp.view.SoundMusicView;
import net.imoran.auto.music.ui.fragment.sound.SoundAlbumListFragment;
import net.imoran.auto.music.ui.fragment.sound.SoundCollectionFragment;
import net.imoran.auto.music.utils.JsonUtils;
import net.imoran.auto.music.utils.ReplyUtils;
import net.imoran.rripc.lib.ResponseCallback;
import net.imoran.sdk.bean.base.BaseContentEntity;
import net.imoran.sdk.bean.base.BaseSceneEntity;
import net.imoran.sdk.bean.bean.AudioAlbumBean;
import net.imoran.sdk.bean.bean.AudioProgramBean;
import net.imoran.sdk.bean.bean.BaseReply;
import net.imoran.sdk.bean.bean.LetingCatalogBean;
import net.imoran.sdk.bean.bean.LetingNewsBean;
import net.imoran.sdk.bean.bean.PodcastCategoryBean;

import java.util.ArrayList;
import java.util.List;

public class SoundMusicPresenter extends BasePresenter<SoundMusicView> implements ISoundMusicPresenter {
    private String errorMes = "网络出错请重试";

    public SoundMusicPresenter() {
        super();
    }

    @Override
    public void getRandomAudio() {
        if (!MusicApp.getInstance().isRClientCon()) {
            view.showRandomResult(null);
            return;
        }
        view.showLoading();
        Bundle bundle = new Bundle();
        bundle.putString("target", "audio_init");
        bundle.putString("pageid", view.getClass().getName());
        MusicApp.getInstance().getRrClient().send(RrClientWhat.REQUEST_TO_REST, bundle, new ResponseCallback() {
            @Override
            public void onResponse(String s) {
                if (s == null || "".equals(s)) {
                    view.showRandomResult(null);
                    return;
                }
                BaseContentEntity baseContentEntity = JsonUtils.getBaseContentEntity(s);
                if (baseContentEntity == null) return;
                String type = baseContentEntity.getBaseSceneEntity().getType();
                if (!"audio_program".equals(type)) {
                    getRandomAudio();
                    return;
                }
                BaseReply reply = baseContentEntity.getBaseReply();
                if (!(reply instanceof AudioProgramBean)) {
                    getRandomAudio();
                    return;
                }
                AudioProgramBean audioProgramBean = (AudioProgramBean) reply;
                List<AudioProgramBean.AudioProgramEntity> audio_programs = audioProgramBean.getAudio_program();
                view.hideLoading();
                if (audio_programs.size() == 0) {
                    getRandomAudio();
                    return;
                }
                view.showRandomResult(audio_programs);
                view.hideLoading();
            }

            @Override
            public void onResponseError(String s, int i) {
                view.showRandomResult(null);
            }
        });
    }

    @Override
    public void getCollectionList() {
        view.showLoading();
        if (!MusicApp.getInstance().isRClientCon()) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("target", "audio_get_track");
        bundle.putString("page", "0");
        bundle.putString("count", "100");
        bundle.putString("pageid", SoundCollectionFragment.class.getName());
        MusicApp.getInstance().getRrClient().send(RrClientWhat.REQUEST_TO_REST, bundle, new ResponseCallback() {
            @Override
            public void onResponse(String s) {
                BaseContentEntity baseContentEntity = ReplyUtils.createResponseFromJson(s);
                if (baseContentEntity == null) return;
                BaseReply baseReply = baseContentEntity.getBaseReply();
                BaseSceneEntity baseSceneEntity = baseContentEntity.getBaseSceneEntity();
                int totalCount = baseSceneEntity.getTotal_count();
                if (!(baseReply instanceof AudioProgramBean)) {
                    return;
                }
                AudioProgramBean audioAlbumBean = (AudioProgramBean) baseReply;
                ArrayList<AudioProgramBean.AudioProgramEntity> audio_program = audioAlbumBean.getAudio_program();
                view.showCollectionResult(audio_program, totalCount);
            }

            @Override
            public void onResponseError(String s, int i) {
                Log.e("haha", "Error----String s" + s);
                view.showErrorView();
            }
        });
    }

    @Override
    public void addAudioCollection(String track) {
        if (!MusicApp.getInstance().isRClientCon()) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("target", "audio_add_track");
        bundle.putString("track", track);
        bundle.putString("pageid", view.getClass().getName());
        MusicApp.getInstance().getRrClient().send(RrClientWhat.REQUEST_TO_REST, bundle, new ResponseCallback() {
            @Override
            public void onResponse(String s) {
                Log.e("hehe", s);
            }

            @Override
            public void onResponseError(String s, int i) {
            }
        });
    }

    @Override
    public void delAudioCollection(String track) {
        if (!MusicApp.getInstance().isRClientCon()) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("target", "audio_del_track");
        bundle.putString("track", track);
        bundle.putString("pageid", view.getClass().getName());
        MusicApp.getInstance().getRrClient().send(RrClientWhat.REQUEST_TO_REST, bundle, new ResponseCallback() {
            @Override
            public void onResponse(String s) {
                Log.e("hehe", s);
            }

            @Override
            public void onResponseError(String s, int i) {
                Log.e("hehe", s);
            }
        });
    }

    @Override
    public void getAllAudioTag() {
        view.showLoading();
        if (!MusicApp.getInstance().isRClientCon()) {
            view.showErrorView();
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("target", "audio_tag");
        bundle.putString("source", "qt");
        bundle.putString("pageid", view.getClass().getName() + "_audio_tag");
        MusicApp.getInstance().getRrClient().send(RrClientWhat.REQUEST_TO_REST, bundle, new ResponseCallback() {
            @Override
            public void onResponse(String s) {
                BaseContentEntity baseContentEntity = JsonUtils.getBaseContentEntity(s);
                if (baseContentEntity == null) {
                    view.showAllAudioTag(null);
                    return;
                }
                BaseReply baseReply = baseContentEntity.getBaseReply();
                if (!(baseReply instanceof PodcastCategoryBean)) {
                    getAllAudioTag();
                    return;
                }
                PodcastCategoryBean podcastCategoryBean = (PodcastCategoryBean) baseReply;
                ArrayList<PodcastCategoryBean.PodcastCategoryEntity> podcast_category = podcastCategoryBean.getPodcast_category();
                view.showAllAudioTag(podcast_category);
            }

            @Override
            public void onResponseError(String s, int i) {
                view.showAllAudioTag(null);
            }
        });
    }

    @Override
    public void getSearchAudioTag(String tag) {
        if (!MusicApp.getInstance().isRClientCon()) {
            view.showSearchResult(null);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("target", "audio_search_tag");
        bundle.putString("tag", tag);
        bundle.putString("pageid", view.getClass().getName());
        MusicApp.getInstance().getRrClient().send(RrClientWhat.REQUEST_TO_REST, bundle, new ResponseCallback() {
            @Override
            public void onResponse(String s) {
                AudioAlbumBean audioAlbumBean = (AudioAlbumBean)ReplyUtils.createResponseFromJson(s).getBaseReply();
                if (audioAlbumBean == null || audioAlbumBean.getAudio_album() == null || audioAlbumBean.getAudio_album().size() == 0) {
                    view.showSearchResult(null);
                    return;
                }
                view.showSearchTagResult(audioAlbumBean);
            }

            @Override
            public void onResponseError(String s, int i) {
                Log.e("haha", "error" + s);
            }
        });
    }

    @Override
    public void getLetingCatalog() {
        if (!MusicApp.getInstance().isRClientCon()) {
            view.showSearchResult(null);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("target", "leting_cata_log");
        bundle.putString("pageid", view.getClass().getName());
        MusicApp.getInstance().getRrClient().send(RrClientWhat.REQUEST_TO_REST, bundle, new ResponseCallback() {
            @Override
            public void onResponse(String s) {
                if (s == null || "".equals(s)) {
                    view.showLetingNewsCatalog(null);
                }
                BaseContentEntity responseFromJson = ReplyUtils.createResponseFromJson(s);
                if (responseFromJson == null) return;
                BaseReply baseReply = responseFromJson.getBaseReply();
                if (baseReply instanceof LetingCatalogBean) {
                    LetingCatalogBean letingCatalogBean = (LetingCatalogBean) baseReply;
                    view.showLetingNewsCatalog(letingCatalogBean);
                }
            }

            @Override
            public void onResponseError(String s, int i) {
                Log.e("haha", "error" + s);
            }
        });
    }

    @Override
    public void getLetingNewsRec() {
        if (!MusicApp.getInstance().isRClientCon()) {
            view.showSearchResult(null);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("target", "leting_news_rec");
        bundle.putString("pageid", view.getClass().getName());
        MusicApp.getInstance().getRrClient().send(RrClientWhat.REQUEST_TO_REST, bundle, new ResponseCallback() {
            @Override
            public void onResponse(String s) {
                if (s == null || "".equals(s)) {
                    view.showLetingNewsRec(null,null);
                    return;
                }
                BaseContentEntity responseFromJson = ReplyUtils.createResponseFromJson(s);
                if (responseFromJson == null) return;
                BaseReply baseReply = responseFromJson.getBaseReply();
                if (baseReply instanceof LetingNewsBean) {
                    LetingNewsBean letingNewsBean = (LetingNewsBean) baseReply;
                    view.showLetingNewsRec(letingNewsBean,"推荐");
                }
            }

            @Override
            public void onResponseError(String s, int i) {

            }
        });
    }

    private String mSearchTitle;

    @Override
    public void getSearchAudioKey(String title) {
        mSearchTitle = title;
        if (!MusicApp.getInstance().isRClientCon()) {
            view.showSearchResult(null);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("target", "audio_search_title");
        bundle.putString("title", title);
        bundle.putString("pageid", view.getClass().getName());
        MusicApp.getInstance().getRrClient().send(RrClientWhat.REQUEST_TO_REST, bundle, new ResponseCallback() {
            @Override
            public void onResponse(String s) {
                view.hideLoading();
                if (s == null || "".equals(s)) {
                    view.showSearchResult(null);
                    return;
                }

                BaseContentEntity baseContentEntity = JsonUtils.getBaseContentEntity(s);
                if (baseContentEntity == null) return;
                BaseReply baseReply = baseContentEntity.getBaseReply();
                if (!(baseReply instanceof AudioAlbumBean)) {
                    getSearchAudioKey(mSearchTitle);
                    view.showLoading();
                    return;
                }
                AudioAlbumBean audioAlbumBean = (AudioAlbumBean) baseReply;
                List<AudioAlbumBean.AudioAlbumEntity> audio_album = audioAlbumBean.getAudio_album();
                view.showSearchResult(audio_album);
            }

            @Override
            public void onResponseError(String s, int i) {
                view.showSearchResult(null);
            }
        });
    }

    private String mAlbumid;

    @Override
    public void getAudioProgramDetail(String albumid) {
        mAlbumid = albumid;
        if (!MusicApp.getInstance().isRClientCon()) {
            return;
        }
        final Bundle bundle = new Bundle();
        bundle.putString("target", "audio_album");
        bundle.putString("albumid", albumid);
        bundle.putString("pageid", SoundAlbumListFragment.class.getName() + "_audio_search_tag");
        MusicApp.getInstance().getRrClient().send(RrClientWhat.REQUEST_TO_REST, bundle, new ResponseCallback() {
            @Override
            public void onResponse(String s) {
                BaseContentEntity baseContentEntity = JsonUtils.getBaseContentEntity(s);
                AudioProgramBean audioProgramBean = (AudioProgramBean) baseContentEntity.getBaseReply();
                if (audioProgramBean == null) {
                    getAudioProgramDetail(mAlbumid);
                    return;
                }
                view.showAudioProgramDetail(audioProgramBean);
            }

            @Override
            public void onResponseError(String s, int i) {

            }
        });
    }

    @Override
    public void getLetingDetail(String categoryId) {
        if (!MusicApp.getInstance().isRClientCon()) {
            view.showSearchResult(null);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("target", "leting_news_cataid");
        bundle.putString("catalogid",categoryId);
        bundle.putString("pageid", view.getClass().getName());
        MusicApp.getInstance().getRrClient().send(RrClientWhat.REQUEST_TO_REST, bundle, new ResponseCallback() {
            @Override
            public void onResponse(String s) {
                if (s == null || "".equals(s)) {
                    view.showLetingNewsRec(null,null);
                    return;
                }
                BaseContentEntity responseFromJson = ReplyUtils.createResponseFromJson(s);
                if (responseFromJson == null) return;
                BaseReply baseReply = responseFromJson.getBaseReply();
                if (baseReply instanceof LetingNewsBean) {
                    LetingNewsBean letingNewsBean = (LetingNewsBean) baseReply;
                    view.showLetingNewsRec(letingNewsBean,"");
                }
            }

            @Override
            public void onResponseError(String s, int i) {

            }
        });
    }

    @Override
    public void getLetingDetailByKeyWord(final String keyWord) {
        if (!MusicApp.getInstance().isRClientCon()) {
            view.showSearchResult(null);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("target", "leting_news_keyword");
        bundle.putString("keywords",keyWord);
        bundle.putString("pageid", view.getClass().getName());
        MusicApp.getInstance().getRrClient().send(RrClientWhat.REQUEST_TO_REST, bundle, new ResponseCallback() {
            @Override
            public void onResponse(String s) {
                if (s == null || "".equals(s)) {
                    view.showLetingNewsRec(null,null);
                    return;
                }
                BaseContentEntity responseFromJson = ReplyUtils.createResponseFromJson(s);
                if (responseFromJson == null) return;
                BaseReply baseReply = responseFromJson.getBaseReply();
                if (baseReply instanceof LetingNewsBean) {
                    LetingNewsBean letingNewsBean = (LetingNewsBean) baseReply;
                    view.showLetingNewsRec(letingNewsBean,keyWord);
                }
            }

            @Override
            public void onResponseError(String s, int i) {

            }
        });
    }
}
