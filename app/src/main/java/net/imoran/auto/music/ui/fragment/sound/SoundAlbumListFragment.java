package net.imoran.auto.music.ui.fragment.sound;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mediatek.mmsdk.IFeatureManager;

import net.imoran.auto.music.ui.adapter.SoundAlbumAdapter;
import net.imoran.auto.music.utils.SharedPreferencesUtil;

import net.imoran.auto.music.R;
import net.imoran.auto.music.app.MusicApp;
import net.imoran.auto.music.ui.adapter.SoundAlbumAdapter;
import net.imoran.rripc.lib.ResponseCallback;
import net.imoran.sdk.bean.bean.AudioAlbumBean;
import net.imoran.sdk.bean.bean.AudioProgramBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Created by xinhua.shi on 2018/6/20.
 */

public class SoundAlbumListFragment extends SoundBaseFragment {
    private static final String TAG = "SoundAlbumListFragment";
    private ImageView ivBack;
    private SoundAlbumAdapter albumAdapter;
    private RecyclerView mRecycleView;
    private TextView lastAudio;
    private TextView nextAudio;
    private TextView currentPage;
    private int mCurrentPage = 1;
    private int mTotalPage;
    private int mLastPageItem;

    public static SoundAlbumListFragment newInstance(Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        SoundAlbumListFragment fragment = new SoundAlbumListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected String getPageType() {
        return "list";
    }

    @Override
    public void vuiAlbumList(AudioAlbumBean audioAlbumBean, String queryId) {
        super.vuiAlbumList(audioAlbumBean, queryId);
        dealVuiData(true,audioAlbumBean);
        updateQueryId(getPageId(),queryId);
    }

    @Override
    protected void onViewCreated() {
        initView();
        initListener();
    }

    private void initListener() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ISupportFragment preFragment = getPreFragment();
                if (preFragment != null && preFragment instanceof SoundBaseFragment) {
                    ((SoundBaseFragment) preFragment).onResume();
                }
                if (preFragment != null && preFragment instanceof SoundPlayFragment) {
                    ((SoundPlayFragment)preFragment).getRandomAudio();
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

    @Override
    public void vuiNextPage() {
        super.vuiNextPage();
        nextPage();
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
    public void vuiPreviousPage() {
        super.vuiPreviousPage();
        lastPage();
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
        dealVuiData(false, null);
    }

    public void setViewGroupLayoutParams() {
        int scale = (int) activity.getResources().getDisplayMetrics().density;
        ViewGroup.LayoutParams layoutParams = mRecycleView.getLayoutParams();
        layoutParams.height = mLastPageItem * 114 * scale;
        mRecycleView.setLayoutParams(layoutParams);
    }

    public void setViewGroupLayoutParamsNormal() {
        int scale = (int) activity.getResources().getDisplayMetrics().density;
        ViewGroup.LayoutParams layoutParams = mRecycleView.getLayoutParams();
        layoutParams.height = 684 * scale;
        mRecycleView.setLayoutParams(layoutParams);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private String come;
    private String mQueryId;
    AudioAlbumBean.AudioAlbumEntity mContact;
    List<AudioAlbumBean.AudioAlbumEntity> audio_albums;

    public void dealVuiData(boolean isShow, AudioAlbumBean audioAlbumBean) {
        if (isShow) {
            audio_albums = audioAlbumBean.getAudio_album();
            if (audio_albums == null || audio_albums.size() == 0) {
                currentPage.setText(1 + "/" + 1);
                mCurrentPage = 1;
                mTotalPage = 1;
                lastAudio.setSelected(false);
                nextAudio.setSelected(false);
                return;
            }
            SharedPreferencesUtil.setDataList(activity, "audio_albums", audio_albums);
            mTotalPage = audio_albums.size()%6 == 0 ? audio_albums.size()/6 : (audio_albums.size() / 6) + 1;
            mLastPageItem = audio_albums.size() % 6;
            currentPage.setText(1 + "/" + mTotalPage);
            if (mTotalPage > 1) {
                lastAudio.setSelected(false);
                nextAudio.setSelected(true);
            }
            albumAdapter = new SoundAlbumAdapter(activity, audio_albums);
            albumAdapter.setOnItemClickListener(new SoundAlbumAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(AudioAlbumBean.AudioAlbumEntity contact) {
                    mContact = contact;
                    hideVuiCard();
                    presenter.getAudioProgramDetail(contact.getAlbum_id());
                }
            });
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL);
            dividerItemDecoration.setDrawable(ContextCompat.getDrawable(activity,R.drawable.custom_divider));
            mRecycleView.addItemDecoration(dividerItemDecoration);
            mRecycleView.setAdapter(albumAdapter);
        } else {
            Bundle arguments = getArguments();
            if (arguments != null) {
                audio_albums = (List<AudioAlbumBean.AudioAlbumEntity>) arguments.getSerializable("audio_album");
                come = (String) arguments.get("listSoundFragment");
                mQueryId = (String) arguments.get("queryId");
                if (mQueryId != null) {
                    updateQueryId(getPageId(),mQueryId);
                }
            } else {
                audio_albums = SharedPreferencesUtil.getVuiDataList("audio_albums", activity);
            }
            if (audio_albums == null || audio_albums.size() == 0) {
                currentPage.setText(1 + "/" + 1);
                mCurrentPage = 1;
                mTotalPage = 1;
                lastAudio.setSelected(false);
                nextAudio.setSelected(false);
                return;
            }
            SharedPreferencesUtil.setDataList(activity, "audio_albums", audio_albums);
            mTotalPage = (audio_albums.size() / 6) + 1;
            mLastPageItem = (audio_albums.size() % 6);
            currentPage.setText(1 + "/" + mTotalPage);
            if (mTotalPage > 1) {
                lastAudio.setSelected(false);
                nextAudio.setSelected(true);
            }
            String queryId = (String) SharedPreferencesUtil.getData(activity, "queryId", "");
            updateQueryId(this.getClass().getName(), queryId);
            albumAdapter = new SoundAlbumAdapter(activity, audio_albums);
            albumAdapter.setOnItemClickListener(new SoundAlbumAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(AudioAlbumBean.AudioAlbumEntity contact) {
                    mContact = contact;
                    presenter.getAudioProgramDetail(contact.getAlbum_id());
                }
            });
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL);
            dividerItemDecoration.setDrawable(ContextCompat.getDrawable(activity,R.drawable.custom_divider));
            mRecycleView.addItemDecoration(dividerItemDecoration);
            mRecycleView.setAdapter(albumAdapter);
        }
    }

