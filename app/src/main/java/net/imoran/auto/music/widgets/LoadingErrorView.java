package net.imoran.auto.music.widgets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import net.imoran.auto.music.R;

public class LoadingErrorView extends FrameLayout {
    private Context context;
    private ProgressBar pbList;
    private LinearLayout llRefresh;
    private onRefreshListener refreshListener;

    public interface onRefreshListener {
        void onRefresh();
    }

    public void setRefreshListener(onRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    public LoadingErrorView(@NonNull Context context) {
        super(context);
        this.context = context;
        init();
    }

    public LoadingErrorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public LoadingErrorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        View rootView = LayoutInflater.from(context).inflate(R.layout.view_loading_error, null);
        pbList = (ProgressBar) rootView.findViewById(R.id.pbList);
        llRefresh = (LinearLayout) rootView.findViewById(R.id.llRefresh);
        llRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (refreshListener != null)
                    refreshListener.onRefresh();
            }
        });
        addView(rootView);
    }

    public void showErrorView() {
        setVisibility(VISIBLE);
        pbList.setVisibility(GONE);
        llRefresh.setVisibility(VISIBLE);
    }

    public void showLoadingView() {
        setVisibility(VISIBLE);
        pbList.setVisibility(VISIBLE);
        llRefresh.setVisibility(GONE);
    }

    public void hideLoadingView() {
        setVisibility(GONE);
        pbList.setVisibility(VISIBLE);
        llRefresh.setVisibility(GONE);
    }
}
