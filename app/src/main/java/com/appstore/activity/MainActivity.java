package com.appstore.activity;

import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appstore.R;
import com.appstore.adapter.MyPagerAdapter;
import com.appstore.widget.ObServerScrollView;
import com.appstore.widget.ScrollViewListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MyPagerAdapter mAdapter;
    private ViewPager mPager;
    private ObServerScrollView scrollView;
    private ArrayList<String> list;
    private int idex = 0;
    private int offset;
    private int cursorWidth;
    private ImageView cursor = null;
    private Animation animation = null;
    private int nowScroll = 0;
    private int oldX = 0;
    private int scrollX = 0;
    private int oldScrollX = 0;
    private int currentX = 0;
    private int oldPosition = 0;
    private int position = 0;
    private TextView tag1;
    private TextView tag2;
    private TextView tag3;
    private TextView tag4;
    private TextView tag5;
    private TextView tag6;
    private TextView tag7;
    private int screenWidth;
    View view;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.addHeaderView(headerView());
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                actionBarDrawerToggle.syncState();
            }
        });
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        mAdapter = new MyPagerAdapter(fragmentManager);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
        mPager.setCurrentItem(0);
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        screenWidth = display.getWidth();
        init();
        initCursor(4);
    }

    @Override
    public void onClick(View v) {
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        int[] location1 = new int[2];
        cursor.getLocationOnScreen(location1);
        currentX = location[0] + offset;

        switch (v.getId()) {
            case R.id.tvTag1:
                mPager.setCurrentItem(0);
                break;
            case R.id.tvTag2:
                mPager.setCurrentItem(1);
                break;
            case R.id.tvTag3:
                mPager.setCurrentItem(2);
                break;
            case R.id.tvTag4:
                mPager.setCurrentItem(3);
                break;
            case R.id.tvTag5:
                mPager.setCurrentItem(4);
                break;
            case R.id.tvTag6:
                mPager.setCurrentItem(5);
                break;
            case R.id.tvTag7:
                mPager.setCurrentItem(6);
                break;
        }
    }

    private void init() {
        String[] str = new String[]{"首页", "应用", "游戏", "专题", "推荐", "分类", "排行", "时尚"}; //
        scrollView = (ObServerScrollView) findViewById(R.id.horizontalScrollView_home);
        tag1 = ((TextView) findViewById(R.id.tvTag1));
        tag1.setOnClickListener(this);
        tag2 = ((TextView) findViewById(R.id.tvTag2));
        tag2.setOnClickListener(this);
        tag3 = ((TextView) findViewById(R.id.tvTag3));
        tag3.setOnClickListener(this);
        tag4 = ((TextView) findViewById(R.id.tvTag4));
        tag4.setOnClickListener(this);
        tag5 = ((TextView) findViewById(R.id.tvTag5));
        tag5.setOnClickListener(this);
        tag6 = ((TextView) findViewById(R.id.tvTag6));
        tag6.setOnClickListener(this);
        tag7 = ((TextView) findViewById(R.id.tvTag7));
        tag7.setOnClickListener(this);

        tag1.setLayoutParams(new LinearLayout.LayoutParams(screenWidth/4,LinearLayout.LayoutParams.WRAP_CONTENT));
        tag2.setLayoutParams(new LinearLayout.LayoutParams(screenWidth/4,LinearLayout.LayoutParams.WRAP_CONTENT));
        tag3.setLayoutParams(new LinearLayout.LayoutParams(screenWidth/4,LinearLayout.LayoutParams.WRAP_CONTENT));
        tag4.setLayoutParams(new LinearLayout.LayoutParams(screenWidth/4,LinearLayout.LayoutParams.WRAP_CONTENT));
        tag5.setLayoutParams(new LinearLayout.LayoutParams(screenWidth/4,LinearLayout.LayoutParams.WRAP_CONTENT));
        tag6.setLayoutParams(new LinearLayout.LayoutParams(screenWidth/4,LinearLayout.LayoutParams.WRAP_CONTENT));
        tag7.setLayoutParams(new LinearLayout.LayoutParams(screenWidth/4,LinearLayout.LayoutParams.WRAP_CONTENT));
        list = new ArrayList<String>();
        for (int i = 0; i < str.length; i++) {
            list.add(str[i]);
        }
        scrollView.setScrollViewListener(new ScrollViewListener() {
            @Override
            public void onScrollChanged(ObServerScrollView scrollView, int x, int y, int oldx, int oldy) {

                scrollX = x;
                nowScroll = scrollX - oldScrollX;
                oldX = currentX - nowScroll;
            }
        });
    }

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int arg0) {
            position = arg0;
            int x = arg0 - oldPosition;
            currentX = oldX + x * (screenWidth / 4);
            if (oldPosition < position) {
                if (currentX + (screenWidth / 8) <= screenWidth) {//超出
                    animation = new TranslateAnimation(oldX + scrollX, currentX + scrollX, 0, 0);
                    animation.setFillAfter(true);
                    animation.setDuration(100);
                    cursor.startAnimation(animation);
                    oldX = currentX;
                } else {
                    animation = new TranslateAnimation(oldX + scrollX, currentX + scrollX, 0, 0);
                    animation.setFillAfter(true);
                    animation.setDuration(100);
                    cursor.startAnimation(animation);
                    oldX = currentX;
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            scrollView.post(new Runnable() {
                                @Override
                                public void run() {
                                    scrollView.smoothScrollBy((screenWidth / 4), 0);
                                }
                            });
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                }
            } else if (position < oldPosition) {
                int width = screenWidth / 8;
                if (currentX - width > 0) {
                    animation = new TranslateAnimation(oldX + scrollX, currentX + scrollX, 0, 0);
                    animation.setFillAfter(true);
                    animation.setDuration(100);
                    cursor.startAnimation(animation);
                    oldX = currentX;
                } else {
                    animation = new TranslateAnimation(oldX + scrollX, currentX + scrollX, 0, 0);
                    animation.setFillAfter(true);
                    animation.setDuration(100);
                    cursor.startAnimation(animation);
                    oldX = currentX;
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            scrollView.post(new Runnable() {
                                @Override
                                public void run() {
                                    int s = screenWidth / 4;
                                    scrollView.smoothScrollBy(-s, 0);
                                }
                            });
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            }
            oldX = currentX;
            oldScrollX = scrollX;
            oldPosition = position;

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

    }

    public void initCursor(int tagNum) {
        cursorWidth = screenWidth / 4;
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        offset = ((dm.widthPixels / tagNum) - cursorWidth) / 2;
        cursor = (ImageView) findViewById(R.id.ivCursor);
        animation = new TranslateAnimation(0, offset, 0, 0);
        animation.setFillAfter(true);
        animation.setDuration(100);
        cursor.startAnimation(animation);
        oldX = offset;
    }

    private View headerView() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View headerView = inflater.inflate(
                R.layout.head_layout, null);
        return headerView;
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
