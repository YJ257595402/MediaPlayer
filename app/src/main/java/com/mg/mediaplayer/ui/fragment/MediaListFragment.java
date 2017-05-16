package com.mg.mediaplayer.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mg.mediaplayer.R;
import com.mg.mediaplayer.bean.MediaBean;
import com.mg.mediaplayer.ui.adapter.AudioItemRecyclerViewAdapter;
import com.mg.mediaplayer.ui.adapter.VideoItemRecyclerViewAdapter;
import com.mg.mediaplayer.util.SimpleQueryHandler;
import com.mg.utils.adapter.RecyclerViewCursorAdapter;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MediaListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_MEDIA_TYPE = "media-type";
    public static final int TYPE_MEDIA_AUDIO = 0;
    public static final int TYPE_MEDIA_VEDIO = 1;
    private int mColumnCount = 1;//列数
    private int mMediaType = 0;//0:audio,1:vedio
    private OnListFragmentInteractionListener mListener;
    private SimpleQueryHandler queryHandler;
    private RecyclerViewCursorAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MediaListFragment() {
    }

    // TODO: Customize parameter initialization

    /**
     * @param columnCount
     * @param flag        0:audio,1:vedio
     * @return
     */
    @SuppressWarnings("unused")
    public static MediaListFragment newInstance(int columnCount, int flag) {
        MediaListFragment fragment = new MediaListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putInt(ARG_MEDIA_TYPE, flag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mMediaType = getArguments().getInt(ARG_MEDIA_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_media_list, container, false);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            // String[] projection = {MediaStore.Video.Media._ID, MediaStore.Audio.Media.SIZE,MediaStore.Video.Media.DURATION,MediaStore.Video.Media.TITLE,MediaStore.Video.Media.DATA};
            //Cursor cursor = getActivity().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
            //先传入空参数，然后异步查询后更新界面
            if (mMediaType == MediaListFragment.TYPE_MEDIA_VEDIO) {
                adapter = new VideoItemRecyclerViewAdapter(context, null, 0, mListener);
            } else if (mMediaType == MediaListFragment.TYPE_MEDIA_AUDIO) {
                adapter = new AudioItemRecyclerViewAdapter(context, null, 0, mListener);
            }
            recyclerView.setAdapter(adapter);
            //TODO 记加载数据
            addAdapterData();


        }
        return view;
    }

    private void addAdapterData() {
        queryHandler = new SimpleQueryHandler(getActivity().getContentResolver());
        String[] projection = null;
        //根据标识获取内容
        switch (mMediaType) {
            case TYPE_MEDIA_AUDIO:
                projection = new String[]{MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DURATION};
                queryHandler.startQuery(0, adapter, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
                break;
            case TYPE_MEDIA_VEDIO:
                projection = new String[]{MediaStore.Video.Media._ID, MediaStore.Audio.Media.SIZE,
                        MediaStore.Video.Media.DURATION, MediaStore.Video.Media.TITLE, MediaStore.Video.Media.DATA};
                queryHandler.startQuery(0, adapter, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener<MB extends MediaBean> {
        // TODO: Update argument type and name

        /**
         * 标识，是属于audio还是video
         */
        void onListFragmentInteraction(MB item, int flag);
    }
}
