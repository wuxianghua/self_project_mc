package net.imoran.auto.music.ui.fragment.sound;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import net.imoran.auto.music.R;
import net.imoran.auto.music.bean.SoundColectBean;
import net.imoran.auto.music.ui.adapter.SoundCollectionAdapter;
import net.imoran.auto.music.utils.DataConvertUtils;
import net.imoran.auto.music.widgets.LoadingErrorView;
import net.imoran.sdk.bean.bean.AudioProgramBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinhuashi on 2018/7/22.
 */

public class SoundCollectionFragment extends SoundBaseFragment {
    private static final String TAG = "SoundCollectionFragment";
    private ImageView ivBack;
    private RecyclerView mRecycleView;
    private TextView palyAll;
    private TextView lastAudio;
    private TextView nextAudio;
    private TextView currentPage;
    private int mCurrentPage = 1;
    private int mTotalPage;
    private int mLastPageItem;
    private TextView tvEmpty;
    private TextView mTvSaveList;
    private FrameLayout flLoading;
    private View mBottomLine;
    private RelativeLayout collPageControl;
    private SoundCollectionAdapter soundCollectionAdapter;
    private ArrayList<AudioProgramBean.AudioProgramEntity> audio_program;

    @Override
    protected void onViewCreated() {
        initView();
        presenter.getCollectionList();
        initListener();
    }

    @Override
    protected String getPageType() {
        return "list";
    }

