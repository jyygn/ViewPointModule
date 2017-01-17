package com.viewpoint.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.letv.mobile.common.ICallback;
import com.letv.mobile.core.utils.NetworkUtil;
import com.letv.mobile.player.highlight.viewpoint.common.ViewPointConstants;
import com.letv.mobile.player.highlight.viewpoint.core.IViewPointsInfoReader;
import com.letv.mobile.player.highlight.viewpoint.util.FileUtil;
import com.letv.mobile.player.highlight.viewpoint.util.NetImg;

/**
 * 视图点信息描述阅读器
 * 检查本地再网络获取
 * @author yangn
 */
public class ViewPointsInfoReader implements IViewPointsInfoReader {

    /**
     * 检查url表示的文件是否存在，若不存在则到网络上下载
     * @param url
     * @param callback
     *            若存在返回本地路径否则返回null
     * @throws Exception
     */
    public void checkAndDownload(final String url,
            final ICallback<String> callback) throws Exception {
        if (null == url) {
            throw new NullPointerException("params url is null");
        }
        final String localPath = ViewPointConstants.getJsonUrlCachePath(url);
        final File jsonFile = new File(localPath);

        if (jsonFile.exists()) {
            if (null != callback) {
                callback.hand(jsonFile.getAbsolutePath());
            }
            return;
        }

        // 非wifi情况不下载视点图

        if (NetworkUtil.getNetworkType() != NetworkUtil.NETWORK_TYPE_WIFI) {
            if (null != callback) {
                callback.hand(null);
            }
            return;
        }

        new Thread() {
            @Override
            public void run() {
                InputStream is = null;
                try {
                    is = NetImg.getImageBytes(url);
                    if (null == is) {
                        throw new Exception("net img stream is null");
                    }

                    FileUtil.saveFile(is, jsonFile.getAbsolutePath());
                    if (null != callback) {
                        callback.hand(jsonFile.getAbsolutePath());
                    }

                } catch (Exception e) {
                    if (null != callback) {
                        callback.hand(null);
                    }
                    e.printStackTrace();
                } finally {
                    if (null != is) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }.start();

    }

    /**
     * 读视图点JSON文件内容（文本文件）
     * @param url
     * @param callback
     * @throws Exception
     */
    @Override
    public void readFileText(String url, final ICallback<String> readCallback)
            throws Exception {
        if (null == url) {
            throw new NullPointerException("params url is null");
        }

        this.checkAndDownload(url, new ICallback<String>() {

            @Override
            public void hand(String result) {
                if (result != null) {
                    if (null != readCallback) {

                        try {
                            String content = FileUtil.converFileToStr(result);
                            readCallback.hand(content);
                        } catch (FileNotFoundException e) {
                            if (null != readCallback) {
                                readCallback.hand(null);
                            }
                            e.printStackTrace();
                        } catch (Exception e) {
                            if (null != readCallback) {
                                readCallback.hand(null);
                            }
                            e.printStackTrace();
                        }

                    }
                } else {
                    if (null != readCallback) {
                        readCallback.hand(null);
                    }
                }

            }
        });
    }

}
