package net.imoran.auto.music.ui.fragment.local;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.imoran.auto.music.R;
import net.imoran.auto.music.bean.PageInfoBean;
import net.imoran.auto.music.player.model.SongModel;
import net.imoran.auto.music.ui.adapter.BaseRecycleAdapter;
import net.imoran.auto.music.ui.adapter.NetListAdapter;
import net.imoran.auto.music.ui.fragment.net.NetPlayFragment;
import net.imoran.auto.music.utils.ClickUtils;
import net.imoran.auto.music.utils.KeyBoardUtils;

import net.imoran.auto.music.utils.ListUtils;
import net.imoran.auto.music.utils.StringUtils;

import java.util.List;

import me.yokeyword.fragmentation.ISupportFragment;

public class LocalSearchFragment extends LocalBaseFragment implements View.OnClickListener {
    private ImageView ivBack;
    private EditText etMusicSearch;
    private ImageView ivMusicSearch;
    //搜索相关
    private LinearLayout llSearchContainer;
    private View line;
    private TextView tvPlayAll;
    private TextView tvSearchResult;
    private TextView tvEmpty;
    private RecyclerView rvSearchList;
    private ProgressBar pbList;
    private RelativeLayout rlPageContainer;
    private TextView tvPreviousPage;
    private TextView tvCurrentPage;
    private TextView tvNextPage;
    private NetListAdapter listAdapter;
    private PageInfoBean currentPagInfo = new PageInfoBean();
    private List<SongModel> list;

    public static LocalSearchFragment newInstance() {
        Bundle args = new Bundle();
        LocalSearchFragment fragment = new LocalSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_local_search;
    }

    @Override
    protected String getPageType() {
        return "search";
    }

    @Override
    protected void onViewCreated() {
        init();
        setListener();
    }

    private void init() {
        ivBack = (ImageView) rootView.findViewById(R.id.ivBack);
        etMusicSearch = (EditText) rootView.findViewById(R.id.etMusicSearch);
        ivMusicSearch = (ImageView) rootView.findViewById(R.id.ivMusicSearch);

        llSearchContainer = (LinearLayout) rootView.findViewById(R.id.llSearchContainer);
        line = rootView.findViewById(R.id.line);
        tvPlayAll = (TextView) rootView.findViewById(R.id.tvPlayAll);
        tvSearchResult = (TextView) rootView.findViewById(R.id.tvSearchResult);
        tvEmpty = (TextView) rootView.findViewById(R.id.tvEmpty);
        rvSearchList = (RecyclerView) rootView.findViewById(R.id.rvSearchList);
        pbList = (ProgressBar) rootView.findViewById(R.id.pbList);
        rlPageContainer = (RelativeLayout) rootView.findViewById(R.id.rlPageContainer);
        tvPreviousPage = (TextView) rootView.findViewById(R.id.tvPreviousPage);
        tvCurrentPage = (TextView) rootView.findViewById(R.id.tvCurrentPage);
        tvNextPage = (TextView) rootView.findViewById(R.id.tvNextPage);
    }

