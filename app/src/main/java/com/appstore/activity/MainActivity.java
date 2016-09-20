package com.appstore.activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appstore.R;
import com.appstore.adapter.MyPagerAdapter;
import com.appstore.utils.PopupWindowUtils;
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
    private ImageView Ivmenu;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fragmentManager = getSupportFragmentManager();
        mAdapter = new MyPagerAdapter(fragmentManager);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
        mPager.setCurrentItem(0);
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        screenWidth = display.getWidth();
        Ivmenu= (ImageView) findViewById(R.id.slide_menu);
        Ivmenu.setOnClickListener(this);
        init();
        initCursor(6);
    }

    @Override
    public void onClick(View v) {
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        int[] location1 = new int[2];
        cursor.getLocationOnScreen(location1);
        currentX = location[0] + offset;
      //  Toast.makeText(getApplicationContext(), currentX + "", Toast.LENGTH_SHORT).show();

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
            case R.id.slide_menu:
                initView2();
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
            Log.d("Debug", currentX + ".......currentX");
            if (oldPosition < position) {
                if (currentX + (screenWidth / 8) <= screenWidth) {//超出
                    Log.d("Debug", "2222222222");
                    animation = new TranslateAnimation(oldX + scrollX, currentX + scrollX, 0, 0);
                    animation.setFillAfter(true);
                    animation.setDuration(100);
                    cursor.startAnimation(animation);
                    oldX = currentX;
                } else {
                    Log.d("Debug", "333333333333333");
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
                    Log.d("Debug", "111111111");
                    Log.d("Debug", currentX + ".......currentX........." + oldX);
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
        Log.d("Debug", cursorWidth + "....");
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        offset = ((dm.widthPixels / tagNum) - cursorWidth) / 2;
        Log.d("Debug", offset + "......offset........" + (dm.widthPixels / tagNum));
        cursor = (ImageView) findViewById(R.id.ivCursor);
        animation = new TranslateAnimation(0, offset, 0, 0);
        animation.setFillAfter(true);
        animation.setDuration(100);
        cursor.startAnimation(animation);
        oldX = offset;
    }

    public void initView2() {
        // 找到设置好的layout
        view = LayoutInflater.from(MainActivity.this).inflate(R.layout.popup,
                null);
        // TODO Auto-generated method stub
        int screenWidth = MainActivity.this.getWindowManager()
                .getDefaultDisplay().getWidth();
        int screenHeigh = MainActivity.this.getWindowManager()
                .getDefaultDisplay().getHeight();
        View downView = (LinearLayout) view.findViewById(R.id.mLayout);
        // 启动popupWindow；
        PopupWindowUtils popu = new PopupWindowUtils(MainActivity.this,
                screenWidth, screenHeigh - 100, downView, view);
       // center = (TextView) view.findViewById(R.id.center);// 个人中心
    }

}
