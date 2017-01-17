package com.viewpoint.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.letv.mobile.common.ICallback;
import com.letv.mobile.player.highlight.viewpoint.bean.ImgData;
import com.letv.mobile.player.highlight.viewpoint.common.ViewPointConstants;

public class NetImg {

    public static byte[] readImgArr(String imageUrl) throws Exception {
        InputStream inStream = getImageBytes(imageUrl);
        // FileUtil.saveFile(inStream, "/sdcard/videoPoint/bigImg.jpg");
        return readStream(inStream);
    }

    public static InputStream getImageBytes(String imageUrl) throws Exception {
        HttpGet httpRequest = new HttpGet(imageUrl);
        // 取得HttpClient 对象
        HttpClient httpclient = new DefaultHttpClient();
        try {
            // 请求httpClient ，取得HttpRestponse
            HttpResponse httpResponse = httpclient.execute(httpRequest);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 取得相关信息 取得HttpEntiy
                HttpEntity httpEntity = httpResponse.getEntity();
                // 获得一个输入流
                InputStream is = httpEntity.getContent();

                // byte[] data = readStream(is);
                // System.out.println(is.available());
                System.out.println("Get, Yes!");
                // Bitmap bitmap = BitmapFactory.decodeStream(is);
                // is.close();
                return is;

            } else {
                return null;
            }

        } catch (ClientProtocolException e) {

            e.printStackTrace();
            return null;
        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get image from newwork
     * @param path
     *            The path of image
     * @return byte[]
     * @throws Exception
     */
    public static byte[] getImage(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(20 * 1000);
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream inStream = conn.getInputStream();
            return readStream(inStream);
        }
        return null;
    }

    /**
     * Get image from newwork
     * @param path
     *            The path of image
     * @return InputStream
     * @throws Exception
     */
    public static InputStream getImageStream(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return conn.getInputStream();
        }
        return null;
    }

    /**
     * Get data from stream
     * @param inStream
     * @return byte[]
     * @throws Exception
     */
    public static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }

    /**
     * 获取网络图片（应在子线程中调用）
     * @param url
     * @param callback
     */
    public static void getNetImg(final String url,
            final ICallback<ImgData> callback) {

        if (ViewPointConstants.isExistsUrlImgOfCache(url)) {
            callback.hand(new ImgData(url));
            return;
        }

        byte[] data = null;

        try {

            data = readImgArr(url);
            if (data != null) {
                // context.runOnUiThread(uiRunnable);

                callback.hand(new ImgData(url, data));
            } else {
                callback.hand(null);
                // context.runOnUiThread(uiRunnable);
            }
        } catch (Exception e) {
            callback.hand(null);
            // context.runOnUiThread(uiRunnable);
            e.printStackTrace();
        }

    }

}
