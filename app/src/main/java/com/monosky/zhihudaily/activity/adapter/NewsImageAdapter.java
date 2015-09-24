package com.monosky.zhihudaily.activity.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.monosky.zhihudaily.BaseApplication;
import com.monosky.zhihudaily.R;
import com.monosky.zhihudaily.util.ImageFileUtil;
import com.monosky.zhihudaily.util.ScreenUtils;
import com.monosky.zhihudaily.widget.photoView.PhotoView;

import java.util.ArrayList;

/**
 * viewpaper adapter
 * Created by jonez_000 on 2015/9/9.
 */
public class NewsImageAdapter extends PagerAdapter {

    private Context mContext;
    private ArrayList<String> mImageUrls;
    private View mCurrentView;
    private int pos = -1;
    private Bitmap tempBitmap;

    public NewsImageAdapter(Context context, ArrayList<String> mImageUrls) {
        this.mContext = context;
        this.mImageUrls = mImageUrls;
    }

    @Override
    public int getCount() {
        if (mImageUrls != null && !mImageUrls.isEmpty()) {
            return mImageUrls.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (View) object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View viewContaine = LayoutInflater.from(mContext).inflate(R.layout.activity_news_image_item, null, false);
        PhotoView view = (PhotoView) viewContaine.findViewById(R.id.image_view_item);
        view.enable();
        Bitmap bitmap = BaseApplication.tempImgMap.get(String.valueOf(position));
        if(bitmap == null) {
            bitmap = BitmapFactory.decodeFile(mImageUrls.get(position));
            bitmap = ImageFileUtil.resizeImageDependWidth(bitmap, ScreenUtils.getScreenWidth(mContext), null);
        }
        if(position == pos && tempBitmap != null) {
            view.setImageBitmap(tempBitmap);
            BaseApplication.tempImgMap.put(String.valueOf(position), tempBitmap);
        } else {
            view.setImageBitmap(bitmap);
            BaseApplication.tempImgMap.put(String.valueOf(position), bitmap);
        }
        container.addView(viewContaine);
        return viewContaine;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        mCurrentView = (View) object;
    }

    public View getPrimaryItem() {
        return mCurrentView;
    }

    public void refreshAdapter(Bitmap bitmap, int pos) {
        this.tempBitmap = bitmap;
        this.pos = pos;
        this.notifyDataSetChanged();
    }
}
