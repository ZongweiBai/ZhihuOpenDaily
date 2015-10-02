package com.monosky.zhihudaily.activity.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.monosky.zhihudaily.R;
import com.monosky.zhihudaily.module.CommentData;
import com.monosky.zhihudaily.util.DateUtils;
import com.monosky.zhihudaily.util.DensityUtils;
import com.monosky.zhihudaily.util.ImageFileUtil;
import com.monosky.zhihudaily.util.ImageLoaderOption;
import com.monosky.zhihudaily.util.LogUtils;
import com.monosky.zhihudaily.util.ScreenUtils;
import com.monosky.zhihudaily.util.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * 文章评论adapter
 * Created by jonez_000 on 2015/9/12.
 */
public class NewsCommentAdapter extends RecyclerView.Adapter<NewsCommentAdapter.ViewHolder> {

    private Context mContext;
    private ImageLoader mImageLoader = ImageLoader.getInstance();
    private int mLongCommentNum;    // 长评论总数
    private int mShoreCommentNum;   // 短评论总数
    private List<CommentData> mLongDatas;   // 长评论
    private List<CommentData> mShortDatas;  // 短评论
    private List<CommentData> mAllCommentDatas = new ArrayList<>(); // 所有的总评论
    private View.OnClickListener myOnClickListener;

    public NewsCommentAdapter(Context mContext, int mLongCommentNum, List<CommentData> mLongDatas, int mShoreCommentNum, List<CommentData> mShortDatas) {
        this.mContext = mContext;
        this.mLongCommentNum = mLongCommentNum;
        this.mLongDatas = mLongDatas;
        this.mShoreCommentNum = mShoreCommentNum;
        this.mShortDatas = mShortDatas;
        getAllComment();
        this.myOnClickListener = onClickListener;
    }

