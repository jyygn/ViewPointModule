package com.viewpoint.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.letv.mobile.core.log.Logger;
import com.letv.mobile.player.highlight.viewpoint.common.ViewPointConstants;

public class FileUtil {

    static final String TAG = "FileUtil";

    /**
     * 根据图片路径获取bitmap
     * @param path
     * @return
     * @throws FileNotFoundException
     */
    public static Bitmap getBitmap(String path) throws FileNotFoundException,
            IOException {
        File file = new File(path);
        /*
         * if (!isImg(file.getName())) return null;
         */

        FileInputStream stream = new FileInputStream(file);
        Bitmap bitmap = BitmapFactory.decodeStream(stream);
        stream.close();
        stream = null;

        return bitmap;
    }

    /**
     * 将bitmap保存到文件outPath
     * @param bm
     * @param outPath
     */
    public static void saveBitmap(Bitmap bm, String outPath) {
        Logger.d(TAG, "保存图片");
        File f = ViewPointConstants.getThumCacheFile(outPath);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Log.i(TAG, "e已经保存");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 字节转文件
     * @param data
     * @param outPath
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void convertBytesToFile(byte[] data, String outPath)
            throws FileNotFoundException, IOException {
        File file = new File(outPath);
        if (file.exists()) {
            file.delete();
        }

        FileOutputStream fOut = new FileOutputStream(file);
        fOut.write(data);
        fOut.flush();
        fOut.close();
        fOut = null;
    }

    /**
     * 将InputStrem存储到文件
     * @param is
     * @param outPath
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void saveFile(InputStream inputStream, String outPath)
            throws FileNotFoundException, IOException, NullPointerException {
        if (null == inputStream) {
            throw new NullPointerException("is null from param inputStream");
        }
        /*
         * FileInputStream stream=null; FileOutputStream fOut=null;
         */
        File file = new File(outPath);
        if (file.exists()) {
            file.delete();
        }

        FileOutputStream fOut = new FileOutputStream(file);
        int c;
        while ((c = inputStream.read()) != -1) {
            fOut.write(c);
        }
        // bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fOut);// 压缩 100压缩质量
        fOut.flush();
        fOut.close();
        fOut = null;

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
     * 文本文件转文本内容（字符串）
     * @param data
     * @return
     * @throws Exception
     * @throws FileNotFoundException
     */
    public static String converFileToStr(String path)
            throws FileNotFoundException, Exception {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        byte[] data = readStream(new FileInputStream(file));
        if (null == data || data.length == 0) {
            return null;
        }
        return new String(data);
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     * @param dir
     *            将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     *         If a deletion fails, the method stops attempting to
     *         delete and returns "false".
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            // 递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }
}
