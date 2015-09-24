package com.monosky.zhihudaily.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.JsonReader;
import android.util.JsonToken;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.monosky.zhihudaily.BaseApplication;
import com.monosky.zhihudaily.ConstData;
import com.monosky.zhihudaily.R;
import com.monosky.zhihudaily.http.APIConstData;
import com.monosky.zhihudaily.http.ImageDownLoadTask;
import com.monosky.zhihudaily.http.JsonParseUtils;
import com.monosky.zhihudaily.module.NewsData;
import com.monosky.zhihudaily.module.NewsExtraData;
import com.monosky.zhihudaily.sqlite.dao.NewsDao;
import com.monosky.zhihudaily.util.AssetsUtils;
import com.monosky.zhihudaily.util.ImageFileUtil;
import com.monosky.zhihudaily.util.LogUtils;
import com.monosky.zhihudaily.util.ToastUtils;
import com.monosky.zhihudaily.view.actionItemBadge.ActionItemBadge;
import com.monosky.zhihudaily.volley.IRequest;
import com.monosky.zhihudaily.volley.RequestListener;
import com.monosky.zhihudaily.volley.VolleyErrorHelper;
import com.monosky.zhihudaily.widget.observableScrollView.ObservableScrollView;
import com.monosky.zhihudaily.widget.observableScrollView.ObservableScrollViewCallbacks;
import com.monosky.zhihudaily.widget.observableScrollView.ScrollState;
import com.monosky.zhihudaily.widget.observableScrollView.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 文章详情
 */
public class NewsContentActivity extends BaseActivity {

