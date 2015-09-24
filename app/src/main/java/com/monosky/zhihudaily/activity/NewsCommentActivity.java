package com.monosky.zhihudaily.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.VolleyError;
import com.monosky.zhihudaily.R;
import com.monosky.zhihudaily.activity.adapter.NewsCommentAdapter;
import com.monosky.zhihudaily.http.APIConstData;
import com.monosky.zhihudaily.http.JsonParseUtils;
import com.monosky.zhihudaily.module.CommentData;
import com.monosky.zhihudaily.module.NewsExtraData;
import com.monosky.zhihudaily.util.ToastUtils;
import com.monosky.zhihudaily.volley.IRequest;
import com.monosky.zhihudaily.volley.RequestListener;
import com.monosky.zhihudaily.volley.VolleyErrorHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 评论详情页
 */
public class NewsCommentActivity extends BaseActivity {

    private NewsCommentActivity mInstance;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private NewsCommentAdapter mAdapter;
    private List<CommentData> mLongDatas = new ArrayList<>();   // 长评论
    private List<CommentData> mShortDatas = new ArrayList<>();  // 短评论

    // 额外信息
    private NewsExtraData mExtraData;
    // 文章的ID
    private String newsId;
    // 长评论是否加载完
    private Boolean mLongCommentFinished = false;
    // 短评论是否加载完
    private Boolean mShortCommentFinished = false;
    // 当前请求的类型
    private String type;
    // 当前的lastID
    private String lastId;
    // 是否在刷新中
    private Boolean loading = false;
    // recyclerView定位
    private String mRecyclerSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_comment);
        mInstance = NewsCommentActivity.this;

        mExtraData = (NewsExtraData) getIntent().getSerializableExtra("extra");
        newsId = getIntent().getStringExtra("newsId");
        if ("0".equals(mExtraData.getLongComments())) {
            mLongCommentFinished = true;
        }
        mLongDatas.add(new CommentData());
        mShortDatas.add(new CommentData());

        // 设置loading
        showMaterialDialog();

        findViews();
        setViews();

        // 读取长评论详情
        getComments(APIConstData.COMMENTS_LONG);
    }

    /**
     * 获取评论详情
     * http://news-at.zhihu.com/api/4/story/#{newsId}/#{type}/before/#{id}
     *
     * @param type
     */
    private void getComments(String type) {
        loading = true;
        if (mExtraData.getLongComments() == 0 && type.equals(APIConstData.COMMENTS_LONG)) {
            mAdapter.refreshAdapter(mLongDatas, mShortDatas);
            materialDialog.dismiss();
            return;
        }

        this.type = type;
        StringBuffer url = new StringBuffer(APIConstData.NEWS_COMMENTS)
                .append(newsId).append("/").append(type);
        if (!TextUtils.isEmpty(lastId)) {
            url.append("/before/").append(lastId);
        }

        IRequest.get(mInstance, url.toString(), myRequestListener);
    }

    /**
     * 获取view
     */
    private void findViews() {
        mToolbar = (Toolbar) findViewById(R.id.comment_toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.comment_recycler_view);
    }

    /**
     * 设置view
     */
    private void setViews() {
        mToolbar.setNavigationIcon(R.mipmap.back);
        mToolbar.setTitle(getResources().getString(R.string.comment_title, String.valueOf(mExtraData.getComments())));
        mToolbar.setTitleTextAppearance(this, R.style.ToolbarTitleAppearance);
        setSupportActionBar(mToolbar);

        mLayoutManager = new LinearLayoutManager(mInstance);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnScrollListener(myOnScrollListener);

        mAdapter = new NewsCommentAdapter(mInstance, mExtraData.getLongComments(),
                mLongDatas, mExtraData.getShortComments(), mShortDatas);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnGetShortCommentsListener(myOnGetShortCommentsListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_news_comment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_comment_write) {
            showTipDialog();
            return true;
        } else if (id == android.R.id.home) {
            mInstance.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 自定义滑动监听器
     */
    RecyclerView.OnScrollListener myOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int currentPosition = mLayoutManager.findLastVisibleItemPosition();

            // 如果没有发起请求过或者正在刷新中，不执行以下请求
            if (TextUtils.isEmpty(type) || loading) {
                return;
            }
            if (type.equals(APIConstData.COMMENTS_LONG) && mExtraData.getLongComments() > 16) {
                if (!mLongCommentFinished && (currentPosition > mLongDatas.size() - 5)) {
                    getComments(APIConstData.COMMENTS_LONG);
                }
            } else if (type.equals(APIConstData.COMMENTS_SHORT) && mExtraData.getShortComments() > 10) {
                if (!mShortCommentFinished && (currentPosition > (mLongDatas.size() + mShortDatas.size() - 5))) {
                    getComments(APIConstData.COMMENTS_SHORT);
                }
            }
        }
    };

    /**
     * 自定义volley请求回调监听
     */
    RequestListener myRequestListener = new RequestListener() {
        @Override
        public void requestSuccess(String json) {
            List<CommentData> resDatas = JsonParseUtils.getCommentDatas(json);
            if (type.equals(APIConstData.COMMENTS_LONG)) {
                if (resDatas.size() < 20) {
                    mLongCommentFinished = true;
                }
                mLongDatas.addAll(resDatas);
            } else {
                if (resDatas.size() < 12) {
                    mShortCommentFinished = true;
                }
                mShortDatas.addAll(resDatas);
            }
            mAdapter.refreshAdapter(mLongDatas, mShortDatas);
            if (materialDialog.isShowing()) {
                materialDialog.dismiss();
            }
            if (resDatas != null && !resDatas.isEmpty()) {
                lastId = resDatas.get(resDatas.size() - 1).getId();
            }
            loading = false;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (TextUtils.isEmpty(mRecyclerSelection)) {
                        return;
                    }
                    if (mRecyclerSelection.equals(APIConstData.COMMENTS_LONG)) {
                        mLayoutManager.scrollToPosition(0);
                    } else {
                        mLayoutManager.scrollToPosition(mLongDatas.size());
                    }
                    mRecyclerSelection = null;
                }
            }, 200);
        }

        @Override
        public void requestError(VolleyError e) {
            materialDialog.dismiss();
            ToastUtils.showShort(mInstance, VolleyErrorHelper.getMessage(e));
            loading = false;
        }
    };

    /**
     * 自定义adapter的监听：是否加载短评论
     */
    NewsCommentAdapter.OnGetShortCommentsListener myOnGetShortCommentsListener = new NewsCommentAdapter.OnGetShortCommentsListener() {
        @Override
        public void getShortComments() {
            if (mExtraData.getShortComments() == 0) {
                return;
            }
            if (mShortDatas.size() > 1) {
                type = APIConstData.COMMENTS_LONG;
                lastId = null;
                mShortCommentFinished = false;
                mShortDatas.clear();
                mShortDatas.add(new CommentData());
                mAdapter.refreshAdapter(mLongDatas, mShortDatas);
                mRecyclerSelection = APIConstData.COMMENTS_LONG;
            } else {
                lastId = null;
                materialDialog.show();
                getComments(APIConstData.COMMENTS_SHORT);
                mRecyclerSelection = APIConstData.COMMENTS_SHORT;
            }
        }
    };

}
