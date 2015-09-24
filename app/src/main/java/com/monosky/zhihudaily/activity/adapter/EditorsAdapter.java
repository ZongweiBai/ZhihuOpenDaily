package com.monosky.zhihudaily.activity.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.monosky.zhihudaily.R;
import com.monosky.zhihudaily.activity.EditorDetailActivity;
import com.monosky.zhihudaily.module.EditorData;
import com.monosky.zhihudaily.util.ImageFileUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * 主编信息
 * Created by jonez_000 on 2015/9/17.
 */
public class EditorsAdapter extends RecyclerView.Adapter<EditorsAdapter.ViewHolder> {

    private Context mContext;
    private List<EditorData> mEditorDataList;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private View.OnClickListener myOnclickListener;

    public EditorsAdapter(Context mContext, List<EditorData> mEditorDataList) {
        this.mContext = mContext;
        this.mEditorDataList = mEditorDataList;
        this.myOnclickListener = onclickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.activity_editors_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        EditorData editorData = mEditorDataList.get(position);
        ImageFileUtil.displayImage(imageLoader, editorData.getAvatar(), holder.mEditorAvatar);
        holder.mEditorName.setText(editorData.getName());
        holder.mEditorBio.setText(editorData.getBio());

        holder.mEditorLayout.setTag(position);
        holder.mEditorLayout.setOnClickListener(myOnclickListener);
    }

    @Override
    public int getItemCount() {
        return mEditorDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout mEditorLayout;
        public ImageView mEditorAvatar;
        public TextView mEditorName;
        public TextView mEditorBio;

        public ViewHolder(View itemView) {
            super(itemView);
            mEditorLayout = (RelativeLayout) itemView.findViewById(R.id.editor_item_layout);
            mEditorAvatar = (ImageView) itemView.findViewById(R.id.editor_item_avatar);
            mEditorName = (TextView) itemView.findViewById(R.id.editor_item_name);
            mEditorBio = (TextView) itemView.findViewById(R.id.editor_item_bio);
        }
    }

    View.OnClickListener onclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.editor_item_layout:
                    int pos = (int) v.getTag();
                    Intent intent = new Intent(mContext, EditorDetailActivity.class);
                    intent.putExtra("editorId", mEditorDataList.get(pos).getId());
                    mContext.startActivity(intent);
                    break;
            }
        }
    };
}
