package com.appstore.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.appstore.fragment.AppsFragment;
import com.appstore.fragment.GameFragment;
import com.appstore.fragment.MainFragment;
import com.appstore.fragment.RankFragment;
import com.appstore.fragment.RecommentFragment;
import com.appstore.fragment.SubjectFragment;
import com.appstore.fragment.TypeFragment;

/**
 * Created by skysoft on 2016/8/2.
 */
public class MyPagerAdapter extends FragmentStatePagerAdapter {
    protected static final String[] SUB_FRAGMENT = new String[]{"首页", "应用", "游戏", "专题", "推荐", "分类", "排行"};
    private int mCount = SUB_FRAGMENT.length;

    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    MainFragment mainFragment;

    public Fragment getCurrentFrag(int position){
        switch (position){
            case 0:
                return mainFragment;
        }
        return null;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                mainFragment = new MainFragment();
                Bundle args = new Bundle();
                args.putString("arg", SUB_FRAGMENT[position]);
                mainFragment.setArguments(args);
                return mainFragment;
            case 1:
                Fragment fragment1 = new AppsFragment();
                Bundle args1 = new Bundle();
                args1.putString("arg1", SUB_FRAGMENT[position]);
                fragment1.setArguments(args1);
                return fragment1;
            case 2:
                Fragment fragment2 = new GameFragment();
                Bundle args2 = new Bundle();
                args2.putString("arg2", SUB_FRAGMENT[position]);
                fragment2.setArguments(args2);
                return fragment2;
            case 3:
                Fragment fragment3 = new SubjectFragment();
                Bundle args3 = new Bundle();
                args3.putString("arg3", SUB_FRAGMENT[position]);
                fragment3.setArguments(args3);
                return fragment3;
            case 4:
                Fragment fragment4 = new RecommentFragment();
                Bundle args4 = new Bundle();
                args4.putString("arg4", SUB_FRAGMENT[position]);
                fragment4.setArguments(args4);
                return fragment4;
            case 5:
                Fragment fragment5 = new TypeFragment();
                Bundle args5 = new Bundle();
                args5.putString("arg5", SUB_FRAGMENT[position]);
                fragment5.setArguments(args5);
                return fragment5;
            case 6:
                Fragment fragment6 = new RankFragment();
                Bundle args6 = new Bundle();
                args6.putString("arg6", SUB_FRAGMENT[position]);
                fragment6.setArguments(args6);
                return fragment6;
            default:
                System.out.println("创建子Fragment1_" + position + "失败");
                return null;
        }
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return SUB_FRAGMENT[position % mCount];
    }
}