    private NewsContentActivity mInstance;
    private Toolbar mToolbar;
    private ObservableScrollView mNewsScrollView;
    private int mParallaxImageHeight;
    private View mImageLayout;
    private View mNewsAnchor;
    private ImageView mNewsImage;
    private TextView mNewsImageTitle;
    private TextView mNewsImageSource;
    private WebView mNewsBodyWebView;
    private NewsData mNewsData;
    private NewsDao mNewsDao;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    // 保存图片的网络地址
    private List<String> mDetailImageList = new ArrayList<String>();
    // 保存图片的本地地址
    private static ArrayList<String> mLocalImageList = new ArrayList<>();
    // 额外信息
    private NewsExtraData mExtraData;
    // 文章的ID
    private String newsId;
    // toolbar是否显示
    private Boolean mToolBarVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_content);
        mInstance = NewsContentActivity.this;
        mLocalImageList.clear();
        BaseApplication.tempImgMap.clear();
        mNewsDao = new NewsDao(mInstance);

        getViews();
        setViews();

        // 获取文章的详情
        newsId = getIntent().getStringExtra("newsId");
        mNewsData = mNewsDao.getNewsDataById(newsId);
        if (mNewsData == null) {
            IRequest.get(mInstance, APIConstData.NEWS_GET + newsId, newsRequestListener);
        } else {
            setNewsInfo();
        }

        // 获取文章的额外信息
        IRequest.get(mInstance, APIConstData.STORY_EXTRA + newsId, newsExtraRequestListener);
    }

    private void getViews() {
        mToolbar = (Toolbar) findViewById(R.id.news_content_toolbar);
        mNewsBodyWebView = (WebView) findViewById(R.id.news_body);
        mNewsScrollView = (ObservableScrollView) findViewById(R.id.news_content_scroll_view);

        mImageLayout = findViewById(R.id.news_image_layout);
        mNewsAnchor = findViewById(R.id.news_anchor);
        mNewsImage = (ImageView) findViewById(R.id.news_image);
        mNewsImageTitle = (TextView) findViewById(R.id.news_image_title);
        mNewsImageSource = (TextView) findViewById(R.id.news_image_source);
    }

    private void setViews() {
        mToolbar.setNavigationIcon(R.mipmap.back);
        mToolbar.setTitle("");
        mToolbar.setTitleTextAppearance(mInstance, R.style.ToolbarTitleAppearance);
        setSupportActionBar(mToolbar);

        setUpWebViewDefaults();
        mNewsBodyWebView.setWebViewClient(mWebViewClient);
        mNewsScrollView.setScrollViewCallbacks(myScrollViewCallbacks);
        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);
        mImageLayout.setVisibility(View.GONE);
        mNewsAnchor.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_news_content, menu);
        if (mExtraData == null) {
            ActionItemBadge.update(this, menu.findItem(R.id.action_reply), R.mipmap.comment, "…");
            ActionItemBadge.update(this, menu.findItem(R.id.action_praise), R.mipmap.praise, "…");
        } else {
            ActionItemBadge.update(this, menu.findItem(R.id.action_reply), R.mipmap.comment, String.valueOf(mExtraData.getComments()));
            ActionItemBadge.update(this, menu.findItem(R.id.action_praise), R.mipmap.praise, String.valueOf(mExtraData.getPopularity()));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            mInstance.finish();
            return true;
        } else if (id == R.id.action_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.app_info));
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "选择分享"));
            return true;
        } else if (id == R.id.action_reply) {
            Intent intent = new Intent(mInstance, NewsCommentActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("extra", mExtraData);
            intent.putExtra("newsId", newsId);
            intent.putExtras(bundle);
            mInstance.startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Volley自定义监听接口：文章详情
     */
    RequestListener newsRequestListener = new RequestListener() {
        @Override
        public void requestSuccess(String json) {
            mNewsData = JsonParseUtils.getNewsData(json);
            if (mNewsData != null) {
                setNewsInfo();
                mNewsDao.saveNewsData(mNewsData);
            }
        }

        @Override
        public void requestError(VolleyError e) {
            ToastUtils.showShort(mInstance, VolleyErrorHelper.getMessage(e));
        }
    };

    /**
     * Volley自定义监听接口：文章附加内容
     */
    RequestListener newsExtraRequestListener = new RequestListener() {
        @Override
        public void requestSuccess(String json) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                mExtraData = new NewsExtraData(jsonObject.optInt("comments", 0), jsonObject.optInt("long_comments", 0),
                        jsonObject.optInt("popularity", 0), jsonObject.optInt("short_comments", 0));
            } catch (JSONException e) {
                e.printStackTrace();
                mExtraData = new NewsExtraData(0, 0, 0, 0);
            }
            invalidateOptionsMenu();
        }

        @Override
        public void requestError(VolleyError e) {
            mExtraData = new NewsExtraData(0, 0, 0, 0);
            invalidateOptionsMenu();
        }
    };

    /**
     * 设置页面
     */
    private void setNewsInfo() {
        if (!TextUtils.isEmpty(mNewsData.getImage())) {
            mImageLayout.setVisibility(View.VISIBLE);
            mNewsAnchor.setVisibility(View.VISIBLE);
            imageLoader.displayImage(mNewsData.getImage(), mNewsImage);
            mNewsImageTitle.setText(mNewsData.getTitle());
            if (TextUtils.isEmpty(mNewsData.getImageSource())) {
                mNewsImageSource.setVisibility(View.GONE);
            } else {
                mNewsImageSource.setText(mNewsData.getImageSource());
            }
        }

        String html = AssetsUtils.loadText(this, ConstData.TEMPLATE_DEF_URL);
        html = html.replace("{content}", mNewsData.getBody());
        html = html.replace("<div class=\"img-place-holder\">", "");
        String resultHTML = replaceImgTagFromHTML(html);

        mNewsBodyWebView.loadDataWithBaseURL(null, resultHTML, "text/html", "UTF-8", null);
    }

    /**
     * 设置webview
     */
    private void setUpWebViewDefaults() {
        mNewsBodyWebView.addJavascriptInterface(new JavaScriptObject(this), "injectedObject");

        /**
         * 设置WebView的属性，此时可以去执行JavaScript脚本
         */
        mNewsBodyWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);//滚动条风格，为0就是不给滚动条留空间，滚动条覆盖在网页上

        // 设置缓存模式
        mNewsBodyWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mNewsBodyWebView.getSettings().setJavaScriptEnabled(true);

        mNewsBodyWebView.getSettings().setSupportZoom(false);
        mNewsBodyWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

        // Use WideViewport and Zoom out if there is no viewport defined
        mNewsBodyWebView.getSettings().setUseWideViewPort(true);
        mNewsBodyWebView.getSettings().setLoadWithOverviewMode(true);

        mNewsBodyWebView.setVerticalScrollBarEnabled(true);
        mNewsBodyWebView.setHorizontalScrollBarEnabled(false);

        // 支持通过js打开新的窗口
        mNewsBodyWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        mNewsBodyWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                result.cancel();
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                return true;
            }
        });

        // 这行代码 会导致ScrollView嵌套Webview出错，webview空白
