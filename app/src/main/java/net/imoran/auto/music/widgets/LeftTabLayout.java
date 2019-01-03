package net.imoran.auto.music.widgets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import net.imoran.auto.music.R;

public class LeftTabLayout extends FrameLayout implements View.OnClickListener {
    private Context context;
    private TabItemView tabNet;
    private TabItemView tabRadio;
    private TabItemView tabSound;
    private TabItemView tabLocal;
    private TabItemView tabBlue;

    private onItemSelectListener listener;

    public void switchToLocalFragment() {
        tabLocal.performClick();
    }

    public interface onItemSelectListener {
        void onSelect(int index);
    }

    public void setOnItemSelectListener(onItemSelectListener listener) {
        this.listener = listener;
    }

    public LeftTabLayout(@NonNull Context context) {
        super(context);
        this.context = context;
        init();
    }

    public LeftTabLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public LeftTabLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        View rootView = LayoutInflater.from(context).inflate(R.layout.view_left_tab, null);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(120, -1);
        addView(rootView, params);

        tabNet = (TabItemView) rootView.findViewById(R.id.tabNet);
        tabRadio = (TabItemView) rootView.findViewById(R.id.tabRadio);
        tabSound = (TabItemView) rootView.findViewById(R.id.tabSound);
        tabLocal = (TabItemView) rootView.findViewById(R.id.tabLocal);
        tabBlue = (TabItemView) rootView.findViewById(R.id.tabBlue);
        tabNet.setOnClickListener(this);
        tabRadio.setOnClickListener(this);
        tabSound.setOnClickListener(this);
        tabLocal.setOnClickListener(this);
        tabBlue.setOnClickListener(this);
    }

    public void switchToSoundToFragment() {
        tabSound.performClick();
    }

    public void switchToNetToFragment() {
        tabNet.performClick();
    }

    public void switchToRadioFragment() {
        tabRadio.performClick();
    }

    public void switchToBlueToothFragment() {
        tabBlue.performClick();
    }

    public void setRadioSelect() {
        tabNet.setSelect(false);
        tabRadio.setSelect(true);
        tabSound.setSelect(false);
        tabLocal.setSelect(false);
        tabBlue.setSelect(false);
    }

    public void setSoundSelect() {
        tabNet.setSelect(false);
        tabRadio.setSelect(false);
        tabSound.setSelect(true);
        tabLocal.setSelect(false);
        tabBlue.setSelect(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tabNet:
                tabNet.setSelect(true);
                tabRadio.setSelect(false);
                tabSound.setSelect(false);
                tabLocal.setSelect(false);
                tabBlue.setSelect(false);
                if (listener != null) listener.onSelect(0);
                break;
            case R.id.tabRadio:
                tabNet.setSelect(false);
                tabRadio.setSelect(true);
                tabSound.setSelect(false);
                tabLocal.setSelect(false);
                tabBlue.setSelect(false);
                if (listener != null) listener.onSelect(1);
                break;
            case R.id.tabSound:
                tabNet.setSelect(false);
                tabRadio.setSelect(false);
                tabSound.setSelect(true);
                tabLocal.setSelect(false);
                tabBlue.setSelect(false);
                if (listener != null) listener.onSelect(2);
                break;
            case R.id.tabLocal:
                tabNet.setSelect(false);
                tabRadio.setSelect(false);
                tabSound.setSelect(false);
                tabLocal.setSelect(true);
                tabBlue.setSelect(false);
                if (listener != null) listener.onSelect(3);
                break;
            case R.id.tabBlue:
                tabNet.setSelect(false);
                tabRadio.setSelect(false);
                tabSound.setSelect(false);
                tabLocal.setSelect(false);
                tabBlue.setSelect(true);
                if (listener != null) listener.onSelect(4);
                break;
        }
    }
}
