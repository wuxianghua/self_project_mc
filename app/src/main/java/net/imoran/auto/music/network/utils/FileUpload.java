package net.imoran.auto.music.network.utils;

import android.os.Message;

import net.imoran.tv.common.lib.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FileUpload extends Thread {
    private String filePath, urlStr;


    public FileUpload(String urlStr, String filePath) {
        this.urlStr = urlStr;
        this.filePath = filePath;
    }

    @Override
    public void run() {
        try {
            URL httpUrl = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-type", "audio/pcm");
            connection.setRequestProperty("Connection", "close");
            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
            FileInputStream fStream = new FileInputStream(filePath);
            if (fStream != null) {
                byte[] b = new byte[1024];
                int len;
                while ((len = fStream.read(b)) != -1) {
                    dos.write(b, 0, len);
                }
            }
            dos.flush();
            StringBuffer sb = new StringBuffer();
            if (connection.getResponseCode() == 200) {//请求成功
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                Message msg = Message.obtain();
                JSONObject object = new JSONObject(sb.toString());
                LogUtils.e("FileUpload", sb.toString());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
