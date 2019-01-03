package net.imoran.auto.music.network.utils;

import android.os.Handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.config.RequestConfig;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.ContentProducer;
import cz.msebera.android.httpclient.entity.EntityTemplate;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;

public class HttpClientUpload {
    private static ExecutorService service = Executors.newCachedThreadPool();
    private HttpConnectionTool.OnUploadFileListener listener;
    private Handler handler = new Handler();

    public interface OnUploadFileListener {
        void onSuccess(String s);

        void onError();
    }

    public void uploadPcmFile(final String url, final String filePath, final OnUploadFileListener listener) {
        service.execute(new Runnable() {
            @Override
            public void run() {
                uploadFile(url, filePath, listener);
            }
        });
    }

    private void uploadFile(final String url, final String filePath, final OnUploadFileListener listener) {
        ContentProducer cp = new ContentProducer() {
            public void writeTo(OutputStream outstream) throws IOException {
                File tempFile = new File(filePath);
                byte[] buffer = new byte[(int) tempFile.length()];
                FileInputStream fin = null;
                int bufferLen = 0;
                try {
                    fin = new FileInputStream(tempFile);
                    bufferLen = fin.read(buffer, 0, buffer.length);
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null)
                                listener.onError();
                        }
                    });
                } finally {
                    try {
                        if (fin != null) {
                            fin.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                outstream.write(buffer);
                outstream.flush();
            }
        };
        try {
            HttpEntity requestEntity = new EntityTemplate(cp);
            HttpClient httpClient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(requestEntity);
            // set Timeout
            RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(5000).setConnectTimeout(5000).setSocketTimeout(5000).build();
            httppost.setConfig(requestConfig);
            // get responce
            HttpResponse responce = httpClient.execute(httppost);
            // get http status code
            int resStatu = responce.getStatusLine().getStatusCode();
            if (resStatu == HttpStatus.SC_OK) {
                HttpEntity responseEntity = responce.getEntity();
                final String reStr = EntityUtils.toString(responseEntity);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null)
                            listener.onSuccess(reStr);
                    }
                });
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null)
                            listener.onError();
                    }
                });
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onError();
                }
            });
        }
        ;
    }
}
