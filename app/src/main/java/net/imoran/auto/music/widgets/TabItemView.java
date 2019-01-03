package net.imoran.auto.music.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.imoran.auto.music.R;


/**
 * Created by Horizony on 2018/5/15.
 */

public class TabItemView extends FrameLayout {
    private Context context;
    ImageView ivUnSelect;
    TextView tvName;
    LinearLayout llUnSelect;
    ImageView ivSelect;
    FrameLayout rlSelect;

    public TabItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    public TabItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.view_tab, null);
        initView(rootView);
        addView(rootView);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TabItem);
        String text = ta.getString(R.styleable.TabItem_text);
        boolean select = ta.getBoolean(R.styleable.TabItem_select, false);
        int iconUnSelectResId = ta.getResourceId(R.styleable.TabItem_icon_un_select, Color.TRANSPARENT);
        int iconSelectResId = ta.getResourceId(R.styleable.TabItem_icon_select, Color.TRANSPARENT);
        ta.recycle();

        tvName.setText(text);
        ivUnSelect.setImageResource(iconUnSelectResId);
        ivSelect.setImageResource(iconSelectResId);
        setSelect(select);
    }

    private void initView(View rootView) {
        ivUnSelect = (ImageView) rootView.findViewById(R.id.iv_un_select);
        tvName = (TextView) rootView.findViewById(R.id.tv_name);
        llUnSelect = (LinearLayout) rootView.findViewById(R.id.ll_un_select);
        ivSelect = (ImageView) rootView.findViewById(R.id.iv_select);
        rlSelect = (FrameLayout) rootView.findViewById(R.id.rl_select);
    }

    public void setSelect(boolean isSelect) {
        if (isSelect) {
            rlSelect.setVisibility(VISIBLE);
            llUnSelect.setVisibility(INVISIBLE);
        } else {
            rlSelect.setVisibility(INVISIBLE);
            llUnSelect.setVisibility(VISIBLE);
        }
    }
}
