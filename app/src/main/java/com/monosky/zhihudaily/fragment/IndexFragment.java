package com.monosky.zhihudaily.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.monosky.zhihudaily.R;
import com.monosky.zhihudaily.fragment.adapter.IndexAdapter;
import com.monosky.zhihudaily.http.APIConstData;
import com.monosky.zhihudaily.http.ImageDownLoadTask;
import com.monosky.zhihudaily.http.JsonParseUtils;
import com.monosky.zhihudaily.module.EditorData;
import com.monosky.zhihudaily.module.StoryData;
import com.monosky.zhihudaily.util.DateUtils;
import com.monosky.zhihudaily.util.LogUtils;
import com.monosky.zhihudaily.util.ToastUtils;
import com.monosky.zhihudaily.volley.IRequest;
import com.monosky.zhihudaily.volley.RequestListener;
import com.monosky.zhihudaily.volley.VolleyErrorHelper;
import com.monosky.zhihudaily.widget.convenientBanner.ConvenientBanner;
import com.monosky.zhihudaily.widget.swipyRefreshLayout.SwipyRefreshLayout;
import com.monosky.zhihudaily.widget.swipyRefreshLayout.SwipyRefreshLayoutDirection;

import java.util.ArrayList;
import java.util.List;

/**
 * 今日热门（首页）Fragment
 */
