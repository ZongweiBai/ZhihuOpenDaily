package com.monosky.zhihudaily.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.monosky.zhihudaily.R;
import com.monosky.zhihudaily.http.APIConstData;

/**
 * 主编详情
 */
public class EditorDetailActivity extends BaseActivity {

    private EditorDetailActivity mInstance;
    private Toolbar mToolbar;
    private WebView mWebview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_detail);
        mInstance = EditorDetailActivity.this;

        mToolbar = (Toolbar) findViewById(R.id.editor_detail_toolbar);
        mToolbar.setNavigationIcon(R.mipmap.back);
        mToolbar.setTitle(getResources().getString(R.string.theme_editor_detail));
        mToolbar.setTitleTextAppearance(mInstance, R.style.ToolbarTitleAppearance);
        setSupportActionBar(mToolbar);

        mWebview = (WebView) findViewById(R.id.editor_detail_webview);
        String url = APIConstData.EDITOR + getIntent().getStringExtra("editorId") + "/profile-page/android";
        mWebview.loadUrl(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_common, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
