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

    private List<ThemeData> themeDatas;

    public void startThemsDatasTask() {
        IRequest.get(BaseApplication.getContext(), APIConstData.THEMES, new RequestListener() {
            @Override
            public void requestSuccess(String json) {
                themeDatas = JsonParseUtils.getThemeDatas(json);
                if (themeDatas != null && !themeDatas.isEmpty()) {
                    BaseApplication.themesDatas = themeDatas;
                    BaseApplication.themeDataMap.clear();
                    for (ThemeData themeData: themeDatas) {
                        BaseApplication.themeDataMap.put(themeData.getThemeId(), themeData);
                    }
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
