package com.monosky.zhihudaily.util;

import com.monosky.zhihudaily.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * ImageLoader配置
 * Created by jonez_000 on 2015/8/16.
 */
public class ImageLoaderOption {

    public static DisplayImageOptions optionInfo() {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(20))
                .build();

    }

    public static DisplayImageOptions optionInfoImage(int drawable) {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(drawable)
                .showImageForEmptyUri(drawable)
                .showImageOnFail(drawable)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(100)) // 渐变动画
                .build();

    }
}
