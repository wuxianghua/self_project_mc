package net.imoran.auto.music.utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import net.imoran.auto.music.R;
import net.imoran.auto.music.bean.SongsBean;
import net.imoran.auto.music.player.model.SongModel;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocalMusicUtils {
    private static final String TAG = "MusicUtils";
    private List<SongModel> list;
    private Handler mHandler;
    private static LocalMusicUtils musicUtils;
    private final Context mContext;

    public static LocalMusicUtils getInstance(final Context mContext) {
        if (musicUtils == null) {
            synchronized (LocalMusicUtils.class) {
                if (musicUtils == null) {
                    musicUtils = new LocalMusicUtils(mContext);
                }
            }
        }
        return musicUtils;
    }

    public LocalMusicUtils(final Context mContext) {
        this.mContext = mContext.getApplicationContext();
        initHandlerThread();
        list = new ArrayList();
    }

    private void initHandlerThread() {
        HandlerThread handlerThread = new HandlerThread("MusicUtils");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        final List<String> paths = getMountedVolumePaths(mContext);
                        if (ListUtils.isNotEmpty(paths)) {
                            for (String path : paths) {
                                scanFile(path);
                            }
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mediaScannerListener.onScanCompleted(list);
                                }
                            });
                        }
                        break;
                    case 2:
                        break;

                }
                return false;
            }
        });
    }

    /**
     * 扫描系统里面的音频文件，返回一个list集合
     */
    public List<SongsBean> getMusicDataByDB(Context context) {
        List<SongsBean> list = new ArrayList();
        // 媒体库查询语句（写一个工具类MusicUtils）
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        int songId = 0;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                SongsBean songEntity = new SongsBean();
                songEntity.setMusic_id("localMusic_" + (songId++));
                songEntity.setSongsType("localMusic");
//                musicInfo.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE));
                if (!(type.contains("ape") || type.contains("flac"))) {
                    songEntity.setSong_url(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                    songEntity.setName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
                    ArrayList<String> singer = new ArrayList<String>();
                    singer.add(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                    songEntity.setSinger(singer);
                    long songSize = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                    if (songSize > 1000 * 800) {
                        // 注释部分是切割标题，分离出歌曲名和歌手 （本地媒体库读取的歌曲信息不规范）
//                    if (song.song.contains("-")) {
//                        String[] str = song.song.split("-");
//                        song.singer = str[0];
//                        song.song = str[1];
//                    }
                        list.add(songEntity);
                    }
                }
            }
            // 释放资源
            cursor.close();
        }
        return list;
    }

    private MediaScannerListener mediaScannerListener;

    public LocalMusicUtils setMediaScannerListener(MediaScannerListener mediaScannerListener) {
        this.mediaScannerListener = mediaScannerListener;
        return this;
    }

    public interface MediaScannerListener {
        void onScanCompleted(List<SongModel> songsBeanList);
    }

    /**
     * 自定义算法查找歌曲
     */
    public synchronized void startScanMusic() {
        Message message = Message.obtain();
        message.what = 1;
        mHandler.sendMessage(message);
    }


    public void scanFile(final String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            Log.w(TAG, "scanFile fileName empty!!!");
            return;
        }
        File f = new File(fileName);
        if (!f.exists()) {
            Log.w(TAG, "scanFile this file does not exist!!!");
            return;
        }

        if (f.isDirectory()) {
            //过滤隐藏的文件
            File[] filesInThisDir = f.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return !pathname.isHidden();
                }
            });
            if ((filesInThisDir != null)
                    && (filesInThisDir.length > 0)) {
                for (File file : filesInThisDir) {
                    String filePath = file.getAbsolutePath();
                    Log.i(TAG, "scanFile directory file:getAbsolutePath=" + filePath);
                    scanFile(file.getAbsolutePath());
                }
            }
            Log.i(TAG, "scanFile it's a directory,now scan its files done");
        } else {
            if (fileName.endsWith(".mp3") || fileName.endsWith(".wav")) {
                SongModel songModel = null;
                try {
                    songModel = getMusicDataByScan(fileName);
                    if (songModel != null) {
                        list.add(songModel);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 扫描系统里面的音频文件，返回一个list集合
     */
    private SongModel getMusicDataByScan(String songUrl) {
        File file = new File(songUrl);
        SongModel songModel = new SongModel();
        songModel.setUuid("localMusic_" + (list.size() + 1));
        songModel.setType("localMusic");
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(songUrl);
        String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        // 专辑名
        String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        // 媒体格式
        String mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
        // 艺术家
        String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        // 播放时长单位为毫秒
//        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long duration = Long.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
//        long fileSize = file.getFile().length();
//        long bitRate = Long.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE));
//        long duration = (fileSize*8) /(bitRate);
        // 从api level 14才有，即从ICS4.0才有此功能
        String bitrate = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
        // 路径
        String date = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
        //专辑封面图
        Bitmap bitmap = null;
        byte[] embeddedPicture = mmr.getEmbeddedPicture();
        if (embeddedPicture != null && embeddedPicture.length > 0) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.outHeight = 200;
            options.outWidth = 200;
            options.inSampleSize = 4;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            bitmap = BitmapFactory.decodeByteArray(embeddedPicture, 0, embeddedPicture.length);
        }
        songModel.setSongUrl(songUrl);
        songModel.setDuration(duration);
        songModel.setAlbum(album);
        songModel.setName((title == null || title.equals("")) ? file.getName() : title);
        songModel.setBmp(bitmap);
        ArrayList<String> singer = new ArrayList<>();
        if (artist == null) {
            artist = "";
        }
        singer.add(artist);
        songModel.setSinger(singer);
        if (file.length() > 1000 * 800) {
            return songModel;
        }
        return null;
    }

    private Bitmap getAlbumArt(int album_id) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = mContext.getContentResolver().query(Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)), projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        Bitmap bm = null;
        if (album_art != null) {
            bm = BitmapFactory.decodeFile(album_art);
        } else {
            bm = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bg_music_musicplay_default);
        }
        return bm;
    }

    /**
     * 获取系统中已经挂载的所有存储设备路径
     */
    private List<String> getMountedVolumePaths(Context mContext) {
        ArrayList<String> mountedVolumes = new ArrayList<String>();
        List<String> volumes = getVolumePaths(mContext);
        for (String volume : volumes) {
            if (getVolumeState(mContext, volume).equals("mounted")) {
                mountedVolumes.add(volume);
            }
        }
        return mountedVolumes;
    }

    /**
     * 获取系统中的存储设备路径
     */
    private List<String> getVolumePaths(Context mContext) {
        boolean bException = false;
        StorageManager mStorageManager;
        Method mMethod;
        try {
            mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
            mMethod = mStorageManager.getClass().getMethod("getVolumePaths");
            return Arrays.asList((String[]) mMethod.invoke(mStorageManager));
        } catch (NoSuchMethodException e) {
            bException = true;
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            bException = true;
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            bException = true;
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            bException = true;
            e.printStackTrace();
        } finally {
            if (bException) {
                return new ArrayList<String>();
            }
        }
        return new ArrayList<String>();
    }

    /**
     * 获取存储设备的挂载状态
     */
    private String getVolumeState(Context mContext, String mountPoint) {
        boolean bException = false;
        StorageManager storageManager = (StorageManager) mContext.getSystemService(Activity.STORAGE_SERVICE);
        try {
            Class<?> pTypes = Class.forName("java.lang.String");
            return (String) storageManager.getClass().getMethod("getVolumeState", pTypes).invoke(storageManager, mountPoint);
        } catch (NoSuchMethodException e) {
            bException = true;
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            bException = true;
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            bException = true;
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            bException = true;
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            bException = true;
            e.printStackTrace();
        } finally {
            if (bException) {
                return "";
            }
        }
        return "";
    }


}
