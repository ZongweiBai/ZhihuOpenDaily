package com.monosky.zhihudaily.fragment.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.monosky.zhihudaily.R;
import com.monosky.zhihudaily.activity.NewsContentActivity;
import com.monosky.zhihudaily.module.StoryData;
import com.monosky.zhihudaily.util.ImageLoaderOption;
import com.monosky.zhihudaily.util.LogUtils;
import com.monosky.zhihudaily.util.ToastUtils;
import com.monosky.zhihudaily.widget.convenientBanner.CBViewHolderCreator;
import com.monosky.zhihudaily.widget.convenientBanner.ConvenientBanner;
import com.monosky.zhihudaily.widget.convenientBanner.NetworkImageHolderView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 首页Adapter
 * Created by jonez_000 on 2015/8/28.
 */
public class IndexAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int HEAD_TYPE = 0;
    private static final int ITEM_TYPE = 1;

    private ImageLoader imageLoader = ImageLoader.getInstance();
    public Context mContext;
    public List<StoryData> todayStories;
    public List<StoryData> topStories;
    private ConvenientBanner mIndexBannerCopy;
    private View.OnClickListener myOnclickListener;

    public IndexAdapter(Context mContext, List<StoryData> todayStories, List<StoryData> topStories) {
        this.mContext = mContext;
        this.todayStories = todayStories;
        this.topStories = topStories;
        this.myOnclickListener = onClickListener;
    }

    /**
     * 自定义监听
     */
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.index_item_cardview:
                    int position = (int) v.getTag();
                    intent = new Intent(mContext, NewsContentActivity.class);
                    intent.putExtra("newsId", todayStories.get(position).getStoryId());
                    mContext.startActivity(intent);
                    break;
                case R.id.index_item_cardview_theme_layout:
                    String themeId = String.valueOf(v.getTag());
                    intent = new Intent(mContext, NewsContentActivity.class);
                    ToastUtils.showShort(mContext, themeId);
                    break;
            }

        }
    };

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder viewHolder = null;
        if (viewType == HEAD_TYPE) {
            view = View.inflate(mContext, R.layout.fragment_index_head, null);
            viewHolder = new ViewHolderHead(view);
        } else if (viewType == ITEM_TYPE) {
            view = View.inflate(mContext, R.layout.fragment_index_item, null);
            viewHolder = new ViewHolderItem(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if(viewHolder instanceof ViewHolderHead) {
            ViewHolderHead holder = (ViewHolderHead) viewHolder;
            holder.mIndexBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
                @Override
                public NetworkImageHolderView createHolder() {
                    return new NetworkImageHolderView();
                }
            }, topStories);
            //设置两个点图片作为翻页指示器，不设置则没有指示器
            holder.mIndexBanner.setPageIndicator(new int[]{R.mipmap.ic_page_indicator, R.mipmap.ic_page_indicator_focused});
            //设置翻页的效果，不需要翻页效果可用不设
            holder.mIndexBanner.setPageTransformer(ConvenientBanner.Transformer.DefaultTransformer);
            //设置触摸监听事件
            holder.mIndexBanner.setOnCBLoopViewPaperTouchListner(new ConvenientBanner.OnCBLoopViewPaperTouchListner() {
                @Override
                public void onParentSwipeRefreshEnabled(Boolean enabled) {
                    onBannerTouchListner.onSwipeRefreshEnabled(enabled);
                }
            });
            // 设置完成，开始自动轮播
            holder.mIndexBanner.startTurning(5000);
            mIndexBannerCopy = holder.mIndexBanner;
        } else if (viewHolder instanceof ViewHolderItem) {
            ViewHolderItem holder = (ViewHolderItem) viewHolder;
            StoryData storyData = getItem(position);
            if (TextUtils.isEmpty(storyData.getStoryId())) {
                holder.mCardView.setVisibility(View.GONE);
                holder.mItemDivide.setVisibility(View.GONE);
                holder.mItemTitle.setVisibility(View.VISIBLE);
                holder.mItemTitle.setText(storyData.getTitle());
            } else {
                holder.mCardView.setVisibility(View.VISIBLE);
                holder.mItemDivide.setVisibility(View.VISIBLE);
                holder.mItemTitle.setVisibility(View.GONE);

                if (TextUtils.isEmpty(storyData.getImage())) {
                    holder.mItemCardImg.setVisibility(View.GONE);
                } else {
                    imageLoader.displayImage(storyData.getImage(), holder.mItemCardImg, ImageLoaderOption.optionInfoImage(R.mipmap.ic_logo));
                    holder.mItemCardImg.setVisibility(View.VISIBLE);
                }
                holder.mItemCardTitle.setText(storyData.getTitle());
                if (storyData.getMultipic()) {
                    holder.mItemCardMultiImg.setVisibility(View.VISIBLE);
                } else {
                    holder.mItemCardMultiImg.setVisibility(View.GONE);
                }

                holder.mCardView.setTag(position);
                holder.mCardView.setOnClickListener(myOnclickListener);

                if (storyData.getThemeData() != null) {
                    holder.mThemeLayout.setVisibility(View.VISIBLE);
                    String html = "选自&nbsp;&nbsp;" + "<font color=\"#2196F3\">" + storyData.getThemeData().getThemeName() + "</font>";
                    holder.mThemeName.setText(Html.fromHtml(html));
                    holder.mThemeLayout.setTag(storyData.getThemeData().getThemeId());
                    holder.mThemeLayout.setOnClickListener(myOnclickListener);
                } else {
                    holder.mThemeLayout.setVisibility(View.GONE);
                }
            }
        }
    }

    private StoryData getItem(int position) {
        return todayStories.get(position);
    }

    @Override
    public int getItemCount() {
        return todayStories.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEAD_TYPE;
        }
        return ITEM_TYPE;
    }

    /**
     * 列表的ViewHolder
     */
    public static class ViewHolderHead extends RecyclerView.ViewHolder {

        public ConvenientBanner mIndexBanner;

        public ViewHolderHead(View itemView) {
            super(itemView);
            mIndexBanner = (ConvenientBanner) itemView.findViewById(R.id.app_index_banner);
        }
    }

    /**
     * 列表的ViewHolder
     */
    public static class ViewHolderItem extends RecyclerView.ViewHolder {

        public TextView mItemTitle;
        public CardView mCardView;
        public ImageView mItemCardImg;
        public TextView mItemCardTitle;
        public View mItemDivide;
        public ImageView mItemCardMultiImg;
        public LinearLayout mThemeLayout;
        public TextView mThemeName;

        public ViewHolderItem(View itemView) {
            super(itemView);
            mItemTitle = (TextView) itemView.findViewById(R.id.index_item_title);
            mCardView = (CardView) itemView.findViewById(R.id.index_item_cardview);
            mItemCardImg = (ImageView) itemView.findViewById(R.id.index_item_cardview_img);
            mItemCardTitle = (TextView) itemView.findViewById(R.id.index_item_cardview_title);
            mItemDivide = (View) itemView.findViewById(R.id.index_item_divide);
            mItemCardMultiImg = (ImageView) itemView.findViewById(R.id.index_item_cardview_multiimg);
            mThemeLayout = (LinearLayout) itemView.findViewById(R.id.index_item_cardview_theme_layout);
            mThemeName = (TextView) itemView.findViewById(R.id.index_item_cardview_theme);
        }
    }

    /**
     * 调用方法，开始自动轮播
     */
    public void startTurning() {
        if (mIndexBannerCopy != null) {
            LogUtils.i("开始自动轮播" + (new SimpleDateFormat("HHmmss")).format(new Date()));
            mIndexBannerCopy.startTurning(5000);
        }
    }

    /**
     * 关闭banner的自动轮播
     */
    public void stopTurning() {
        LogUtils.i("停止自动轮播" + (new SimpleDateFormat("HHmmss")).format(new Date()));
        if (mIndexBannerCopy != null) {
            mIndexBannerCopy.stopTurning();
        }
    }

    /**
     * 以下为自定义触摸事件的监听
     * 防止SwipeRefreshLayout嵌套ViewPaper时，ViewPaper的滑动事件被SwipeRefreshLayout影响
     */
    private OnBannerTouchListner onBannerTouchListner;

    public interface OnBannerTouchListner {
        void onSwipeRefreshEnabled(Boolean enabled);
    }

    public void setOnBannerTouchListner(OnBannerTouchListner listner) {
        this.onBannerTouchListner = listner;
    }
}
