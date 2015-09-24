package com.monosky.zhihudaily.widget.convenientBanner;

import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.monosky.zhihudaily.module.StoryData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sai on 15/7/29.
 * 翻页指示器适配器
 */
public class CBPageChangeListener<T> implements ViewPager.OnPageChangeListener {
    private ArrayList<ImageView> pointViews;
    private int[] page_indicatorId;
    private TextView titleView;
    private List<T> bannerDatas;

    public CBPageChangeListener(ArrayList<ImageView> pointViews, int page_indicatorId[],
                                TextView titleView, List<T> bannerDatas) {
        this.pointViews = pointViews;
        this.page_indicatorId = page_indicatorId;
        this.titleView = titleView;
        this.bannerDatas = bannerDatas;
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int index) {
        for (int i = 0; i < pointViews.size(); i++) {
            pointViews.get(index).setImageResource(page_indicatorId[1]);
            if (index != i) {
                pointViews.get(i).setImageResource(page_indicatorId[0]);
            }
        }
        if (bannerDatas != null && !bannerDatas.isEmpty() && titleView != null) {
            StoryData bannerData = (StoryData) bannerDatas.get(index);
            if (bannerData != null && !TextUtils.isEmpty(bannerData.getTitle())) {
                titleView.setText(bannerData.getTitle());
            }
        }
    }
}
