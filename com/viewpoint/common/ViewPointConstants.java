package com.viewpoint.common;

import java.io.File;

import com.letv.mobile.core.config.LeTVConfig;
import com.letv.mobile.player.highlight.viewpoint.ViewPointsModule;
import com.letv.mobile.player.highlight.viewpoint.util.MD5;
import com.letv.mobile.player.highlight.viewpoint.util.FileUtil;

public class ViewPointConstants {

    public static String CACHE_PATH;

    /**
     * 缩略图缓存路径
     * @param name
     * @return
     */
    public static final File getThumCacheFile(String name) {
        return new File(getViewPointCacheMkdirPathByVideoId(), name + ".png");
    }

    /**
     * 大图url生成本地文件路径
     * @param url
     * @return
     */
    public static final String getBigMapUrlCachePath(String url) {

        return getViewPointCacheMkdirPathByVideoId() + MD5.toMd5(url) + ".png";
    }

    /**
     * 视图点json url生成本地文件路径
     * @param url
     * @return
     */
    public static final String getJsonUrlCachePath(String url) {

        return getViewPointCacheMkdirPathByVideoId() + MD5.toMd5(url) + ".json";
    }

    /**
     * 判断url图片是否存在本地文件
     * @param url
     * @return
     */
    public static final boolean isExistsUrlImgOfCache(String url) {
        File file = new File(getBigMapUrlCachePath(url));
        return file.exists();
    }

    private static String sViewPointCacheMkdirPath = "";

    /**
     * 获取根据视频ID创建的缓存文件夹路径
     * @return
     */
    public static String getViewPointCacheMkdirPathByVideoId() {
        if (null == sViewPointCacheMkdirPath || "".equals(sViewPointCacheMkdirPath)) {
            // videoid错误情况使用系统默认文件夹
            sViewPointCacheMkdirPath = CACHE_PATH + "/videoIdGenerateProxy/";
        }
        return sViewPointCacheMkdirPath;
    }

    /**
     * 根据视频ID创建缓存文件夹
     * @param videoId
     */
    public static void createViewPointCacheMkdirByVideoId(String videoId) {
        File file = new File(CACHE_PATH + videoId);
        if (!file.exists()) {
            file.mkdir();
        }
        sViewPointCacheMkdirPath = file.getAbsolutePath() + "/";
    }

    public static void delViewPointCacheMkdirByVideoId(String videoId) {
        File file = new File(CACHE_PATH + videoId);
        FileUtil.deleteDir(file);
    }

}