//        if (Build.VERSION.SDK_INT >= 11) {
//            mNewsBodyWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
//        }
    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
            startActivity(intent);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            LogUtils.i("页面加载完成");

            // 开始下载图片
            new ImageDownLoadTask(mInstance, mDetailImageList, myImageCacheResponseListener).saveImageToSd();

        }
    };

    /**
     * 自定义图片下载完成后的调用监听事件
     */
    ImageDownLoadTask.ImageCacheResponseListener myImageCacheResponseListener = new ImageDownLoadTask.ImageCacheResponseListener() {
        @Override
        public void responseSuccess() {
            String javascript = "img_replace_all();";
            evaluateJs(javascript);
        }

        @Override
        public void responseError(String error) {
            LogUtils.e(error);
        }
    };

    /**
     * 调用javascript
     *
     * @param javascript
     */
    public void evaluateJs(String javascript) {
        // In KitKat+ you should use the evaluateJavascript method
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mNewsBodyWebView.evaluateJavascript(javascript, new ValueCallback<String>() {
                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public void onReceiveValue(String s) {
                    JsonReader reader = new JsonReader(new StringReader(s));

                    // Must set lenient to parse single values
                    reader.setLenient(true);

                    try {
                        if (reader.peek() != JsonToken.NULL) {
                            if (reader.peek() == JsonToken.STRING) {
                                String msg = reader.nextString();
                                if (msg != null) {
                                    LogUtils.i(msg);
                                }
                            }
                        }
                    } catch (IOException e) {
                        LogUtils.e("MainActivity: IOException:" + e);
                    } finally {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            LogUtils.e(e.toString());
                        }
                    }
                }
            });
        } else {
            mNewsBodyWebView.loadUrl("javascript:" + javascript);
        }
    }

    /**
     * 自定义javaScript行为
     */
    public static class JavaScriptObject {

        private Activity mInstance;

        public JavaScriptObject(Activity instance) {
            mInstance = instance;
        }

        @JavascriptInterface
        public void openImage(String url) {
            if (mInstance != null && !mInstance.isFinishing()) {
                Intent intent = new Intent(mInstance, NewsImageActivity.class);
                intent.putExtra("imageUrl", url);
                intent.putStringArrayListExtra("localImageUrls", mLocalImageList);
                mInstance.startActivity(intent);
            }
        }

        @JavascriptInterface
        public void openTheme(String themeId) {
            ToastUtils.showShort(mInstance, "themeId:" + themeId);
        }

        @JavascriptInterface
        public void openSection(String sectionId) {
            Intent intent = new Intent(mInstance, SectionActivity.class);
            intent.putExtra("sectionId", sectionId);
            mInstance.startActivity(intent);
        }
    }

    /**
     * 替换html中的<img标签的属性
     *
     * @param html
     * @return
     */
    private String replaceImgTagFromHTML(String html) {

        Document doc = Jsoup.parse(html);
        Elements es = doc.getElementsByTag("img");
        String attrClass = null;
        String attrId = null;
        File file = null;

        for (Element e : es) {
            attrClass = e.attr("class");
            attrId = e.attr("id");
            String imgUrl = e.attr("src");
            if (!"avatar".equals(attrClass) && !"bottomImg".equals(attrId)) {
                mDetailImageList.add(imgUrl);

                String localImgPath = ImageFileUtil.getCacheImgFilePath(this, imgUrl);
                mLocalImageList.add(localImgPath);

                e.attr("src_link", "file://" + localImgPath);
                e.attr("ori_link", imgUrl);
                file = new File(localImgPath);
                if (file.exists()) {
                    e.attr("src", "file://" + localImgPath);
                } else {
                    e.attr("src", "file:///android_asset/www/default_pic_content_image_loading_light.png");
                }
                e.attr("onclick", "openImage('" + localImgPath + "')");
            } else {
                e.attr("src_link", imgUrl);
                e.attr("ori_link", imgUrl);
                e.attr("src", imgUrl);
            }
        }

        // 添加尾部
        if (!TextUtils.isEmpty(mNewsData.getSectionInfo())) {
            Elements bottom = doc.getElementsByClass("question");
            try {
                if (bottom != null && !bottom.isEmpty()) {
                    JSONObject sectionInfoJson = new JSONObject(mNewsData.getSectionInfo());
                    bottom.get(bottom.size() - 1).append("<div class=\"origin-source-wrap\" onclick=openSection(" + sectionInfoJson.optString("section_id") + ")><a class=\"origin-source with-link\" ><img id=\"bottomImg\" src=\"" + sectionInfoJson.optString("section_thumbnail") + "\" class=\"source-logo\"><span class=\"text\">本文来自：" + sectionInfoJson.optString("section_name") + " · 合集</span></a></div>");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return doc.html();
    }

    ObservableScrollViewCallbacks myScrollViewCallbacks = new ObservableScrollViewCallbacks() {
        @Override
        public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
            int baseColor = getResources().getColor(R.color.primary_color);
            float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
            mToolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(1f - alpha, baseColor));
            ViewHelper.setTranslationY(mImageLayout, scrollY / 2);
            if (alpha > 0.95) {
                if (mToolBarVisible) {
                    mToolbar.setVisibility(View.INVISIBLE);
                    mToolBarVisible = false;
                }
            } else {
                if (!mToolBarVisible) {
                    mToolbar.setVisibility(View.VISIBLE);
                    mToolBarVisible = true;
                }
            }
        }

        @Override
        public void onDownMotionEvent() {
        }

        @Override
        public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        }
    };

}