public class IndexFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private SwipyRefreshLayout mSwipyLayout;
    private RecyclerView mRecycleView;
    private LinearLayoutManager mLayoutManager;
    private List<StoryData> storyDataList = new ArrayList<>();
    private List<StoryData> topStoryDataList = new ArrayList<>();
    private IndexAdapter mIndexAdapter;
    // 第一次请求时读取到的内容数量
    private int firstStoriesNum;
    // 当前页面的显示标题
    private String currentTitle;
    // 记录当前读取到了那一天
    private String currentPageTime = DateUtils.getDate();
    /**
     * 自定义handler，接受子线程发送的数据， 并用此数据配合主线程更新UI。
     */
    private static Handler handler;

    public IndexFragment() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        mSwipyLayout.setRefreshing(false);
                        mSwipyLayout.setDirection(SwipyRefreshLayoutDirection.BOTH);
                        break;
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_index, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSwipyLayout = (SwipyRefreshLayout) getView().findViewById(R.id.app_index_swipy);
        mRecycleView = (RecyclerView) getView().findViewById(R.id.app_index_recycleview);
        currentTitle = getActivity().getResources().getString(R.string.main_page);

        // 读取首页数据
        getLatestDatas();
    }

    /**
     * 读取首页数据
     */
    private void getLatestDatas() {
        // 设置SwipyRefreshLayout
        mSwipyLayout.setDirection(SwipyRefreshLayoutDirection.TOP);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecycleView.setLayoutManager(mLayoutManager);
        mSwipyLayout.setOnRefreshListener(myOnRefreshListener);
        // 刷新的样式
        mSwipyLayout.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_green_light,
                android.R.color.holo_blue_bright, android.R.color.holo_orange_light);
        // 开始读取数据
        mSwipyLayout.setRefreshing(true);
        IRequest.get(getActivity(), APIConstData.LASTEST, new RequestListener() {
            @Override
            public void requestSuccess(String json) {
                storyDataList = JsonParseUtils.getLatestStory(json);
                firstStoriesNum = storyDataList.size();
                topStoryDataList = JsonParseUtils.getTopStorys(json);
                storyDataList.add(0, new StoryData());
                mIndexAdapter = new IndexAdapter(getActivity(), storyDataList, topStoryDataList);
                mIndexAdapter.setOnBannerTouchListner(myOnBannerTouchListner);
                mRecycleView.setAdapter(mIndexAdapter);
                mRecycleView.addOnScrollListener(myOnScrollListener);
                Message message = new Message();
                message.what = 0;
                handler.sendMessage(message);

                // 开始缓存图片到本地
                List<String> imageUrlList = new ArrayList<>();
                for (StoryData storyData : topStoryDataList) {
                    imageUrlList.add(storyData.getImage());
                }
                new ImageDownLoadTask(getActivity(), imageUrlList, null).saveImageToSd();
            }

            @Override
            public void requestError(VolleyError e) {
                ToastUtils.showLong(getActivity(), VolleyErrorHelper.getMessage(e));
                Message message = new Message();
                message.what = 0;
                handler.sendMessage(message);
            }
        });
    }

    /**
     * 自定义触摸banner的viewpaper时的监听事件
     */
    IndexAdapter.OnBannerTouchListner myOnBannerTouchListner = new IndexAdapter.OnBannerTouchListner() {
        @Override
        public void onSwipeRefreshEnabled(Boolean enabled) {
            mSwipyLayout.setEnabled(enabled);
        }
    };

    /**
     * 自定义SwipyRefreshLayout的刷新监听
     */
    SwipyRefreshLayout.OnRefreshListener myOnRefreshListener = new SwipyRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh(SwipyRefreshLayoutDirection direction) {
            // 下拉刷新
            if (direction.equals(SwipyRefreshLayoutDirection.TOP)) {
                IRequest.get(getActivity(), APIConstData.LASTEST, new RequestListener() {
                    @Override
                    public void requestSuccess(String json) {
                        List<StoryData> storyNewDataList = JsonParseUtils.getLatestStory(json);
                        if (firstStoriesNum != storyNewDataList.size()) {
                            topStoryDataList.clear();
                            topStoryDataList.addAll(JsonParseUtils.getTopStorys(json));
                            storyDataList.clear();
                            storyDataList.addAll(storyNewDataList);
                            storyDataList.add(0, new StoryData());
                            mIndexAdapter.notifyDataSetChanged();
                        }
                        Message message = new Message();
                        message.what = 0;
                        handler.sendMessage(message);
                    }

                    @Override
                    public void requestError(VolleyError e) {
                        ToastUtils.showLong(getActivity(), VolleyErrorHelper.getMessage(e));
                        Message message = new Message();
                        message.what = 0;
                        handler.sendMessage(message);
                    }
                });
            }
            // 上拉刷新
            else {
                getBeforeDatas();
            }
        }
    };

    /**
     * 获取历史数据
     */
    private void getBeforeDatas() {
        String requestUrl = APIConstData.BEFORE + currentPageTime;
        IRequest.get(getActivity(), requestUrl, new RequestListener() {
            @Override
            public void requestSuccess(String json) {
                List<StoryData> beforeDataList = JsonParseUtils.getLatestStory(json);
                if (beforeDataList != null && !beforeDataList.isEmpty()) {
                    for (StoryData storyData : beforeDataList) {
                        storyDataList.add(storyData);
                    }
                }
                mIndexAdapter.notifyDataSetChanged();
                currentPageTime = DateUtils.getPastOneDay(currentPageTime);
                Message message = new Message();
                message.what = 0;
                handler.sendMessage(message);
            }

            @Override
            public void requestError(VolleyError e) {
                ToastUtils.showLong(getActivity(), VolleyErrorHelper.getMessage(e));
                Message message = new Message();
                message.what = 0;
                handler.sendMessage(message);
            }
        });
    }

    /**
     * 设置recycleView的滚动监听
     */
    RecyclerView.OnScrollListener myOnScrollListener = new RecyclerView.OnScrollListener() {
        int currentPosition = 0;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            currentPosition = mLayoutManager.findFirstVisibleItemPosition();
            if (storyDataList == null || storyDataList.isEmpty() || mListener == null) {
                return;
            }

            // 如果到了顶部。显示title为“首页”
            if (currentPosition == 0) {
                mListener.onFragmentInteraction(getActivity().getResources().getString(R.string.main_page));
                currentTitle = getActivity().getResources().getString(R.string.main_page);
                return;
            }
            StoryData storyData = storyDataList.get(currentPosition);
            if (!TextUtils.isEmpty(storyData.getStoryId())) {
                return;
            }
            if (!(storyData.getTitle()).equals(currentTitle)) {
                currentTitle = storyData.getTitle();
                mListener.onFragmentInteraction(currentTitle);
            }
        }
    };

    /**
     * 自定义监听
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Object object);
    }

    /**
     * 设置监听
     *
     * @param listener
     */
    public void setOnFragmentInteractionListener(OnFragmentInteractionListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onStop() {
        super.onStop();
        //停止翻页
        if (mIndexAdapter != null) {
            mIndexAdapter.stopTurning();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //开始自动翻页
        if (mIndexAdapter != null) {
            mIndexAdapter.startTurning();
        }
    }

}