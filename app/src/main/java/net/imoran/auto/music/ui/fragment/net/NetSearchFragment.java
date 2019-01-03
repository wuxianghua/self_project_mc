package net.imoran.auto.music.ui.fragment.net;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;

import net.imoran.auto.music.R;
import net.imoran.auto.music.bean.PageInfoBean;
import net.imoran.auto.music.bean.SearchKeyWordBean;
import net.imoran.auto.music.player.model.SongModel;
import net.imoran.auto.music.ui.adapter.NetListAdapter;
import net.imoran.auto.music.utils.ClickUtils;
import net.imoran.auto.music.utils.KeyBoardUtils;
import net.imoran.auto.music.utils.ListUtils;
import net.imoran.auto.music.utils.StringUtils;
import net.imoran.auto.music.vui.ContextSyncManager;
import net.imoran.tv.common.lib.utils.ToastUtil;

import java.util.List;

public class NetSearchFragment extends NetBaseFragment implements View.OnClickListener {
    private ImageView ivBack;
    private EditText etMusicSearch;
    private TextView ivMusicSearch;
    private ImageView ivClearKey, ivClearLine;
    private LinearLayout llKeyWordContainer;
    private TextView tvClearHistory;
    private FlexboxLayout flHistorySearch;
    private FlexboxLayout flHotSearch;
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

