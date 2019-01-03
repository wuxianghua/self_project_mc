package net.imoran.auto.music.utils;

import android.animation.BidirectionalTypeConverter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import net.imoran.auto.music.R;
import net.imoran.auto.music.app.GlideApp;

public class GlideUtils {

    public static void setImageView(Context context, String url, int width, int height, int defaultRes, ImageView imageView) {
        GlideApp.with(context).load(url).centerCrop().placeholder(defaultRes)
                .circleCrop().override(width, height).into(imageView);
    }

    public static void setImageView(Context context, String url, int defaultRes, ImageView imageView) {
        GlideApp.with(context).load(url).centerCrop().placeholder(defaultRes)
                .circleCrop().override(200, 200).into(imageView);
    }

    public static void setImageView(Context context, String url, ImageView imageView) {
        GlideApp.with(context).load(url).centerCrop().placeholder(R.drawable.bg_music_musicplay_default)
                .circleCrop().override(200, 200).into(imageView);
    }

    public static void setImageView(Context context, Drawable drawable, ImageView imageView) {
        GlideApp.with(context).load(drawable).centerCrop().placeholder(R.drawable.bg_music_musicplay_default)
                .circleCrop().override(200, 200).into(imageView);
    }

    public static void setImageView(Context context, Bitmap bmp, ImageView imageView) {
        GlideApp.with(context).load(bmp).centerCrop().placeholder(R.drawable.bg_music_musicplay_default)
                .circleCrop().override(200, 200).into(imageView);
    }
}
