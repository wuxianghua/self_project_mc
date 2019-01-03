package net.imoran.auto.music.mvp.presenter.impl;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.imoran.auto.music.app.MusicApi;
import net.imoran.auto.music.app.MusicApp;
import net.imoran.auto.music.bean.NetTypeBean;
import net.imoran.auto.music.bean.SearchKeyWordBean;
import net.imoran.auto.music.constant.RrClientWhat;
import net.imoran.auto.music.mvp.base.BasePresenter;
import net.imoran.auto.music.mvp.presenter.INetMusicPresenter;
import net.imoran.auto.music.mvp.view.NetMusicView;
import net.imoran.auto.music.net.ReqParamBuilder;
import net.imoran.auto.music.network.api.ApiObserver;
import net.imoran.auto.music.network.api.ApiRxComposer;
import net.imoran.auto.music.network.api.ApiService;
import net.imoran.auto.music.network.bean.AcrMusicBean;
import net.imoran.auto.music.network.bean.AcrResponseRootBean;
import net.imoran.auto.music.network.bean.AcrRootBean;
import net.imoran.auto.music.network.manager.RetrofitManager;
import net.imoran.auto.music.network.utils.HttpClientUpload;
import net.imoran.auto.music.player.model.SongModel;
import net.imoran.auto.music.utils.DataConvertUtils;
import net.imoran.auto.music.utils.GsonUtils;
import net.imoran.auto.music.utils.JsonUtils;
import net.imoran.auto.music.utils.ListUtils;
import net.imoran.auto.music.utils.ReplyUtils;
import net.imoran.auto.music.utils.SharedPreferencesUtil;
import net.imoran.auto.music.utils.StringUtils;
import net.imoran.auto.music.vui.ContextSyncManager;
import net.imoran.rripc.lib.ResponseCallback;
import net.imoran.sdk.bean.base.BaseContentEntity;
import net.imoran.sdk.bean.bean.MusicCategoryBean;
import net.imoran.sdk.bean.bean.SongBean;
import net.imoran.tv.common.lib.utils.LogUtils;
import net.imoran.tv.common.lib.utils.PhoneUtils;
import net.imoran.tv.common.lib.utils.SharedPreferenceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static net.imoran.auto.music.vui.ContextSyncManager.PAGE_ID_NET_MUSIC_PLAY;

public class NetMusicPresenter extends BasePresenter<NetMusicView> implements INetMusicPresenter {
    private String errorMes = "网络出错请重试";
    private static NetMusicPresenter instance;
    private Handler handler = new Handler();
    private ArrayList<String> hotWords = new ArrayList<>();
    private Runnable timeoutRun = new Runnable() {
        @Override
        public void run() {
            view.hideLoading();
            view.getMusicError("服务器请求超时");
        }
    };

    public static NetMusicPresenter newInstance() {
        if (instance == null) {
            synchronized (NetMusicPresenter.class) {
                if (instance == null) {
                    instance = new NetMusicPresenter();
                }
            }
        }
        return instance;
    }

    private NetMusicPresenter() {
        super();
    }

    @Override
    public void loadNetMusic() {
        if (!MusicApp.getInstance().isRClientCon()) {
            view.onNetError(errorMes);
            LogUtils.e("NetMusicPresenter", "loadNetMusic RClient is Not Con");
            return;
        }
        view.showLoading();
        handler.postDelayed(timeoutRun, 10 * 1000);
        Bundle bundle = new Bundle();
        bundle.putString("target", "music_init");
        bundle.putString("pageid", PAGE_ID_NET_MUSIC_PLAY);
        MusicApp.getInstance().getRrClient().send(RrClientWhat.REQUEST_TO_REST, bundle, new ResponseCallback() {
            @Override
            public void onResponse(String s) {
                handler.removeCallbacks(timeoutRun);
                view.hideLoading();
                if (StringUtils.isNotEmpty(s)) {
                    BaseContentEntity contentEntity = ReplyUtils.createResponseFromJson(s);
                    if (contentEntity != null && contentEntity.getBaseSceneEntity() != null) {
                        String queryId = contentEntity.getBaseSceneEntity().getQueryid();
                        view.netRequestQueryId(queryId);
                    }
                    if (contentEntity != null && contentEntity.getBaseReply() != null) {
                        if (contentEntity.getBaseReply() instanceof SongBean) {
                            SongBean songBean = (SongBean) contentEntity.getBaseReply();
                            int total = contentEntity.getBaseSceneEntity().getTotal_count();
                            List<SongModel> songModels = DataConvertUtils.getSongModelList(songBean);
                            if (ListUtils.isNotEmpty(songModels))
                                view.getMusicSuccess(total, songModels);
                            else view.getMusicError(errorMes);
                        } else {
                            view.getMusicError(errorMes);
                        }
                    } else {
                        view.getMusicError(errorMes);
                    }
                } else {
                    view.getMusicError(errorMes);
                }
            }

            @Override
            public void onResponseError(String s, int i) {
                view.hideLoading();
                view.getMusicError(s);
                handler.removeCallbacks(timeoutRun);
            }
        });
    }

