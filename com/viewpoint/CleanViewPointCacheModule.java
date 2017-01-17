package com.viewpoint;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.letv.mobile.core.log.Logger;
import com.letv.mobile.core.utils.ParseUtil;
import com.letv.mobile.core.utils.StringUtils;
import com.letv.mobile.player.highlight.viewpoint.common.ViewPointConstants;
import com.letv.mobile.player.highlight.viewpoint.util.FileUtil;

/**
 * 清除视点图缓存业务
 * 缓存超过MAX_CACHE_SIZE个视点图时则进行删除 若正在进行删除时进行切换播放的视频将不被删除
 * @author yangn
 */
public class CleanViewPointCacheModule {

    private static final String TAG = "DelViewPointCacheModel";

    /**
     * 视点图最大缓存数
     */
    private static int sMaxCacheSize = 3;

    // 删除操作执行状态
    private static volatile byte mDelExecState = 0;
    private static volatile Set<String> mDelIgnoreDirSet = Collections
            .synchronizedSet(new HashSet<String>());

    /**
     * 正在进行删除操作
     */
    private static final byte DEL_EXEC_ING = 1;
    /**
     * 已完成或没有执行删除操作
     */
    private static final byte DEL_EXEC_OVER_OR_NONE = 0;

    public static void init(int maxCacheSize) {
        sMaxCacheSize = maxCacheSize;
    }



    /**
     * 删除视点图缓存目录
     */
    public static void delViewPointCacheMkdir(String ignoreVideoId) {
        if (StringUtils.equalsNull(ignoreVideoId)) {
            return;
        }
        Logger.d(TAG, "add ignore set  " + ignoreVideoId);
        mDelIgnoreDirSet.add(ignoreVideoId);
        if (DEL_EXEC_ING == mDelExecState) {
            Logger.d(TAG, "DEL_EXEC_ING");
            return;
        } else {
            synchronized (CleanViewPointCacheModule.class) {
                if (DEL_EXEC_OVER_OR_NONE == mDelExecState) {
                    Logger.d(TAG, "DEL_EXEC_OVER_OR_NONE");
                    mDelExecState = DEL_EXEC_ING;
                    getDelViewPointCacheMkdirThread().start();
                } else {
                    Logger.d(TAG, "DEL_EXEC_ING");
                }
            }
        }

    }

    private static Thread getDelViewPointCacheMkdirThread() {

        return new Thread() {

            /*
             * (non-Javadoc)
             * @see java.lang.Thread#run()z
             */
            @Override
            public void run() {
                super.run();

                Logger.d(TAG, "start exec  del dir ....");
                long start = System.currentTimeMillis();
                File file = new File(ViewPointConstants.CACHE_PATH);

                String childDirName = null;

                if (!file.isDirectory()) {
                    noneState();
                    return;
                }

                File[] childListFiles = file.listFiles();
                if (null == childListFiles) {
                    noneState();
                    return;
                }

                int len = childListFiles.length;

                if (len < sMaxCacheSize) {
                    Logger.d(TAG, "len < MAX_CACHE_SIZE len: " + len + " max: " + sMaxCacheSize);
                    noneState();
                    return;
                }
                Logger.d(TAG, "real exec delete ing...");

                // 进行清空操作
                for (int i = 0; i < len; ++i) {

                    childDirName = childListFiles[i].getName();

                    if (isDelIgnoreDir(childDirName)) {
                        Logger.d(TAG, "ignore child dir name " + childDirName);
                        continue;
                    }

                    FileUtil.deleteDir(childListFiles[i]);
                }

                Logger.d(TAG, "use time" + (System.currentTimeMillis() - start));
                Iterator<String> delIgnoreDirSetIterator = mDelIgnoreDirSet.iterator();
                while (delIgnoreDirSetIterator.hasNext()) {
                    Logger.d(TAG, "ignore del dir id =" + delIgnoreDirSetIterator.next());
                }
                noneState();
                Logger.d(TAG, "end exec  del dir ....");

            }

        };
    }

    /**
     * 设置为可被执行删除任务状态
     */
    static void noneState() {
        mDelIgnoreDirSet.clear();
        mDelExecState = DEL_EXEC_OVER_OR_NONE;
    }

    /**
     * 判断是否是忽略删除目录
     * @param childDirName
     * @return true 忽略删除则不进行删除操作 false 正常删除
     */
    private static boolean isDelIgnoreDir(String childDirName) {
        if (null == childDirName || null == mDelIgnoreDirSet) {
            return false;
        }

        Iterator<String> delIgnoreDirSetIterator = mDelIgnoreDirSet.iterator();
        boolean isIgnore = false;
        while (delIgnoreDirSetIterator.hasNext()) {
            // 判断如果准备删除childDirName在忽略列表时则跳过不进行删除操作
            if (isIgnore = (delIgnoreDirSetIterator.next()).equals(childDirName)) {
                break;
            }
        }
        return isIgnore;
    }
}
