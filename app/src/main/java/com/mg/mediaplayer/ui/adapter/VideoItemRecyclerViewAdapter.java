package com.mg.mediaplayer.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mg.mediaplayer.R;
import com.mg.mediaplayer.bean.MediaVideo;
import com.mg.mediaplayer.ui.fragment.MediaListFragment;
import com.mg.mediaplayer.util.StringUtil;
import com.mg.utils.adapter.RecyclerViewCursorAdapter;

/**
 * Created by YJ on 2017/5/14.
 * VideoItemRecyclerViewAdapter
 */

public class VideoItemRecyclerViewAdapter extends RecyclerViewCursorAdapter<VideoItemRecyclerViewAdapter.ViewHolder> {
    private final Context mContext;
    private final MediaListFragment.OnListFragmentInteractionListener mListener;

    public VideoItemRecyclerViewAdapter(Context ctx, Cursor cor, int flags, MediaListFragment.OnListFragmentInteractionListener listener) {
        super(ctx, cor, flags);
        this.mContext = ctx;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_fragment_media_list_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, Cursor cursor) {
        holder.mItem = MediaVideo.fromCursor(cursor);
        holder.initData();
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem, MediaListFragment.TYPE_MEDIA_VEDIO);//标识 video
                }
            }
        });
    }

    @Override
    protected void onContentChanged() {
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mIconView;
        public final TextView mTitleView;
        public final TextView mDurationView;
        public final TextView mSizeView;
        public MediaVideo mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIconView = (ImageView) view.findViewById(R.id.iv_frag_media_item_img);
            mTitleView = (TextView) view.findViewById(R.id.tv_frag_media_item_title);
            mDurationView = (TextView) view.findViewById(R.id.tv_frag_media_item_duration);
            mSizeView = (TextView) view.findViewById(R.id.tv_frag_media_item_size);
        }

        private void initData() {
            if (mItem != null) {
                mIconView.setImageBitmap(StringUtil.getVideoThumbnail(mItem.getPath()));
                mTitleView.setText(mItem.getTitle());
                mDurationView.setText(StringUtil.formatVideoDuration(mItem.getDuration()));
                mSizeView.setText(Formatter.formatFileSize(mContext, mItem.getSize()));
            }
        }
    }
}
