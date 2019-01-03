package net.imoran.auto.music.widgets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import net.imoran.auto.music.R;

public class RadioStarView extends FrameLayout {
    private Context context;
    private ImageView ivRadioLike;
    private TextView tvRadioNum;

    public RadioStarView(@NonNull Context context) {
        super(context);
        this.context = context;
        init();
    }

    public RadioStarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public RadioStarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        View rootView = LayoutInflater.from(context).inflate(R.layout.view_radio_star, null);
        ivRadioLike = (ImageView) rootView.findViewById(R.id.ivRadioLike);
        tvRadioNum = (TextView) rootView.findViewById(R.id.tvRadioNum);
        addView(rootView);
    }

    public void setText(String text) {
        tvRadioNum.setText(text);
    }

    public void setSelected(boolean selected) {
        ivRadioLike.setSelected(selected);
    }

    public void setRadioLikeVisibility(int visibility) {
        ivRadioLike.setVisibility(visibility);
    }
}