    public static SoundCollectionFragment newInstance() {
        Bundle args = new Bundle();
        SoundCollectionFragment fragment = new SoundCollectionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void initListener() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop();
            }
        });
        palyAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(audio_program == null || audio_program.size() == 0) {
                    Toast.makeText(activity,"没有节目可以播放",Toast.LENGTH_SHORT).show();
                    return;
                }
                SoundPlayFragment fragment = findFragment(SoundPlayFragment.class);
                fragment.bindData(audio_program, 0, "collection");
                popTo(SoundPlayFragment.class, false);
            }
        });

        lastAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lastPage();
            }
        });
        nextAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextPage();
            }
        });
    }

    @Override
    public void vuiNextPage() {
        super.vuiNextPage();
        nextPage();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void nextPage() {
        if (mTotalPage < 2) return;
        if (mCurrentPage < mTotalPage) {
            int position = mCurrentPage * 6;
            mRecycleView.scrollToPosition(position);
            LinearLayoutManager mLayoutManager = (LinearLayoutManager) mRecycleView.getLayoutManager();
            mLayoutManager.scrollToPositionWithOffset(position, 0);
            mCurrentPage++;
            currentPage.setText(mCurrentPage + "/" + mTotalPage);
        }

        if (mCurrentPage < mTotalPage) {
            nextAudio.setSelected(true);
        } if (mCurrentPage == mTotalPage) {
            nextAudio.setSelected(false);
            setViewGroupLayoutParams();
        }
        if (mCurrentPage > 1) {
            lastAudio.setSelected(true);
        }
    }

    @Override
    public void vuiPreviousPage() {
        super.vuiPreviousPage();
        lastPage();
    }

    private void lastPage() {
        if (mTotalPage < 2) return;
        if (mCurrentPage > 1) {
            mCurrentPage--;
            int position = (mCurrentPage - 1) * 6;
            mRecycleView.scrollToPosition(position);
            LinearLayoutManager mLayoutManager = (LinearLayoutManager) mRecycleView.getLayoutManager();
            mLayoutManager.scrollToPositionWithOffset(position, 0);
            currentPage.setText(mCurrentPage + "/" + mTotalPage);
        }
        if (mCurrentPage >= 1) {
            setViewGroupLayoutParamsNormal();
        }
        if (mCurrentPage > 1) {
            lastAudio.setSelected(true);
            if (mCurrentPage < mTotalPage) {
                nextAudio.setSelected(true);
            }
        } else if (mCurrentPage > 0) {
            lastAudio.setSelected(false);
            nextAudio.setSelected(true);
        }
    }

    @Override
    public void showCollectionResult(List<AudioProgramBean.AudioProgramEntity> audioProgramEntities, int totalPage) {
        if (audioProgramEntities == null ||  audioProgramEntities.size() == 0) {
            palyAll.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mTvSaveList.getLayoutParams();
            layoutParams.rightMargin = 60;
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,R.id.tv_save_list);
            showErrorView();
            return;
        }
        hideLoading();
        audio_program = (ArrayList<AudioProgramBean.AudioProgramEntity>) audioProgramEntities;
        super.showCollectionResult(audioProgramEntities, totalPage);
        mTotalPage = (totalPage % 6 == 0 ? (totalPage / 6) : (totalPage / 6) + 1);
        mLastPageItem = (totalPage % 6 == 0 ? 6 : (totalPage % 6));
        currentPage.setText(1 + "/" + mTotalPage);
        if (mTotalPage < 2) {
            nextAudio.setSelected(false);
        } else {
            nextAudio.setSelected(true);
        }
        soundCollectionAdapter = new SoundCollectionAdapter(activity, audio_program);
        soundCollectionAdapter.setOnItemClickListener(new SoundCollectionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AudioProgramBean.AudioProgramEntity contact, int postion) {
                SoundPlayFragment fragment = findFragment(SoundPlayFragment.class);
                fragment.bindData(DataConvertUtils.getSongModelList(audio_program), postion, "collection");
                popTo(SoundPlayFragment.class, false);
            }

            @Override
            public void onDeleteClick(int position, AudioProgramBean.AudioProgramEntity audioProgramEntity, boolean isRemoveAll) {
                SoundColectBean collectionsBean = new SoundColectBean();
                collectionsBean.setAlbumId(audioProgramEntity.getAlbum_id());
                collectionsBean.setTrackId(audioProgramEntity.getTrack_id());
                collectionsBean.setType("track");
                String s = new Gson().toJson(collectionsBean);
                presenter.delAudioCollection(s);
                findFragment(SoundPlayFragment.class).updateCollection(audioProgramEntity.getTrack_id());
                if (isRemoveAll) {
                    showErrorView();
                }
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mRecycleView.setLayoutManager(layoutManager);
        soundCollectionAdapter.setPlayPosition(-1);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(activity,R.drawable.custom_divider));
        mRecycleView.addItemDecoration(dividerItemDecoration);
        mRecycleView.setAdapter(soundCollectionAdapter);
    }

    private void initView() {
        ivBack = (ImageView) rootView.findViewById(R.id.back);
        mRecycleView = (RecyclerView) rootView.findViewById(R.id.ls_collection);
        palyAll = (TextView) rootView.findViewById(R.id.playAll);
        lastAudio = (TextView) rootView.findViewById(R.id.lastaudio);
        nextAudio = (TextView) rootView.findViewById(R.id.nextaudio);
        currentPage = (TextView) rootView.findViewById(R.id.currentaudioPage);
        tvEmpty = (TextView) rootView.findViewById(R.id.tvEmpty);
        flLoading = (FrameLayout) rootView.findViewById(R.id.fl_loading);
        mBottomLine = (View)rootView.findViewById(R.id.bottom_line);
        collPageControl = (RelativeLayout) rootView.findViewById(R.id.coll_page_control);
        mTvSaveList = (TextView) rootView.findViewById(R.id.tv_save_list);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_music_collection_list;
    }

    @Override
    public void showLoading() {
        super.showLoading();
        mRecycleView.setVisibility(View.GONE);
        collPageControl.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.GONE);
        mBottomLine.setVisibility(View.GONE);
        flLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        super.hideLoading();
        mRecycleView.setVisibility(View.VISIBLE);
        collPageControl.setVisibility(View.VISIBLE);
        mBottomLine.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        flLoading.setVisibility(View.GONE);
    }

    @Override
    public void showErrorView() {
        super.showErrorView();
        mRecycleView.setVisibility(View.GONE);
        collPageControl.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.VISIBLE);
        mBottomLine.setVisibility(View.GONE);
        flLoading.setVisibility(View.GONE);

    }

    public void setViewGroupLayoutParams() {
        int scale = (int) activity.getResources().getDisplayMetrics().density;
        ViewGroup.LayoutParams layoutParams = mRecycleView.getLayoutParams();
        layoutParams.height = mLastPageItem * 115 * scale;
        mRecycleView.setLayoutParams(layoutParams);
    }

    public void setViewGroupLayoutParamsNormal() {
        int scale = (int) activity.getResources().getDisplayMetrics().density;
        ViewGroup.LayoutParams layoutParams = mRecycleView.getLayoutParams();
        layoutParams.height = 690 * scale;
        mRecycleView.setLayoutParams(layoutParams);
    }
}
