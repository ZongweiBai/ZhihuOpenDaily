package com.monosky.zhihudaily.activity.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * 栏目adapter
 * Created by jonez_000 on 2015/9/21.
 */
public class SectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<StoryData> storyDataList;
    private View.OnClickListener myOnclickListener;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    public SectionAdapter(Context mContext, List<StoryData> storyDataList) {
        this.mContext = mContext;
        this.storyDataList = storyDataList;
        this.myOnclickListener = onClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.fragment_index_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        holder.mItemTitle.setVisibility(View.GONE);
        holder.mThemeLayout.setVisibility(View.GONE);
        StoryData storyData = getItem(position);
        holder.mCardView.setVisibility(View.VISIBLE);
        holder.mItemDivide.setVisibility(View.VISIBLE);

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

        holder.mItemCardTime.setText(storyData.getTime());
        holder.mItemCardTime.setVisibility(View.VISIBLE);

        holder.mCardView.setTag(position);
        holder.mCardView.setOnClickListener(myOnclickListener);
    }

    private StoryData getItem(int position) {
        return storyDataList.get(position);
    }

    @Override
    public int getItemCount() {
        return storyDataList.size();
    }

    /**
     * 列表的ViewHolder
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mItemTitle;
        public CardView mCardView;
        public ImageView mItemCardImg;
        public TextView mItemCardTitle;
        public View mItemDivide;
        public ImageView mItemCardMultiImg;
        public LinearLayout mThemeLayout;
        public TextView mThemeName;
        public TextView mItemCardTime;

        public ViewHolder(View itemView) {
            super(itemView);
            mItemTitle = (TextView) itemView.findViewById(R.id.index_item_title);
            mCardView = (CardView) itemView.findViewById(R.id.index_item_cardview);
            mItemCardImg = (ImageView) itemView.findViewById(R.id.index_item_cardview_img);
            mItemCardTitle = (TextView) itemView.findViewById(R.id.index_item_cardview_title);
            mItemDivide = (View) itemView.findViewById(R.id.index_item_divide);
            mItemCardMultiImg = (ImageView) itemView.findViewById(R.id.index_item_cardview_multiimg);
            mThemeLayout = (LinearLayout) itemView.findViewById(R.id.index_item_cardview_theme_layout);
            mThemeName = (TextView) itemView.findViewById(R.id.index_item_cardview_theme);
            mItemCardTime = (TextView) itemView.findViewById(R.id.index_item_cardview_time);
        }
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
                    intent.putExtra("newsId", storyDataList.get(position).getStoryId());
                    mContext.startActivity(intent);
                    break;
            }

        }
    };
}
