package com.appstore.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.appstore.R;
import com.appstore.widget.KeywordsFlow;
import com.appstore.widget.KeywordsFlowView;

/**
 * Created by 张艳琴 on 2016/9/19.
 * <p>
 * 推荐界面
 */
public class RecommentFragment extends Fragment {

    private KeywordsFlowView mKeywordsView;

    public static final String[] keywords = {"Apple", "Android", "呵呵",
            "高富帅", "女神", "拥抱", "旅行", "爱情", "屌丝", "搞笑", "暴走漫画", "重邮", "信科",
            "唯美", "汪星人", "秋天", "雨天", "科幻", "黑夜",
            "孤独", "星空", "东京食尸鬼", "金正恩", "张全蛋", "东京热", "陈希妍",
            "明星", "NBA", "马云", "码农", "动漫", "时尚", "熊孩子", "地理", "伤感",
            "二次元"
    };

    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_recomment, null);
        mContext = getActivity();
        initView(contentView);
        return contentView;
    }

    private void initView(View v) {
        mKeywordsView = (KeywordsFlowView) v.findViewById(R.id.keywordsFlowView);
        //设置每次随机飞入文字的个数
        mKeywordsView.setTextShowSize(15);
        //设置是否允许滑动屏幕切换文字
        mKeywordsView.shouldScroolFlow(true);
        //开始展示
        mKeywordsView.show(keywords, KeywordsFlow.ANIMATION_IN);
        //设置文字的点击点击监听
        mKeywordsView.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, ((TextView) v).getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}