    public static NetSearchFragment newInstance() {
        Bundle args = new Bundle();
        NetSearchFragment fragment = new NetSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String getPageType() {
        return "search";
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_net_search;
    }

    @Override
    protected void onViewCreated() {
        init();
        setListener();
        requestData();
    }

    private void init() {
        ivBack = (ImageView) rootView.findViewById(R.id.ivBack);
        etMusicSearch = (EditText) rootView.findViewById(R.id.etMusicSearch);
        ivMusicSearch = (TextView) rootView.findViewById(R.id.ivMusicSearch);
        ivClearKey = (ImageView) rootView.findViewById(R.id.ivClear);
        ivClearLine = (ImageView) rootView.findViewById(R.id.ivClearLine);
        llKeyWordContainer = (LinearLayout) rootView.findViewById(R.id.llKeyWordContainer);
        tvClearHistory = (TextView) rootView.findViewById(R.id.tvClearHistory);
        flHistorySearch = (FlexboxLayout) rootView.findViewById(R.id.flHistorySearch);
        flHotSearch = (FlexboxLayout) rootView.findViewById(R.id.flHotSearch);

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
        tvClearHistory.setOnClickListener(this);
        ivMusicSearch.setOnClickListener(this);
        ivClearKey.setOnClickListener(this);
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
                mainFragment.getPlayFragment().setType(NetPlayListFragment.TYPE_SEARCH);

                NetMainFragment mainFragment = (NetMainFragment) getParentFragment();
                NetPlayFragment playFragment = mainFragment.getPlayFragment();
                playFragment.bindData(list, currentPagInfo, position);
                popTo(NetPlayFragment.class, false);
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
                    llKeyWordContainer.setVisibility(View.VISIBLE);
                    ivClearKey.setVisibility(View.INVISIBLE);
                    ivClearLine.setVisibility(View.INVISIBLE);
                } else {
                    ivClearLine.setVisibility(View.VISIBLE);
                    ivClearKey.setVisibility(View.VISIBLE);
                }
            }
        });
        etMusicSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    KeyBoardUtils.closeKeyBord(etMusicSearch, activity);
                    searchMusicByKeyWord(true);
                    return true;
                }
                return false;
            }
        });
    }

    private void requestData() {
        presenter.loadHistoryKeyword(activity);
        presenter.loadHotSearchKeyword();
    }

    @Override
    public void onClick(View view) {
        if (ClickUtils.isFastClick()) return;
        switch (view.getId()) {
            case R.id.ivBack:
                KeyBoardUtils.closeKeyBord(etMusicSearch, activity);
                if (llSearchContainer.getVisibility() == View.VISIBLE) {
                    llSearchContainer.setVisibility(View.GONE);
                    llKeyWordContainer.setVisibility(View.VISIBLE);
                    etMusicSearch.setText("");
                } else {
                    pop();
                }
                break;
            case R.id.tvClearHistory:
                presenter.saveHistoryKeyword(activity, null);
                break;
            case R.id.ivMusicSearch:
                searchMusicByKeyWord(true);
                break;
            case R.id.tvPlayAll:
                if (ListUtils.isNotEmpty(list)) {
                    mainFragment.getPlayFragment().setType(NetPlayListFragment.TYPE_SEARCH);

                    NetMainFragment mainFragment = (NetMainFragment) getParentFragment();
                    NetPlayFragment playFragment = mainFragment.getPlayFragment();
                    playFragment.bindData(list, currentPagInfo, 0);
                    popTo(NetPlayFragment.class, false);
                }
                break;
            case R.id.tvPreviousPage:
                loadMusicByPageNum(false);
                break;
            case R.id.tvNextPage:
                loadMusicByPageNum(true);
                break;
            case R.id.ivClear:
                etMusicSearch.setText("");
                break;
        }
    }

    private void loadMusicByPageNum(boolean isNext) {
        int currentPage = currentPagInfo.getCurrentPageNum();
        if (isNext) {
            if (currentPage >= currentPagInfo.getTotalPageNum()) {
                return;
            } else {
                int page = currentPage + 1;
                presenter.loadMusicByPageNum(page, NetSearchFragment.class.getName());
            }
        } else {
            if (currentPage <= 1) {
                return;
            } else {
                int page = currentPage - 1;
                presenter.loadMusicByPageNum(page, NetSearchFragment.class.getName());
            }
        }
    }

    private void searchMusicByKeyWord(boolean saveKey) {
        tvEmpty.setVisibility(View.GONE);
        String keyWord = etMusicSearch.getText().toString().trim();
        if (StringUtils.isEmpty(keyWord)) {
            ToastUtil.shortShow(activity, "请输入搜索关键字");
            return;
        }
        currentPagInfo.setCurrentPageNum(1);
        presenter.searchMusicByKeyWord(keyWord);
        if (saveKey)
            presenter.saveHistoryKeyword(activity, keyWord);
        KeyBoardUtils.closeKeyBord(etMusicSearch, activity);
    }

    @Override
    public void netRequestQueryId(String queryId) {
        ContextSyncManager.getInstant().setCurrentListQueryId(queryId);
    }

    @Override
    public void loadHistoryKeywordSuccess(List<SearchKeyWordBean> list) {
        addKeyWordView(flHistorySearch, list);
    }

    @Override
    public void loadHotSearchKeywordSuccess(List<SearchKeyWordBean> list) {
        addKeyWordView(flHotSearch, list);
    }

    @Override
    public void searchMusicByKeyWordSuccess(int total, List<SongModel> list) {
        currentPagInfo.setTotal(total);
        this.list = list;
        bindData(list);
    }

    @Override
    public void loadMusicByPageNumSuccess(int currentPage, List<SongModel> list, boolean isHeadPlay) {
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

    @Override
    public void showLoading() {
        llSearchContainer.setVisibility(View.VISIBLE);
        llKeyWordContainer.setVisibility(View.INVISIBLE);
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
        llKeyWordContainer.setVisibility(View.INVISIBLE);
        rvSearchList.setVisibility(View.VISIBLE);
        pbList.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onNetError(String errorMsg) {
        super.onNetError(errorMsg);
        llKeyWordContainer.setVisibility(View.VISIBLE);
    }

    private void addKeyWordView(FlexboxLayout flexboxLayout, List<SearchKeyWordBean> list) {
        flexboxLayout.removeAllViews();
        if (ListUtils.isNotEmpty(list)) {
            for (int i = 0; i < list.size(); i++) {
                View view = LayoutInflater.from(activity).inflate(R.layout.layout_item_search_keyword, null);
                View viewMargin = view.findViewById(R.id.viewMargin);
                viewMargin.setVisibility((list.size() > 1 && i < list.size() - 1) ? View.VISIBLE : View.GONE);
                final TextView searchKeyword = (TextView) view.findViewById(R.id.searchKeyword);
                searchKeyword.setText(list.get(i).getSearchName());
                searchKeyword.setTag(list.get(i));
                searchKeyword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SearchKeyWordBean bean = (SearchKeyWordBean) view.getTag();
                        if (bean != null) {
                            etMusicSearch.setText(bean.getSearchName());
                            etMusicSearch.setSelection(bean.getSearchName().length());
                            presenter.saveHistoryKeyword(activity, bean.getSearchName());

                            searchMusicByKeyWord(false);
                        }
                    }
                });
                flexboxLayout.addView(view);
            }
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
    public void vuiSearch(String searchKey) {
        etMusicSearch.setText(searchKey);
        searchMusicByKeyWord(true);
    }

    @Override
    public void vuiNextPage() {
        tvNextPage.performClick();
    }

    @Override
    public void vuiPreviousPage() {
        tvPreviousPage.performClick();
    }

    @Override
    public void vuiPlayIndex(int index) {
        if (mainFragment != null) {
            mainFragment.getPlayFragment().setType(NetPlayListFragment.TYPE_SEARCH);

            NetMainFragment mainFragment = (NetMainFragment) getParentFragment();
            NetPlayFragment playFragment = mainFragment.getPlayFragment();
            playFragment.bindData(list, currentPagInfo, index);
            popTo(NetPlayFragment.class, false);
        }
    }
}
