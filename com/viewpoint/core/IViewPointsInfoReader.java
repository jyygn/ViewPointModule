/**
 *
 */
package com.viewpoint.core;

import com.letv.mobile.common.ICallback;

/**
 * 视图点信息内容阅读器
 * @author yangn
 */
public interface IViewPointsInfoReader {
    /**
     * 读取视图点JSON文件内容（文本文件）
     * @param url
     * @param callback
     * @throws Exception
     */
    void readFileText(String url, final ICallback<String> readCallback)
            throws Exception;
}
