package net.imoran.auto.music.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import net.imoran.auto.music.utils.ListUtils;

import net.imoran.auto.music.R;
import net.imoran.auto.music.player.manager.MusicPlayMangerImp;
import net.imoran.auto.music.player.model.SongModel;
import net.imoran.auto.music.utils.ClickUtils;
import net.imoran.auto.music.utils.GlideUtils;
import net.imoran.auto.music.utils.StringUtils;
import net.imoran.auto.music.widgets.progress.CircleProgress;

import java.util.ArrayList;
import java.util.List;

import am.util.viewpager.adapter.RecyclePagerAdapter;

public class PlayViewPagerAdapter extends RecyclePagerAdapter<PlayViewPagerAdapter.MorPagerViewHolder> {
    private List<SongModel> soundList = new ArrayList<>();
    private MorPagerViewHolder[] holderList = new MorPagerViewHolder[100];
    private Context context;
    private UpdatePlayProgressListener mUpdatePlayProgressListener;

    public PlayViewPagerAdapter(Context context, List<SongModel> list) {
        this.context = context;
        if (ListUtils.isNotEmpty(list)) {
            soundList.addAll(list);
        }
    }

    @Override
    public int getItemCount() {
        return soundList.size();
    }

    @Override
    public MorPagerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MorPagerViewHolder viewHolder = new MorPagerViewHolder(parent);
        return viewHolder;
    }

    public void setUpdatePlayProgressListener(UpdatePlayProgressListener updatePlayProgressListener) {
        mUpdatePlayProgressListener = updatePlayProgressListener;
    }

    public interface UpdatePlayProgressListener {
        void updatePlayProgress(long progress);
    }

    @Override
    public void onBindViewHolder(MorPagerViewHolder holder, int position) {
        //处理不同页面的不同数据
        SongModel songModel = soundList.get(position);
        holder.bindData(context, songModel);
        holderList[position] = holder;
    }

    @Override
    public int getItemViewType(int position) {
        //设置不同类型的页面。
        return 0;
    }

    @Override
    public void onViewRecycled(MorPagerViewHolder holder) {
        //当ViewPager执行destroyItem时，会回收Holder,此时会调用该方法，你可以重写该方法实现你要的效果
        holder.ivMusicCover.setImageResource(R.drawable.ic_music_musicplay_played);
        holder.progressView.setValue(0.01F);
    }

    public MorPagerViewHolder getViewHolder(int position) {
        int index = (position >= holderList.length - 1) ? holderList.length - 1 : position;
        return holderList[index < 0 ? 0 : index];
    }


    public void update(List<SongModel> list) {
        if (ListUtils.isNotEmpty(list)) {
            soundList.clear();
            soundList.addAll(list);
            soundList.add(new SongModel());
            notifyDataSetChanged();
        }
    }

    public void update(List<SongModel> list, boolean addHeader, boolean addFooter) {
        if (ListUtils.isNotEmpty(list)) {
            soundList.clear();
            soundList.addAll(list);
            if (addHeader)
                soundList.add(0, new SongModel());
            if (addFooter)
                soundList.add(new SongModel());
            notifyDataSetChanged();
        }
    }

    public class MorPagerViewHolder extends RecyclePagerAdapter.PagerViewHolder {
        private ImageView ivMusicCover;
        private CircleProgress progressView;
        private ImageView ivPlayState;
        private ProgressBar pbBuffer;

        public MorPagerViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_pager_item, parent, false));
            ivMusicCover = (ImageView) itemView.findViewById(R.id.ivMusicCover);
            progressView = (CircleProgress) itemView.findViewById(R.id.progressView);
            ivPlayState = (ImageView) itemView.findViewById(R.id.ivPlayState);
            pbBuffer = (ProgressBar) itemView.findViewById(R.id.pbBuffer);
            ivPlayState.setTag(0);
        }

        //应用到页面上的数据
        public void bindData(Context context, SongModel songModel) {
            if (!"localMusic".equals(songModel.getType())) {
                if (StringUtils.isNotEmpty(songModel.getPicUrl()))
                    GlideUtils.setImageView(context, songModel.getPicUrl(), ivMusicCover);
                else
                    GlideUtils.setImageView(context, context.getResources().getDrawable(R.drawable.bg_music_musicplay_default), ivMusicCover);
            } else {
                Bitmap bmp = songModel.getBmp();
                if (bmp != null)
                    GlideUtils.setImageView(context, bmp, ivMusicCover);
            }

            progressView.setValue(0.01F);
            progressView.setVisibility(View.VISIBLE);
            ivPlayState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ClickUtils.isFastClick()) return;
                    int resId = (int) view.getTag();
                    if (resId == R.drawable.ic_music_musicplay_load) return;
                    if (MusicPlayMangerImp.getInstance().isPlaying()) {
                        MusicPlayMangerImp.getInstance().pause();
                        long progress = MusicPlayMangerImp.getInstance().getPlayPosition();
                        mUpdatePlayProgressListener.updatePlayProgress(progress);
                    } else {
                        MusicPlayMangerImp.getInstance().play();
                    }
                }
            });
        }

        public void setPlayProgress(float progress) {
            if (progressView == null) return;
            if (progress >= 100F) {
                progressView.setValue(100 * 100);
            } else {
                progressView.setValue(progress * 100);
            }
        }

        public void setPlayStateIcon(int resId) {
            ivPlayState.setTag(resId);
            if (resId == R.drawable.ic_music_musicplay_load) {
                ivPlayState.setVisibility(View.INVISIBLE);
                pbBuffer.setVisibility(View.VISIBLE);
            } else {
                pbBuffer.setVisibility(View.INVISIBLE);
                ivPlayState.setVisibility(View.VISIBLE);
                ivPlayState.setImageResource(resId);
            }
        }

        public void setPlayPause() {
            ivPlayState.performClick();
        }
    }

    private Bitmap loadingCover(String mediaUri) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(mediaUri);
        byte[] picture = mediaMetadataRetriever.getEmbeddedPicture();
        Bitmap bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
        return bitmap;
    }
}
