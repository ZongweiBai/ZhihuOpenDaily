package com.monosky.zhihudaily.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.monosky.zhihudaily.R;
import com.monosky.zhihudaily.fragment.adapter.ThemeAdapter;
import com.monosky.zhihudaily.http.APIConstData;
import com.monosky.zhihudaily.http.ImageDownLoadTask;
import com.monosky.zhihudaily.http.JsonParseUtils;
import com.monosky.zhihudaily.module.EditorData;
import com.monosky.zhihudaily.module.StoryData;
import com.monosky.zhihudaily.module.ThemeData;
import com.monosky.zhihudaily.util.ToastUtils;
import com.monosky.zhihudaily.volley.IRequest;
import com.monosky.zhihudaily.volley.RequestListener;
import com.monosky.zhihudaily.volley.VolleyErrorHelper;
import com.monosky.zhihudaily.widget.swipyRefreshLayout.SwipyRefreshLayout;
import com.monosky.zhihudaily.widget.swipyRefreshLayout.SwipyRefreshLayoutDirection;

import java.util.ArrayList;
import java.util.List;

/**
 * 主题日报列表查询
 */
public class ThemeFragment extends Fragment {

    private SwipeRefreshLayout mSwipeLayout;
    private RecyclerView mRecycleView;
    private LinearLayoutManager mLayoutManager;
    private ThemeAdapter mThemeAdapter;
    private ThemeData mThemeData = null;
    private List<StoryData> mStoryDataList = new ArrayList<>();
    private ArrayList<EditorData> mEditorDataList = new ArrayList<>();
    private String themeId;

    private String lastId;
    private Boolean loadFinish = false; // 是否加载了所有的内容
    private Boolean loading = false;    //是否在加载中

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_theme, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        themeId = getArguments().getString("themeId");
        mSwipeLayout = (SwipeRefreshLayout) getView().findViewById(R.id.theme_refresh_layout);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_green_light,
                android.R.color.holo_blue_bright, android.R.color.holo_orange_light);
        mRecycleView = (RecyclerView) getView().findViewById(R.id.theme_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecycleView.setLayoutManager(mLayoutManager);
        mThemeAdapter = new ThemeAdapter(getActivity(), mEditorDataList, mStoryDataList, mThemeData);
        mRecycleView.setAdapter(mThemeAdapter);
        mRecycleView.addOnScrollListener(myOnScrollListener);

        // 获取主题列表
        getThemeDataList();
    }

    /**
     * 获取主题对应列表
     */
    private void getThemeDataList(String... params) {
        mSwipeLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeLayout.setRefreshing(true);
            }
        });
        StringBuffer url = new StringBuffer(APIConstData.THEME_LIST).append(themeId);
        if (!TextUtils.isEmpty(lastId)) {
            url.append("/before/").append(lastId);
        }
        if (params != null && params.length > 0 && "loading".equals(params[0])) {
            IRequest.get(getActivity(), url.toString(), myLoadingRequestListener);
        } else {
            IRequest.get(getActivity(), url.toString(), myRequestListener);
        }
    }

    /**
     * 自定义volley请求回调监听
     */
    RequestListener myRequestListener = new RequestListener() {
        @Override
        public void requestSuccess(String json) {
            List<StoryData> storyDataList = JsonParseUtils.getThemeStoryDatas(json);
            List<EditorData> editorDataList = JsonParseUtils.getThemeEditorDatas(json);
            mThemeData = JsonParseUtils.getThemeBaseData(json);
            if (storyDataList != null && !storyDataList.isEmpty()) {
                mStoryDataList.add(new StoryData());
                for (StoryData storyData : storyDataList) {
                    mStoryDataList.add(storyData);
                }
                lastId = storyDataList.get(storyDataList.size() - 1).getStoryId();
            }
            if (editorDataList != null && !editorDataList.isEmpty()) {
                for (EditorData editorData : editorDataList) {
                    mEditorDataList.add(editorData);
                }
            }
            mThemeAdapter.refreshAdapter(mEditorDataList, mStoryDataList, mThemeData);
            mSwipeLayout.setRefreshing(false);

            // 开始缓存图片到本地
            List<String> imageUrlList = new ArrayList<>();
            imageUrlList.add(mThemeData.getThumbnail());
            for (EditorData editorData : mEditorDataList) {
                imageUrlList.add(editorData.getAvatar());
            }
            new ImageDownLoadTask(getActivity(), imageUrlList, null).saveImageToSd();
        }

        @Override
        public void requestError(VolleyError e) {
            mSwipeLayout.setRefreshing(false);
            ToastUtils.showShort(getActivity(), VolleyErrorHelper.getMessage(e));
        }
    };

    /**
     * 自定义volley请求回调监听
     */
    RequestListener myLoadingRequestListener = new RequestListener() {
        @Override
        public void requestSuccess(String json) {
            List<StoryData> storyDataList = JsonParseUtils.getThemeStoryDatas(json);
            if (storyDataList != null && !storyDataList.isEmpty()) {
                for (StoryData storyData : storyDataList) {
                    mStoryDataList.add(storyData);
                }
                if (storyDataList.size() < 18) {
                    loadFinish = true;
                }
                lastId = storyDataList.get(storyDataList.size() - 1).getStoryId();
            } else {
                loadFinish = true;
            }
            mThemeAdapter.refreshAdapter(mEditorDataList, mStoryDataList, mThemeData);
            mSwipeLayout.setRefreshing(false);
            loading = false;
        }

        @Override
        public void requestError(VolleyError e) {
            mSwipeLayout.setRefreshing(false);
            loading = false;
            ToastUtils.showShort(getActivity(), VolleyErrorHelper.getMessage(e));
        }
    };

    /**
     * 自定义recyclerview滑动监听
     */
    RecyclerView.OnScrollListener myOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (!loadFinish && !loading) {
                int lastPosition = mLayoutManager.findLastVisibleItemPosition();
                if (mStoryDataList.size() - lastPosition <= 5) {
                    loading = true;
                    getThemeDataList("loading");
                }
            }
        }
    };

}