    private void hideVuiCard() {
        Intent intent = new Intent();
        intent.setAction("net.imoran.action.hidevui");
        intent.putExtra("appName","music");
        activity.sendBroadcast(intent);
    }

    @Override
    public void showAudioProgramDetail(AudioProgramBean audioProgramBean) {
        super.showAudioProgramDetail(audioProgramBean);
        Bundle bundle1 = new Bundle();
        bundle1.putSerializable("program", audioProgramBean.getAudio_program());
        bundle1.putSerializable("album", mContact);
        bundle1.putInt("playPosition", -1);
        bundle1.putInt("playState", -1);
        bundle1.putString("lastfragment", SoundAlbumListFragment.class.getName());
        start(SoundAlbumDetailFragment.newInstance(bundle1));
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG,"onResume");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            Log.e(TAG,"onHiddenChanged-- true");
        }else {
            Log.e(TAG,"onHiddenChanged-- false");
        }
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        Log.e(TAG,"onSupportVisible");
        if (!MusicApp.getInstance().isRClientCon()) {
            return;
        }
        // 页面显示的时候设置当前页面的热词
        Bundle data = new Bundle();
        // 指定动作是要更新queryid
        data.putString("updateAction", "updatePage");
        // 可见的页面是列表
        String pages = gson.toJson(getVisiblePages());
        data.putString("pageIdList", pages);
        MusicApp.getInstance().getRrClient().send(10102, data, new ResponseCallback() {
            @Override
            public void onResponse(String s) {

            }

            @Override
            public void onResponseError(String s, int i) {

            }
        });
    }

    @Override
    public void vuiProgramList(AudioProgramBean audioProgramBean, String queryId) {
        super.vuiProgramList(audioProgramBean, queryId);
        SoundPlayFragment fragment = findFragment(SoundPlayFragment.class);
        if (audioProgramBean == null && audioProgramBean.getAudio_program() == null || audioProgramBean.getAudio_program().size() ==0){
            Toast.makeText(activity,"没有数据，请重试",Toast.LENGTH_SHORT).show();
            return;
        }
        fragment.bindData(audioProgramBean.getAudio_program(), 0, null);
        fragment.resumeVuiPlay();
        popTo(SoundPlayFragment.class, false);
    }

    protected ArrayList<String> getVisiblePages() {
        ArrayList<String> pages = new ArrayList<>();
        pages.add(getPageId());
        return pages;
    }

    /**
     * 默认的页面id
     *
     * @return
     */
    protected String getDefaultPageId() {
        Log.e("hehe", this.getClass().getName());
        return this.getClass().getName();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_music_album_list;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
