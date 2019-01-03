package net.imoran.auto.music.ui.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.imoran.auto.music.R;
import net.imoran.auto.music.app.GlideApp;
import net.imoran.auto.music.player.model.SongModel;
import net.imoran.auto.music.widgets.SwipeMenuLayout;
import net.imoran.sdk.bean.bean.AudioProgramBean;

import java.util.List;

/**
 * Created by jingz on 2018/6/26.
 */

public class SoundCollectionAdapter extends RecyclerView.Adapter<SoundCollectionAdapter.AudioProgramHolder> {

    private List<AudioProgramBean.AudioProgramEntity> mSubCategoryEntity;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private boolean isPositiveOrder = true;

    public SoundCollectionAdapter(Context context, List<AudioProgramBean.AudioProgramEntity> subCategoryArrayListEntities) {
        mSubCategoryEntity = subCategoryArrayListEntities;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    private SoundCollectionAdapter.OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClick(AudioProgramBean.AudioProgramEntity contact, int postion);

        void onDeleteClick(int position, AudioProgramBean.AudioProgramEntity audioProgramEntity,boolean isRemoveAll);
    }

    int playPosition;
    public void setPlayPosition(int position) {
        playPosition = position;
    }

    int mPlayState;
    public void setPlayState(int playState) {
        mPlayState = playState;
    }

    public void setOnItemClickListener(SoundCollectionAdapter.OnItemClickListener itemClickListener) {
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
    public AudioProgramHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SoundCollectionAdapter.AudioProgramHolder(mLayoutInflater.inflate(R.layout.collections_item, parent, false));
    }

    AnimationDrawable frameAnim;
    @Override
    public void onBindViewHolder(@NonNull final AudioProgramHolder holder, final int position) {
        holder.rlContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(mSubCategoryEntity.get(position),(int)getItemId(position));
                }
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (itemClickListener != null) {
                    if (mSubCategoryEntity.size() == 1) {
                        itemClickListener.onDeleteClick(position, mSubCategoryEntity.get(position),true);
                    }else {
                        itemClickListener.onDeleteClick(position, mSubCategoryEntity.get(position),false);
                    }

                }
                mSubCategoryEntity.remove(position);
                notifyDataSetChanged();
                holder.swipeMenuLayout.quickClose();
            }
        });
        AudioProgramBean.AudioProgramEntity audioProgramEntity = mSubCategoryEntity.get(position);
        holder.swipeMenuLayout.setSwipeEnable(true);
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
                }else {
                    frameAnim.stop();
                }
                holder.programDuration.setText(audioProgramEntity.getDuration());
                holder.programAuthor.setText(audioProgramEntity.getAuthor());
                holder.programName.setText(audioProgramEntity.getTrack_title());
                holder.programTime.setText(audioProgramEntity.getDate());
                GlideApp.with(mContext)
                        .load(audioProgramEntity.getCover_url())
                        .override(80, 80)
                        .thumbnail(0.4f)
                        .circleCrop()
                        .into(holder.programImage);
            }
        }else {
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
                GlideApp.with(mContext)
                        .load(audioProgramEntity.getCover_url())
                        .override(80, 80)
                        .thumbnail(0.4f)
                        .circleCrop()
                        .into(holder.programImage);
            }
        }
    }

    @Override
    public long getItemId(int i) {
        if (isPositiveOrder) {
            return i;
        }else {
            return mSubCategoryEntity.size() - i - 1;
        }
    }

    @Override
    public int getItemCount() {
        return mSubCategoryEntity.size();
    }

    public static class AudioProgramHolder extends RecyclerView.ViewHolder {
        private SwipeMenuLayout swipeMenuLayout;
        private  Button btnDelete;
        private RelativeLayout rlContainer;
        private TextView id;
        private TextView programName;
        private TextView programAuthor;
        private TextView programTime;
        private TextView programDuration;
        private ImageView programAnimation;
        private ImageView programImage;

        public AudioProgramHolder(View convertView) {
            super(convertView);
            swipeMenuLayout = (SwipeMenuLayout) convertView.findViewById(R.id.swipeMenuLayout);
            btnDelete = (Button) convertView.findViewById(R.id.btn_delete);
            id = (TextView) convertView.findViewById(R.id.collection_id);
            programImage = (ImageView) convertView.findViewById(R.id.collection_img);
            programName = (TextView) convertView.findViewById(R.id.collection_name);
            programAuthor = (TextView) convertView.findViewById(R.id.collection_author);
            programDuration = (TextView) convertView.findViewById(R.id.collection_qihao);
            programTime = (TextView) convertView.findViewById(R.id.collection_time);
            programAnimation = (ImageView) convertView.findViewById(R.id.collection_animation);
            rlContainer = (RelativeLayout) convertView.findViewById(R.id.collection_container);
        }
    }
}
