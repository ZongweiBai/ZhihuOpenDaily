package com.monosky.zhihudaily.http;

import com.android.volley.VolleyError;
import com.monosky.zhihudaily.BaseApplication;
import com.monosky.zhihudaily.ConstData;
import com.monosky.zhihudaily.module.EditorData;
import com.monosky.zhihudaily.util.LogUtils;
import com.monosky.zhihudaily.util.SPUtils;
import com.monosky.zhihudaily.volley.IRequest;
import com.monosky.zhihudaily.volley.RequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取知乎日报的APP展示页
 * Created by jonez_000 on 2015/9/3.
 */
public class HttpStartImageGet {

    public static void startImageDataGet() {
        IRequest.get(BaseApplication.getContext(), APIConstData.START_IMAGE, new RequestListener() {
            @Override
            public void requestSuccess(String json) {
                try {
                    JSONObject imageJson = new JSONObject(json);
                    SPUtils.put(ConstData.LOADING_IMAGE, imageJson.optString("img"));
                    SPUtils.put(ConstData.LOADING_TEXT, imageJson.optString("text"));

                    // 开始缓存图片到本地
                    List<String> imageUrlList = new ArrayList<>();
                    imageUrlList.add(imageJson.optString("img"));
                    new ImageDownLoadTask(BaseApplication.getContext(), imageUrlList, null).saveImageToSd();
                } catch (JSONException e) {
                    LogUtils.e("解析APP Loading数据失败：" + e);
                }
            }

            @Override
            public void requestError(VolleyError e) {
                LogUtils.e("获取APP Loading图片失败：" + e);
            }
        });
    }
}
