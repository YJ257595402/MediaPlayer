/*
 * Copyright (C) 2006 The Android Open Source Project
 * Copyright (C) 2013 YIXIA.COM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mg.mediaplayer.ui.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.mg.mediaplayer.R;
import com.mg.utils.tool.LogUtil;

import java.lang.reflect.Method;

import io.vov.vitamio.utils.Log;
import io.vov.vitamio.utils.StringUtils;
import io.vov.vitamio.widget.OutlineTextView;

public class MediaController extends FrameLayout {
    private static final int sDefaultTimeout = 3000;
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;
    private MediaPlayerControl mPlayer;//播放控制接口，view的接口回掉函数
    private Context mContext;
    private PopupWindow mWindow;
    private int mAnimStyle;
    private View mAnchor;
    private View mRoot;
    private SeekBar mProgress;
    private TextView mEndTime, mCurrentTime;
    private TextView mFileName;
    private OutlineTextView mInfoView;
    private String mTitle;
    private long mDuration;
    private boolean mShowing;
    private boolean mDragging;
    private boolean mInstantSeeking = false;
    private boolean mFromXml = false;
    //中间图标显示
    View centerLayout;
    ImageView centerImg;
    TextView centerText;

    //播放按钮
    private ImageButton mLastButton;
    private ImageButton mPauseButton;
    private ImageButton mNextButton;
    private ImageButton mVolumePasueButton;
    private ImageButton mVolumeAddButton;
    private AudioManager mAM;
    private OnShownListener mShownListener;
    private OnHiddenListener mHiddenListener;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            long pos;
            switch (msg.what) {
                case FADE_OUT:
                    hide();
                    break;
                case SHOW_PROGRESS:
                    pos = setProgress();
                    if (!mDragging && mShowing) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                        updatePausePlay();
                    }
                    break;
            }
        }
    };
    /**
     * 播放/暂停按钮监听
     */
    private OnClickListener mPauseListener = new OnClickListener() {
        public void onClick(View v) {
            doPauseResume();
            show(sDefaultTimeout);
        }
    };
    /**
     * 上一集
     */
    private OnClickListener mPlayButtonListener = new OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                //暂停，播放
                case R.id.mediacontroller_play_pause:
                    doPauseResume();
                    show(sDefaultTimeout);
                    break;
                //上一级
                case R.id.mediacontroller_play_next:
                    doPauseResume();
                    show(sDefaultTimeout);
                    break;
                //下一集
                case R.id.mediacontroller_play_last:
                    doPauseResume();
                    show(sDefaultTimeout);
                    break;
            }
        }
    };
    //进度条
    private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
        public void onStartTrackingTouch(SeekBar bar) {
            mDragging = true;
            show(3600000);
            mHandler.removeMessages(SHOW_PROGRESS);
            if (mInstantSeeking)
                mAM.setStreamMute(AudioManager.STREAM_MUSIC, true);
            if (mInfoView != null) {
                mInfoView.setText("");
                mInfoView.setVisibility(View.VISIBLE);
            }
        }

        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (!fromuser)
                return;

            long newposition = (mDuration * progress) / 1000;
            String time = StringUtils.generateTime(newposition);
            if (mInstantSeeking)
                mPlayer.seekTo(newposition);
            if (mInfoView != null)
                mInfoView.setText(time);
            if (mCurrentTime != null)
                mCurrentTime.setText(time);
        }

        public void onStopTrackingTouch(SeekBar bar) {
            if (!mInstantSeeking)
                mPlayer.seekTo((mDuration * bar.getProgress()) / 1000);
            if (mInfoView != null) {
                mInfoView.setText("");
                mInfoView.setVisibility(View.GONE);
            }
            show(sDefaultTimeout);
            mHandler.removeMessages(SHOW_PROGRESS);
            mAM.setStreamMute(AudioManager.STREAM_MUSIC, false);
            mDragging = false;
            mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
        }
    };

    public MediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRoot = this;
        mFromXml = true;
        initController(context);
    }

    public MediaController(Context context) {
        super(context);
        if (!mFromXml && initController(context))
            initFloatingWindow();
    }

    private boolean initController(Context context) {
        mContext = context;
        mAM = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        return true;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mRoot != null)
            initControllerView(mRoot);
    }

    private void initFloatingWindow() {
        mWindow = new PopupWindow(mContext);
        mWindow.setFocusable(false);
        mWindow.setBackgroundDrawable(null);
        mWindow.setOutsideTouchable(true);
        mAnimStyle = android.R.style.Animation;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setWindowLayoutType() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            try {
                mAnchor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
                Method setWindowLayoutType = PopupWindow.class.getMethod("setWindowLayoutType", new Class[]{int.class});
                setWindowLayoutType.invoke(mWindow, WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG);
            } catch (Exception e) {
                Log.e("setWindowLayoutType", e);
            }
        }
    }

    public void setAnchorView(View view) {
        mAnchor = view;
        if (!mFromXml) {
            removeAllViews();
            mRoot = makeControllerView();
            mWindow.setContentView(mRoot);
            mWindow.setWidth(LayoutParams.MATCH_PARENT);
            mWindow.setHeight(LayoutParams.WRAP_CONTENT);
        }
        initControllerView(mRoot);
    }

    /**
     * 构建获取主界面
     *
     * @return
     */
    protected View makeControllerView() {
        return ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(getResources().getIdentifier("layout_mediacontroller", "layout", mContext.getPackageName()), this);
    }

    /**
     * 获取视图控件
     *
     * @param v
     */
    private void initControllerView(View v) {
        mLastButton = (ImageButton) v.findViewById(getResources().getIdentifier("mediacontroller_play_last", "id", mContext.getPackageName()));//上一级
        mPauseButton = (ImageButton) v.findViewById(getResources().getIdentifier("mediacontroller_play_pause", "id", mContext.getPackageName()));//播放，暂停
        mNextButton = (ImageButton) v.findViewById(getResources().getIdentifier("mediacontroller_play_next", "id", mContext.getPackageName()));//下一集
        mVolumePasueButton = (ImageButton) v.findViewById(getResources().getIdentifier("mediacontroller_volume_decrease", "id", mContext.getPackageName()));//静音
        mVolumeAddButton = (ImageButton) v.findViewById(getResources().getIdentifier("mediacontroller_volume_increase", "id", mContext.getPackageName()));//增加音量

        if (mPauseButton != null) {
            mPauseButton.requestFocus();//获取点击事件
            mPauseButton.setOnClickListener(mPlayButtonListener);//添加监听
        }
        //TODO 增加监听
        if (mLastButton != null) {
            mLastButton.requestFocus();
            mLastButton.setOnClickListener(mPlayButtonListener);
        }
        //TODO 增加监听111
        if (mNextButton != null) {
            mNextButton.requestFocus();
            mNextButton.setOnClickListener(mPlayButtonListener);
        }

        mProgress = (SeekBar) v.findViewById(getResources().getIdentifier("mediacontroller_seekbar", "id", mContext.getPackageName()));
        if (mProgress != null) {
            if (mProgress instanceof SeekBar) {
                SeekBar seeker = (SeekBar) mProgress;
                seeker.setOnSeekBarChangeListener(mSeekListener);
            }
            mProgress.setMax(1000);
        }

        mEndTime = (TextView) v.findViewById(getResources().getIdentifier("mediacontroller_time_total", "id", mContext.getPackageName()));
        mCurrentTime = (TextView) v.findViewById(getResources().getIdentifier("mediacontroller_time_current", "id", mContext.getPackageName()));
        mFileName = (TextView) v.findViewById(getResources().getIdentifier("mediacontroller_file_name", "id", mContext.getPackageName()));
        if (mFileName != null)
            mFileName.setText(mTitle);

        //中间控件生命
        centerLayout = v.findViewById(getResources().getIdentifier("mediacontroller_center", "id", mContext.getPackageName()));
        centerImg = (ImageView) v.findViewById(getResources().getIdentifier("mediacontroller_center_icon", "id", mContext.getPackageName()));
        centerText = (TextView) v.findViewById(getResources().getIdentifier("mediacontroller_center_txt", "id", mContext.getPackageName()));

    }

    public void setMediaPlayer(MediaPlayerControl player) {
        mPlayer = player;
        updatePausePlay();
    }

    /**
     * Control the action when the seekbar dragged by user
     *
     * @param seekWhenDragging True the media will seek periodically
     */
    public void setInstantSeeking(boolean seekWhenDragging) {
        mInstantSeeking = seekWhenDragging;
    }

    public void show() {
        show(sDefaultTimeout);
    }

    /**
     * Set the content of the file_name TextView
     *
     * @param name
     */
    public void setFileName(String name) {
        mTitle = name;
        if (mFileName != null)
            mFileName.setText(mTitle);
    }

    /**
     * Set the View to hold some information when interact with the
     * MediaController
     *
     * @param v
     */
    public void setInfoView(OutlineTextView v) {
        mInfoView = v;
    }

    public void setAnimationStyle(int animationStyle) {
        mAnimStyle = animationStyle;
    }

    public void show(int timeout) {
        if (!mShowing && mAnchor != null && mAnchor.getWindowToken() != null) {
            if (mPauseButton != null)
                mPauseButton.requestFocus();

            if (mFromXml) {
                setVisibility(View.VISIBLE);
            } else {
                int[] location = new int[2];

                mAnchor.getLocationOnScreen(location);
                Rect anchorRect = new Rect(location[0], location[1], location[0] + mAnchor.getWidth(), location[1] + mAnchor.getHeight());

                mWindow.setAnimationStyle(mAnimStyle);
                setWindowLayoutType();
                mWindow.showAtLocation(mAnchor, Gravity.NO_GRAVITY, anchorRect.left, anchorRect.bottom);
            }
            mShowing = true;
            if (mShownListener != null)
                mShownListener.onShown();
        }
        updatePausePlay();
        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT), timeout);
        }
    }

    public boolean isShowing() {
        return mShowing;
    }

    public void hide() {
        if (mAnchor == null)
            return;
        if (mShowing) {
            try {
                mHandler.removeMessages(SHOW_PROGRESS);
                if (mFromXml)
                    setVisibility(View.GONE);
                else
                    mWindow.dismiss();
            } catch (IllegalArgumentException ex) {
                Log.d("MediaController already removed");
            }
            mShowing = false;
            if (mHiddenListener != null)
                mHiddenListener.onHidden();
        }
    }

    public void setOnShownListener(OnShownListener l) {
        mShownListener = l;
    }

    public void setOnHiddenListener(OnHiddenListener l) {
        mHiddenListener = l;
    }

    /**
     * 设置进度条进度
     *
     * @return
     */
    private long setProgress() {
        if (mPlayer == null || mDragging)
            return 0;

        long position = mPlayer.getCurrentPosition();
        long duration = mPlayer.getDuration();
        if (mProgress != null) {
            if (duration > 0) {
                long pos = 1000L * position / duration;
                mProgress.setProgress((int) pos);
            }
            int percent = mPlayer.getBufferPercentage();
            mProgress.setSecondaryProgress(percent * 10);
        }

        mDuration = duration;

        if (mEndTime != null)
            mEndTime.setText(StringUtils.generateTime(mDuration));
        if (mCurrentTime != null)
            mCurrentTime.setText(StringUtils.generateTime(position));

        return position;
    }

