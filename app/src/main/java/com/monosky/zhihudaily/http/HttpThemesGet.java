package com.monosky.zhihudaily.http;

import com.android.volley.VolleyError;
import com.monosky.zhihudaily.BaseApplication;
import com.monosky.zhihudaily.ConstData;
import com.monosky.zhihudaily.module.ThemeData;
import com.monosky.zhihudaily.util.LogUtils;
import com.monosky.zhihudaily.util.SPUtils;
import com.monosky.zhihudaily.volley.IRequest;
import com.monosky.zhihudaily.volley.RequestListener;

import java.util.List;

/**
 * 获取知乎主题（日报的种类）内容
 * Created by jonez_000 on 2015/8/28.
 */
public class HttpThemesGet {

    private List<ThemeData> themesDatas;

    public void startThemsDatasTask() {
        IRequest.get(BaseApplication.getContext(), APIConstData.THEMES, new RequestListener() {
            @Override
            public void requestSuccess(String json) {
                themesDatas = JsonParseUtils.getThemeDatas(json);
                if (themesDatas != null && !themesDatas.isEmpty()) {
                    BaseApplication.themesDatas = themesDatas;
                }
                SPUtils.put(ConstData.THEMES_DATA, json);
            }

            @Override
            public void requestError(VolleyError e) {
                LogUtils.e("获取主题列表失败：" + e);
            }
        });
    }
}
