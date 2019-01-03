package net.imoran.auto.music.ui.fragment.sound;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagFlowLayout;

import net.imoran.auto.music.R;
import net.imoran.auto.music.ui.adapter.FlowLayoutAdapter;
import net.imoran.auto.music.ui.adapter.SoundAlbumAdapter;
import net.imoran.auto.music.utils.SharedPreferencesUtil;
import net.imoran.sdk.bean.bean.AudioAlbumBean;
import net.imoran.sdk.bean.bean.AudioProgramBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by xinhua.shi on 2018/6/19.
 */

public class SoundSearchFragment extends SoundBaseFragment {
    private static final String TAG = "SoundSearchFragment";
    private List<String> mHotWords = new ArrayList<>();
    private List<String> mHistoryWords = new ArrayList<>();
    private TagFlowLayout hotFlowLayout;
    private TagFlowLayout historyFlowLayout;
    private ImageView searchIvBack;
    private ImageView mSearchBtn;
    private EditText searchContent;
    private FlowLayoutAdapter mHistoryAdapter;
    private TextView clearHistory;
    private RelativeLayout result1;
    private RelativeLayout result;
    private RecyclerView recyclerView;
    private ProgressBar pbList;
    private RelativeLayout searchResultControl;
    private View mBootomLine;
    private TextView searchNum;
    private TextView lastAudio;
    private TextView nextAudio;
    private TextView currentPage;
    private int mCurrentPage = 1;
    private int mTotalPage;
    private int mLastPageItem;

