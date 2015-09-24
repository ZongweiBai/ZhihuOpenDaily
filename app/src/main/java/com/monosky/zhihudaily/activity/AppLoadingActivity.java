package com.monosky.zhihudaily.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.monosky.zhihudaily.ConstData;
import com.monosky.zhihudaily.R;
import com.monosky.zhihudaily.util.ImageFileUtil;
import com.monosky.zhihudaily.util.SPUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * loading页
 */
public class AppLoadingActivity extends AppCompatActivity {

    private AppLoadingActivity mInstance;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private ImageView mSplash;
    private ImageView mSplashLogo;
    private TextView mText;
    // 初始化镜头风格动画（从远至近）
    private Animation mFadeInScale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_app_loading);
        mInstance = AppLoadingActivity.this;

        getViews();
        // 初始化动画
        mFadeInScale = AnimationUtils.loadAnimation(this, R.anim.welcome_fade_in_scale);
        mFadeInScale.setFillAfter(true);

        // 获取loading图片
        String loadingImage = String.valueOf(SPUtils.get(ConstData.LOADING_IMAGE, ""));
        String loadingText = String.valueOf(SPUtils.get(ConstData.LOADING_TEXT, ""));
        if(!TextUtils.isEmpty(loadingImage)) {
            ImageFileUtil.displayImage(imageLoader, loadingImage, mSplash);
            mText.setText(loadingText);
            mText.setVisibility(View.VISIBLE);
        } else {
            mSplash.setImageResource(R.drawable.splash);
        }

        mSplash.setVisibility(View.VISIBLE);
        mSplashLogo.setVisibility(View.VISIBLE);
        mSplash.startAnimation(mFadeInScale);

        startMainActivity();

    }

    /**
     * 跳转到MainActivity
     */
    private void startMainActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(mInstance, MainActivity.class);
                mInstance.startActivity(intent);
                mInstance.finish();
            }
        }, 3000);
    }

    private void getViews() {
        mSplash = (ImageView) findViewById(R.id.loading_splash);
        mText = (TextView) findViewById(R.id.loading_text);
        mSplashLogo = (ImageView) findViewById(R.id.loading_splash_logo);
    }

}
