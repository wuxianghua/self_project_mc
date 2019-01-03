package net.imoran.auto.music.ui.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.imoran.auto.music.R;
import net.imoran.sdk.bean.bean.AudioProgramBean;

import java.util.List;

/**
 * Created by jingz on 2018/6/26.
 */

public class SoundProgramAdapter extends RecyclerView.Adapter<SoundProgramAdapter.SoundProgramHolder> {

    private List<AudioProgramBean.AudioProgramEntity> mSubCategoryEntity;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private boolean isPositiveOrder = true;

    public SoundProgramAdapter(Context context, List<AudioProgramBean.AudioProgramEntity> subCategoryArrayListEntities) {
        mSubCategoryEntity = subCategoryArrayListEntities;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    private SoundProgramAdapter.OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClick(AudioProgramBean.AudioProgramEntity contact, int postion);
    }

    int playPosition;

    public void setPlayPosition(int position) {
        playPosition = position;
    }

    int mPlayState;

    public void setPlayState(int playState) {
        mPlayState = playState;
    }

    public void setOnItemClickListener(SoundProgramAdapter.OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setOrder(boolean isOrder) {
        isPositiveOrder = isOrder;
    }

    public void startAnimation() {
        if (frameAnim != null) {
            frameAnim.invalidateSelf();
        }
    }

    @NonNull
    @Override
    public SoundProgramHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SoundProgramHolder(mLayoutInflater.inflate(R.layout.program_item, parent, false));
    }

    AnimationDrawable frameAnim;

    @Override
    public void onBindViewHolder(@NonNull SoundProgramHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(mSubCategoryEntity.get(position), (int) getItemId(position));
                }
            }
        });
        AudioProgramBean.AudioProgramEntity audioProgramEntity = mSubCategoryEntity.get(position);
        if (playPosition == getItemId(position)) {
            if (audioProgramEntity != null) {
                if (holder.id.getVisibility() != View.INVISIBLE) {
                    holder.id.setVisibility(View.INVISIBLE);
                }
                if (holder.programAnimation.getVisibility() != View.VISIBLE) {
                    holder.programAnimation.setVisibility(View.VISIBLE);
                }
                holder.programAnimation.setImageResource(R.drawable.tv_music_play_animation);
                frameAnim = (AnimationDrawable) holder.programAnimation.getDrawable();
                if (mPlayState == 1) {
                    frameAnim.start();
                } else {
                    frameAnim.stop();
                }
                holder.programDuration.setText(audioProgramEntity.getDuration());
                holder.programAuthor.setText(audioProgramEntity.getAuthor());
                holder.programName.setText(audioProgramEntity.getTrack_title());
                holder.programTime.setText(audioProgramEntity.getDate());
            }
        } else {
            if (audioProgramEntity != null) {
                holder.id.setText(getItemId(position) + 1 + "");
                if (holder.programAnimation.getVisibility() != View.INVISIBLE) {
                    holder.programAnimation.setVisibility(View.INVISIBLE);
                }
                if (holder.id.getVisibility() != View.VISIBLE) {
                    holder.id.setVisibility(View.VISIBLE);
                }
                holder.programDuration.setText(audioProgramEntity.getDuration());
                holder.programAuthor.setText(audioProgramEntity.getAuthor());
                holder.programName.setText(audioProgramEntity.getTrack_title());
                holder.programTime.setText(audioProgramEntity.getDate());
            }
        }
    }

    @Override
    public long getItemId(int i) {
        if (isPositiveOrder) {
            return i;
        } else {
            return mSubCategoryEntity.size() - i - 1;
        }
    }

    @Override
    public int getItemCount() {
        return mSubCategoryEntity.size();
    }

    public static class SoundProgramHolder extends RecyclerView.ViewHolder {
        private TextView id;
        private TextView programName;
        private TextView programAuthor;
        private TextView programTime;
        private TextView programDuration;
        private ImageView programAnimation;

        public SoundProgramHolder(View convertView) {
            super(convertView);
            id = (TextView) convertView.findViewById(R.id.subscribe_id);
            programName = (TextView) convertView.findViewById(R.id.subscribe_name);
            programAuthor = (TextView) convertView.findViewById(R.id.subscribe_author);
            programDuration = (TextView) convertView.findViewById(R.id.subscribe_qihao);
            programTime = (TextView) convertView.findViewById(R.id.subscribe_time);
            programAnimation = (ImageView) convertView.findViewById(R.id.subscribe_animation);
        }
    }
}
