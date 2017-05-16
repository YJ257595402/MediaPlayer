package com.mg.mediaplayer.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mg.mediaplayer.R;
import com.mg.mediaplayer.bean.MediaAudio;
import com.mg.mediaplayer.bean.MediaBean;
import com.mg.mediaplayer.bean.MediaVideo;
import com.mg.mediaplayer.ui.fragment.MediaListFragment;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements View.OnClickListener, MediaListFragment.OnListFragmentInteractionListener {
    private TextView tabVideo, tabAudio;
    private View indicateLine;
    private int indicateLineWidth;

    private ViewPager viewPager;
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    private int lastCurrentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initListener();
        initData();
    }

    public void initViews() {
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.vp_act_main_tab_view_count);
        tabVideo = (TextView) findViewById(R.id.tv_act_main_view_tab_video);
        tabAudio = (TextView) findViewById(R.id.tv_act_main_view_tab_audio);
        indicateLine = findViewById(R.id.v_act_main_tab_view_indicate_line);
    }

    public void initListener() {
        tabVideo.setOnClickListener(this);
        tabAudio.setOnClickListener(this);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int targetPosition = position * indicateLineWidth + positionOffsetPixels / fragments.size();
                ViewPropertyAnimator.animate(indicateLine).translationX(targetPosition).setDuration(0);
            }

            @Override
            public void onPageSelected(int position) {
                lightAndScaleTitle();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void initData() {
        fragments.add(MediaListFragment.newInstance(1, MediaListFragment.TYPE_MEDIA_AUDIO));
        fragments.add(MediaListFragment.newInstance(2, MediaListFragment.TYPE_MEDIA_VEDIO));
        initIndicateLineWidth();
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }
        });
        lightAndScaleTitle();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_act_main_view_tab_video:
                viewPager.setCurrentItem(1);
                break;
            case R.id.tv_act_main_view_tab_audio:
                viewPager.setCurrentItem(0);
                break;
        }

    }

    /**
     * 计算下方指示线的宽
     */
    private void initIndicateLineWidth() {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        indicateLineWidth = screenWidth / fragments.size();
        indicateLine.getLayoutParams().width = indicateLineWidth;
        indicateLine.requestLayout();
    }

    /**
     * 将当前先选中tab高亮
     */
    private void lightAndScaleTitle() {
        int currentPage = viewPager.getCurrentItem();
        Resources res = getResources();
        tabAudio.setTextColor(currentPage == 0 ? res.getColor(R.color.indicate_line) : res.getColor(R.color.gray_white));
        tabVideo.setTextColor(currentPage == 1 ? res.getColor(R.color.indicate_line) : res.getColor(R.color.gray_white));

        ViewPropertyAnimator.animate(tabAudio).scaleX(currentPage == 0 ? 1.2f : 1.0f).scaleY(currentPage == 0 ? 1.2f : 1.0f).setDuration(200);
        ViewPropertyAnimator.animate(tabVideo).scaleX(currentPage == 1 ? 1.2f : 1.0f).scaleY(currentPage == 0 ? 1.2f : 1.0f).setDuration(200);
//        ViewPropertyAnimator.animate(tabAudio).scaleY(currentPage == 0 ? 1.2f : 1.0f).setDuration(200);
//        ViewPropertyAnimator.animate(tabAudio).scaleX(currentPage == 0 ? 1.2f : 1.0f).setDuration(200);
//        ViewPropertyAnimator.animate(tabVideo).scaleX(currentPage == 1 ? 1.2f : 1.0f).setDuration(200);
//        ViewPropertyAnimator.animate(tabVideo).scaleY(currentPage == 1 ? 1.2f : 1.0f).setDuration(200);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onListFragmentInteraction(MediaBean item, int flag) {
        Bundle bundle = new Bundle();
        switch (flag) {
            case MediaListFragment.TYPE_MEDIA_VEDIO:
                bundle.putSerializable("videoList", (MediaVideo) item);
                startActivity(PlayVideoActivity.class, bundle);
                break;
            case MediaListFragment.TYPE_MEDIA_AUDIO:
                bundle.putSerializable("audioList", (MediaAudio) item);
                startActivity(PlayAudioActivity.class, bundle);
                break;
        }
        Toast.makeText(this, item.toString(), Toast.LENGTH_SHORT).show();
    }


    private void startActivity(Class cls, Bundle bundle) {
        Intent intent = new Intent(this, cls);
        intent.putExtras(bundle);
        startActivity(intent);

    }
}