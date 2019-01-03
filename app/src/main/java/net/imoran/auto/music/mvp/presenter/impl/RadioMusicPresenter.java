package net.imoran.auto.music.mvp.presenter.impl;


import android.os.Bundle;
import android.util.Log;

import com.google.gson.JsonObject;

import net.imoran.auto.music.app.MusicApi;
import net.imoran.auto.music.bean.MapLocation;
import net.imoran.auto.music.constant.RrClientWhat;
import net.imoran.auto.music.net.RequestParamUtils;
import net.imoran.auto.music.network.api.ApiObserver;
import net.imoran.auto.music.network.api.ApiRxComposer;
import net.imoran.auto.music.network.api.ApiService;
import net.imoran.auto.music.network.bean.AcrMusicBean;
import net.imoran.auto.music.network.bean.AcrRootBean;
import net.imoran.auto.music.network.bean.RadioAcrChildBean;
import net.imoran.auto.music.network.bean.RadioAcrChildItemBean;
import net.imoran.auto.music.network.bean.RadioAcrParentBean;
import net.imoran.auto.music.network.bean.RadioAcrRootBean;
import net.imoran.auto.music.network.manager.RetrofitManager;
import net.imoran.auto.music.network.utils.ParamsBuilder;
import net.imoran.auto.music.utils.AppUtils;
import net.imoran.auto.music.utils.GsonUtils;
import net.imoran.auto.music.utils.JsonUtils;
import net.imoran.auto.music.utils.ListUtils;
import net.imoran.auto.music.utils.ReplyUtils;
import net.imoran.auto.music.utils.StringUtils;

