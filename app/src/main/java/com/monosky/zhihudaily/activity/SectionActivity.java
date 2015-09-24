package com.monosky.zhihudaily.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.VolleyError;
import com.monosky.zhihudaily.R;
import com.monosky.zhihudaily.activity.adapter.SectionAdapter;
import com.monosky.zhihudaily.http.APIConstData;
import com.monosky.zhihudaily.http.JsonParseUtils;
import com.monosky.zhihudaily.module.SectionData;
import com.monosky.zhihudaily.module.StoryData;
import com.monosky.zhihudaily.util.ToastUtils;
import com.monosky.zhihudaily.volley.IRequest;
import com.monosky.zhihudaily.volley.RequestListener;
import com.monosky.zhihudaily.volley.VolleyErrorHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 栏目列表
 */
public class SectionActivity extends BaseActivity {

    public SectionActivity mInstance;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager layoutManager;
    private SectionAdapter mAdapter;

    private String sectionId;
    private String lastId;
    private int currentPos;
    private List<StoryData> storyDataList = new ArrayList<>();
    private Boolean loading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section);
        mInstance = SectionActivity.this;
        sectionId = getIntent().getStringExtra("sectionId");

        getViews();
        setViews();

        getSectionDatas();
    }

    /**
     * 获取栏目数据
     */
    private void getSectionDatas() {
        loading = true;
        StringBuffer urlBuffer = new StringBuffer(APIConstData.SECTION).append(sectionId);
        if (!TextUtils.isEmpty(lastId)) {
            urlBuffer.append("/before/").append(lastId);
        }
        IRequest.get(mInstance, urlBuffer.toString(), myRequestListener);
    }

    private void getViews() {
        mToolbar = (Toolbar) findViewById(R.id.section_toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.section_recycler_view);
    }

    private void setViews() {
        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(R.mipmap.back);
        mToolbar.setTitleTextAppearance(mInstance, R.style.ToolbarTitleAppearance);
        setSupportActionBar(mToolbar);

        mAdapter = new SectionAdapter(mInstance, storyDataList);
        layoutManager = new LinearLayoutManager(mInstance);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(myOnScrollListener);
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
            mInstance.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 自定义接口请求监听
     */
    RequestListener myRequestListener = new RequestListener() {
        @Override
        public void requestSuccess(String json) {
            SectionData sectionData = JsonParseUtils.getSectionData(json);
            if (sectionData == null) {
                loading = false;
                return;
            }
            lastId = sectionData.getTimestamp();
            getSupportActionBar().setTitle(sectionData.getName());
            List<StoryData> storyDatas = sectionData.getStoryDataList();
            if (storyDatas != null && !storyDatas.isEmpty()) {
                storyDataList.addAll(storyDatas);
                mAdapter.notifyDataSetChanged();
            }
            loading = false;
        }

        @Override
        public void requestError(VolleyError e) {
            ToastUtils.showShort(mInstance, VolleyErrorHelper.getMessage(e));
            loading = false;
        }
    };

    /**
     * 自定义RecyclerView滚动监听
     */
    RecyclerView.OnScrollListener myOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            currentPos = layoutManager.findLastVisibleItemPosition();
            if (!loading && (storyDataList.size() - currentPos) < 5) {
                getSectionDatas();
            }
        }
    };
}
