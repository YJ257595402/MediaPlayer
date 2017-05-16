package com.mg.mediaplayer.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;

import com.mg.mediaplayer.R;

public class SplashActivity extends Activity {
    private Handler handler = new Handler();

    private boolean hasEnterMain = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        delayMainActivity(true);
    }

    private void delayMainActivity(final boolean isDelay) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //设置标志，直接调用时，延时2秒执行，若这两秒中用户点击了屏幕，直接进入，将标识设为false，让两秒后不再重新进入
                if (hasEnterMain) {
                    hasEnterMain = false;
                    // Intent intent = new Intent(SplashActivity.this,isDelay?MainActivity.class:ADActivity.class);
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, isDelay ? 1000 : 0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                delayMainActivity(false);
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(null);
    }
}
