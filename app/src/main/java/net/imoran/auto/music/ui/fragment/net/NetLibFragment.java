package net.imoran.auto.music.ui.fragment.net;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import net.imoran.auto.music.R;
import net.imoran.auto.music.bean.NetTypeBean;
import net.imoran.auto.music.ui.adapter.BaseRecycleAdapter;
import net.imoran.auto.music.ui.adapter.NetTypeAdapter;

import net.imoran.auto.music.utils.ClickUtils;
import net.imoran.auto.music.utils.SharedPreferencesUtil;
import net.imoran.auto.music.utils.StringUtils;
import net.imoran.auto.music.utils.UseLoginUtils;
import net.imoran.auto.music.vui.ContextSyncManager;
import net.imoran.tv.common.lib.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class NetLibFragment extends NetBaseFragment implements View.OnClickListener, NetTypeAdapter.OnItemClickListener {
    private ImageView ivBack;
    private ImageView ivCollectionList;
    private ImageView ivPrivateFm;
    private RecyclerView rvLanguageList;
    private RecyclerView rvStyleView;
    private RecyclerView rvEmotionList;
    private NetTypeAdapter languageTypeAdapter;
    private NetTypeAdapter styleTypeAdapter;
    private NetTypeAdapter emotionTypeAdapter;

    public static NetLibFragment newInstance() {
        Bundle args = new Bundle();
        NetLibFragment fragment = new NetLibFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected boolean isNliPage() {
        return true;
    }

    @Override
    protected ArrayList<String> getHotWords() {
        return presenter.getHotWords();
    }

    @Override
    protected String getPageType() {
        return "play";
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_net_type;
    }

    @Override
    protected void onViewCreated() {
        initView();
        initAdapter();
    }

    private void initView() {
        ivBack = (ImageView) rootView.findViewById(R.id.ivBack);
        ivCollectionList = (ImageView) rootView.findViewById(R.id.ivCollectionList);
        ivPrivateFm = (ImageView) rootView.findViewById(R.id.ivPrivateFm);
        rvLanguageList = (RecyclerView) rootView.findViewById(R.id.rvLanguageList);
        rvStyleView = (RecyclerView) rootView.findViewById(R.id.rvStyleView);
        rvEmotionList = (RecyclerView) rootView.findViewById(R.id.rvEmotionList);

        ivBack.setOnClickListener(this);
        ivCollectionList.setOnClickListener(this);
        ivPrivateFm.setOnClickListener(this);
    }

    private void initAdapter() {
        GridLayoutManager languageListGridLayoutManager = new GridLayoutManager(activity, 6);
        rvLanguageList.setLayoutManager(languageListGridLayoutManager);
        languageTypeAdapter = new NetTypeAdapter(activity, 0);
        rvLanguageList.setAdapter(languageTypeAdapter);
        languageTypeAdapter.setOnItemClickListener(this);

        GridLayoutManager typeListGridLayoutManager = new GridLayoutManager(activity, 5);
        rvStyleView.setLayoutManager(typeListGridLayoutManager);
        styleTypeAdapter = new NetTypeAdapter(activity, 1);
        rvStyleView.setAdapter(styleTypeAdapter);
        styleTypeAdapter.setOnItemClickListener(this);

        GridLayoutManager emotionListGridLayoutManager = new GridLayoutManager(activity, 5);
        rvEmotionList.setLayoutManager(emotionListGridLayoutManager);
        emotionTypeAdapter = new NetTypeAdapter(activity, 1);
        rvEmotionList.setAdapter(emotionTypeAdapter);
        emotionTypeAdapter.setOnItemClickListener(this);

        presenter.getNetType();
    }

    @Override
    public void loadNetTypeSuccess(List<NetTypeBean> languageList, List<NetTypeBean> styleList, List<NetTypeBean> EmotionList) {
        if (languageList != null)
            languageTypeAdapter.addDataList(languageList);
        if (styleList != null)
            styleTypeAdapter.addDataList(styleList);
        if (EmotionList != null)
            emotionTypeAdapter.addDataList(EmotionList);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                pop();
                break;
            case R.id.ivCollectionList:
                if ("".equals(UseLoginUtils.getInstance().getCurrentUid())) {
                    Toast.makeText(activity, "您还没有登录，请到个人中心登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                start(NetPlayListFragment.newInstance(NetPlayListFragment.TYPE_COLLECT, new NetTypeBean()));
                break;
            case R.id.ivPrivateFm:
                ToastUtil.shortShow(activity, "暂无此功能");
                break;
        }
    }


    @Override
    public void onItemClick(int position, NetTypeBean typeBean) {
        if (ClickUtils.isFastClick()) return;
        start(NetPlayListFragment.newInstance(NetPlayListFragment.TYPE_LIB, typeBean));
    }

    @Override
    public void vuiHotWords(String words) {
        if (isSupportVisible())
            if (StringUtils.isNotEmpty(words)) {
                start(NetPlayListFragment.newInstance(NetPlayListFragment.TYPE_LIB, new NetTypeBean(words, "")));
            }
    }
}
