package net.imoran.auto.music.record;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AudioRecorder {
    private String TAG = "AudioRecorder";
    // 缓冲区字节大小
    private int bufferSizeInBytes = 0;
    //录音对象
    private AudioRecord audioRecord;
    //录音状态
    private Status status = Status.STATUS_NO_READY;
    //线程池
    private ExecutorService mExecutorService;
    //录音文件名称
    private String fileName;


    public AudioRecorder() {
        mExecutorService = Executors.newCachedThreadPool();
    }

    /**
     * 创建录音对象
     */
    public void createAudio() {
        // 获得缓冲区字节大小
        int frequency = 8000;
        int channelConfiguration = 1;
        int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
        bufferSizeInBytes = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration, audioEncoding, this.bufferSizeInBytes);
        status = Status.STATUS_READY;
    }


    /**
     * 开始录音
     */
    public void startRecord(final String fileName) {
        if (status == Status.STATUS_NO_READY || audioRecord == null) {
            Log.e(TAG, "录音尚未初始化,请检查是否禁止了录音权限~");
            return;
        }
        if (status == Status.STATUS_START) {
            Log.e(TAG, "正在录音");
        }
        Log.d(TAG, "startRecord" + audioRecord.getState());
        audioRecord.startRecording();
        //将录音状态设置成正在录音状态
        status = Status.STATUS_START;
        //使用线程池管理线程
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                writeDataTOFile(fileName);
            }
        });
    }

    /**
     * 暂停录音
     */
    public void pauseRecord() {
        Log.d(TAG, "pauseRecord");
        if (status != Status.STATUS_START) {
            Log.d(TAG, "没有在录音");
        } else {
            audioRecord.stop();
            status = Status.STATUS_PAUSE;
        }
    }

    /**
     * 停止录音
     */
    public void stopRecord() {
        Log.d(TAG, "stopRecord");
        if (status == Status.STATUS_NO_READY || status == Status.STATUS_READY) {
            Log.e(TAG, "录音尚未开始");
        } else {
            audioRecord.stop();
            status = Status.STATUS_STOP;
            release();
        }
    }

    public boolean isRecording() {
        if (status == Status.STATUS_START) return true;
        else return false;
    }

    /**
     * 释放资源
     */
    public void release() {
        Log.d(TAG, "release");
        if (audioRecord != null) {
            audioRecord.release();
            audioRecord = null;
        }
        status = Status.STATUS_NO_READY;
    }

    /**
     * 取消录音
     */
    public void cancel() {
        if (audioRecord != null) {
            audioRecord.release();
            audioRecord = null;
        }
        status = Status.STATUS_NO_READY;
    }


    /**
     * 将音频信息写入文件
     */
    private void writeDataTOFile(String fileName) {
        // new一个byte数组用来存一些字节数据，大小为缓冲区大小
        byte[] audiodata = new byte[bufferSizeInBytes];
        FileOutputStream fos = null;
        int readsize = 0;
        try {
            File file = new File(fileName);
            if (file.exists()) {
                file.delete();
            }
            fos = new FileOutputStream(file);// 建立一个可存取字节的文件
        } catch (IllegalStateException e) {
            Log.e(TAG, e.getMessage());
            throw new IllegalStateException(e.getMessage());
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        while (status == Status.STATUS_START) {
            readsize = audioRecord.read(audiodata, 0, bufferSizeInBytes);
            if (AudioRecord.ERROR_INVALID_OPERATION != readsize && fos != null) {
                try {
                    fos.write(audiodata);
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        try {
            if (fos != null) {
                fos.close();// 关闭写入流
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

}
