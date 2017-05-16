package com.mg.mediaplayer.ui;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.mg.mediaplayer.R;
import com.mg.mediaplayer.bean.MediaVideo;
import com.mg.mediaplayer.ui.view.MediaController;
import com.mg.mediaplayer.ui.view.VideoView;

import io.vov.vitamio.MediaPlayer;

public class PlayVideoActivity extends Activity implements VideoView.OnVideoViewCallBackLisenter {
    private int maxVolume;//最大音量
    private int currentVolume;//当前音量
    private AudioManager audioManager;
    private boolean isMute = false;//是否是静音模式

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        final VideoView videoView = (VideoView) findViewById(R.id.vv_video_view);
        //获取uri，如果有数据，说明示网络文件，如果为空，说明是本地文件
        Uri uri = getIntent().getData();
        if (uri != null) {
            //从文件发起播放请求
            videoView.setVideoURI(uri);
        } else {
            //从视频列表传入的
            MediaVideo videoContent = (MediaVideo) getIntent().getExtras().getSerializable("videoList");
            videoView.setVideoURI(Uri.parse(videoContent.getPath()));
        }
//        videoView.setVideoURI(Uri.parse("http://qiubai-video.qiushibaike.com/91B2TEYP9D300XXH_3g.mp4"));
        videoView.setMediaController(new MediaController(this));
        //准备好了
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //横屏播放
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                videoView.setVideoLayout(VideoView.VIDEO_LAYOUT_ZOOM, 0);//参数1：播放为视屏画面原始大小；参数2：设置视频的宽高比，0表示自动检测
                Toast.makeText(PlayVideoActivity.this, "准备好了", Toast.LENGTH_SHORT).show();
//                videoView.setVolume(0.5f,0.9f);

            }
        });
        //播放错误
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(PlayVideoActivity.this, "错误", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        //播放完成
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(PlayVideoActivity.this, "播放完成", Toast.LENGTH_SHORT).show();

            }
        });


        initVolume();
    }

    private void initVolume() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //maxVolume最大是15，
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//        volumn_seekbar.setMax(maxVolume);
//        volumn_seekbar.setProgress(currentVolume);
    }

    /**
     * 更新音量
     */
    private void updateVolume() {
        if (isMute) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
//            volumn_seekbar.setProgress(0);
        } else {
            //第三个参数为1的时候，会显示一个view指示当前音量的变化，一般用0
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
//            volumn_seekbar.setProgress(currentVolume);
        }
    }

    ///////////////////////////////////////////////VedioView毁掉////////////////////////////////////////////
    @Override
    public void onCenterMove(float scale, int flag) {
        if (flag == 0) {
//第三个参数为1的时候，会显示一个view指示当前音量的变化，一般用0
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, scale>0?--currentVolume:++currentVolume, 0);
        }
    }
}
