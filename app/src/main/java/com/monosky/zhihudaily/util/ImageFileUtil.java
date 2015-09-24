package com.monosky.zhihudaily.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.widget.ImageView;

import com.monosky.zhihudaily.BaseApplication;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

/**
 * 图片、文件操作类
 * Created by jonez_000 on 2015/9/7.
 */
public class ImageFileUtil {

    /**
     * 设置过期时间为1个小时
     */
    private static final Long mTimeDiff = 3600000L;

    /**
     * 创建文件夹时的可重复次数
     */
    private static int repeatCount = 0;

    /**
     * 根据一个图片的url获取其在本机缓存中的存储url
     * <p/>
     * from: https://avatars3.githubusercontent.com/u/3348483
     * to: /storage/emulated/0/Android/data/com.cundong.izhihu/cache/f72a0397abad8edbd98a8d2a701464ee.jpg
     *
     * @param context
     * @param imageUrl
     * @return
     */
    public static String getCacheImgFilePath(Context context, String imageUrl) {
        return SDCardUtils.getExternalCacheDir(context)
                + MD5Util.encrypt(imageUrl) + ".jpg";
    }

    /**
     * 修改文件的最后修改时间
     *
     * @param fileName
     */
    public void updateFileTime(Context mContext, String fileName) {
        File file = new File(SDCardUtils.getExternalCacheDir(mContext), fileName);
        long newModifiedTime = System.currentTimeMillis();
        file.setLastModified(newModifiedTime);
    }

    /**
     * 删除过期文件
     *
     * @param filename
     */
    public void removeExpiredCache(Context mContext, String filename) {
        File file = new File(SDCardUtils.getExternalCacheDir(mContext), filename);
        if (System.currentTimeMillis() - file.lastModified() > mTimeDiff) {
            LogUtils.i("Clear some expiredcache files ");
            file.delete();
        }
    }

    /**
     * 根据屏幕宽高按比例缩放图片
     * 只有screenWidth时，按照screenWidth进行比例缩放
     * 只有screenHeight时，按照screenHeight进行比例缩放
     *
     * @param bitmap
     * @param screenWidth  屏幕宽度
     * @param screenHeight 屏幕高度
     * @return
     */
    public static Bitmap resizeImageDependWidth(Bitmap bitmap, Integer screenWidth, Integer screenHeight) {
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();

        float scaleWidth = 1;
        float scaleHeight = 1;

        if (screenWidth != null && screenHeight == null) {
            scaleWidth = ((float) screenWidth) / width;
            scaleHeight = scaleWidth;
        } else if (screenWidth == null && screenHeight != null) {
            scaleHeight = ((float) screenHeight) / height;
            scaleWidth = scaleHeight;
        } else if (screenWidth != null && screenHeight != null) {
            scaleWidth = ((float) screenWidth) / width;
            scaleHeight = ((float) screenHeight) / height;
        }

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);
        return resizedBitmap;
    }


    /**
     * 获取图片存放的本地文件夹路径，不存在则新建
     *
     * @return
     */
    private static String getDirectory(String appPath) {
        File file = new File(appPath);
        if (!file.exists()) {
            if (file.mkdirs()) {
                repeatCount = 0;
                return appPath;
            } else {
                if (repeatCount < 3) {
                    repeatCount++;
                    return getDirectory(appPath);
                } else {
                    return null;
                }
            }
        } else {
            return appPath;
        }
    }


    /**
     * 将图片纠正到指定方向
     *
     * @param degree ： 图片被系统旋转的角度
     * @param bitmap ： 需纠正方向的图片
     * @return 纠向后的图片
     */
    public static Bitmap rotateBitmap(int degree, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        return bm;
    }

    /**
     * 加载本地或网络图片
     * @param imageLoader
     * @param urlPath
     * @param imageView
     */
    public static void displayImage(ImageLoader imageLoader, String urlPath, ImageView imageView) {
        String filePath = ImageFileUtil.getCacheImgFilePath(BaseApplication.getContext(), urlPath);
        File imageFile = new File(filePath);
        if (!imageFile.exists()) {
            imageLoader.displayImage(urlPath, imageView);
        } else {
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            imageView.setImageBitmap(bitmap);
        }
    }

}