    private void setListener() {
        ivBack.setOnClickListener(this);
        ivMusicSearch.setOnClickListener(this);

        tvPlayAll.setOnClickListener(this);
        tvSearchResult.setOnClickListener(this);
        tvPreviousPage.setOnClickListener(this);
        tvNextPage.setOnClickListener(this);

        listAdapter = new NetListAdapter(activity);
        rvSearchList.setLayoutManager(new LinearLayoutManager(activity));
        rvSearchList.setAdapter(listAdapter);
        listAdapter.setOnItemClickListener(new NetListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, SongModel songModel) {
                pop();
                ISupportFragment fragment = getPreFragment();
                if (fragment instanceof NetPlayFragment) {
                    LocalPlayFragment playFragment = (LocalPlayFragment) fragment;
                    playFragment.bindData(list, null, position, true);
                }
            }

            @Override
            public void onDeleteClick(int position, SongModel songModel) {
            }
        });

        etMusicSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editable.toString().trim();
                if (StringUtils.isEmpty(str)) {
                    llSearchContainer.setVisibility(View.GONE);
                }
            }
        });
        etMusicSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    KeyBoardUtils.closeKeyBord(etMusicSearch, activity);
                    searchMusicByKeyWord();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (ClickUtils.isFastClick()) return;
        switch (view.getId()) {
            case R.id.ivBack:
                pop();
                break;
            case R.id.ivMusicSearch:
                searchMusicByKeyWord();
                break;
            case R.id.tvPlayAll:
                if (ListUtils.isNotEmpty(list)) {
                    ISupportFragment fragment = getPreFragment();
                    if (fragment instanceof NetPlayFragment) {
                        LocalPlayFragment playFragment = (LocalPlayFragment) fragment;
                        playFragment.bindData(list, null, 0, true);
                    }
                }
                pop();
                break;
            case R.id.tvPreviousPage:
                loadMusicByPageNum(false);
                break;
            case R.id.tvNextPage:
                loadMusicByPageNum(true);
                break;
        }
    }

    private void searchMusicByKeyWord() {
        String keyWord = etMusicSearch.getText().toString().trim();
        presenter.searchMusicByKeyWord(keyWord);
        KeyBoardUtils.closeKeyBord(etMusicSearch, activity);
    }

    private void loadMusicByPageNum(boolean isNext) {
        int currentPage = currentPagInfo.getCurrentPageNum();
        if (isNext) {
            if (currentPage >= currentPagInfo.getTotalPageNum()) {
                return;
            } else {
                int page = currentPage + 1;
                presenter.searchMusicByPageNum(page);
            }
        } else {
            if (currentPage <= 1) {
                return;
            } else {
                int page = currentPage - 1;
                presenter.searchMusicByPageNum(page);
            }
        }
    }

    @Override
    public void loadLocalMusicSuccess(int total, int currentPage, List<SongModel> list) {
        currentPagInfo.setCurrentPageNum(currentPage);
        this.list = list;
        bindData(list);
    }

    private void bindData(List<SongModel> list) {
        if (ListUtils.isNotEmpty(list)) {
            line.setVisibility(View.VISIBLE);
            tvPlayAll.setVisibility(View.VISIBLE);
            rvSearchList.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            listAdapter.setDataList(list);

            setPageStatus();
            tvSearchResult.setVisibility(View.VISIBLE);
            if (currentPagInfo.getTotal() > 100) {
                tvSearchResult.setText("已为您找到100+相关歌曲");
            } else {
                tvSearchResult.setText("已为您找到" + currentPagInfo.getTotal() + "相关歌曲");
            }
        } else {
            tvPlayAll.setVisibility(View.INVISIBLE);
            rvSearchList.setVisibility(View.INVISIBLE);
            tvEmpty.setVisibility(View.VISIBLE);
            tvSearchResult.setVisibility(View.INVISIBLE);
            line.setVisibility(View.INVISIBLE);
        }
    }

    //设置页面控制按钮状态
    private void setPageStatus() {
        if (currentPagInfo.getTotalPageNum() > 1) {
            rlPageContainer.setVisibility(View.VISIBLE);
            tvCurrentPage.setText(currentPagInfo.getCurrentPageNum() + "/" + currentPagInfo.getTotalPageNum());
            if (currentPagInfo.getCurrentPageNum() == 1) {
                tvPreviousPage.setSelected(false);
                tvPreviousPage.setClickable(false);

                tvNextPage.setClickable(true);
                tvNextPage.setSelected(true);
            } else if (currentPagInfo.getCurrentPageNum() == currentPagInfo.getTotalPageNum()) {
                tvPreviousPage.setSelected(true);
                tvPreviousPage.setClickable(true);

                tvNextPage.setClickable(false);
                tvNextPage.setSelected(false);
            } else {
                tvPreviousPage.setSelected(true);
                tvPreviousPage.setClickable(true);

                tvNextPage.setClickable(true);
                tvNextPage.setSelected(true);
            }
        } else {
            rlPageContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void showLoading() {
        llSearchContainer.setVisibility(View.VISIBLE);
        rvSearchList.setVisibility(View.INVISIBLE);
        pbList.setVisibility(View.VISIBLE);
        line.setVisibility(View.INVISIBLE);
        tvPlayAll.setVisibility(View.INVISIBLE);
        tvSearchResult.setVisibility(View.INVISIBLE);
        rlPageContainer.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hideLoading() {
        llSearchContainer.setVisibility(View.VISIBLE);
        rvSearchList.setVisibility(View.VISIBLE);
        pbList.setVisibility(View.INVISIBLE);
    }
}
