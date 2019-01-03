package net.imoran.auto.music.ui.fragment.sound;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagFlowLayout;

import net.imoran.auto.music.R;
import net.imoran.auto.music.ui.adapter.FlowLayoutAdapter;
import net.imoran.auto.music.ui.adapter.SoundLetingAdapter;
import net.imoran.auto.music.ui.adapter.SoundListAdapter;
import net.imoran.auto.music.utils.UseLoginUtils;
import net.imoran.sdk.bean.bean.AudioAlbumBean;
import net.imoran.sdk.bean.bean.LetingCatalogBean;
import net.imoran.sdk.bean.bean.LetingNewsBean;
import net.imoran.sdk.bean.bean.PodcastCategoryBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinhua.shi on 2018/6/19.
 */

public class SoundLibFragment extends SoundBaseFragment {
    private static final String TAG = "SoundLibFragment";
    private ImageView ivBack;
    private GridView hotGridView;
    private TagFlowLayout entertainGridView;
    private TagFlowLayout talkShowGridView;
    private TagFlowLayout commicGridView;
    private TagFlowLayout topGridView;
    private TagFlowLayout letingGridView;
    private ImageView saveList;
    private ImageView subscribeJiemu;
    private ScrollView mLibScrollView;
    private FrameLayout mFlLoading;
    private TextView mTvEmpty;

    @Override
    protected void onViewCreated() {
        initView();
        initData();
    }

