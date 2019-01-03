package net.imoran.auto.music.base;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import net.imoran.auto.music.app.MusicApp;
import net.imoran.auto.music.constant.RrClientWhat;
import net.imoran.auto.music.mvp.base.BasePresenter;
import net.imoran.auto.music.mvp.base.BaseView;
import net.imoran.auto.music.player.model.SongModel;
import net.imoran.auto.music.utils.StringUtils;
import net.imoran.auto.music.vui.IVUICallBack;
import net.imoran.auto.music.vui.VUIManager;
import net.imoran.rripc.lib.ResponseCallback;
import net.imoran.sdk.bean.bean.AudioAlbumBean;
import net.imoran.sdk.bean.bean.AudioProgramBean;
import net.imoran.tv.common.lib.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseFragment<P extends BasePresenter> extends SupportFragment implements BaseView, IVUICallBack {
    protected P presenter;
    protected View rootView;
    protected SupportActivity activity;

    protected abstract P createPresenter();

    protected abstract int getLayoutRes();

    protected abstract void onViewCreated();

    /**
     * @return "main","play" "list" "search" 暂时只支持这几个
     */
    protected abstract String getPageType();

    @Override
    protected ArrayList<String> getHotWords() {
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.activity = (SupportActivity) getActivity();
        rootView = inflater.inflate(getLayoutRes(), container, false);
        presenter = createPresenter();
        if (presenter != null)
            presenter.setView(this);
        setVuiCallBack();
        onViewCreated();
        return rootView;
    }

    protected void setVuiCallBack() {
        VUIManager.getInstance().setVuiCallBack(this, getPageType());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (presenter != null)
            presenter.setView(this);
    }


    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showErrorView() {

    }

    protected void onBackPressed() {
        if (activity != null)
            activity.onBackPressedSupport();
    }

    protected int getResColor(int colorId) {
        return activity.getResources().getColor(colorId);
    }

    protected Drawable getResDrawable(int drawableId) {
        return activity.getResources().getDrawable(drawableId);
    }

    /*****************************************VUI回调接口******************************************
     * @see IVUICallBack
     */
    @Override
    public void vuiPause() {

    }

    @Override
    public void vuiPlay() {

    }

    @Override
    public void vuiNext() {

    }

    @Override
    public void vuiPrevious() {

    }

    @Override
    public void vuiPlayIndex(int index) {

    }

    @Override
    public void vuiChangeRepeatMode(int mode) {

    }

    @Override
    public void vuiChangeSpeed(float speed) {

    }

    @Override
    public void vuiNextPage() {

    }

    @Override
    public void vuiPreviousPage() {

    }

    @Override
    public void vuiFastForward(long milliseconds) {

    }

    @Override
    public void vuiFastBack(long milliseconds) {

    }

    @Override
    public void vuiPlayList(int total, List<SongModel> list) {

    }

    @Override
    public void vuiSearch(String searchKey) {

    }

    @Override
    public void vuiCollectSong() {

    }

    @Override
    public void vuiCancelCollecSong() {

    }

    @Override
    public void openPlayList(String queryId) {

    }

    @Override
    public void vuiHotWords(String words) {

    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        if (presenter != null)
            presenter.setView(this);
        setVuiCallBack();
    }

    public void updatePageId() {
        if (!MusicApp.getInstance().isRClientCon()) {
            return;
        }
        String pageId = getPageId();
        if (StringUtils.isEmpty(pageId)) return;
        LogUtils.e("VUI", "updatePageId pageid = " + getPageId());
        Bundle data = new Bundle();
        data.putString("updateAction", "updatePage");
        ArrayList<String> pages = new ArrayList<>();
        pages.add(pageId);
        String pagesStr = new Gson().toJson(pages);
        data.putString("pageIdList", pagesStr);
        MusicApp.getInstance().getRrClient().send(RrClientWhat.RESPONSE_TO_NLI, data, new ResponseCallback() {
            @Override
            public void onResponse(String s) {
                Log.e("VUI", "updatePageIdSuccess" + s);
            }

            @Override
            public void onResponseError(String s, int i) {
                Log.e("VUI", "updatePageIdOnError" + s);
            }
        });
    }

    @Override
    public void vuiAlbumList(AudioAlbumBean audioAlbumBean, String queryId) {

    }

    @Override
    public void vuiProgramList(AudioProgramBean audioProgramBean, String queryId) {

    }

    @Override
    public void vuiListQueryId(String queryId) {

    }
}