    @Override
    public void collectSong(final SongModel songModel, final boolean isCollect) {
        if (!MusicApp.getInstance().isRClientCon()) {
            view.onNetError(errorMes);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean("isStar", isCollect);
        bundle.putString("target", "music_collect");
        bundle.putString("id", songModel.getUuid());
        bundle.putString("pageid", PAGE_ID_NET_MUSIC_PLAY);
        MusicApp.getInstance().getRrClient().send(RrClientWhat.REQUEST_TO_REST, bundle, new ResponseCallback() {
            @Override
            public void onResponse(String s) {
                view.collectSongSuccess(songModel, isCollect);
            }

            @Override
            public void onResponseError(String s, int i) {

            }
        });
    }

    @Override
    public void getNetType() {
        final List<NetTypeBean> languageList = new ArrayList<>();
        String listData = (String) SharedPreferencesUtil.getData(MusicApp.instance, "languageListData", "");
        final Gson gson = new Gson();
        List<NetTypeBean> tempList = gson.fromJson(listData, new TypeToken<List<NetTypeBean>>() {
        }.getType());

        long lastTime = (long) SharedPreferencesUtil.getData(MusicApp.instance, "languageListTime", 0L);
        if (!ListUtils.isNotEmpty(tempList) || lastTime == 0L || (System.currentTimeMillis() - lastTime) > 86400000L) {
            if (MusicApp.getInstance().isRClientCon()) {
                Bundle bundle = new Bundle();
                bundle.putString("target", "music_category");
                bundle.putString("pageid", PAGE_ID_NET_MUSIC_PLAY);
                MusicApp.getInstance().getRrClient().send(RrClientWhat.REQUEST_TO_REST, bundle, new ResponseCallback() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtils.isNotEmpty(s)) {
                            BaseContentEntity contentEntity = ReplyUtils.createResponseFromJson(s);
                            if (contentEntity != null && contentEntity.getBaseReply() != null) {
                                if (contentEntity.getBaseReply() instanceof MusicCategoryBean) {
                                    MusicCategoryBean categoryBean = (MusicCategoryBean) contentEntity.getBaseReply();
                                    List<NetTypeBean> netTypeBeanList = DataConvertUtils.getNetTypeBeanList(categoryBean);
                                    SharedPreferencesUtil.saveData(MusicApp.instance, "languageListData", gson.toJson(netTypeBeanList));
                                    SharedPreferencesUtil.saveData(MusicApp.instance, "languageListTime", System.currentTimeMillis());
                                    for (NetTypeBean typeBean : netTypeBeanList) {
                                        languageList.add(typeBean);
                                    }

                                    view.loadNetTypeSuccess(languageList, null, null);
                                }
                            }
                        }
                    }

                    @Override
                    public void onResponseError(String s, int i) {
                        languageList.add(new NetTypeBean("华语", "http://image.xiaomor.com/group1/M00/00/B5/ChlZDlrZjrOAUgBBAABhKHog2Ts268.jpg"));
                        languageList.add(new NetTypeBean("网络", "http://image.xiaomor.com/group1/M00/00/B5/ChlZDlrZjnWAboTsAAB94GHkaB4137.jpg"));
                        languageList.add(new NetTypeBean("中国风", "http://image.xiaomor.com/group1/M00/00/B5/ChlZDlrZjnWAboTsAAB94GHkaB4137.jpg"));
                        languageList.add(new NetTypeBean("治愈", "http://image.xiaomor.com/group1/M00/00/B5/ChlZDlrZjnWAboTsAAB94GHkaB4137.jpg"));
                        languageList.add(new NetTypeBean("小清新", "http://image.xiaomor.com/group1/M00/00/B5/ChlZDlrZjtCAfZ-lAABuhQ3Wl9A557.jpg"));
                        languageList.add(new NetTypeBean("恋爱", "http://image.xiaomor.com/group1/M00/00/B5/ChlZDlrZjpqAUEFFAAB9wBL5C7Q081.jpg"));
                        view.loadNetTypeSuccess(languageList, null, null);
                    }
                });
            }
        } else {
            languageList.addAll(tempList);
        }

        List<NetTypeBean> styleList = new ArrayList<>();
        styleList.add(new NetTypeBean("华语", ""));
        styleList.add(new NetTypeBean("粤语", ""));
        styleList.add(new NetTypeBean("闽南语", ""));
        styleList.add(new NetTypeBean("英语", ""));
        styleList.add(new NetTypeBean("法语", ""));
        styleList.add(new NetTypeBean("日韩", ""));

        List<NetTypeBean> emotionList = new ArrayList<>();
        emotionList.add(new NetTypeBean("乡村", ""));
        emotionList.add(new NetTypeBean("民谣", ""));
        emotionList.add(new NetTypeBean("古典", ""));
        emotionList.add(new NetTypeBean("蓝调", ""));
        emotionList.add(new NetTypeBean("金属", ""));
        emotionList.add(new NetTypeBean("舞曲", ""));
        emotionList.add(new NetTypeBean("网络", ""));
        emotionList.add(new NetTypeBean("中国风", ""));
        emotionList.add(new NetTypeBean("儿童", ""));
        emotionList.add(new NetTypeBean("治愈", ""));
        emotionList.add(new NetTypeBean("小清新", ""));
        emotionList.add(new NetTypeBean("失恋", ""));
        emotionList.add(new NetTypeBean("恋爱", ""));
        emotionList.add(new NetTypeBean("倾听", ""));
        emotionList.add(new NetTypeBean("安静", ""));
        emotionList.add(new NetTypeBean("失落", ""));
        emotionList.add(new NetTypeBean("怀旧", ""));
        emotionList.add(new NetTypeBean("放松", ""));


        for (NetTypeBean bean : languageList) {
            hotWords.add(bean.getName());
        }
        for (NetTypeBean bean : styleList) {
            hotWords.add(bean.getName());
        }
        for (NetTypeBean bean : emotionList) {
            hotWords.add(bean.getName());
        }

        view.loadNetTypeSuccess(languageList, styleList, emotionList);
    }

    public ArrayList<String> getHotWords() {
        return hotWords;
    }

    @Override
    public void loadNetMusicByType(String type) {
        if (!MusicApp.getInstance().isRClientCon()) {
            view.onNetError(errorMes);
            return;
        }
        view.showLoading();
        Bundle bundle = new Bundle();
        bundle.putString("target", "music_type");
        bundle.putString("musicTypeUrl", MusicApi.music_type + ReqParamBuilder.getInstance().
                addCommonParamsToString().addSingleParamToString("tag", type).getRequestString());
        bundle.putString("pageid", PAGE_ID_NET_MUSIC_PLAY);
        MusicApp.getInstance().getRrClient().send(RrClientWhat.REQUEST_TO_REST, bundle, new ResponseCallback() {
            @Override
            public void onResponse(String s) {
                view.hideLoading();
                if (StringUtils.isNotEmpty(s)) {
                    BaseContentEntity contentEntity = ReplyUtils.createResponseFromJson(s);
                    if (contentEntity != null && contentEntity.getBaseSceneEntity() != null) {
                        String queryId = contentEntity.getBaseSceneEntity().getQueryid();
                        view.netRequestQueryId(queryId);
                        LogUtils.e("listRequestId", " queryId = " + queryId);
                    }
                    if (contentEntity != null && contentEntity.getBaseReply() != null) {
                        if (contentEntity.getBaseReply() instanceof SongBean) {
                            int total = contentEntity.getBaseSceneEntity().getTotal_count();
                            SongBean songBean = (SongBean) contentEntity.getBaseReply();
                            view.loadMusicByTypeSuccess(total, DataConvertUtils.getSongModelList(songBean));
                        } else {
                            view.getMusicError(errorMes);
                        }
                    } else {
                        view.getMusicError(errorMes);
                    }
                } else {
                    view.getMusicError(errorMes);
                }
            }

            @Override
            public void onResponseError(String s, int i) {
                view.hideLoading();
                view.getMusicError(s);
            }
        });
    }

    @Override
    public void loadCollectedMusic() {
        if (!MusicApp.getInstance().isRClientCon()) {
            view.onNetError(errorMes);
            return;
        }
        view.showLoading();
        Bundle bundle = new Bundle();
        bundle.putString("target", "music_type");
        bundle.putString("musicTypeUrl", MusicApi.music_star + ReqParamBuilder.getInstance().
                addCommonParamsToString().addSingleParamToString("keyword", "all").getRequestString());
        bundle.putString("pageid", PAGE_ID_NET_MUSIC_PLAY);
        MusicApp.getInstance().getRrClient().send(RrClientWhat.REQUEST_TO_REST, bundle, new ResponseCallback() {
            @Override
            public void onResponse(String s) {
                view.hideLoading();
                if (StringUtils.isNotEmpty(s)) {
                    BaseContentEntity contentEntity = ReplyUtils.createResponseFromJson(s);
                    if (contentEntity != null && contentEntity.getBaseSceneEntity() != null) {
                        String queryId = contentEntity.getBaseSceneEntity().getQueryid();
                        view.netRequestQueryId(queryId);
                        LogUtils.e("listRequestId", " queryId = " + queryId);
                    }
                    if (contentEntity != null && contentEntity.getBaseReply() != null) {
                        if (contentEntity.getBaseReply() instanceof SongBean) {
                            int total = contentEntity.getBaseSceneEntity().getTotal_count();
                            SongBean songBean = (SongBean) contentEntity.getBaseReply();
                            view.loadCollectedMusicSuccess(total, DataConvertUtils.getSongModelList(songBean));
                        } else {
                            view.getMusicError(errorMes);
                        }
                    } else {
                        view.getMusicError(errorMes);
                    }
                } else {
                    view.getMusicError(errorMes);
                }
            }

            @Override
            public void onResponseError(String s, int i) {
                view.hideLoading();
                view.getMusicError(s);
            }
        });
    }

    //keyWord null的时候清除历史记录
    @Override
    public void saveHistoryKeyword(Context context, String keyWord) {
        if (keyWord == null) {
            if (list != null)
                list.clear();
            SharedPreferenceUtils.put(context, "history", "[]");
            loadHistoryKeyword(context);
        } else {
            if (list == null) {
                list = new ArrayList<>();
            }
            if (list.size() >= 20) {
                list.remove(list.size() - 1);
            }
            SearchKeyWordBean removeBean = null;
            for (SearchKeyWordBean bean : list) {
                if (keyWord.equals(bean.getSearchName())) {
                    removeBean = bean;
                    break;
                }
            }
            if (removeBean != null) {
                list.remove(removeBean);
            }
            list.add(0, new SearchKeyWordBean("history", keyWord));
            SharedPreferenceUtils.put(context, "history", JsonUtils.convertBeanToString(list));
            loadHistoryKeyword(context);
        }
    }

    private List<SearchKeyWordBean> list;

    @Override
    public void loadHistoryKeyword(Context context) {
        if (list == null) {
            String temp = (String) SharedPreferenceUtils.get(context, "history", "");
            list = JsonUtils.stringToArray(temp, SearchKeyWordBean[].class);
        }
        if (list == null) {
            list = new ArrayList<>();
        }
        view.loadHistoryKeywordSuccess(list);
    }

    @Override
    public void loadHotSearchKeyword() {
        List<SearchKeyWordBean> list = new ArrayList<>();
        list.add(new SearchKeyWordBean("hot", "抖音"));
        list.add(new SearchKeyWordBean("hot", "刘德华"));
        list.add(new SearchKeyWordBean("hot", "张学友"));
        list.add(new SearchKeyWordBean("hot", "薛之谦"));
        list.add(new SearchKeyWordBean("hot", "王力宏"));
        list.add(new SearchKeyWordBean("hot", "可不可以"));
        list.add(new SearchKeyWordBean("hot", "唯一"));
        list.add(new SearchKeyWordBean("hot", "作曲家"));
        view.loadHotSearchKeywordSuccess(list);
    }

    @Override
    public void searchMusicByKeyWord(String keyWord) {
        if (!MusicApp.getInstance().isRClientCon()) {
            view.onNetError(errorMes);
            return;
        }
        view.showLoading();
        Bundle bundle = new Bundle();
        bundle.putString("target", "music_search");
        bundle.putString("keyword", keyWord);
        bundle.putString("pageid", PAGE_ID_NET_MUSIC_PLAY);
        MusicApp.getInstance().getRrClient().send(RrClientWhat.REQUEST_TO_REST, bundle, new ResponseCallback() {
            @Override
            public void onResponse(String s) {
                view.hideLoading();
                if (StringUtils.isNotEmpty(s)) {
                    BaseContentEntity contentEntity = ReplyUtils.createResponseFromJson(s);
                    if (contentEntity != null && contentEntity.getBaseSceneEntity() != null) {
                        String queryId = contentEntity.getBaseSceneEntity().getQueryid();
                        view.netRequestQueryId(queryId);
                        LogUtils.e("listRequestId", " queryId = " + queryId);
                    }
                    if (contentEntity != null && contentEntity.getBaseReply() != null) {
                        if (contentEntity.getBaseReply() instanceof SongBean) {
                            int total = contentEntity.getBaseSceneEntity().getTotal_count();
                            SongBean songBean = (SongBean) contentEntity.getBaseReply();
                            view.searchMusicByKeyWordSuccess(total, DataConvertUtils.getSongModelList(songBean));
                        } else {
                            view.getMusicError(errorMes);
                        }
                    } else {
                        view.getMusicError(errorMes);
                    }
                } else {
                    view.getMusicError(errorMes);
                }
            }

            @Override
            public void onResponseError(String s, int i) {
                view.hideLoading();
                view.onNetError(s);

            }
        });
    }

    //后台page是从0 开始的 页面从 1开始
    public void loadMusicByPageNum(final int page, String pageId) {
        loadMusicByPageNum(page, pageId, true);
    }

    //后台page是从0 开始的 页面从 1开始
    public void loadMusicByPageNum(final int page, String pageId, final boolean isHeadPlay) {
        if (!MusicApp.getInstance().isRClientCon()) {
            view.onNetError(errorMes);
            return;
        }
        view.showLoading();
        Bundle bundle = new Bundle();
        bundle.putString("target", "paging");
        bundle.putString("domain", "music");
        bundle.putString("type", "song");
        bundle.putInt("page", page - 1);
        bundle.putInt("size", 10);
        bundle.putString("pageid", PAGE_ID_NET_MUSIC_PLAY);
        MusicApp.getInstance().getRrClient().send(RrClientWhat.REQUEST_TO_REST, bundle, new ResponseCallback() {
            @Override
            public void onResponse(String s) {
                view.hideLoading();
                if (StringUtils.isNotEmpty(s)) {
                    BaseContentEntity contentEntity = ReplyUtils.createResponseFromJson(s);
                    if (contentEntity != null && contentEntity.getBaseSceneEntity() != null) {
                        String queryId = contentEntity.getBaseSceneEntity().getQueryid();
                        view.netRequestQueryId(queryId);

                        LogUtils.e("listRequestId", " queryId = " + queryId);
                    }
                    if (contentEntity != null && contentEntity.getBaseReply() != null) {
                        if (contentEntity.getBaseReply() instanceof SongBean) {
                            SongBean songBean = (SongBean) contentEntity.getBaseReply();
                            view.loadMusicByPageNumSuccess(page, DataConvertUtils.getSongModelList(songBean), isHeadPlay);
                        } else {
                            view.loadMusicByPageNumError(errorMes);
                        }
                    } else {
                        view.loadMusicByPageNumError(errorMes);
                    }
                } else {
                    view.loadMusicByPageNumError(errorMes);
                }
            }

            @Override
            public void onResponseError(String s, int i) {
                view.hideLoading();
                view.loadMusicByPageNumError(s);
            }
        });
    }


    protected ApiService apiService;

    @Override
    public void acrByPcmFile(final String filePath) {
        if (StringUtils.isEmpty(filePath)) return;
        if (apiService == null)
            apiService = RetrofitManager.getInstance().getRetrofit().create(ApiService.class);
        final File file = new File(filePath);
        if (!file.exists()) return;
        final String url = MusicApp.ACR_BASE_URL + "/api/voice/acr/pcm?"
                + "key=" + MusicApp.MOR_KEY
                + "&deviceid=" + PhoneUtils.generateDeviceId(MusicApp.instance)
                + "&pcmAudioBufferLen=" + file.length();
        new HttpClientUpload().uploadPcmFile(url, filePath, new HttpClientUpload.OnUploadFileListener() {
            @Override
            public void onSuccess(String s) {
                if (StringUtils.isNotEmpty(s)) {
                    AcrResponseRootBean responseRootBean = GsonUtils.convertObj(s, AcrResponseRootBean.class);
                    if (responseRootBean.getData() != null && responseRootBean.getData().getMetadata() != null) {
                        List<AcrMusicBean> acrMusicBeans = responseRootBean.getData().getMetadata().getMusic();
                        view.onAcrResult(acrMusicBeans);
                    } else {
                        view.onAcrResult(null);
                    }
                } else {
                    view.onAcrResult(null);
                }
                file.delete();
            }

            @Override
            public void onError() {
                view.onAcrResult(null);
                file.delete();
            }
        });
    }
}