    public static SoundLibFragment newInstance() {
        Bundle args = new Bundle();
        SoundLibFragment fragment = new SoundLibFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected String getPageType() {
        return "play";
    }

    private void initView() {
        ivBack = (ImageView) rootView.findViewById(R.id.back);
        hotGridView = (GridView) rootView.findViewById(R.id.gd_hot);
        entertainGridView = (TagFlowLayout) rootView.findViewById(R.id.gd_entertain);
        talkShowGridView = (TagFlowLayout) rootView.findViewById(R.id.gd_knowledge);
        commicGridView = (TagFlowLayout) rootView.findViewById(R.id.gd_comic);
        topGridView = (TagFlowLayout) rootView.findViewById(R.id.gd_top);
        letingGridView = (TagFlowLayout) rootView.findViewById(R.id.gd_leting);
        saveList = (ImageView) rootView.findViewById(R.id.save_list);
        subscribeJiemu = (ImageView) rootView.findViewById(R.id.subscirbe_jiemu);
        mLibScrollView = (ScrollView) rootView.findViewById(R.id.sv_lib_sound);
        mFlLoading = (FrameLayout) rootView.findViewById(R.id.fl_loading);
        mTvEmpty = (TextView) rootView.findViewById(R.id.tvEmpty);
        hotGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    Bundle data = new Bundle();
                    data.putString("keyword","推荐");
                    start(SoundLetingDetailFragment.newInstance(data));
                }else {
                    presenter.getSearchAudioTag(eudioListAdapter.getItem(i).getName());
                }
            }
        });
        entertainGridView.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                presenter.getSearchAudioTag((String) fudioListAdapter.getItem(position));
                return false;
            }
        });
        talkShowGridView.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                presenter.getSearchAudioTag((String) audioListOtherAdapter.getItem(position));
                return false;
            }
        });
        commicGridView.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                presenter.getSearchAudioTag((String) cudioListOtherAdapter.getItem(position));
                return false;
            }
        });
        topGridView.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                presenter.getSearchAudioTag((String) tudioListOtherAdapter.getItem(position));
                return false;
            }
        });
        letingGridView.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                Bundle data = new Bundle();
                data.putString("keyword",(String)letingListAdapter.getItem(position));
                start(SoundLetingDetailFragment.newInstance(data));
                return false;
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop();
            }
        });
        saveList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("".equals(UseLoginUtils.getInstance().getCurrentUid())) {
                    Toast.makeText(activity, "您还没有登录，请到个人中心登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                start(SoundCollectionFragment.newInstance());
            }
        });
        subscribeJiemu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity, "此功能暂不支持，敬请期待", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initData() {
        presenter.getAllAudioTag();
        showLetingNewsCatalog();
    }


    @Override
    public int getLayoutRes() {
        return R.layout.fragment_music_list;
    }

    private SoundListAdapter eudioListAdapter;
    private FlowLayoutAdapter fudioListAdapter;
    private FlowLayoutAdapter audioListOtherAdapter;
    private FlowLayoutAdapter cudioListOtherAdapter;
    private FlowLayoutAdapter tudioListOtherAdapter;
    private FlowLayoutAdapter letingListAdapter;
    private List<String> flowTagList;

    @Override
    public void showAllAudioTag(List<PodcastCategoryBean.PodcastCategoryEntity> podcast_category) {
        if (podcast_category == null || podcast_category.size() == 0) {
            showErrorView();
            return;
        }
        hideLoading();
        for (PodcastCategoryBean.PodcastCategoryEntity podcastCategoryEntity : podcast_category) {
            switch (podcastCategoryEntity.getName()) {
                case "精品":
                    ArrayList<PodcastCategoryBean.PodcastCategoryEntity.SubCategoryArrayListEntity> list = new ArrayList<>();
                    list.addAll(podcastCategoryEntity.getSub_category_list());
                    PodcastCategoryBean.PodcastCategoryEntity.SubCategoryArrayListEntity mSubCategoryArrayListEntity = new PodcastCategoryBean.PodcastCategoryEntity.SubCategoryArrayListEntity();
                    mSubCategoryArrayListEntity.setName("乐听头条");
                    mSubCategoryArrayListEntity.setPic("");
                    list.add(0,mSubCategoryArrayListEntity);
                    eudioListAdapter = new SoundListAdapter(activity, list);
                    hotGridView.setAdapter(eudioListAdapter);
                    break;
                case "小说":
                    flowTagList = new ArrayList<>();
                    for (PodcastCategoryBean.PodcastCategoryEntity.SubCategoryArrayListEntity subCategoryArrayListEntity : podcastCategoryEntity.getSub_category_list()) {
                        flowTagList.add(subCategoryArrayListEntity.getName());
                    }
                    fudioListAdapter = new FlowLayoutAdapter(flowTagList, activity);
                    entertainGridView.setAdapter(fudioListAdapter);
                    break;
                case "脱口秀":
                    flowTagList = new ArrayList<>();
                    for (PodcastCategoryBean.PodcastCategoryEntity.SubCategoryArrayListEntity subCategoryArrayListEntity : podcastCategoryEntity.getSub_category_list()) {
                        flowTagList.add(subCategoryArrayListEntity.getName());
                    }
                    audioListOtherAdapter = new FlowLayoutAdapter(flowTagList, activity);
                    talkShowGridView.setAdapter(audioListOtherAdapter);
                    break;
                case "相声小品":
                    flowTagList = new ArrayList<>();
                    for (PodcastCategoryBean.PodcastCategoryEntity.SubCategoryArrayListEntity subCategoryArrayListEntity : podcastCategoryEntity.getSub_category_list()) {
                        flowTagList.add(subCategoryArrayListEntity.getName());
                    }
                    cudioListOtherAdapter = new FlowLayoutAdapter(flowTagList, activity);
                    commicGridView.setAdapter(cudioListOtherAdapter);
                    break;
                case "头条":
                    flowTagList = new ArrayList<>();
                    for (PodcastCategoryBean.PodcastCategoryEntity.SubCategoryArrayListEntity subCategoryArrayListEntity : podcastCategoryEntity.getSub_category_list()) {
                        flowTagList.add(subCategoryArrayListEntity.getName());
                    }
                    tudioListOtherAdapter = new FlowLayoutAdapter(flowTagList, activity);
                    topGridView.setAdapter(tudioListOtherAdapter);
                    break;
            }
        }
    }

    private String[] mLetingData = {"国内","国际","军事","社会","体育","娱乐","财经","科技","汽车","生活","旅游","人文","教育",};
    @Override
    public void showLetingNewsCatalog(LetingCatalogBean catalogBean) {
        super.showLetingNewsCatalog(catalogBean);
        flowTagList = new ArrayList<>();
        ArrayList<LetingCatalogBean.LetingCatalogEntity> leting_catalog = catalogBean.getLeting_catalog();
        if (leting_catalog == null || leting_catalog.isEmpty()) return;
        for (LetingCatalogBean.LetingCatalogEntity letingCatalogEntity : leting_catalog) {
            flowTagList.add(letingCatalogEntity.getName());
        }
        letingListAdapter = new FlowLayoutAdapter(flowTagList,activity);
        letingGridView.setAdapter(letingListAdapter);
    }

    public void showLetingNewsCatalog() {
        flowTagList = new ArrayList<>();
        for (String mLetingDatum : mLetingData) {
            flowTagList.add(mLetingDatum);
        }
        letingListAdapter = new FlowLayoutAdapter(flowTagList,activity);
        letingGridView.setAdapter(letingListAdapter);
    }

    @Override
    public void showLoading() {
        super.showLoading();
        mFlLoading.setVisibility(View.VISIBLE);
        mLibScrollView.setVisibility(View.GONE);
        mTvEmpty.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        super.hideLoading();
        mFlLoading.setVisibility(View.GONE);
        mLibScrollView.setVisibility(View.VISIBLE);
        mTvEmpty.setVisibility(View.GONE);
    }

    @Override
    public void showErrorView() {
        super.showErrorView();
        mFlLoading.setVisibility(View.GONE);
        mLibScrollView.setVisibility(View.GONE);
        mTvEmpty.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void showSearchTagResult(AudioAlbumBean audioAlbumBean) {
        if (audioAlbumBean == null || audioAlbumBean.getAudio_album() == null || audioAlbumBean.getAudio_album().size() == 0) return;
        Bundle bundle = new Bundle();
        bundle.putSerializable("audio_album", audioAlbumBean.getAudio_album());
        bundle.putString("listSoundFragment", "listSoundFragment");
        start(SoundAlbumListFragment.newInstance(bundle));
    }

}
