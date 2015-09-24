package com.monosky.zhihudaily.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.monosky.zhihudaily.BaseApplication;
import com.monosky.zhihudaily.R;
import com.monosky.zhihudaily.activity.adapter.NewsImageAdapter;
import com.monosky.zhihudaily.util.ImageFileUtil;
import com.monosky.zhihudaily.util.ToastUtils;

import java.util.ArrayList;

/**
 * 图片浏览
 */
public class NewsImageActivity extends BaseActivity {

    public NewsImageActivity mInstance;
    private Toolbar mToolbar;
    private NewsImageAdapter mImageAdapter;
    private ViewPager mViewPager;
    private TextView mViewPagerIndicator;
    private ImageView mViewPagerLeft;
    private ImageView mViewPagerRight;
    private ArrayList<String> mLocalImageList = new ArrayList<String>();
    private String imageUrl;    //当前图片的url
    private int currentPosition;    //当前图片的位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_image);
        mInstance = NewsImageActivity.this;

        imageUrl = getIntent().getStringExtra("imageUrl");
        mLocalImageList = getIntent().getStringArrayListExtra("localImageUrls");

        getViews();
        setViews();
    }

    private void getViews() {
        mToolbar = (Toolbar) findViewById(R.id.news_image_toolbar);
        mViewPager = (ViewPager) findViewById(R.id.image_view_paper);
        mViewPagerIndicator = (TextView) findViewById(R.id.image_view_paper_indicator);
        mViewPagerLeft = (ImageView) findViewById(R.id.image_view_paper_left);
        mViewPagerRight = (ImageView) findViewById(R.id.image_view_paper_right);
    }

    private void setViews() {
        // 设置toolbar
        mToolbar.setNavigationIcon(R.mipmap.back);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);

        // 设置viewpager
        mImageAdapter = new NewsImageAdapter(mInstance, mLocalImageList);
        mViewPager.setAdapter(mImageAdapter);
        mViewPager.addOnPageChangeListener(myOnPageChangeListener);
        for (int index = 0; index < mLocalImageList.size(); index++) {
            if (imageUrl.equals(mLocalImageList.get(index))) {
                currentPosition = index;
                break;
            }
        }
        mViewPager.setCurrentItem(currentPosition);
        mViewPagerIndicator.setText((currentPosition + 1) + " / " + mLocalImageList.size());

        // 设置其他
        expandViewTouchDelegate(mViewPagerLeft, 16, 16, 16, 16);
        mViewPagerLeft.setOnClickListener(myOnClickListener);
        expandViewTouchDelegate(mViewPagerRight, 16, 16, 16, 16);
        mViewPagerRight.setOnClickListener(myOnClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_news_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            saveImage2Gallery(mLocalImageList.get(currentPosition));
            return true;
        } else if (id == android.R.id.home) {
            mInstance.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 自定义viewpager改变监听
     */
    ViewPager.OnPageChangeListener myOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            currentPosition = position;
            mViewPagerIndicator.setText((position + 1) + " / " + mLocalImageList.size());
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    /**
     * 自定义点击监听
     */
    View.OnClickListener myOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bitmap bitmap = BaseApplication.tempImgMap.get(String.valueOf(currentPosition));
            if (bitmap == null) {
                bitmap = BitmapFactory.decodeFile(mLocalImageList.get(currentPosition));
            }
            Bitmap tempBitmap = null;
            switch (v.getId()) {
                case R.id.image_view_paper_left:
                    if (bitmap != null) {
                        tempBitmap = ImageFileUtil.rotateBitmap(-90, bitmap);
                        BaseApplication.tempImgMap.put(String.valueOf(currentPosition), tempBitmap);
                        mImageAdapter.refreshAdapter(tempBitmap, currentPosition);
                    } else {
                        ToastUtils.showShort(mInstance, "正在加载图片,请稍后");
                    }
                    break;
                case R.id.image_view_paper_right:
                    if (bitmap != null) {
                        tempBitmap = ImageFileUtil.rotateBitmap(90, bitmap);
                        BaseApplication.tempImgMap.put(String.valueOf(currentPosition), tempBitmap);
                        mImageAdapter.refreshAdapter(tempBitmap, currentPosition);
                    } else {
                        ToastUtils.showShort(mInstance, "正在加载图片,请稍后");
                    }
                    break;
            }
        }
    };

    /**
     * 将图片保存至系统图库
     *
     * @param imagePath
     * @return
     */
    private void saveImage2Gallery(String imagePath) {
        try {
            // 插入到系统图库
            MediaStore.Images.Media.insertImage(mInstance.getContentResolver(), imagePath, "title", "desc..");

            // 通知图库更新
            mInstance.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + imagePath)));

            ToastUtils.showShort(mInstance, "图片已保存至相册");
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showShort(mInstance, "图片保存失败");
        }
    }

}
