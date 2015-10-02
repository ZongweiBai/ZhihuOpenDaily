package com.monosky.zhihudaily.activity.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.monosky.zhihudaily.BaseApplication;
import com.monosky.zhihudaily.R;
import com.monosky.zhihudaily.module.ThemeData;

import java.util.Iterator;
import java.util.List;

/**
 * 首页菜单列表Adapter
 * Created by jonez_000 on 2015/8/28.
 */
public class DrawerMenuAdapter extends RecyclerView.Adapter<DrawerMenuAdapter.ViewHolder> {

    private Context mContext;
    private List<ThemeData> themeDatas;
    private List<String> themeLikes;
    private String mCurrentThemeId = "-1";
    private View.OnClickListener onClickListener;
    private OnDrawerItemClickListener onDrawerItemClickListener;

    public DrawerMenuAdapter(Context context, List<ThemeData> themeDatas, List<String> themeLikes) {
        this.mContext = context;
        this.themeDatas = themeDatas;
        this.themeLikes = themeLikes;
        this.onClickListener = onMyClickListener;
        resetData();
    }

    /**
     * 对数据进行处理
     */
    private void resetData() {
        if (themeLikes != null && !themeLikes.isEmpty()) {
            ThemeData theme = null;
            for (int i = themeLikes.size() - 1; i >= 0; i--) {
                for (Iterator<ThemeData> iterator = themeDatas.iterator(); iterator.hasNext();) {
                    theme = iterator.next();
                    if (theme.getThemeId().equals(themeLikes.get(i))) {
                        iterator.remove();
                    }
                }
                themeDatas.add(0, BaseApplication.themeDataMap.get(themeLikes.get(i)));
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 创建一个View
        View view = View.inflate(parent.getContext(), R.layout.activity_main_drawer_item, null);
        // 创建一个ViewHolder
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // 绑定数据到ViewHolder上
        ThemeData themeData = themeDatas.get(position);
        if (position == 0) {
            holder.mTopLayout.setVisibility(View.VISIBLE);
            holder.mMiddleLayout.setVisibility(View.VISIBLE);

            holder.mTopLogin.setOnClickListener(onClickListener);
            holder.mTopFavorites.setOnClickListener(onClickListener);
            holder.mTopDownload.setOnClickListener(onClickListener);
            holder.mMiddleLayout.setOnClickListener(onClickListener);
            if(mCurrentThemeId.equals("-1")) {
                holder.mMiddleLayout.setBackgroundColor(mContext.getResources().getColor(R.color.app_main_color));
            } else {
                holder.mMiddleLayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            }
        } else {
            holder.mTopLayout.setVisibility(View.GONE);
            holder.mMiddleLayout.setVisibility(View.GONE);
        }
        holder.mBottomLayout.setTag(position);
        holder.mBottomLayout.setOnClickListener(onClickListener);
        holder.mBottonImgLayout.setTag(position);
        holder.mBottonImgLayout.setOnClickListener(onClickListener);
        if(BaseApplication.themeLikes.contains(themeData.getThemeId())) {
            holder.mBottomImg.setImageResource(R.mipmap.menu_arrow);
        } else {
            holder.mBottomImg.setImageResource(R.mipmap.menu_follow);
        }
        if(!mCurrentThemeId.equals("-1")) {
            if(themeData.getThemeId().equals(mCurrentThemeId)) {
                holder.mBottomLayout.setBackgroundColor(mContext.getResources().getColor(R.color.app_main_color));
            } else {
                holder.mBottomLayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            }
        }
        holder.mBottonTitle.setText(themeData.getThemeName());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout mTopLayout;
        public LinearLayout mMiddleLayout;
        public RelativeLayout mBottomLayout;
        public LinearLayout mTopLogin;
        public ImageView mTopAvatar;
        public TextView mTopLoginStatus;
        public LinearLayout mTopFavorites;
        public LinearLayout mTopDownload;
        public RelativeLayout mBottonImgLayout;
        public TextView mBottonTitle;
        public ImageView mBottomImg;

        public ViewHolder(View itemView) {
            super(itemView);
            mTopLayout = (LinearLayout) itemView.findViewById(R.id.drawer_item_top_layout);
            mMiddleLayout = (LinearLayout) itemView.findViewById(R.id.drawer_item_middle_layout);
            mBottomLayout = (RelativeLayout) itemView.findViewById(R.id.drawer_item_bottom_layout);
            mTopLogin = (LinearLayout) itemView.findViewById(R.id.top_login_layout);
            mTopAvatar = (ImageView) itemView.findViewById(R.id.top_avatar);
            mTopLoginStatus = (TextView) itemView.findViewById(R.id.login_status);
            mTopFavorites = (LinearLayout) itemView.findViewById(R.id.top_favorites_layout);
            mTopDownload = (LinearLayout) itemView.findViewById(R.id.top_download_layout);
            mBottonImgLayout = (RelativeLayout) itemView.findViewById(R.id.bottom_img_layout);
            mBottonTitle = (TextView) itemView.findViewById(R.id.bottom_title);
            mBottomImg = (ImageView) itemView.findViewById(R.id.bottom_img);
        }
    }

    View.OnClickListener onMyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (onDrawerItemClickListener != null) {
                ThemeData themeData = null;
                if (v.getTag() != null) {
                    try {
                        int pos = Integer.parseInt(String.valueOf(v.getTag()));
                        themeData = themeDatas.get(pos);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                onDrawerItemClickListener.onItemClick(v, themeData);
            }
        }
    };

    @Override
    public int getItemCount() {
        if (themeDatas == null || themeDatas.isEmpty()) {
            return 0;
        }
        return themeDatas.size();
    }

    public interface OnDrawerItemClickListener {
        void onItemClick(View view, Object data);
    }

    public void setItemClickListner(OnDrawerItemClickListener listner) {
        this.onDrawerItemClickListener = listner;
    }

    /**
     * 刷新adapter
     * @param currentThemeId
     */
    public void refreshAdapter(String currentThemeId) {
        this.mCurrentThemeId = currentThemeId;
        this.notifyDataSetChanged();
    }

    /**
     * 刷新adapter
     * @param themeDataList
     * @param themeLikes
     */
    public void refreshAdapter(List<ThemeData> themeDataList, List<String> themeLikes) {
        this.themeDatas = themeDataList;
        this.themeLikes = themeLikes;
        resetData();
        this.notifyDataSetChanged();
    }
}