    public static SoundSearchFragment newInstance() {
        Bundle args = new Bundle();
        SoundSearchFragment fragment = new SoundSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onViewCreated() {
        List<String> historyData = SharedPreferencesUtil.getDataList("historyData", activity);
        mHistoryWords.addAll(historyData);
        initView();
        initDatas();
        hotFlowLayout.setAdapter(new FlowLayoutAdapter(mHotWords, activity));
        hotFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                resetListView();
                result.setVisibility(View.GONE);
                result1.setVisibility(View.VISIBLE);
                searchContent.setText(mHotWords.get(position));
                return false;
            }
        });
        historyFlowLayout.setAdapter(mHistoryAdapter);
        historyFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                resetListView();
                result.setVisibility(View.GONE);
                result1.setVisibility(View.VISIBLE);
                searchContent.setText(mHistoryWords.get(position));
                return false;
            }
        });
        initListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isVisible()) {
            sendHotWords(getHotWords());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        clearHotWords();
    }

    /**
     * 主动清空当前的热词
     * <p>
     * 一般不用调用，该方法会在Activity的生命周期 onPause 中去调用
     */
    protected void clearHotWords() {
        Intent intent = new Intent();
        intent.setAction("net.imoran.auto.clientaction.settings.scene");
        intent.putExtra("hotwordsClear", "");
        getContext().sendBroadcast(intent);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            Log.e(TAG, "onHiddenChanged--hidden");
        } else {
            Log.e(TAG, "onHiddenChanged--show");
        }
    }

    @Override
    protected String getPageType() {
        return "search";
    }

    private void initListener() {
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
        if (mTotalPage < 2) return;
        if (mCurrentPage < mTotalPage) {
            int position = mCurrentPage * 5;
            recyclerView.scrollToPosition(position);
            LinearLayoutManager mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            mLayoutManager.scrollToPositionWithOffset(position, 0);
            mCurrentPage++;
            currentPage.setText(mCurrentPage + "/" + mTotalPage);
        }

        if (mCurrentPage < mTotalPage) {
            nextAudio.setSelected(true);
        }
        if (mCurrentPage == mTotalPage) {
            nextAudio.setSelected(false);
            setViewGroupLayoutParams();
        }
        if (mCurrentPage > 1) {
            lastAudio.setSelected(true);
        }
    }

    protected ArrayList<String> getHotWords() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("清空历史记录");
        return list;
    }

    /**
     * 主动发送热词给服务
     * <p>
     * 一般不用调用，该方法会在Activity的生命周期 onResume onPause 中去调用
     */
    protected void sendHotWords(ArrayList<String> list) {
        Intent intent = new Intent();
        intent.setAction("net.imoran.auto.clientaction.settings.scene");

        if (list != null) {
            intent.putExtra("hotwords", list);
        }
        if (getContext() != null) {
            getContext().sendBroadcast(intent);
        }
    }

    @Override
    public void vuiPreviousPage() {
        super.vuiPreviousPage();
        lastPage();
    }

    @Override
    public void vuiHotWords(String words) {
        if ("清空历史记录".equals(words)) {
            mHistoryWords.clear();
            SharedPreferencesUtil.clearDataList(activity);
            mHistoryAdapter.notifyDataChanged();
        }
    }

    private void lastPage() {
        if (mTotalPage < 2) return;
        if (mCurrentPage > 1) {
            mCurrentPage--;
            int position = (mCurrentPage - 1) * 5;
            recyclerView.scrollToPosition(position);
            LinearLayoutManager mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            mLayoutManager.scrollToPositionWithOffset(position, 0);
            currentPage.setText(mCurrentPage + "/" + mTotalPage);
        }

        if (mCurrentPage == 1) {
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

    public void resetListView() {
        if (audio_album != null && soundAlbumAdapter != null) {
            audio_album.clear();
            soundAlbumAdapter.notifyDataSetChanged();
        }
    }

    private void initView() {
        result1 = (RelativeLayout) rootView.findViewById(R.id.search_result1);
        result = (RelativeLayout) rootView.findViewById(R.id.search_result);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_search_result);
        mHistoryAdapter = new FlowLayoutAdapter(mHistoryWords, activity);
        hotFlowLayout = (TagFlowLayout) rootView.findViewById(R.id.id_flowlayout_hot);
        searchIvBack = (ImageView) rootView.findViewById(R.id.search_ic_back);
        mSearchBtn = (ImageView) rootView.findViewById(R.id.ivMusicSearch);
        searchContent = (EditText) rootView.findViewById(R.id.etMusicSearch);
        clearHistory = (TextView) rootView.findViewById(R.id.clear_search_history);
        pbList = (ProgressBar) rootView.findViewById(R.id.pbList);
        historyFlowLayout = (TagFlowLayout) rootView.findViewById(R.id.id_flowlayout_history);
        currentPage = (TextView) rootView.findViewById(R.id.currentaudioPage);
        searchResultControl = (RelativeLayout) rootView.findViewById(R.id.search_result_control);
        lastAudio = (TextView) rootView.findViewById(R.id.lastaudio);
        nextAudio = (TextView) rootView.findViewById(R.id.nextaudio);
        mBootomLine = (View) rootView.findViewById(R.id.bottom_line);
        searchNum = (TextView) rootView.findViewById(R.id.text_munber);
        searchIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (result1.getVisibility() == View.VISIBLE) {
                    result1.setVisibility(View.INVISIBLE);
                    result.setVisibility(View.VISIBLE);
                    mHistoryAdapter.notifyDataChanged();
                } else {
                    SharedPreferencesUtil.setSearchDataList(activity, "historyData", mHistoryWords);
                    pop();
                }
            }
        });
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetListView();
                result.setVisibility(View.GONE);
                result1.setVisibility(View.VISIBLE);
                mHistoryAdapter.notifyDataChanged();
                presenter.getSearchAudioKey(searchContent.getText().toString().trim());
                showLoading();
                addHistoryWord(searchContent.getText().toString().trim());
                SharedPreferencesUtil.setSearchDataList(activity, "historyData", mHistoryWords);
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
            }
        });
        clearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHistoryWords.clear();
                SharedPreferencesUtil.clearDataList(activity);
                mHistoryAdapter.notifyDataChanged();

            }
        });
        searchContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (searchContent.getText().toString() == null || searchContent.getText().toString().equals("")) {
                    result.setVisibility(View.VISIBLE);
                    result1.setVisibility(View.GONE);
                    mHistoryAdapter.notifyDataChanged();
                } else {
                    resetListView();
                    result.setVisibility(View.GONE);
                    result1.setVisibility(View.VISIBLE);
                    presenter.getSearchAudioKey(searchContent.getText().toString().trim());
                    showLoading();
                }
            }
        });

        searchContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    resetListView();
                    result.setVisibility(View.GONE);
                    result1.setVisibility(View.VISIBLE);
                    mHistoryAdapter.notifyDataChanged();
                    presenter.getSearchAudioKey(searchContent.getText().toString().trim());
                    showLoading();
                    addHistoryWord(searchContent.getText().toString().trim());
                    SharedPreferencesUtil.setSearchDataList(activity, "historyData", mHistoryWords);
                }
                return false;
            }
        });

        soundAlbumAdapter = new SoundAlbumAdapter(activity, audio_album);
        soundAlbumAdapter.setOnItemClickListener(new SoundAlbumAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AudioAlbumBean.AudioAlbumEntity contact) {
                mContact = contact;
                presenter.getAudioProgramDetail(contact.getAlbum_id());
                addHistoryWord(searchContent.getText().toString().trim());
                SharedPreferencesUtil.setSearchDataList(activity, "historyData", mHistoryWords);
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(activity, R.drawable.custom_divider));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(soundAlbumAdapter);
    }

    public void showLoading() {
        searchResultControl.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        mBootomLine.setVisibility(View.GONE);
        pbList.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        searchResultControl.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        mBootomLine.setVisibility(View.VISIBLE);
        pbList.setVisibility(View.GONE);
    }

    public void addHistoryWord(String historyWord) {
        Collections.reverse(mHistoryWords);
        if (mHistoryWords.contains(historyWord)) {
            mHistoryWords.remove(historyWord);
            mHistoryWords.add(historyWord);
        } else {
            if (mHistoryWords.size() >= 10) {
                mHistoryWords.remove(0);
            }
            mHistoryWords.add(historyWord);
        }
        Collections.reverse(mHistoryWords);
    }

    private void initDatas() {
        mHotWords.add("历");
        mHotWords.add("文");
        mHotWords.add("军");
        mHotWords.add("财");
        mHotWords.add("评");
    }

    @Override
    public int getLayoutRes() {
        return R.layout.sound_search;
    }


    private AudioAlbumBean.AudioAlbumEntity mContact;
    private List<AudioAlbumBean.AudioAlbumEntity> audio_album = new ArrayList<>();
    private SoundAlbumAdapter soundAlbumAdapter;

    @Override
    public void showSearchResult(List<AudioAlbumBean.AudioAlbumEntity> audioAlbumEntities) {
        if (audioAlbumEntities == null || audioAlbumEntities.size() == 0) {
            searchNum.setText("已为您找到0个搜索结果");
            searchResultControl.setVisibility(View.GONE);
            return;
        }
        searchResultControl.setVisibility(View.VISIBLE);
        audio_album.clear();
        audio_album.addAll(audioAlbumEntities);
        mTotalPage = (audio_album.size() % 5 == 0 ? (audio_album.size() / 5) : (audio_album.size() / 5) + 1);
        mLastPageItem = (audio_album.size() % 5 == 0 ? 5 : (audio_album.size() % 5));
        currentPage.setText(1 + "/" + mTotalPage);
        if (mTotalPage < 2) {
            nextAudio.setSelected(false);
        } else {
            nextAudio.setSelected(true);
        }
        searchNum.setText("已为您找到" + audioAlbumEntities.size() + "个搜索结果");
        soundAlbumAdapter.notifyDataSetChanged();
    }

    @Override
    public void showAudioProgramDetail(AudioProgramBean audioProgramBean) {
        super.showAudioProgramDetail(audioProgramBean);
        Bundle bundle1 = new Bundle();
        bundle1.putSerializable("program", audioProgramBean.getAudio_program());
        bundle1.putSerializable("album", mContact);
        bundle1.putInt("playPosition", -1);
        bundle1.putInt("playState", -1);
        start(SoundAlbumDetailFragment.newInstance(bundle1));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void setViewGroupLayoutParams() {
        int scale = (int) activity.getResources().getDisplayMetrics().density;
        ViewGroup.LayoutParams layoutParams = recyclerView.getLayoutParams();
        layoutParams.height = mLastPageItem * 110 * scale;
        recyclerView.setLayoutParams(layoutParams);
    }

    public void setViewGroupLayoutParamsNormal() {
        int scale = (int) activity.getResources().getDisplayMetrics().density;
        ViewGroup.LayoutParams layoutParams = recyclerView.getLayoutParams();
        layoutParams.height = 550 * scale;
        recyclerView.setLayoutParams(layoutParams);
    }
}
