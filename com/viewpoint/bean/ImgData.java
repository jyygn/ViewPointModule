package com.viewpoint.bean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.letv.mobile.player.highlight.viewpoint.common.ViewPointConstants;
import com.letv.mobile.player.highlight.viewpoint.util.FileUtil;

public class ImgData {

    private String mUrl;
    private String mPath;
    private byte[] mData;

    public ImgData(String url, byte[] data) {
        super();
        this.mUrl = url;
        this.mData = data;
    }

    public ImgData(String url) {
        super();
        this.mUrl = url;
        this.mPath = ViewPointConstants.getBigMapUrlCachePath(url);
    }

    public Bitmap getBitmap() throws FileNotFoundException, IOException {
        if (null == this.mData) {
            if (null == this.mPath) {
                return null;
            }
            File file = new File(this.mPath);
            if (file.exists()) {
                try {
                    System.gc();
                    return FileUtil.getBitmap(this.mPath);
                } catch (OutOfMemoryError error) {
                    System.gc();
                    return FileUtil.getBitmap(this.mPath);
                }
            } else {
                return null;
            }
        } else {
            return BitmapFactory.decodeByteArray(this.mData, 0, this.mData.length);
        }

    }

    public void outputFile() {
        if (null == this.mData) {
            return;
        }
        try {
            String path = getPath(this.mUrl);
            FileUtil.convertBytesToFile(this.mData, path);
            this.mPath = path;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getPath(String url) {
        return ViewPointConstants.getBigMapUrlCachePath(url);

    }

    public String getPath() {
        return mPath;
    }

    public void close() {
        this.mData = null;
    }

    public String getUrl() {
        return this.mUrl;
    }

}
