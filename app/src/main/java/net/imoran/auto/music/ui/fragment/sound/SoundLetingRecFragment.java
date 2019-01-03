package net.imoran.auto.music.ui.fragment.sound;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.imoran.auto.music.R;
import net.imoran.auto.music.ui.adapter.SoundAlbumAdapter;
import net.imoran.auto.music.ui.adapter.SoundLetingAdapter;
import net.imoran.auto.music.utils.SharedPreferencesUtil;
import net.imoran.sdk.bean.bean.AudioAlbumBean;
import net.imoran.sdk.bean.bean.LetingNewsBean;

import java.util.List;

import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Created by xinhuashi on 2018/9/27.
 */

public class SoundLetingRecFragment extends SoundBaseFragment{
    private static final String TAG = "SoundLetingRecFragment";
    private ImageView ivBack;
    private SoundLetingAdapter albumAdapter;
    private RecyclerView mRecycleView;
    private TextView lastAudio;
    private TextView nextAudio;
    private TextView currentPage;
    private int mCurrentPage = 1;
    private int mTotalPage;
    private int mLastPageItem;
    private List<LetingNewsBean.LetingNewsEntity> letingList;

    public static SoundLetingRecFragment newInstance(Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        SoundLetingRecFragment fragment = new SoundLetingRecFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_music_leting_rec;
    }

    @Override
    protected void onViewCreated() {
        initView();
        initListener();
    }

    @Override
    protected String getPageType() {
        return null;
    }

    private void initListener() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ISupportFragment preFragment = getPreFragment();
                if (preFragment != null && preFragment instanceof SoundBaseFragment) {
                    ((SoundBaseFragment) preFragment).onResume();
                }
                pop();
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

    private void nextPage() {
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
    public void onResume() {
        super.onResume();
        Log.e("onResume",TAG);
    }

    private void initView() {
        ivBack = (ImageView) rootView.findViewById(R.id.back);
        currentPage = (TextView) rootView.findViewById(R.id.currentaudioPage);
        lastAudio = (TextView) rootView.findViewById(R.id.lastaudio);
        nextAudio = (TextView) rootView.findViewById(R.id.nextaudio);
        mRecycleView = (RecyclerView) rootView.findViewById(R.id.ls_subscribe);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mRecycleView.setLayoutManager(layoutManager);
        dealVuiData();
    }

    private void dealVuiData() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            letingList = (List<LetingNewsBean.LetingNewsEntity>) arguments.getSerializable("leting_rec_news");
        }
        if (letingList == null || letingList.size() == 0) {
            currentPage.setText(1 + "/" + 1);
            mCurrentPage = 1;
            mTotalPage = 1;
            lastAudio.setSelected(false);
            nextAudio.setSelected(false);
            return;
        }
        mTotalPage = (letingList.size() / 6) + 1;
        mLastPageItem = (letingList.size() % 6);
        currentPage.setText(1 + "/" + mTotalPage);
        if (mTotalPage > 1) {
            lastAudio.setSelected(false);
            nextAudio.setSelected(true);
        }
        String queryId = (String) SharedPreferencesUtil.getData(activity, "queryId", "");
        updateQueryId(this.getClass().getName(), queryId);
        albumAdapter = new SoundLetingAdapter(activity, letingList);
        albumAdapter.setOnItemClickListener(new SoundLetingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String categoryId) {
                presenter.getLetingDetail(categoryId);
            }
        });
        mRecycleView.addItemDecoration(new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));
        mRecycleView.setAdapter(albumAdapter);
    }

    @Override
    public void showLetingNewsRec(LetingNewsBean letingNewsBean,String keyWord) {
        super.showLetingNewsRec(letingNewsBean,keyWord);
        if (letingNewsBean == null || letingNewsBean.getLeting_news() == null || letingNewsBean.getLeting_news().size() == 0) return;
        Bundle bundle = new Bundle();
        bundle.putSerializable("leting_rec_news", letingNewsBean.getLeting_news());
        start(SoundLetingDetailFragment.newInstance(bundle));
    }

    private void lastPage() {
        if (mCurrentPage > 1) {
            mCurrentPage--;
            int position = (mCurrentPage - 1) * 6;
            mRecycleView.scrollToPosition(position);
            LinearLayoutManager mLayoutManager = (LinearLayoutManager) mRecycleView.getLayoutManager();
            mLayoutManager.scrollToPositionWithOffset(position, 0);
            currentPage.setText(mCurrentPage + "/" + mTotalPage);
        }
        if (mCurrentPage < mTotalPage) {
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


    public void setViewGroupLayoutParams() {
        int scale = (int) activity.getResources().getDisplayMetrics().density;
        ViewGroup.LayoutParams layoutParams = mRecycleView.getLayoutParams();
        layoutParams.height = mLastPageItem * 110 * scale;
        mRecycleView.setLayoutParams(layoutParams);
    }

    public void setViewGroupLayoutParamsNormal() {
        int scale = (int) activity.getResources().getDisplayMetrics().density;
        ViewGroup.LayoutParams layoutParams = mRecycleView.getLayoutParams();
        layoutParams.height = 660 * scale;
        mRecycleView.setLayoutParams(layoutParams);
    }
}
