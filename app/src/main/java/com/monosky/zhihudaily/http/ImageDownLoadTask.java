package com.monosky.zhihudaily.http;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.text.TextUtils;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.monosky.zhihudaily.util.ImageFileUtil;
import com.monosky.zhihudaily.util.LogUtils;
import com.monosky.zhihudaily.util.SDCardUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * SD卡图片保存
 */
public class ImageDownLoadTask {

    private Context mContext;

    /**
     * 图片保存的路径
     */
    private String externalCacheDir;

    /**
     * 图片完整路径列表
     */
    private List<String> urlPaths;

    /**
     * 调用结束的返回监听
     */
    private ImageCacheResponseListener imageCacheResponseListener;

    /**
     * 设置保存图片最小需要的SD卡空间为20MB
     */
    private static final Long FREE_SD_SPACE_NEEDED_TO_CACHE = 20L;

    public ImageDownLoadTask(Context mContext, List<String> urlPaths, ImageCacheResponseListener imageCacheResponseListener) {
        this.mContext = mContext;
        this.urlPaths = urlPaths;
        this.imageCacheResponseListener = imageCacheResponseListener;
        this.externalCacheDir = SDCardUtils.getExternalCacheDir(mContext);
    }

    /**
     * 保存网络图片到本地SD卡
     */
    public void saveImageToSd() {
        if (urlPaths == null || urlPaths.isEmpty() || TextUtils.isEmpty(externalCacheDir)) {
            LogUtils.e("image files is empty or cache dir is not exists");
            if (imageCacheResponseListener != null) {
                imageCacheResponseListener.responseError("image files is empty or cache dir is not exists");
            }
            return;
        }
        //判断sdcard上的空间
        if (FREE_SD_SPACE_NEEDED_TO_CACHE > SDCardUtils.getSDCardAllSize()) {
            LogUtils.e("Low free space onsd, do not cache");
            if (imageCacheResponseListener != null) {
                imageCacheResponseListener.responseError("Low free space onsd, do not cache");
            }
            return;
        }
        String filePath = null;
        File imageFile = null;
        RequestQueue mQueue = null;
        ImageRequest imageRequest = null;
        for (String urlPath : urlPaths) {
            filePath = ImageFileUtil.getCacheImgFilePath(mContext, urlPath);
            imageFile = new File(filePath);
            // 如果图片不存在，则进行保存
            mQueue = Volley.newRequestQueue(mContext);
            if (!imageFile.exists()) {
                imageRequest = new ImageRequest(urlPath,
                        responseListener(imageFile), 0, 0, ImageView.ScaleType.MATRIX, Config.RGB_565, responseErrorListener());
                mQueue.add(imageRequest);
            } else {
                LogUtils.i("Image has been saved to the sdcard succeed：" + imageFile.getAbsolutePath());
                return;
            }
        }
        if (mQueue.getSequenceNumber() > 0) {
            mQueue.start();
        } else {
            if (imageCacheResponseListener != null) {
                imageCacheResponseListener.responseSuccess();
            }
        }
    }

    /**
     * 成功返回结果调用
     *
     * @return
     */
    private Response.Listener<Bitmap> responseListener(final File file) {
        return new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                try {
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                    response.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    bos.flush();
                    bos.close();
                    LogUtils.i("Image saved to sdcard succeed：" + file.getAbsolutePath());
                    if (imageCacheResponseListener != null) {
                        imageCacheResponseListener.responseSuccess();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    LogUtils.i("Image saved to sdcard failed：" + file);
                    if (imageCacheResponseListener != null) {
                        imageCacheResponseListener.responseError("Image saved to sdcard failed：" + file);
                    }
                }
            }
        };
    }

    /**
     * 失败返回结果调用
     *
     * @return
     */
    private ErrorListener responseErrorListener() {
        return new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.e("Image download to sdcard failed");
                if (imageCacheResponseListener != null) {
                    imageCacheResponseListener.responseError("Image download to sdcard failed");
                }
            }
        };
    }

    public interface ImageCacheResponseListener {
        /**
         * 调用成功的返回
         */
        void responseSuccess();

        /**
         * 调用失败的返回
         *
         * @param error
         */
        void responseError(String error);
    }

}