    /**
     * 自定义点击事件监听
     */
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.comment_detail_layout:
                    int pos = (int) v.getTag();
                    showCommentDialog(pos);
                    break;
                case R.id.comment_bottom_title:
                    getOrHideShortComments(v);
                    break;
                case R.id.short_empty_title:
                    getOrHideShortComments(v);
                    break;
            }
        }
    };

    /**
     * 弹出每条评论的操作dialog
     *
     * @param pos
     */
    private void showCommentDialog(int pos) {
        new MaterialDialog.Builder(mContext)
                .items(R.array.comment_options)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        LogUtils.i("你选择了"+which + ": " + text);
                        ToastUtils.showLong(mContext, mContext.getResources().getString(R.string.app_info));
                    }
                })
                .show();
    }

    /**
     * 隐藏或显示短评
     */
    private void getOrHideShortComments(View view) {
        if (mShoreCommentNum == 0) {
            return;
        }
        if (onGetShortCommentsListener != null) {
            onGetShortCommentsListener.getShortComments();
        }
    }

    /**
     * 获取所有的总评论列表
     */
    private void getAllComment() {
        mAllCommentDatas.clear();
        mAllCommentDatas.addAll(mLongDatas);
        mAllCommentDatas.addAll(mShortDatas);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_news_comment_item, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CommentData commentData = mAllCommentDatas.get(position);
        // 长评为空的情况
        if (mLongCommentNum == 0) {
            if (position == 0) {
                holder.mLongEmptyLayout.setVisibility(View.VISIBLE);
                holder.mLongTitleEmptyLayout.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams longEmptyTipParams = (RelativeLayout.LayoutParams) holder.mLongEmptyTip.getLayoutParams();
                longEmptyTipParams.height = (ScreenUtils.getScreenHeight(mContext) - 750);
                holder.mLongEmptyTip.setLayoutParams(longEmptyTipParams);
                holder.mLongEmptyTip.setVisibility(View.VISIBLE);
                holder.mCommentLayout.setVisibility(View.GONE);
                holder.mLongEmptyTitle.setText(mContext.getResources().getString(R.string.long_comment_title, mLongCommentNum));
                holder.mShortEmptyLayout.setVisibility(View.GONE);
            } else if (position == mLongDatas.size()) {
                holder.mLongEmptyLayout.setVisibility(View.VISIBLE);
                holder.mLongTitleEmptyLayout.setVisibility(View.GONE);
                holder.mLongEmptyTip.setVisibility(View.GONE);
                holder.mCommentLayout.setVisibility(View.GONE);
                holder.mShortEmptyTitle.setText(mContext.getResources().getString(R.string.short_comment_title, mShoreCommentNum));
                Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.comment_icon_fold);
                Drawable mShortTitleDrawable = null;
                if (mShortDatas.size() == 1) {
                    mShortTitleDrawable = new BitmapDrawable(mContext.getResources(), bitmap);
                } else {
                    mShortTitleDrawable = new BitmapDrawable(mContext.getResources(), ImageFileUtil.rotateBitmap(180, bitmap));
                }
                mShortTitleDrawable.setBounds(0, 0, mShortTitleDrawable.getMinimumWidth(), mShortTitleDrawable.getMinimumHeight());
                holder.mShortEmptyTitle.setCompoundDrawables(null, null, mShortTitleDrawable, null);
                holder.mShortEmptyLayout.setVisibility(View.VISIBLE);
            } else {
                holder.mLongEmptyLayout.setVisibility(View.GONE);
                holder.mCommentLayout.setVisibility(View.VISIBLE);
                holder.mCommentTitleLayout.setVisibility(View.GONE);
                holder.mCommentBottomLayout.setVisibility(View.GONE);

                mImageLoader.displayImage(commentData.getAvatar(), holder.mCommenterAvatar, ImageLoaderOption.optionInfoImage(R.mipmap.account_avatar));
                holder.mCommenterName.setText(commentData.getAuthor());
                holder.mCommentLikes.setText(commentData.getLikes());
                holder.mCommentContent.setText(commentData.getContent());
                holder.mCommentTime.setText(DateUtils.getDataFromLong(Long.parseLong(commentData.getTime())));
            }
        }
        // 长评不为空的情况
        else {
            holder.mLongEmptyLayout.setVisibility(View.GONE);
            holder.mCommentLayout.setVisibility(View.VISIBLE);
            if (position == 0) {
                holder.mCommentTitleLayout.setVisibility(View.VISIBLE);
                holder.mCommentTitle.setText(mContext.getResources().getString(R.string.long_comment_title, mLongCommentNum));
                holder.mCommentDetailLayout.setVisibility(View.GONE);
                holder.mCommentBottomLayout.setVisibility(View.GONE);
            } else if (position == mLongDatas.size()) {
                holder.mCommentTitleLayout.setVisibility(View.GONE);
                holder.mCommentDetailLayout.setVisibility(View.GONE);
                holder.mCommentBottomLayout.setVisibility(View.VISIBLE);
                holder.mCommentBottomTitle.setText(mContext.getResources().getString(R.string.short_comment_title, mShoreCommentNum));
                Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.comment_icon_fold);
                Drawable mShortTitleDrawable = null;
                if (mShortDatas.size() == 1) {
                    mShortTitleDrawable = new BitmapDrawable(mContext.getResources(), bitmap);
                } else {
                    mShortTitleDrawable = new BitmapDrawable(mContext.getResources(), ImageFileUtil.rotateBitmap(180, bitmap));
                }
                mShortTitleDrawable.setBounds(0, 0, mShortTitleDrawable.getMinimumWidth(), mShortTitleDrawable.getMinimumHeight());
                holder.mCommentBottomTitle.setCompoundDrawables(null, null, mShortTitleDrawable, null);
            } else {
                holder.mCommentTitleLayout.setVisibility(View.GONE);
                holder.mCommentDetailLayout.setVisibility(View.VISIBLE);

                mImageLoader.displayImage(commentData.getAvatar(), holder.mCommenterAvatar, ImageLoaderOption.optionInfoImage(R.mipmap.account_avatar));
                holder.mCommenterName.setText(commentData.getAuthor());
                holder.mCommentLikes.setText(commentData.getLikes());
                holder.mCommentContent.setText(commentData.getContent());
                holder.mCommentTime.setText(DateUtils.getDataFromLong(Long.parseLong(commentData.getTime())));
                holder.mCommentBottomLayout.setVisibility(View.GONE);
            }
        }
        holder.mCommentDetailLayout.setTag(position);
        holder.mCommentDetailLayout.setOnClickListener(myOnClickListener);
        holder.mShortEmptyTitle.setOnClickListener(myOnClickListener);
        holder.mCommentBottomTitle.setOnClickListener(myOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mAllCommentDatas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout mLongEmptyLayout;
        public TextView mLongEmptyTitle;
        public LinearLayout mShortEmptyLayout;
        public TextView mShortEmptyTitle;
        public LinearLayout mLongTitleEmptyLayout;
        public LinearLayout mLongEmptyTip;

        public LinearLayout mCommentLayout;
        public LinearLayout mCommentTitleLayout;
        public TextView mCommentTitle;
        public LinearLayout mCommentDetailLayout;
        public ImageView mCommenterAvatar;
        public TextView mCommenterName;
        public TextView mCommentLikes;
        public TextView mCommentContent;
        public TextView mCommentTime;
        public LinearLayout mCommentBottomLayout;
        public TextView mCommentBottomTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            mLongEmptyLayout = (RelativeLayout) itemView.findViewById(R.id.comment_long_empty);
            mLongEmptyTitle = (TextView) itemView.findViewById(R.id.long_empty_title);
            mShortEmptyLayout = (LinearLayout) itemView.findViewById(R.id.short_empty_layout);
            mShortEmptyTitle = (TextView) itemView.findViewById(R.id.short_empty_title);
            mLongTitleEmptyLayout = (LinearLayout) itemView.findViewById(R.id.long_empty_layout);
            mLongEmptyTip = (LinearLayout) itemView.findViewById(R.id.long_empty_tip);
            mCommentLayout = (LinearLayout) itemView.findViewById(R.id.comment_layout);
            mCommentTitleLayout = (LinearLayout) itemView.findViewById(R.id.comment_title_layout);
            mCommentTitle = (TextView) itemView.findViewById(R.id.comment_title);
            mCommentDetailLayout = (LinearLayout) itemView.findViewById(R.id.comment_detail_layout);
            mCommenterAvatar = (ImageView) itemView.findViewById(R.id.commenter_avatar);
            mCommenterName = (TextView) itemView.findViewById(R.id.commenter_name);
            mCommentLikes = (TextView) itemView.findViewById(R.id.comment_likes);
            mCommentContent = (TextView) itemView.findViewById(R.id.comment_content);
            mCommentTime = (TextView) itemView.findViewById(R.id.comment_time);
            mCommentBottomLayout = (LinearLayout) itemView.findViewById(R.id.comment_bottom_title_layout);
            mCommentBottomTitle = (TextView) itemView.findViewById(R.id.comment_bottom_title);
        }
    }

    /**
     * 刷新recyclerview
     *
     * @param mLongDatas
     * @param mShortDatas
     */
    public void refreshAdapter(List<CommentData> mLongDatas, List<CommentData> mShortDatas) {
        this.mLongDatas = mLongDatas;
        this.mShortDatas = mShortDatas;
        getAllComment();
        this.notifyDataSetChanged();
    }

    private OnGetShortCommentsListener onGetShortCommentsListener;

    public interface OnGetShortCommentsListener {
        void getShortComments();
    }

    public void setOnGetShortCommentsListener(OnGetShortCommentsListener onGetShortCommentsListener) {
        this.onGetShortCommentsListener = onGetShortCommentsListener;
    }
}
