package com.monosky.zhihudaily.volley;

import com.android.volley.VolleyError;

/**
 * 自定义监听
 *
 * @param <T>
 */
public interface RequestJsonListener<T> {
    /**
     * 成功
     *
     * @param <T>
     */
    public void requestSuccess(T result);

    /**
     * 错误
     */
    public void requestError(VolleyError e);
}
