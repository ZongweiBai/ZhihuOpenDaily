package com.monosky.zhihudaily.fragment.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.monosky.zhihudaily.R;
import com.monosky.zhihudaily.activity.EditorsActivity;
import com.monosky.zhihudaily.activity.NewsContentActivity;
import com.monosky.zhihudaily.module.EditorData;
import com.monosky.zhihudaily.module.StoryData;
import com.monosky.zhihudaily.module.ThemeData;
import com.monosky.zhihudaily.util.ImageFileUtil;
import com.monosky.zhihudaily.util.ImageLoaderOption;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * 主题日报列表adapter
 * Created by jonez_000 on 2015/9/16.
 */
public class ThemeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int HEAD_TYPE = 0;
    private static final int ITEM_TYPE = 1;

    private Context mContext;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private List<StoryData> mStoryDataList;
    private ArrayList<EditorData> mEditorDataList;
    private EditorData editorData;
    private ThemeData mThemeData;
    private View.OnClickListener myOnClickListener;

    public ThemeAdapter(Context mContext, ArrayList<EditorData> mEditorDataList, List<StoryData> mStoryDataList, ThemeData mThemeData) {
        this.mContext = mContext;
        this.mEditorDataList = mEditorDataList;
        this.mStoryDataList = mStoryDataList;
        this.mThemeData = mThemeData;
        this.myOnClickListener = onClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder viewHolder = null;
        if(viewType == HEAD_TYPE) {
            view = View.inflate(mContext, R.layout.fragment_theme_head, null);
            viewHolder = new ViewHolderHead(view);
        } else if (viewType == ITEM_TYPE) {
            view = View.inflate(mContext, R.layout.fragment_theme_item, null);
            viewHolder = new ViewHolderItem(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolderHead) {
            ViewHolderHead holderHead = (ViewHolderHead) holder;

            // 设置头部的图片信息
            ImageFileUtil.displayImage(imageLoader, mThemeData.getThumbnail(), holderHead.mImage);
            holderHead.mImageTitle.setText(mThemeData.getDescription());
            if (TextUtils.isEmpty(mThemeData.getImageSource())) {
                holderHead.mImageSource.setVisibility(View.GONE);
            } else {
                holderHead.mImageSource.setVisibility(View.VISIBLE);
                holderHead.mImageSource.setText(mThemeData.getImageSource());
            }

            // 设置主编信息
            for (int index = 0; index < holderHead.mEditorImageViews.length; index++) {
                holderHead.mEditorImageViews[index].setVisibility(View.GONE);
            }
            if (mEditorDataList != null && !mEditorDataList.isEmpty()) {
                for (int index = 0; index < mEditorDataList.size(); index++) {
                    editorData = mEditorDataList.get(index);
                    if (index < holderHead.mEditorImageViews.length) {
                        ImageFileUtil.displayImage(imageLoader, editorData.getAvatar(), holderHead.mEditorImageViews[index]);
                        holderHead.mEditorImageViews[index].setVisibility(View.VISIBLE);
                    }
                }
            }
            holderHead.mEditorLayout.setOnClickListener(myOnClickListener);
        } else if (holder instanceof ViewHolderItem) {
            ViewHolderItem holderItem = (ViewHolderItem) holder;
            StoryData storyData = mStoryDataList.get(position);
            if (TextUtils.isEmpty(storyData.getImage())) {
                holderItem.mCardViewImage.setVisibility(View.GONE);
            } else {
                holderItem.mCardViewImage.setVisibility(View.VISIBLE);
                imageLoader.displayImage(storyData.getImage(), holderItem.mCardViewImage, ImageLoaderOption.optionInfoImage(R.mipmap.ic_logo));
            }
            holderItem.mCardViewTitle.setText(storyData.getTitle());
            holderItem.mCardViewLayout.setTag(position);
            holderItem.mCardViewLayout.setOnClickListener(myOnClickListener);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return HEAD_TYPE;
        }
        return ITEM_TYPE;
    }

    @Override
    public int getItemCount() {
        return mStoryDataList.size();
    }

    /**
     * ViewHolder Head类
     */
    public static class ViewHolderHead extends RecyclerView.ViewHolder {

        public ImageView mImage;
        public TextView mImageTitle;
        public TextView mImageSource;
        public LinearLayout mEditorLayout;
        public ImageView[] mEditorImageViews;

        public ViewHolderHead(View itemView) {
            super(itemView);
            mImage = (ImageView) itemView.findViewById(R.id.theme_item_image);
            mImageTitle = (TextView) itemView.findViewById(R.id.theme_item_title);
            mImageSource = (TextView) itemView.findViewById(R.id.theme_item_image_source);
            mEditorLayout = (LinearLayout) itemView.findViewById(R.id.theme_item_editor_layout);
            mEditorImageViews = new ImageView[5];
            mEditorImageViews[0] = (ImageView) itemView.findViewById(R.id.index_item_editor_1);
            mEditorImageViews[1] = (ImageView) itemView.findViewById(R.id.index_item_editor_2);
            mEditorImageViews[2] = (ImageView) itemView.findViewById(R.id.index_item_editor_3);
            mEditorImageViews[3] = (ImageView) itemView.findViewById(R.id.index_item_editor_4);
            mEditorImageViews[4] = (ImageView) itemView.findViewById(R.id.index_item_editor_5);
        }
    }

    /**
     * ViewHolder Item类
     */
    public static class ViewHolderItem extends RecyclerView.ViewHolder {

        public CardView mCardViewLayout;
        public ImageView mCardViewImage;
        public TextView mCardViewTitle;

        public ViewHolderItem(View itemView) {
            super(itemView);
            mCardViewLayout = (CardView) itemView.findViewById(R.id.theme_item_cardview);
            mCardViewImage = (ImageView) itemView.findViewById(R.id.theme_item_cardview_img);
            mCardViewTitle = (TextView) itemView.findViewById(R.id.theme_item_cardview_title);
        }
    }

    /**
     * 刷新adapter
     *
     * @param mEditorDataList
     * @param mStoryDataList
     * @param mThemeData
     */
    public void refreshAdapter(ArrayList<EditorData> mEditorDataList, List<StoryData> mStoryDataList, ThemeData mThemeData) {
        this.mEditorDataList = mEditorDataList;
        this.mStoryDataList = mStoryDataList;
        this.mThemeData = mThemeData;
        this.notifyDataSetChanged();
    }

    /**
     * 自定义点击监听
     */
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()) {
                case R.id.theme_item_cardview:
                    int pos = (int) v.getTag();
                    intent = new Intent(mContext, NewsContentActivity.class);
                    intent.putExtra("newsId", mStoryDataList.get(pos).getStoryId());
                    mContext.startActivity(intent);
                    break;
                case R.id.theme_item_editor_layout:
                    intent = new Intent(mContext, EditorsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("editors", mEditorDataList);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                    break;
            }
        }
    };
}