import net.imoran.auto.music.app.MusicApp;
import net.imoran.auto.music.mvp.base.BasePresenter;
import net.imoran.auto.music.mvp.presenter.IRadioMusicPresenter;
import net.imoran.auto.music.mvp.view.RadioMusicView;
import net.imoran.auto.music.radio.model.RadioModel;
import net.imoran.auto.music.utils.DataConvertUtils;
import net.imoran.personal.lib.SPHelper;
import net.imoran.rripc.lib.ResponseCallback;
import net.imoran.sdk.bean.base.BaseContentEntity;
import net.imoran.sdk.bean.bean.BroadcastBean;
import net.imoran.tv.common.lib.utils.PhoneUtils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class RadioMusicPresenter extends BasePresenter<RadioMusicView> implements IRadioMusicPresenter {
    private String errMsg = "网络异常请重试";
    private static RadioMusicPresenter instance;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private List<RadioAcrChildItemBean> childItemBeans = new ArrayList<>();//当前的电台节目单
    private String currentFreq = "";

    public static RadioMusicPresenter newInstance() {
        if (instance == null) {
            synchronized (RadioMusicPresenter.class) {
                if (instance == null) {
                    instance = new RadioMusicPresenter();
                }
            }
        }
        return instance;
    }


    private RadioMusicPresenter() {
        super();
    }

    @Override
    public void collectedRadio(final boolean isAdd, String type, String frequency, String name) {
        if (!MusicApp.getInstance().isRClientCon()) {
            view.collectedRadioFail(errMsg);
            return;
        }
        if (isAdd) {
            String requestUrl = MusicApi.radio_collect + RequestParamUtils.getInstance().addCommonParamsToString().addSingleParamToString("broadcast", getCollectedRadioUrl(type, frequency, name)).getRequestString();
            Bundle bundle = new Bundle();
            bundle.putString("target", "music_collect_radio");
            bundle.putString("collectedRadioUrl", requestUrl);
            bundle.putString("pageid", getClass().getName() + "_music_collect_radio");
            MusicApp.getInstance().getRrClient().send(RrClientWhat.REQUEST_TO_REST, bundle, new ResponseCallback() {
                @Override
                public void onResponse(String s) {
                    view.collectedRadioSuccess(isAdd);
                }

                @Override
                public void onResponseError(String s, int i) {
                    view.collectedRadioFail(errMsg);
                }
            });
        } else {
            String requestUrl = MusicApi.radio_delcollect + RequestParamUtils.getInstance().addCommonParamsToString().addSingleParamToString("broadcast", getDisCollectedRadioUrl(type, frequency, name)).getRequestString();
            Bundle bundle = new Bundle();
            bundle.putString("target", "music_disCollect_radio");
            bundle.putString("disCollectedRadioUrl", requestUrl);
            bundle.putString("pageid", getClass().getName() + "_music_disCollect_radio");
            MusicApp.getInstance().getRrClient().send(RrClientWhat.REQUEST_TO_REST, bundle, new ResponseCallback() {
                @Override
                public void onResponse(String s) {
                    view.collectedRadioSuccess(isAdd);
                }

                @Override
                public void onResponseError(String s, int i) {

                }
            });
        }

    }

    @Override
    public void getCollectedRadioList() {
        if (!MusicApp.getInstance().isRClientCon()) {
            view.getCollectedRadioListFail(errMsg);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("target", "music_collect_radio_list");
        bundle.putString("pageid", getClass().getName() + "_music_collect_radio_list");
        MusicApp.getInstance().getRrClient().send(RrClientWhat.REQUEST_TO_REST, bundle, new ResponseCallback() {
            @Override
            public void onResponse(String response) {
                if (!StringUtils.isEmpty(response)) {
                    BaseContentEntity baseContentEntity = ReplyUtils.createResponseFromJson(response);
                    if (baseContentEntity != null && baseContentEntity.getBaseReply() instanceof BroadcastBean) {
                        BroadcastBean broadcastBean = (BroadcastBean) baseContentEntity.getBaseReply();
                        List<RadioModel> list = DataConvertUtils.getRadioModelList(broadcastBean);
                        view.getCollectedRadioListSuccess(list);
                    } else {
                        view.getCollectedRadioListFail(errMsg);
                    }
                } else {
                    view.getCollectedRadioListFail(errMsg);
                }
            }

            @Override
            public void onResponseError(String s, int i) {
                view.getCollectedRadioListFail(errMsg);
            }
        });
    }


    private String getCollectedRadioUrl(String type, String frequency, String name) {
        JsonObject jsonObject = new JsonObject();
        try {
            jsonObject.addProperty("type", type);
            jsonObject.addProperty("frequency", frequency);
            jsonObject.addProperty("title", name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JsonUtils.objectToJson(jsonObject);
    }

    private String getDisCollectedRadioUrl(String type, String frequency, String name) {
        JsonObject jsonObject = new JsonObject();
        try {
            jsonObject.addProperty("type", type);
            jsonObject.addProperty("frequency", frequency);
            jsonObject.addProperty("title", name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JsonUtils.objectToJson(jsonObject);
    }

    private ApiService apiService;

    @Override
    public void acrRadioSearch(final String frequency, String type) {
        if (StringUtils.isEmpty(frequency) || StringUtils.isEmpty(type)) return;
        if (apiService == null)
            apiService = RetrofitManager.getInstance().getRetrofit().create(ApiService.class);
        HashMap<String, String> param = ParamsBuilder.start().build();
        String userLocation = SPHelper.getString(MusicApp.instance, "userLocation", "");
        if (!StringUtils.isEmpty(userLocation)) {
            MapLocation location = GsonUtils.convertObj(userLocation, MapLocation.class);
            if (location != null) {
                param.put("gps", location.getLatitude() + "," + location.getLongitude());
            } else {
                return;
            }
        }
        param.put("key", MusicApp.MOR_KEY);
        param.put("deviceid", PhoneUtils.generateDeviceId(MusicApp.instance));
        param.put("freq", frequency);
        param.put("type", type);
        apiService.acrRadioSearch(param).compose(ApiRxComposer.<RadioAcrRootBean>io_mian())
                .subscribe(new ApiObserver<RadioAcrRootBean>() {
                    @Override
                    public void onSuccess(RadioAcrRootBean rootBean) {
                        if (rootBean != null && rootBean.getData() != null) {
                            RadioAcrParentBean parentBean = rootBean.getData();
                            if (parentBean != null && ListUtils.isNotEmpty(parentBean.getRadio_list())) {
                                RadioAcrChildBean childBean = parentBean.getRadio_list().get(0);
                                if (childBean != null && ListUtils.isNotEmpty(childBean.getPlaylist())) {
                                    currentFreq = childBean.getFreq();
                                    childItemBeans.clear();
                                    childItemBeans.addAll(childBean.getPlaylist());

                                    long current = System.currentTimeMillis();
                                    RadioAcrChildItemBean currentBean = null;
                                    for (RadioAcrChildItemBean childItemBean : childItemBeans) {
                                        childItemBean.setStart_time_l(getTimeL(childItemBean.getStart_time_utc8()));
                                        childItemBean.setEnd_time_l(getTimeL(childItemBean.getEnd_time_utc8()));
                                        childItemBean.setFmName(childBean.getName());

                                        if (current >= childItemBean.getStart_time_l() && current <= childItemBean.getEnd_time_l()) {
                                            currentBean = childItemBean;
                                        }
                                    }
                                    view.onRadioAcrResult(currentBean);
                                }
                            } else {
                                view.onRadioAcrResult(null);
                            }
                        } else {
                            view.onRadioAcrResult(null);
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        view.onRadioAcrResult(null);
                    }

                });
    }

    private long getTimeL(String strDate) {
        try {
            Date date = simpleDateFormat.parse(strDate);
            return date.getTime();
        } catch (ParseException px) {
            px.printStackTrace();
        }
        return 0L;
    }

    /**
     * 根据当前的时间得到当前电台的节目单
     *
     * @return
     */
    public RadioAcrChildItemBean getCurrentItemBean(String freq) {
        if (StringUtils.isEmpty(freq)) return null;
        if (currentFreq.equalsIgnoreCase(freq)) {
            if (ListUtils.isNotEmpty(childItemBeans)) {
                long current = System.currentTimeMillis();
                for (RadioAcrChildItemBean bean : childItemBeans) {
                    if (current >= bean.getStart_time_l() && current <= bean.getEnd_time_l()) {
                        return bean;
                    }
                }
            }
        }
        return null;
    }
}