//    /**
//     * 设置屏幕中间进度
//     *
//     * @param lightOrVolume true:亮度，false：音量
//     * @return
//     */
//    private long setCenterProgress(boolean lightOrVolume) {
//
//        if (mPlayer == null || mDragging)
//            return 0;
//        long position = mPlayer.getCurrentPosition();
////        long duration = mPlayer.getDuration();
//        if (centerLayout != null && centerImg != null && centerText != null) {
//            LogUtil.e("KKKKK", "----------------.................................................................................");
//            centerLayout.setVisibility(View.VISIBLE);
//            centerImg.setImageResource(getResources().getIdentifier(lightOrVolume ? "vp_light_icon_big" : "vp_vol_icon_big", "drawable", mContext.getPackageName()));
//            centerText.setText(mPlayer.getVolume() + "%");
////            centerText.setText(StringUtil.formatVideoDuration(position)+"/"+StringUtil.formatVideoDuration(duration));
//        }
//        if (mProgress != null) {
//            if (duration > 0) {
//                long pos = 1000L * position / duration;
//                mProgress.setProgress((int) pos);
//            }
//            int percent = mPlayer.getBufferPercentage();
//            mProgress.setSecondaryProgress(percent * 10);
//        }
//        mDuration = duration;
//        if (mEndTime != null)
//            mEndTime.setText(StringUtils.generateTime(mDuration));
//        if (mCurrentTime != null)
//            mCurrentTime.setText(StringUtils.generateTime(position));
//        return position;
//    }

    int flag = 0;
    static int eX, eY;
    float ffff = 0.5f;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        show(sDefaultTimeout);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                flag = 0;
                eX = (int) event.getX();
                eY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                LogUtil.e("KKKKK", "-----------------flag = " + flag + "----x=" + event.getX() + "-====----y=" + event.getY());
                flag++;
                if (flag > 5 && flag % 5 == 0) {
                    float mX = (int) (eX - event.getX());//zuoyou
                    float mY = (int) (eY - event.getY());//sahmgxia
                    if (Math.abs(mX) > Math.abs(mY)) {//调进度
                        if (mX > 0) {
//                            LogUtil.e("KKKKK", "-----------------t进度=====快进=====================");
                        } else {
//                            LogUtil.e("KKKKK", "-----------------t进度=========到退=================");
                        }
                    } else {//调音量
                        int height = getResources().getDisplayMetrics().heightPixels / 2;
//                        setCenterProgress(eX < getResources().getDisplayMetrics().widthPixels / 2);//真亮度，假音量
                        float scale = Math.abs(mY / height);//scale>0 -->mY > 0:下滑，音量减
                        mPlayer.setCenter(scale, 0);//0:yinliang
                        LogUtil.e("KKKKK", "------"+scale+"-----------flag = " + flag + "----x=" + event.getX() + "-====----y=" + event.getY());

                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                eY = eX = 0;
                flag = 0;
                if (centerLayout != null) {
                    centerLayout.setVisibility(View.GONE);
                }
                break;
        }
        return true;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        show(sDefaultTimeout);
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        //监听多种播放，停止按键事件  hang up calls and stop media.；Play/Pause media key.；Space key.
        if (event.getRepeatCount() == 0 && (keyCode == KeyEvent.KEYCODE_HEADSETHOOK || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_SPACE)) {
            doPauseResume();
            show(sDefaultTimeout);
            if (mPauseButton != null)
                mPauseButton.requestFocus();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP) {//停止媒体 Stop media key.
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
                updatePausePlay();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {//返回，菜单按钮
            hide();
            return true;
        } else {
            show(sDefaultTimeout);
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 更新暂停按钮图标
     */
    private void updatePausePlay() {
        if (mRoot == null || mPauseButton == null)
            return;
        if (mPlayer.isPlaying())
            mPauseButton.setImageResource(getResources().getIdentifier("mediacontroller_pause", "drawable", mContext.getPackageName()));
        else
            mPauseButton.setImageResource(getResources().getIdentifier("mediacontroller_play", "drawable", mContext.getPackageName()));
    }

    /**
     * 暂停，播放时，操作
     */
    private void doPauseResume() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            mPlayer.start();
        }
        //更新图标
        updatePausePlay();
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (mPauseButton != null)
            mPauseButton.setEnabled(enabled);
        if (mProgress != null)
            mProgress.setEnabled(enabled);
        super.setEnabled(enabled);
    }


    public interface OnShownListener {
        public void onShown();
    }

    public interface OnHiddenListener {
        public void onHidden();
    }

    public interface MediaPlayerControl {
        /**
         * 开始播放
         */
        void start();

        /**
         * 暂停播放
         */
        void pause();

        /**
         * 视频时长
         *
         * @return
         */
        long getDuration();

        /**
         * 当前进度
         *
         * @return
         */
        long getCurrentPosition();

        void seekTo(long pos);

        boolean isPlaying();

        int getBufferPercentage();

        /**
         * 下一集
         */
        void next();

        /**
         * 上一集
         */
        void last();

        void setCenter(float scale, int i);
    }
}
