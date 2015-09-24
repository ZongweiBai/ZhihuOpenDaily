package com.monosky.zhihudaily.util;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * SD卡相关的辅助类
 * Created by jonez_000 on 2015/8/28.
 */
public class SDCardUtils {

    private static final String TYPE_CACHE = "cache";

    private static final String TYPE_FILES = "files";

    private SDCardUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 判断SDCard是否可用
     *
     * @return
     */
    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡路径
     *
     * @return
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator;
    }

    /**
     * 获取SD卡的剩余容量 单位MB
     *
     * @return
     */
    public static long getSDCardAllSize() {
        if (isSDCardEnable()) {
            Long freeSpace = null;
            String sDcString = android.os.Environment.getExternalStorageState();
            if (sDcString.equals(android.os.Environment.MEDIA_MOUNTED)) {
                File pathFile = android.os.Environment.getExternalStorageDirectory();
                try {
                    StatFs statfs = new StatFs(pathFile.getPath());
                    // 获取SDCard上每个block的SIZE
                    long nBlocSize = statfs.getBlockSize();
                    // 获取可供程序使用的Block的数量
                    long nAvailaBlock = statfs.getAvailableBlocks();
                    // 计算 SDCard 剩余大小MB
                    freeSpace = nAvailaBlock * nBlocSize;
                    return freeSpace;
                } catch (IllegalArgumentException e) {
                    LogUtils.e(e.toString());
                }
            }
        }
        return 0L;
    }

    /**
     * 获取指定路径所在空间的剩余可用容量字节数，单位byte
     *
     * @param filePath
     * @return 容量字节 SDCard可用空间，内部存储可用空间
     */
    public static long getFreeBytes(String filePath) {
        // 如果是sd卡的下的路径，则获取sd卡可用容量
        if (filePath.startsWith(getSDCardPath())) {
            filePath = getSDCardPath();
        } else {// 如果是内部存储的路径，则获取内存存储的可用容量
            filePath = Environment.getDataDirectory().getAbsolutePath();
        }
        StatFs stat = new StatFs(filePath);
        long availableBlocks = (long) stat.getAvailableBlocks() - 4;
        return stat.getBlockSize() * availableBlocks;
    }

    /**
     * 获取系统存储路径
     *
     * @return
     */
    public static String getRootDirectoryPath() {
        return Environment.getRootDirectory().getAbsolutePath();
    }

    /**
     * 获取扩展存储cache的绝对路径
     * <p/>
     * 返回：sdcard/Android/data/{package_name}/cache/
     *
     * @param context
     * @return
     */
    public static String getExternalCacheDir(Context context) {
        return getExternalDir(context, TYPE_CACHE);
    }

    private static String getExternalDir(Context context, String type) {

        StringBuilder sb = new StringBuilder();

        if (context == null)
            return null;

        if (!isMounted()) {

            if (type.equals(TYPE_CACHE)) {
                sb.append(context.getCacheDir()).append(File.separator);
            } else {
                sb.append(context.getFilesDir()).append(File.separator);
            }
        }

        File file = null;

        if (type.equals(TYPE_CACHE)) {
            file = context.getExternalCacheDir();
        } else {
            file = context.getExternalFilesDir(null);
        }

        // In some case, even the sd card is mounted,
        // getExternalCacheDir will return null
        // may be it is nearly full.

        if (file != null) {
            sb.append(file.getAbsolutePath()).append(File.separator);
        } else {
            sb.append(Environment.getExternalStorageDirectory().getPath()).append("/Android/data/").append(context.getPackageName())
                    .append("/").append(type).append("/").append(File.separator).toString();
        }

        return sb.toString();
    }

    /**
     * SD卡是否挂载
     *
     * @return
     */
    public static boolean isMounted() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED) ? true : false;
    }

}
