package com.mg.mediaplayer.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mg.mediaplayer.R;
import com.mg.mediaplayer.bean.MediaAudio;
import com.mg.mediaplayer.ui.fragment.MediaListFragment;
import com.mg.utils.adapter.RecyclerViewCursorAdapter;

/**
 * Created by YJ on 2017/5/14.
 * VideoItemRecyclerViewAdapter
 */

public class AudioItemRecyclerViewAdapter extends RecyclerViewCursorAdapter<AudioItemRecyclerViewAdapter.ViewHolder> {
    private final Context mContext;
    private final MediaListFragment.OnListFragmentInteractionListener mListener;

    public AudioItemRecyclerViewAdapter(Context ctx, Cursor cor, int flags, MediaListFragment.OnListFragmentInteractionListener listener) {
        super(ctx, cor, flags);
        this.mContext = ctx;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_fragment_media_list_audio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, Cursor cursor) {
        holder.mItem = MediaAudio.fromCursor(cursor);
        holder.initData();
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem, MediaListFragment.TYPE_MEDIA_AUDIO);
                }
            }
        });
    }

    @Override
    protected void onContentChanged() {
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mIconView;
        public final TextView mTitleView;
        public final TextView mDurationView;
        public final TextView mSizeView;
        public MediaAudio mItem;

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
                //音频图片需要特殊设置
//                Bitmap bitmap = StringUtil.getAudioAlbum(mItem.getPath());
//                if (bitmap != null) {
//                    mIconView.setImageBitmap(bitmap);
//                }
                mTitleView.setText(mItem.getTitle());
                mDurationView.setText(mItem.getArtist());
                mSizeView.setText("");
            }
        }
    }
}

