package com.monosky.zhihudaily.widget.convenientBanner;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.monosky.zhihudaily.activity.NewsContentActivity;
import com.monosky.zhihudaily.module.StoryData;
import com.monosky.zhihudaily.util.ImageFileUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Sai on 15/8/4.
 * 网络图片加载例子
 */
public class NetworkImageHolderView implements CBPageAdapter.Holder<StoryData> {
    private ImageView imageView;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    public View createView(Context context) {
        //你可以通过layout文件来创建，也可以像我一样用代码创建，不一定是Image，任何控件都可以进行翻页
        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }

    @Override
    public void UpdateUI(final Context context, int position, final StoryData data) {
        ImageFileUtil.displayImage(imageLoader, data.getImage(), imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NewsContentActivity.class);
                intent.putExtra("newsId", data.getStoryId());
                context.startActivity(intent);
            }
        });
    }
}
