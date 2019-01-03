package net.imoran.auto.music.ui.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.imoran.auto.music.R;
import net.imoran.auto.music.app.MusicApp;
import net.imoran.auto.music.player.model.SongModel;
import net.imoran.auto.music.ui.adapter.base.RVHolder;
import net.imoran.auto.music.utils.DataConvertUtils;
import net.imoran.auto.music.widgets.SwipeMenuLayout;

import java.util.Formatter;
import java.util.Locale;


public class NetListAdapter extends BaseRecycleAdapter<SongModel, NetListAdapter.NetListViewHolder> {

    public NetListAdapter(Context mContext) {
        super(mContext);

    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, SongModel songModel);

        void onDeleteClick(int position, SongModel songModel);
    }

    @Override
    public void onBindViewHolder(final NetListViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        holder.llContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick(position);
                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(position, mDataList.get(position));
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onDeleteClick(position, mDataList.get(position));
                }
                holder.swipeMenuLayout.quickClose();
            }
        });
    }

    @Override
    public NetListViewHolder newViewHolder(ViewGroup parent, int viewType) {
        NetListViewHolder viewHolder = new NetListViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.adapter_item_net_play, parent, false));
        return viewHolder;
    }

    public void onItemPlay(SongModel song) {
        int index = -1;
        int i = 0;
        for (SongModel model : mDataList) {
            if (model.getUuid().equals(song.getUuid())) {
                index = i;
                break;
            }
            i++;
        }
        if (index >= 0) onItemClick(index);
    }

    public void onItemClick(int position) {
        int i = 0;
        for (SongModel model : mDataList) {
            if (i == position) {
                model.setPlay(true);
            } else {
                model.setPlay(false);
            }
            i++;
        }
        notifyDataSetChanged();
    }

    public static class NetListViewHolder extends RVHolder<SongModel> {
        private SwipeMenuLayout swipeMenuLayout;
        private final Button btnDelete;
        private final LinearLayout llContainer;
        private final TextView tvSongName;
        private final TextView tvSingerName;
        private final TextView tvSongTime;
        private final ImageView ivPlaying;
        private final TextView tvNum;
        private StringBuilder formatBuilder;
        private Formatter formatter;
        private Handler handler = new Handler();
        private int playColor, unPlayColor;

        public NetListViewHolder(View itemView) {
            super(itemView);
            swipeMenuLayout = (SwipeMenuLayout) itemView.findViewById(R.id.swipeMenuLayout);
            btnDelete = (Button) itemView.findViewById(R.id.btn_delete);
            llContainer = (LinearLayout) itemView.findViewById(R.id.ll_container);
            tvNum = (TextView) itemView.findViewById(R.id.tvNum);
            tvSongName = (TextView) itemView.findViewById(R.id.tvSongName);
            ivPlaying = (ImageView) itemView.findViewById(R.id.ivPlaying);
            tvSingerName = (TextView) itemView.findViewById(R.id.tvSingerName);
            tvSongTime = (TextView) itemView.findViewById(R.id.tvSongTime);

            playColor = MusicApp.instance.getResources().getColor(R.color.color_ffffff);
            unPlayColor = MusicApp.instance.getResources().getColor(R.color.color_99ffffff);
        }

        @Override
        public void bindData(final SongModel songModel, int position) {
            final AnimationDrawable playAnim = (AnimationDrawable) ivPlaying.getDrawable();
            if (songModel.isPlay()) {
                playAnim.setVisible(true, true);
                tvNum.setVisibility(View.GONE);
                ivPlaying.setVisibility(View.VISIBLE);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        playAnim.invalidateSelf();
                    }
                }, 200);
                playAnim.start();

                tvNum.setTextColor(playColor);
                tvSongName.setTextColor(playColor);
                tvSingerName.setTextColor(playColor);
                tvSongTime.setTextColor(playColor);
            } else {
                playAnim.setVisible(false, false);
                tvNum.setVisibility(View.VISIBLE);
                ivPlaying.setVisibility(View.GONE);
                playAnim.stop();

                tvNum.setTextColor(unPlayColor);
                tvSongName.setTextColor(unPlayColor);
                tvSingerName.setTextColor(unPlayColor);
                tvSongTime.setTextColor(unPlayColor);
            }
            if (songModel.isCollection()) {
                swipeMenuLayout.setSwipeEnable(true);
            } else {
                swipeMenuLayout.setSwipeEnable(false);
            }
            tvNum.setText("" + (position + 1));
            tvSongName.setText(songModel.getName());
            tvSingerName.setText(DataConvertUtils.getAlbumSinger(songModel));
            tvSongTime.setText(stringForTime(songModel.getDuration()));
        }

        private String stringForTime(long timeMs) {
            if (formatBuilder == null)
                formatBuilder = new StringBuilder();
            if (formatter == null)
                formatter = new Formatter(formatBuilder, Locale.getDefault());
            if (timeMs < 0)
                timeMs = 0;
            long totalSeconds = (timeMs + 500) / 1000;
            long seconds = totalSeconds % 60;
            long minutes = (totalSeconds / 60) % 60;
            long hours = totalSeconds / 3600;
            formatBuilder.setLength(0);
            return hours > 0 ? formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
                    : formatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }
}
